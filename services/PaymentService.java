package services;

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

    // ================= ADD BOOKING =================

    public static void addBooking(String type, String details, int amount) {
        loadBookings();
        bookings.add(new Booking(type, details + " - " + getCurrentTime(), amount));
        saveBookings();
    }

    // ================= PROCESS PAYMENT =================

    public static boolean processPayment(String bookingType, int amount) {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("💳 PAYMENT GATEWAY");
            ConsoleUI.line();

            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "🧾 Booking : " + ConsoleUI.BOLD + bookingType + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "💰 Amount  : " + ConsoleUI.BOLD + "₹" + amount + ConsoleUI.RESET));
            System.out.println();

            ConsoleUI.line();

            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "── Select Payment Method ──" + ConsoleUI.RESET));
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

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Choose Payment Method" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );

            int choice = getInt();

            // ── Polymorphism: PaymentMethod reference holds whichever
            //    subclass the user picked. method.pay() calls the correct
            //    subclass implementation at runtime automatically. ──────────
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

    // ================= MENU =================

    public static void menu() {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("💳 PAYMENT CENTER");
            ConsoleUI.line();

            System.out.println();
            System.out.println(ConsoleUI.center("[1] 📋 Booking History"));
            System.out.println();
            System.out.println(ConsoleUI.center("[2] 💰 Total Spending"));
            System.out.println();
            System.out.println(ConsoleUI.center("[0] Back"));

            ConsoleUI.line();

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Enter Choice" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );

            int choice = getInt();

            switch (choice) {
                case 1: showHistory(); break;
                case 2: showTotal();   break;
                case 0: return;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

    // ================= HISTORY =================

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
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 70) + ConsoleUI.RESET));
            System.out.println(ConsoleUI.center(ConsoleUI.BOLD + ConsoleUI.BRIGHT_CYAN + "  BOOKING #" + count++ + "  " + ConsoleUI.RESET));
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 70) + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("🎫 Booking ID : " + ConsoleUI.BOLD + b.getBookingId() + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("🚀 Service    : " + ConsoleUI.BOLD + b.getType() + ConsoleUI.RESET));
            System.out.println();
            System.out.println(ConsoleUI.center("📌 Details    : " + b.getDetails()));
            System.out.println();
            System.out.println(ConsoleUI.center("💰 Amount     : " + ConsoleUI.BOLD + ConsoleUI.BRIGHT_GREEN + "₹" + b.getAmount() + ConsoleUI.RESET));
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 70) + ConsoleUI.RESET));
        System.out.println();

        ConsoleUI.pause();
    }

    // ================= TOTAL =================

    public static void showTotal() {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("💰 TOTAL SPENDING");
        ConsoleUI.line();

        loadBookings();

        System.out.println();

        // ── No bookings: skip animation, show zero directly ──────────────────
        if (bookings.isEmpty()) {

            System.out.println(
                ConsoleUI.center(
                    ConsoleUI.BRIGHT_YELLOW +
                    "💳 No bookings yet!" +
                    ConsoleUI.RESET
                )
            );

            System.out.println();

            System.out.println(
                ConsoleUI.center(
                    ConsoleUI.BRIGHT_GREEN +
                    "✅ Total Spending : ₹0" +
                    ConsoleUI.RESET
                )
            );

            System.out.println();

            ConsoleUI.pause();
            return;
        }

        // ── Has bookings: run animation then show total ───────────────────────
        int total = 0;

        for (Booking b : bookings) {
            total += b.getAmount();
        }

        Animation.paymentProcessing();

        System.out.println(
            ConsoleUI.center(
                "✅ Total Spending (GST Included) : ₹" + total
            )
        );

        System.out.println();

        ConsoleUI.pause();
    }

    // ================= SAVE =================

    public static void saveBookings() {

        String user = AuthService.getLoggedInUser();
        ArrayList<String> allLines = new ArrayList<>();

        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith(user + "|")) allLines.add(line);
                    }
                }
            }
        } catch (Exception e) { }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : allLines)  { bw.write(line);                    bw.newLine(); }
            for (Booking b   : bookings)  { bw.write(user + "|" + b.toFileString()); bw.newLine(); }
        } catch (Exception e) { }
    }

    // ================= LOAD =================

    public static void loadBookings() {

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

    // ================= TIME =================

    public static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));
    }

    // ================= LOCAL HELPER =================

    private static int getInt() {
        try {
            return Integer.parseInt(new java.util.Scanner(System.in).nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}