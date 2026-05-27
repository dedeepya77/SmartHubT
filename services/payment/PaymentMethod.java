package services.payment;

import services.PaymentService;
import ui.ConsoleUI;
import utils.Animation;

public abstract class PaymentMethod {
    protected int amount;
    
    public PaymentMethod(int amount) {
        this.amount = amount;
    }
    
    public abstract void collectDetails();
    public abstract String getIdentifier();
    public abstract String getMethodName();
    
    public boolean pay() {
        collectDetails();
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "⏳ Processing payment of ₹" + amount + " via " + getMethodName() + ConsoleUI.RESET));
        System.out.println();
        Animation.paymentProcessing();
        showReceipt();
        return true;
    }
    
    private void showReceipt() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🧾 PAYMENT RECEIPT");
        ConsoleUI.line();
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 60) + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("✅  " + ConsoleUI.BOLD + ConsoleUI.BRIGHT_GREEN + "PAYMENT SUCCESSFUL" + ConsoleUI.RESET + "  ✅"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('─', 60) + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("💳 Method     : " + ConsoleUI.BOLD + getMethodName() + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("🔖 Identifier : " + getIdentifier()));
        System.out.println();
        System.out.println(ConsoleUI.center("💰 Amount Paid: " + ConsoleUI.BOLD + ConsoleUI.BRIGHT_GREEN + "₹" + amount + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center("🕐 Time       : " + PaymentService.getCurrentTime()));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + ConsoleUI.repeat('═', 60) + ConsoleUI.RESET));
        System.out.println();
        ConsoleUI.pause();
    }
}