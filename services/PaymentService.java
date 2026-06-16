package services;
import utils.Session;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import models.Booking;
import services.payment.*;
import ui.ConsoleUI;
import utils.Animation;

public class PaymentService {

    private static ArrayList<Booking> bookings = new ArrayList<>();
    private static final String FILE_NAME = "bookings.txt";

    // ── Admin credentials (hardcoded) ─────────────────────────────────────────
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin@123";

    // =========================================================================
    //  ADD BOOKING
    // =========================================================================
    public static void addBooking(String type, String details, int amount) {
        loadBookings();
        bookings.add(new Booking(type, details, amount));   // timestamp stored inside Booking
        saveBookings();
    }

    // =========================================================================
    //  PROCESS PAYMENT
    // =========================================================================
    public static boolean processPayment(String bookingType, int amount) {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("💳 PAYMENT GATEWAY");
            ConsoleUI.line();

            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN
                + "🧾 Booking : " + ConsoleUI.BOLD + bookingType + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "💰 Amount  : " + ConsoleUI.BOLD + "₹" + amount + ConsoleUI.RESET));
            System.out.println();
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "── Select Payment Method ──" + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1] 📱 UPI"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2] 🏦 Net Banking"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[3] 💳 Credit / Debit Card"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Cancel Payment"));
            System.out.println();
            ConsoleUI.line();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ Choose Payment Method" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);

            int choice = getInt();

            PaymentMethod method;
            switch (choice) {
                case 1: method = new UpiPayment(amount);        break;
                case 2: method = new NetBankingPayment(amount); break;
                case 3: method = new CardPayment(amount);       break;
                case 0: return false;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
                    continue;
            }

            return method.pay();
        }
    }

    // =========================================================================
    //  MENU  (normal user)
    // =========================================================================
    public static void menu() {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("💳 PAYMENT CENTER");
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1] 📋 Booking History"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2] 💰 Total Spending"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[3] ❌ Cancel a Booking"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Back"));
            System.out.println();
            ConsoleUI.line();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ Enter Choice" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);

            int choice = getInt();

            switch (choice) {
                case 1: showHistory();      break;
                case 2: showTotal();        break;
                case 3: cancelBooking();    break;
                case 0: return;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

    // =========================================================================
    //  BOOKING HISTORY
    // =========================================================================
    public static void showHistory() {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("📋 BOOKING HISTORY");
        ConsoleUI.line();

        loadBookings();

        if (bookings.isEmpty()) {
            ConsoleUI.error("No Bookings Found!");
            ConsoleUI.pause();
            return;
        }

        int count = 1;

        for (Booking b : bookings) {

            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN
                + ConsoleUI.repeat('═', 70) + ConsoleUI.RESET));
            System.out.println(ConsoleUI.center(ConsoleUI.BOLD + ConsoleUI.BRIGHT_CYAN
                + "  BOOKING #" + count++ + "  " + ConsoleUI.RESET));
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN
                + ConsoleUI.repeat('═', 70) + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("🎫 Booking ID : "
                + ConsoleUI.BOLD + b.getBookingId() + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("🚀 Service    : "
                + ConsoleUI.BOLD + b.getType() + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("📌 Details    : " + b.getDetails()));
            System.out.println();
            System.out.println(ConsoleUI.center("🕐 Booked At  : " + b.getTimestamp()));
            System.out.println();
            System.out.println(ConsoleUI.center("💰 Amount     : "
                + ConsoleUI.BOLD + ConsoleUI.BRIGHT_GREEN + "₹" + b.getAmount() + ConsoleUI.RESET));
            System.out.println();

            // Status line
            if (b.isCancelled()) {
                System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED
                    + ConsoleUI.BOLD + "🚫 STATUS : CANCELLED" + ConsoleUI.RESET));
            } else if (b.isCancellable()) {
                System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW
                    + "✅ Cancellable  |  ⏳ "
                    + b.hoursLeftToCancel() + " hr(s) left" + ConsoleUI.RESET));
            } else {
                System.out.println(ConsoleUI.center(ConsoleUI.DIM
                    + "🔒 Cannot cancel (window expired)" + ConsoleUI.RESET));
            }
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN
            + ConsoleUI.repeat('═', 70) + ConsoleUI.RESET));
        System.out.println();

        ConsoleUI.pause();
    }

    // =========================================================================
    //  CANCEL BOOKING  (within 30-minute window)
    // =========================================================================
    public static void cancelBooking() {

        loadBookings();

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("❌ CANCEL A BOOKING");
        ConsoleUI.line();

        if (bookings.isEmpty()) {
            ConsoleUI.error("No bookings found!");
            ConsoleUI.pause();
            return;
        }

        // Collect only cancellable bookings
        ArrayList<Booking> cancellable = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.isCancellable()) cancellable.add(b);
        }

        if (cancellable.isEmpty()) {
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED
                + "No cancellable bookings." + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.DIM
                + "Bookings can only be cancelled within 2 days of booking."
                + ConsoleUI.RESET));
            System.out.println();
            ConsoleUI.pause();
            return;
        }

        // Show cancellable bookings numbered
        System.out.println();
        int idx = 1;
        for (Booking b : cancellable) {
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN
                + ConsoleUI.repeat('─', 60) + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "[" + idx++ + "]  " + b.getType()
                + "  |  ₹" + b.getAmount()
                + "  |  ⏳ " + b.hoursLeftToCancel() + " hr(s) left"
                + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.DIM
                + "    " + b.getDetails() + ConsoleUI.RESET));
            System.out.println();
        }
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Back" + ConsoleUI.RESET));
        System.out.println();
        ConsoleUI.line();

        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  ┌─ Select Booking to Cancel" + ConsoleUI.RESET
            + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  └──➤ " + ConsoleUI.RESET);

        int choice = getInt();
        if (choice <= 0 || choice > cancellable.size()) {
            ConsoleUI.error("Invalid selection!");
            ConsoleUI.pause();
            return;
        }

        Booking selected = cancellable.get(choice - 1);

        // Confirm
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW
            + "Cancel: " + ConsoleUI.BOLD + selected.getType()
            + " – ₹" + selected.getAmount() + ConsoleUI.RESET));
        System.out.println();
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  ┌─ Confirm Cancellation? (yes/no)" + ConsoleUI.RESET
            + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  └──➤ " + ConsoleUI.RESET);

        String confirm = new java.util.Scanner(System.in).nextLine().trim();
        Session.keepAlive();
        if (!confirm.equalsIgnoreCase("yes")) {
            ConsoleUI.error("Cancellation aborted.");
            ConsoleUI.pause();
            return;
        }

        selected.setCancelled(true);
        saveBookings();

        // Export a cancellation receipt
        exportCancellationReceipt(selected);

        ConsoleUI.success("Booking Cancelled! Refund will be processed in 5–7 business days.");
        ConsoleUI.pause();
    }

    private static void exportCancellationReceipt(Booking b) {
        String filename = "cancel_" + b.getBookingId().replace(":", "-") + ".txt";
        String line60   = "=".repeat(60);
        String content  =
            line60 + "\n" +
            "        SMARTHUB CANCELLATION RECEIPT\n" +
            line60 + "\n\n" +
            "  Booking ID  : " + b.getBookingId()  + "\n" +
            "  Customer    : " + AuthService.getLoggedInUser() + "\n" +
            "  Service     : " + b.getType()        + "\n" +
            "  Details     : " + b.getDetails()     + "\n" +
            "  Amount      : Rs. " + b.getAmount()  + "\n" +
            "  Booked At   : " + b.getTimestamp()   + "\n" +
            "  Cancelled At: " + getCurrentTime()   + "\n\n" +
            "  Refund of Rs. " + b.getAmount()
                + " will be processed in 5-7 business days.\n\n" +
            line60 + "\n";
        try (java.io.BufferedWriter bw =
                new java.io.BufferedWriter(new java.io.FileWriter(filename))) {
            bw.write(content);
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "📄 Cancellation receipt → " + ConsoleUI.BOLD + filename + ConsoleUI.RESET));
        } catch (Exception e) {
            // non-fatal — cancellation still succeeded
        }
    }

    // =========================================================================
    //  TOTAL SPENDING
    // =========================================================================
    public static void showTotal() {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("💰 TOTAL SPENDING");
        ConsoleUI.line();

        loadBookings();
        System.out.println();

        if (bookings.isEmpty()) {
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW
                + "💳 No bookings yet!" + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "✅ Total Spending : ₹0" + ConsoleUI.RESET));
            System.out.println();
            ConsoleUI.pause();
            return;
        }

        int total = 0;
        for (Booking b : bookings) {
            if (!b.isCancelled()) total += b.getAmount();
        }

        Animation.paymentProcessing();

        System.out.println(ConsoleUI.center(
            "✅ Total Spending (active bookings, GST included) : ₹" + total));
        System.out.println();
        ConsoleUI.pause();
    }

    // =========================================================================
    //  ADMIN MODE
    //  Separate entry point called from Main — not accessible from normal menu.
    // =========================================================================

    /** Returns true if the provided credentials match the admin account. */
    public static boolean adminLogin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    /**
     * Full admin console.  Reads ALL users' bookings from the file and lets
     * the admin filter by user or service type.
     */
    public static void adminMenu() {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🛡  ADMIN PANEL");
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "[1] 📋 View All Bookings"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "[2] 🔍 Filter by Username"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "[3] 🔍 Filter by Service Type"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "[4] 💰 Total Revenue (all users)"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED
                + "[0] Logout Admin"));
            System.out.println();
            ConsoleUI.line();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ Enter Choice" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);

            int choice = getInt();

            switch (choice) {
                case 1: adminShowAll(null, null);  break;
                case 2: adminFilterUser();          break;
                case 3: adminFilterType();          break;
                case 4: adminRevenue();             break;
                case 0: return;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

    private static void adminFilterUser() {
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  ┌─ Enter Username to Filter" + ConsoleUI.RESET
            + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  └──➤ " + ConsoleUI.RESET);
        String user = new java.util.Scanner(System.in).nextLine().trim();
        Session.keepAlive();
        adminShowAll(user, null);
    }

    private static void adminFilterType() {
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.DIM
            + "Service types: Transport, Bus, Cab, Hotel, Bike Rental, Car Rental"
            + ConsoleUI.RESET));
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  ┌─ Enter Service Type" + ConsoleUI.RESET
            + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
            + "  └──➤ " + ConsoleUI.RESET);
        String type = new java.util.Scanner(System.in).nextLine().trim();
        Session.keepAlive();
        adminShowAll(null, type);
    }

    /**
     * Reads every line of bookings.txt (all users) and displays matching rows.
     * @param filterUser  if non-null, show only this username (case-insensitive)
     * @param filterType  if non-null, show only this service type (case-insensitive)
     */
    private static void adminShowAll(String filterUser, String filterType) {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        String titleStr = "📋 ALL BOOKINGS";
        if (filterUser != null) titleStr += "  —  User: " + filterUser;
        if (filterType != null) titleStr += "  —  Type: " + filterType;
        ConsoleUI.title(titleStr);
        ConsoleUI.line();

        ArrayList<String[]> rows = loadAllBookings();   // [username, bookingLine]

        if (rows.isEmpty()) {
            ConsoleUI.error("No bookings on record.");
            ConsoleUI.pause();
            return;
        }

        int count = 0;

        for (String[] row : rows) {
            String rowUser = row[0];
            Booking b      = Booking.fromFileString(row[1]);
            if (b == null) continue;

            if (filterUser != null && !rowUser.equalsIgnoreCase(filterUser)) continue;
            if (filterType != null && !b.getType().equalsIgnoreCase(filterType)) continue;

            count++;
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN
                + ConsoleUI.repeat('─', 70) + ConsoleUI.RESET));
            System.out.println(ConsoleUI.center(ConsoleUI.BOLD
                + "#" + count + "  [" + rowUser + "]" + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("🎫 " + b.getBookingId()));
            System.out.println();
            System.out.println(ConsoleUI.center("🚀 " + b.getType()
                + "   📌 " + b.getDetails()));
            System.out.println();
            System.out.println(ConsoleUI.center("🕐 " + b.getTimestamp()
                + "   💰 ₹" + b.getAmount()
                + (b.isCancelled()
                    ? "   " + ConsoleUI.BRIGHT_RED + "🚫 CANCELLED" + ConsoleUI.RESET
                    : "   " + ConsoleUI.BRIGHT_GREEN + "✅ ACTIVE" + ConsoleUI.RESET)));
        }

        if (count == 0) {
            ConsoleUI.error("No matching bookings found.");
        } else {
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.DIM
                + "Total records shown: " + count + ConsoleUI.RESET));
        }

        System.out.println();
        ConsoleUI.pause();
    }

    private static void adminRevenue() {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("💰 PLATFORM REVENUE");
        ConsoleUI.line();

        ArrayList<String[]> rows = loadAllBookings();

        int total = 0, active = 0, cancelled = 0;

        for (String[] row : rows) {
            Booking b = Booking.fromFileString(row[1]);
            if (b == null) continue;
            if (b.isCancelled()) { cancelled++; }
            else                 { active++;  total += b.getAmount(); }
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
            + "✅ Active Bookings   : " + active + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED
            + "🚫 Cancelled Bookings: " + cancelled + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD
            + "💰 Total Revenue (active only) : ₹" + total + ConsoleUI.RESET));
        System.out.println();
        ConsoleUI.pause();
    }

    // =========================================================================
    //  SAVE / LOAD  (synchronized for multi-thread safety)
    // =========================================================================

    public static synchronized void saveBookings() {

        String user = AuthService.getLoggedInUser();
        ArrayList<String> allLines = new ArrayList<>();

        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Keep lines belonging to OTHER users unchanged
                        if (!line.startsWith(user + "|")) allLines.add(line);
                    }
                }
            }
        } catch (Exception e) { }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : allLines) { bw.write(line); bw.newLine(); }
            for (Booking b  : bookings)  {
                bw.write(user + "|" + b.toFileString());
                bw.newLine();
            }
        } catch (Exception e) { }
    }

    public static synchronized void loadBookings() {

        bookings.clear();
        String user = AuthService.getLoggedInUser();
        if (user == null) return;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(user + "|")) continue;
                Booking b = Booking.fromFileString(line.substring(user.length() + 1));
                if (b != null) bookings.add(b);
            }
        } catch (Exception e) { }
    }

    /**
     * Reads ALL lines from bookings.txt regardless of username.
     * Returns a list of [username, serialisedBookingLine] pairs.
     * Used exclusively by admin functions.
     */
    private static ArrayList<String[]> loadAllBookings() {
        ArrayList<String[]> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                int sep = line.indexOf('|');
                if (sep < 1) continue;
                String user    = line.substring(0, sep);
                String payload = line.substring(sep + 1);
                result.add(new String[]{ user, payload });
            }
        } catch (Exception e) { }
        return result;
    }

    // =========================================================================
    //  TIME / HELPERS
    // =========================================================================
    public static String getCurrentTime() {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));
    }

    private static int getInt() {
        Session.keepAlive();
        try {
            return Integer.parseInt(new java.util.Scanner(System.in).nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}