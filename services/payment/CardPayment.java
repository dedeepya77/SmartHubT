package services.payment;

import java.time.LocalDate;
import java.util.Scanner;
import ui.ConsoleUI;

public class CardPayment extends PaymentMethod {
    private static final Scanner sc = new Scanner(System.in);
    private String cardType;
    private String cardNumber;

    public CardPayment(int amount) {
        super(amount);
    }

    @Override
    public void collectDetails() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("💳 CARD PAYMENT");
        ConsoleUI.line();

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1]  💳 Credit Card"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2]  💳 Debit Card"));
        System.out.println();
        ConsoleUI.line();

        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Select Card Type" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        int choice;
        try { 
            choice = Integer.parseInt(sc.nextLine()); 
        } catch (Exception e) { 
            choice = -1; 
        }

        switch (choice) {
            case 1: 
                cardType = "Credit Card"; 
                break;
            case 2: 
                cardType = "Debit Card"; 
                break;
            default: 
                ConsoleUI.error("Invalid Card Type!");
                ConsoleUI.pause();
                return;
        }

        while (true) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Card Number (16 digits)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            cardNumber = sc.nextLine().trim().replaceAll("\\s+", "");
            if (cardNumber.length() == 16 && cardNumber.matches("\\d+")) break;
            ConsoleUI.error("Invalid Card Number! Must be exactly 16 digits. Try again.");
        }

        while (true) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Expiry Date (MM/YY)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            String expiry = sc.nextLine().trim();
            if (!expiry.matches("\\d{2}/\\d{2}")) { 
                ConsoleUI.error("Invalid format! Use MM/YY (e.g. 08/27). Try again."); 
                continue; 
            }
            
            int m = Integer.parseInt(expiry.substring(0, 2));
            int y = Integer.parseInt(expiry.substring(3));
            
            if (m < 1 || m > 12) { 
                ConsoleUI.error("Invalid month! Must be 01-12. Try again."); 
                continue; 
            }
            
            LocalDate now = LocalDate.now();
            int nowY = now.getYear() % 100;
            int nowM = now.getMonthValue();
            
            if (y < nowY || (y == nowY && m < nowM)) { 
                ConsoleUI.error("Card has expired! Enter a valid expiry date. Try again."); 
                continue; 
            }
            break;
        }

        while (true) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ CVV (3 digits)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            String cvv = sc.nextLine().trim();
            if (cvv.length() == 3 && cvv.matches("\\d+")) break;
            ConsoleUI.error("Invalid CVV! Must be exactly 3 digits. Try again.");
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "🔐 Verifying " + ConsoleUI.BOLD + cardType + ConsoleUI.RESET + 
            ConsoleUI.BRIGHT_CYAN + " ending in " + ConsoleUI.BOLD + cardNumber.substring(12) + ConsoleUI.RESET));
    }

    @Override
    public String getIdentifier() { 
        return "**** **** **** " + cardNumber.substring(12); 
    }

    @Override
    public String getMethodName() { 
        return cardType; 
    }
}