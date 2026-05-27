package services;

import ui.ConsoleUI;
import utils.Animation;

public class StayService {
    public static void menu() {
        while (true) {
            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🏨 HOTEL & STAY");
            ConsoleUI.line();
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[1] 🛏 Standard Room - ₹2,500/night"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[2] 🌟 Deluxe Room - ₹4,500/night"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "[3] 👑 Luxury Suite - ₹8,000/night"));
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + "[0] Back"));
            System.out.println();
            ConsoleUI.line();
            
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Select Room" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            int choice = getInt();
            
            switch (choice) {
                case 1: roomBooking("🛏 Standard Room", 2500); break;
                case 2: roomBooking("🌟 Deluxe Room", 4500); break;
                case 3: roomBooking("👑 Luxury Suite", 8000); break;
                case 0: return;
                default: showError("Invalid Choice!"); pause();
            }
        }
    }

    public static void roomBooking(String roomType, int roomPrice) {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title(roomType);
        ConsoleUI.line();
        int members;
        while (true) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Number of Members (max 5)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            members = getInt();
            if (members >= 1 && members <= 5) break;
            showError("Members must be between 1 and 5!");
        }
        
        String[] memberNames = new String[members];
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "── Enter Member Details ──" + ConsoleUI.RESET));
        System.out.println();
        java.util.Scanner sc = new java.util.Scanner(System.in);
        
        for (int i = 0; i < members; i++) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Member " + (i + 1) + " Name" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            memberNames[i] = sc.nextLine().trim();
        }
        
        int days;
        while (true) {
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Number of Days" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
            days = getInt();
            if (days > 0) break;
            showError("Enter valid number of days!");
        }
        
        int roomCost = roomPrice * days, gst = (roomCost * 18) / 100, total = roomCost + gst;
        
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🏨 BOOKING SUMMARY");
        ConsoleUI.line();
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "🛏 Room : " + roomType + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "👥 Members : " + members + ConsoleUI.RESET));
        System.out.println();
        
        for (int i = 0; i < members; i++) { 
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "   👤 " + (i+1) + ". " + memberNames[i] + ConsoleUI.RESET)); 
            System.out.println(); 
        }
        
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_CYAN + "📅 Days : " + days + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "💰 Room Cost : ₹" + roomCost + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "🧾 GST (18%) : ₹" + gst + ConsoleUI.RESET));
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_GREEN + "✅ Total Amount : ₹" + total + ConsoleUI.RESET));
        System.out.println();
        ConsoleUI.line();
        
        System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  ┌─ Confirm Booking? (yes/no)" + ConsoleUI.RESET + "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + "  └──➤ " + ConsoleUI.RESET);
        String confirm = sc.nextLine();
        
        if (confirm.equalsIgnoreCase("yes")) {
            Animation.hotelLoading();
            StringBuilder names = new StringBuilder();
            for (int i = 0; i < members; i++) { 
                names.append(memberNames[i]); 
                if (i < members-1) names.append(", "); 
            }
            if (PaymentService.processPayment("Hotel – " + roomType + " (" + days + " day(s))", total)) {
                PaymentService.addBooking("Hotel", roomType + " for " + days + " days - " + members + " member(s): " + names, total);
                showSuccess("Room Booked & Payment Done!");
            } else { 
                showError("Booking Cancelled — No payment made."); 
                pause(); 
            }
        } else { 
            showError("Booking Cancelled!"); 
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