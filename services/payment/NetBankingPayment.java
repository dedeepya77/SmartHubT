package services.payment;

import java.util.Scanner;
import ui.ConsoleUI;

public class NetBankingPayment extends PaymentMethod {
    private static final Scanner sc = new Scanner(System.in);
    private String bankName;
    private String customerId;

    public NetBankingPayment(int amount) {
        super(amount);
    }

    @Override
    public void collectDetails() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🏦 NET BANKING");
        ConsoleUI.line();

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1]  State Bank of India (SBI)"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2]  HDFC Bank"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[3]  ICICI Bank"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[4]  Axis Bank"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[5]  Kotak Mahindra Bank"));
        System.out.println();
        ConsoleUI.line();

        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Select Bank" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        int bankChoice;
        try { 
            bankChoice = Integer.parseInt(sc.nextLine()); 
        } catch (Exception e) { 
            bankChoice = -1; 
        }

        switch (bankChoice) {
            case 1: bankName = "State Bank of India"; break;
            case 2: bankName = "HDFC Bank"; break;
            case 3: bankName = "ICICI Bank"; break;
            case 4: bankName = "Axis Bank"; break;
            case 5: bankName = "Kotak Mahindra Bank"; break;
            default:
                ConsoleUI.error("Invalid Bank Selection!");
                ConsoleUI.pause();
                return;
        }

        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Customer ID / Username" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        customerId = sc.nextLine().trim();

        if (customerId.isEmpty()) {
            ConsoleUI.error("Customer ID cannot be empty!");
            ConsoleUI.pause();
            return;
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "🔐 Connecting to " + ConsoleUI.BOLD + bankName + ConsoleUI.RESET));
    }

    @Override
    public String getIdentifier() { 
        return customerId; 
    }

    @Override
    public String getMethodName() { 
        return "Net Banking (" + bankName + ")"; 
    }
}