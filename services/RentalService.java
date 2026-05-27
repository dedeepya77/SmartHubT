package services;

import ui.ConsoleUI;
import utils.Animation;

public class RentalService {
    public static void menu() {
        while (true) {
            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🏍 VEHICLE RENTALS");
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1] 🏍 Bike Rental"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2] 🚗 Car Rental"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Back"));
            System.out.println();
            ConsoleUI.line();
            
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Enter Choice" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            int choice = getInt();
            
            switch (choice) { 
                case 1: bikeRental(); break; 
                case 2: carRental(); break; 
                case 0: return; 
                default: showError("Invalid Choice!"); pause(); 
            }
        }
    }

    public static void bikeRental() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🏍 BIKE RENTAL");
        ConsoleUI.line();
        System.out.println();
        System.out.println(ConsoleUI.center("1. 🛵 Scooty  - ₹80/hour"));
        System.out.println();
        System.out.println(ConsoleUI.center("2. 🏍 Sports Bike - ₹150/hour"));
        System.out.println();
        ConsoleUI.line();
        
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Select Vehicle" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        int vehicle = getInt();
        
        String vehicleName; 
        int rate;
        switch (vehicle) { 
            case 1: vehicleName = "🛵 Scooty"; rate = 80; break; 
            case 2: vehicleName = "🏍 Sports Bike"; rate = 150; break; 
            default: showError("Invalid Vehicle!"); pause(); return; 
        }
        
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Enter Hours (1-72 hours)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        int hours = getInt();
        
        if (hours <= 0 || hours > 72) { 
            showError("Hours must be between 1 and 72!"); 
            pause(); 
            return; 
        }
        showRentalSummary(vehicleName, hours, rate * hours, "Bike Rental");
    }

    public static void carRental() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🚗 CAR RENTAL");
        ConsoleUI.line();
        System.out.println();
        System.out.println(ConsoleUI.center("1. 🚘 Hatchback - ₹500/hour"));
        System.out.println();
        System.out.println(ConsoleUI.center("2. 🚙 SUV - ₹900/hour"));
        System.out.println();
        ConsoleUI.line();
        
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Select Vehicle" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        int vehicle = getInt();
        
        String vehicleName; 
        int rate;
        switch (vehicle) { 
            case 1: vehicleName = "🚘 Hatchback"; rate = 500; break; 
            case 2: vehicleName = "🚙 SUV"; rate = 900; break; 
            default: showError("Invalid Vehicle!"); pause(); return; 
        }
        
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Enter Hours (1-72 hours)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        int hours = getInt();
        
        if (hours <= 0 || hours > 72) { 
            showError("Hours must be between 1 and 72!"); 
            pause(); 
            return; 
        }
        showRentalSummary(vehicleName, hours, rate * hours, "Car Rental");
    }

    public static void showRentalSummary(String vehicle, int hours, int total, String type) {
        int gst = (int) Math.round(total * 0.18), finalAmount = total + gst;
        
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🧾 RENTAL SUMMARY");
        ConsoleUI.line();
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "🚘 Vehicle : " + vehicle));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "⏰ Duration : " + hours + " hour(s)"));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "💰 Rental Cost : ₹" + total));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "🧾 GST (18%) : ₹" + gst));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "✅ Total Amount : ₹" + finalAmount));
        System.out.println();
        ConsoleUI.line();
        
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Confirm Rental? (yes/no)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        String confirm = new java.util.Scanner(System.in).nextLine();
        
        if (confirm.equalsIgnoreCase("yes")) {
            Animation.loading("Processing Rental");
            if (PaymentService.processPayment(type + " – " + vehicle, finalAmount)) {
                PaymentService.addBooking(type, vehicle + " for " + hours + " hours", finalAmount);
                showSuccess("Rental Confirmed & Payment Done!");
            } else { 
                showError("Rental Cancelled — No payment made."); 
                pause(); 
            }
        } else { 
            showError("Rental Cancelled!"); 
            pause(); 
        }
    }

    private static int getInt() { 
        try { 
            return Integer.parseInt(new java.util.Scanner(System.in).nextLine()); 
        } 
        catch (Exception e) { 
            return -1; 
        } 
    }
    
    private static void pause() { 
        ConsoleUI.pause(); 
    }
    
    private static void showError(String msg) { 
        ConsoleUI.error(msg); 
    }
    
    private static void showSuccess(String msg) { 
        ConsoleUI.success(msg); 
    }
}