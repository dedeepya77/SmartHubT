package services.payment;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import services.PaymentService;
import ui.ConsoleUI;
import utils.Animation;

public abstract class PaymentMethod {

    protected int amount;

    public PaymentMethod(int amount) {
        this.amount = amount;
    }

    /**
     * Collect payment details from the user.
     * Returns true if details were gathered successfully, false if the user
     * entered invalid data or chose to abort — in which case pay() will not proceed.
     */
    public abstract boolean collectDetails();
    public abstract String getIdentifier();
    public abstract String getMethodName();

    public boolean pay() {
        boolean ok = collectDetails();
        if (!ok) return false;          // user entered bad data or backed out — abort silently

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN +
            "⏳ Processing payment of ₹" + amount + " via " + getMethodName() + ConsoleUI.RESET));
        System.out.println();
        Animation.paymentProcessing();
        showReceipt();
        return true;
    }

    // =========================================================================
    //  RECEIPT  — shown on screen AND exported to a .txt file
    // =========================================================================
    private void showReceipt() {

        String time       = PaymentService.getCurrentTime();
        String methodName = getMethodName();
        String identifier = getIdentifier();
        String receiptId  = generateReceiptId();

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🧾 PAYMENT RECEIPT");
        ConsoleUI.line();
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 60) + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("✅  " + ConsoleUI.BOLD + ConsoleUI.BRIGHT_GREEN
            + "PAYMENT SUCCESSFUL" + ConsoleUI.RESET + "  ✅"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('─', 60) + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("🧾 Receipt ID  : " + ConsoleUI.BOLD + receiptId + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("💳 Method      : " + ConsoleUI.BOLD + methodName + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("🔖 Identifier  : " + identifier));
        System.out.println();
        System.out.println(ConsoleUI.center("💰 Amount Paid : " + ConsoleUI.BOLD
            + ConsoleUI.BRIGHT_GREEN + "₹" + amount + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("🕐 Time        : " + time));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 60) + ConsoleUI.RESET));
        System.out.println();

        String filename = exportReceipt(receiptId, methodName, identifier, time);
        if (filename != null) {
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN
                + "📄 Receipt saved → " + ConsoleUI.BOLD + filename + ConsoleUI.RESET));
            System.out.println();
        }

        ConsoleUI.pause();
    }

    private static final String RECEIPTS_FILE = "receipts.txt";

    private String exportReceipt(String receiptId, String methodName,
                                  String identifier, String time) {
        String user   = services.AuthService.getLoggedInUser();
        String line60 = "=".repeat(60);
        String line60d = "-".repeat(60);
        String entry  =
            line60 + "\n" +
            "           SMARTHUB PAYMENT RECEIPT\n" +
            line60 + "\n\n" +
            "  Receipt ID  : " + receiptId   + "\n" +
            "  Customer    : " + (user != null ? user : "Guest") + "\n" +
            "  Method      : " + methodName  + "\n" +
            "  Identifier  : " + identifier  + "\n" +
            "  Amount Paid : Rs. " + amount  + "\n" +
            "  Date & Time : " + time        + "\n\n" +
            line60d + "\n" +
            "  Thank you for choosing SmartHub!\n" +
            "  Keep this receipt for your records.\n" +
            line60 + "\n\n";
        // Append to one shared file (append = true) — no per-receipt files
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECEIPTS_FILE, true))) {
            bw.write(entry);
            return RECEIPTS_FILE;
        } catch (Exception e) {
            return null;
        }
    }

    private static String generateReceiptId() {
        String hex  = Integer.toHexString((int)(Math.random() * 0xFFFF)).toUpperCase();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "RCPT-" + hex + "-" + date;
    }

    protected static String repeat(char ch, int count) {
        return ConsoleUI.repeat(ch, count);
    }
}