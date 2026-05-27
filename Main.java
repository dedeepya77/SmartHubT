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

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        AsciiArt.splash();
        Animation.blinkingTitle("🚇 WELCOME TO SMART HUB 🚇");
        authMenu();
    }

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
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Exit"));
            System.out.println();
            ConsoleUI.line();
            
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Enter Choice" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            int choice = getInt();
            
            switch (choice) {
                case 1:
                    if (AuthService.login()) {
                        homeMenu();
                    }
                    break;
                case 2:
                    AuthService.signup();
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

    public static void homeMenu() {
        Bookable transportLambda = () -> TransportService.menu();
        Bookable stayLambda = () -> StayService.menu();
        Bookable rentalLambda = () -> RentalService.menu();
        //PaymentService paymentService = new PaymentService();

        while (true) {
            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🚇 SMART HUB  |  👤 " + AuthService.getLoggedInUser());
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
            
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Enter Choice" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            int choice = getInt();
            
            switch (choice) {
                case 1:
                    transportLambda.book();
                    break;
                case 2:
                    stayLambda.book();
                    break;
                case 3:
                    rentalLambda.book(); 
                    break;
                case 4:
                    PaymentService.menu();
                    break;
                case 0:
                    AuthService.logout();
                    return;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

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

    public static int getInt() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}