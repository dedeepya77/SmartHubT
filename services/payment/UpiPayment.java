package services.payment;

import java.util.Scanner;
import ui.ConsoleUI;

public class UpiPayment extends PaymentMethod {
    private static final Scanner sc = new Scanner(System.in);
    private String upiId;

    public UpiPayment(int amount) {
        super(amount);
    }

    @Override
    public boolean collectDetails() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("📱 UPI PAYMENT");
        ConsoleUI.line();

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.DIM + "Enter your UPI ID (e.g. name@upi)" + ConsoleUI.RESET));
        System.out.println();

        while (true) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  ┌─ UPI ID" + ConsoleUI.RESET
                + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD
                + "  └──➤ " + ConsoleUI.RESET);
            upiId = sc.nextLine().trim();
            if (!upiId.isEmpty() && upiId.contains("@")) break;
            ConsoleUI.error("Invalid UPI ID! Must contain '@' (e.g. name@upi). Try again.");
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "📲 Sending ₹" + amount
            + " request to " + ConsoleUI.BOLD + upiId + ConsoleUI.RESET));
        return true;
    }

    @Override public String getIdentifier()  { return upiId; }
    @Override public String getMethodName()  { return "UPI"; }
}