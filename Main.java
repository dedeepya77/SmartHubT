import java.util.Scanner;
import services.AuthService;
import services.Bookable;
import services.PaymentService;
import services.RentalService;
import services.StayService;
import services.TransportService;
import ui.AsciiArt;
import ui.ConsoleUI;
import utils.Animation;
import utils.Session;

public class Main {

    static Scanner sc = new Scanner(System.in);

    // ── Idle timeout: 7 minutes ───────────────────────────────────────────────
    private static final long IDLE_TIMEOUT_MS = 7 * 60 * 1000;

    public static void main(String[] args) {
        AsciiArt.splash();
        Animation.blinkingTitle("🚇 WELCOME TO SMART HUB 🚇");
        authMenu();
    }

    // =========================================================================
    //  AUTH MENU
    // =========================================================================
    public static void authMenu() {
        while (true) {
            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🔐 SMART HUB ACCESS");
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1] 🔐 Login"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2] 📝 Signup"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "[9] 🛡  Admin Login"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Exit"));
            System.out.println();
            ConsoleUI.line();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Enter Choice" + ConsoleUI.RESET + "\n" +
                ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            int choice = getInt();

            switch (choice) {
                case 1:
                    if (AuthService.login()) {
                        startUserSession();
                    }
                    break;
                case 2:
                    AuthService.signup();
                    break;
                case 9:
                    adminLogin();
                    break;
                case 0:
                    exit();
                    break;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

    // =========================================================================
    //  ADMIN LOGIN
    // =========================================================================
    private static void adminLogin() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🛡  ADMIN LOGIN");
        ConsoleUI.line();

        final int MAX = 3;
        for (int attempt = 1; attempt <= MAX; attempt++) {

            if (attempt > 1) {
                System.out.println();
                System.out.println(ConsoleUI.center(ConsoleUI.DIM
                    + "Attempt " + attempt + " of " + MAX + ConsoleUI.RESET));
            }

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ Admin Username" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);
            String user = sc.nextLine().trim();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ Admin Password" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);
            String pass = sc.nextLine().trim();

            if (PaymentService.adminLogin(user, pass)) {
                ConsoleUI.success("Admin Access Granted!");
                ConsoleUI.pause();
                PaymentService.adminMenu();
                return;
            }

            ConsoleUI.error("Invalid admin credentials!"
                + (attempt < MAX ? " (" + (MAX - attempt) + " attempt(s) left)" : ""));
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + ConsoleUI.BOLD
            + "Too many failed attempts. Returning to menu." + ConsoleUI.RESET));
        System.out.println();
        ConsoleUI.pause();
    }

    // =========================================================================
    //  SESSION THREAD + IDLE TIMEOUT
    //
    //  Session.lastActivityMs is updated via Session.keepAlive() from every
    //  service on each keypress, so the 7-minute clock only fires on true
    //  inactivity — not just idle time at the home menu.
    // =========================================================================
    private static void startUserSession() {

        Session.keepAlive();   // reset on every new login

        Thread sessionThread = new Thread(() -> {
            try {
                homeMenu();
            } catch (Exception e) {
                // swallow — returns to auth loop
            }
        }, "session-" + AuthService.getLoggedInUser());

        Thread watchdog = new Thread(() -> {
            while (sessionThread.isAlive()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
                long idle = System.currentTimeMillis() - Session.lastActivityMs;
                if (idle >= IDLE_TIMEOUT_MS) {
                    System.out.println();
                    System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + ConsoleUI.BOLD
                        + "⏰  Session timed out due to inactivity. Exiting..."
                        + ConsoleUI.RESET));
                    System.out.println();
                    ConsoleUI.doubleLine();
                    System.out.println();
                    System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD
                        + "THANK YOU FOR USING SMARTHUB"));
                    System.out.println();
                    System.out.println(ConsoleUI.center("🚇 🚌 🚕 🏨 🏍 💳"));
                    System.out.println();
                    System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "Visit Again!"));
                    System.out.println();
                    ConsoleUI.doubleLine();
                    System.exit(0);
                }
            }
        }, "watchdog-" + AuthService.getLoggedInUser());
        watchdog.setDaemon(true);

        sessionThread.start();
        watchdog.start();

        try {
            sessionThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        watchdog.interrupt();
        AuthService.logout();
    }

    // =========================================================================
    //  HOME MENU
    // =========================================================================
    public static void homeMenu() {

        Bookable transportLambda = () -> TransportService.menu();
        Bookable stayLambda      = () -> StayService.menu();
        Bookable rentalLambda    = () -> RentalService.menu();

        while (true) {

            if (AuthService.getLoggedInUser() == null) return;

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🚇 SMART HUB  |  👤 " + AuthService.getLoggedInUser()
                + "  |  🔑 " + AuthService.getSessionToken().substring(0, 8) + "…");
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1] 🚇 Transport Services"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2] 🏨 Hotel & Stay"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[3] 🏍 Vehicle Rentals"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[4] 💳 Payments & History"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Logout"));
            System.out.println();
            ConsoleUI.line();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ Enter Choice" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);

            int choice = pollForInt();

            if (AuthService.getLoggedInUser() == null) return;

            Session.keepAlive();

            switch (choice) {
                case 1: transportLambda.book(); Session.keepAlive(); break;
                case 2: stayLambda.book();      Session.keepAlive(); break;
                case 3: rentalLambda.book();    Session.keepAlive(); break;
                case 4: PaymentService.menu();  Session.keepAlive(); break;
                case 0:
                    AuthService.logout();
                    return;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

    /**
     * Polls System.in.available() every 500 ms instead of blocking forever.
     * Only used at the home menu — sub-menus use normal blocking reads and
     * call Session.keepAlive() themselves on every keypress.
     */
    private static int pollForInt() {
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (AuthService.getLoggedInUser() == null) return -1;

                if (System.in.available() > 0) {
                    int b = System.in.read();
                    if (b == '\n' || b == '\r') {
                        if (b == '\r' && System.in.available() > 0) {
                            System.in.mark(1);
                            int next = System.in.read();
                            if (next != '\n') System.in.reset();
                        }
                        break;
                    }
                    sb.append((char) b);
                } else {
                    Thread.sleep(500);
                }
            }
            return Integer.parseInt(sb.toString().trim());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    // =========================================================================
    //  EXIT
    // =========================================================================
    public static void exit() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD +
            "THANK YOU FOR USING SMARTHUB"));
        System.out.println();
        System.out.println(ConsoleUI.center("🚇 🚌 🚕 🏨 🏍 💳"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "Visit Again!"));
        System.out.println();
        ConsoleUI.doubleLine();
        System.exit(0);
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================
    public static int getInt() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}