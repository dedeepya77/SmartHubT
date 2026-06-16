package services;
import utils.Session;

import java.util.Scanner;
import ui.ConsoleUI;
import utils.Animation;

public class TransportService {

    // ================= SHARED SCANNER =================

    private static Scanner sc = new Scanner(System.in);

    // ================= METRO LINES =================

    static String[] redLine = {
        "Miyapur", "JNTU College", "KPHB Colony", "Kukatpally", "Balanagar",
        "Moosapet", "Bharat Nagar", "Erragadda", "ESI Hospital", "SR Nagar",
        "Ameerpet", "Punjagutta", "Irrum Manzil", "Khairatabad", "Lakdikapul",
        "Assembly", "Nampally", "Gandhi Bhavan", "Osmania Medical College", "MG Bus Station"
    };

    static String[] blueLine = {
        "Raidurg", "Hitech City", "Durgam Cheruvu", "Madhapur", "Peddamma Temple",
        "Jubilee Hills Check Post", "Road No.5 Jubilee Hills", "Yusufguda",
        "Madhura Nagar", "Ameerpet", "Begumpet", "Prakash Nagar", "Rasoolpura",
        "Paradise", "Parade Ground", "Secunderabad East", "Mettuguda", "Tarnaka",
        "Habsiguda", "NGRI", "Stadium", "Uppal", "Nagole"
    };

    static String[] greenLine = {
        "JBS Parade Ground", "Secunderabad West", "Gandhi Hospital", "Musheerabad",
        "RTC X Roads", "Chikkadpally", "Narayanguda", "Sultan Bazar", "MG Bus Station"
    };

    // ================= AREAS =================

    static String[] areas = {
        "Miyapur", "JNTU", "KPHB", "Kukatpally", "Ameerpet", "Punjagutta",
        "Lakdikapul", "Nampally", "Secunderabad", "Paradise", "Uppal", "Nagole",
        "Hitech City", "Raidurg", "Madhapur", "Gachibowli", "Banjara Hills",
        "Jubilee Hills", "Begumpet", "Mehdipatnam"
    };

    // ================= DISTANCE MATRIX =================

    static int[][] cabDistance = {
        {0,3,5,7,12,14,16,18,20,21,28,30,15,18,14,17,16,15,13,19},
        {3,0,2,4,9,11,13,15,17,18,25,27,12,15,11,14,13,12,10,16},
        {5,2,0,2,7,9,11,13,15,16,23,25,10,13,9,12,11,10,8,14},
        {7,4,2,0,5,7,9,11,13,14,21,23,8,11,7,10,9,8,6,12},
        {12,9,7,5,0,2,4,6,8,9,16,18,6,9,5,8,4,3,2,7},
        {14,11,9,7,2,0,2,4,10,11,18,20,8,11,7,10,5,4,3,5},
        {16,13,11,9,4,2,0,2,12,13,20,22,10,13,9,12,7,6,5,4},
        {18,15,13,11,6,4,2,0,14,15,22,24,12,15,11,14,9,8,7,5},
        {20,17,15,13,8,10,12,14,0,2,8,10,14,17,13,16,12,13,6,15},
        {21,18,16,14,9,11,13,15,2,0,9,11,13,16,12,15,11,12,5,14},
        {28,25,23,21,16,18,20,22,8,9,0,3,19,22,18,21,17,18,11,20},
        {30,27,25,23,18,20,22,24,10,11,3,0,21,24,20,23,19,20,13,22},
        {15,12,10,8,6,8,10,12,14,13,19,21,0,3,2,5,7,6,8,10},
        {18,15,13,11,9,11,13,15,17,16,22,24,3,0,4,3,8,7,10,12},
        {14,11,9,7,5,7,9,11,13,12,18,20,2,4,0,4,5,4,7,9},
        {17,14,12,10,8,10,12,14,16,15,21,23,5,3,4,0,6,5,8,11},
        {16,13,11,9,4,5,7,9,12,11,17,19,7,8,5,6,0,2,3,6},
        {15,12,10,8,3,4,6,8,13,12,18,20,6,7,4,5,2,0,4,7},
        {13,10,8,6,2,3,5,7,6,5,11,13,8,10,7,8,3,4,0,9},
        {19,16,14,12,7,5,4,5,15,14,20,22,10,12,9,11,6,7,9,0}
    };

    // ================= MENU =================

    public static void menu() {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🚇 TRANSPORT TERMINAL");
            ConsoleUI.line();

            System.out.println();
            System.out.println(ConsoleUI.center("[1] 🚇 Metro Booking"));
            System.out.println();
            System.out.println(ConsoleUI.center("[2] 🚌 Bus Booking"));
            System.out.println();
            System.out.println(ConsoleUI.center("[3] 🚕 Cab Booking"));
            System.out.println();
            System.out.println(ConsoleUI.center("[0] Back"));
            System.out.println();
            ConsoleUI.line();

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Enter Choice" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );

            int choice = getInt();

            switch (choice) {
                case 1: metroBooking(); break;
                case 2: busBooking();   break;
                case 3: cabBooking();   break;
                case 0: return;
                default:
                    ConsoleUI.error("Invalid Choice!");
                    ConsoleUI.pause();
            }
        }
    }

    // ================= METRO BOOKING =================

    public static void metroBooking() {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🚇 HYDERABAD METRO");
        ConsoleUI.line();

        System.out.println();

        System.out.printf(
            "%-40s %-40s %-40s\n",
            ConsoleUI.BLUE  + "🔵 BLUE LINE"  + ConsoleUI.RESET,
            ConsoleUI.RED   + "🔴 RED LINE"   + ConsoleUI.RESET,
            ConsoleUI.GREEN + "🟢 GREEN LINE" + ConsoleUI.RESET
        );

        System.out.println();

        int max = Math.max(redLine.length, Math.max(blueLine.length, greenLine.length));

        for (int i = 0; i < max; i++) {
            String r = i < redLine.length   ? ConsoleUI.RED   + "R" + (i+1) + "  " + redLine[i]   + ConsoleUI.RESET : "";
            String b = i < blueLine.length  ? ConsoleUI.BLUE  + "B" + (i+1) + "  " + blueLine[i]  + ConsoleUI.RESET : "";
            String g = i < greenLine.length ? ConsoleUI.GREEN + "G" + (i+1) + "  " + greenLine[i] + ConsoleUI.RESET : "";
            System.out.printf("%-40s %-40s %-40s\n", b, r, g);
        }

        System.out.println();
        ConsoleUI.line();

        // ── Source station ────────────────────────────────────────────────────
        System.out.print(
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  ┌─ Source Station Code (e.g., R10, B9, G8)" + ConsoleUI.RESET +
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  └──➤ " + ConsoleUI.RESET
        );
        String fromCode = sc.nextLine().trim().toUpperCase();
        Session.keepAlive();

        // ── Destination station ───────────────────────────────────────────────
        System.out.print(
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  ┌─ Destination Station Code" + ConsoleUI.RESET +
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  └──➤ " + ConsoleUI.RESET
        );
        String toCode = sc.nextLine().trim().toUpperCase();
        Session.keepAlive();

        // ── Same station check ────────────────────────────────────────────────
        //
        //  Catches two scenarios:
        //  1. Exact same code entered twice        (e.g. R11 and R11)
        //  2. Different codes, same station name   (e.g. R11 = B10 = Ameerpet)

        String fromName = getStationName(fromCode);
        String toName   = getStationName(toCode);

        if (fromCode.equals(toCode) || (fromName != null && fromName.equals(toName))) {
            ConsoleUI.error("Oops! Your destination can't be the same as your source station. Please choose a different destination!");
            ConsoleUI.pause();
            return;
        }

        // ── Validate codes ────────────────────────────────────────────────────
        if (fromName == null || toName == null) {
            ConsoleUI.error("Invalid Station Code! Please use formats like R10, B9, G8.");
            ConsoleUI.pause();
            return;
        }

        // ── Passengers ────────────────────────────────────────────────────────
        System.out.print(
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  ┌─ Passengers" + ConsoleUI.RESET +
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  └──➤ " + ConsoleUI.RESET
        );
        int passengers = getInt();

        if (passengers <= 0) {
            ConsoleUI.error("Invalid Passenger Count!");
            ConsoleUI.pause();
            return;
        }

        int fromLine  = getLine(fromCode);
        int toLine    = getLine(toCode);
        int fromIndex = extractIndex(fromCode);
        int toIndex   = extractIndex(toCode);
        int stops;
        String interchange = "";

        // ── Same line ─────────────────────────────────────────────────────────
        if (fromLine == toLine) {
            stops = Math.abs(fromIndex - toIndex);
        }
        // ── Red <-> Blue ──────────────────────────────────────────────────────
        else if ((fromLine == 1 && toLine == 2) || (fromLine == 2 && toLine == 1)) {
            int ameerpetRed  = 10;
            int ameerpetBlue = 9;
            stops = (fromLine == 1)
                ? Math.abs(fromIndex - ameerpetRed)  + Math.abs(toIndex - ameerpetBlue)
                : Math.abs(fromIndex - ameerpetBlue) + Math.abs(toIndex - ameerpetRed);
            interchange = "🔄 Interchange at Ameerpet";
        }
        // ── Red <-> Green ─────────────────────────────────────────────────────
        else if ((fromLine == 1 && toLine == 3) || (fromLine == 3 && toLine == 1)) {
            int mgbsRed   = 19;
            int mgbsGreen = 8;
            stops = (fromLine == 1)
                ? Math.abs(fromIndex - mgbsRed)   + Math.abs(toIndex - mgbsGreen)
                : Math.abs(fromIndex - mgbsGreen) + Math.abs(toIndex - mgbsRed);
            interchange = "🔄 Interchange at MG Bus Station";
        }
        // ── Blue <-> Green ────────────────────────────────────────────────────
        else {
            int paradeBlue  = 14;
            int paradeGreen = 0;
            stops = (fromLine == 2)
                ? Math.abs(fromIndex - paradeBlue)  + Math.abs(toIndex - paradeGreen)
                : Math.abs(fromIndex - paradeGreen) + Math.abs(toIndex - paradeBlue);
            interchange = "🔄 Interchange at JBS Parade Ground";
        }

        int farePerPerson = calculateMetroFare(stops);
        int total         = farePerPerson * passengers;

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🚇 METRO TICKET SUMMARY");
        ConsoleUI.line();

        System.out.println();
        System.out.println(ConsoleUI.center("🚉 From : " + fromName));
        System.out.println();
        System.out.println(ConsoleUI.center("🏁 To   : " + toName));
        System.out.println();

        if (!interchange.isEmpty()) {
            System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + interchange + ConsoleUI.RESET));
            System.out.println();
        }

        System.out.println(ConsoleUI.center("🚉 Stops : " + stops));
        System.out.println();
        System.out.println(ConsoleUI.center("👥 Passengers : " + passengers));
        System.out.println();
        System.out.println(ConsoleUI.center("💰 Fare Per Person : ₹" + farePerPerson));
        System.out.println();
        System.out.println(ConsoleUI.center("✅ Total Fare : ₹" + total));
        System.out.println();

        ConsoleUI.line();

        System.out.print(
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  ┌─ Confirm Booking? (yes/no)" + ConsoleUI.RESET +
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  └──➤ " + ConsoleUI.RESET
        );
        String confirm = sc.nextLine();
        Session.keepAlive();

        if (confirm.equalsIgnoreCase("yes")) {

            Animation.metroTrain();

            if (PaymentService.processPayment(
                    "Metro – " + fromName + " -> " + toName + " (" + passengers + " pax)", total)) {

                PaymentService.addBooking(
                    "Metro",
                    fromName + " to " + toName + " - " + passengers + " passenger(s)",
                    total
                );
                ConsoleUI.success("Metro Ticket Booked & Payment Done!");

            } else {
                ConsoleUI.error("Booking Cancelled — No payment made.");
                ConsoleUI.pause();
            }

        } else {
            ConsoleUI.error("Booking Cancelled!");
            ConsoleUI.pause();
        }
    }

    // ================= BUS BOOKING =================

    public static void busBooking() {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🚌 BUS BOOKING");
            ConsoleUI.line();

            showAreas();

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Select Boarding Point" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );
            int from = getInt() - 1;

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Select Destination" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );
            int to = getInt() - 1;

            // ── Same location check ───────────────────────────────────────────
            if (validArea(from) && validArea(to) && from == to) {
                ConsoleUI.error("Oops! Your destination can't be the same as your boarding point. Please pick a different destination!");
                ConsoleUI.pause();
                continue;
            }

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Passengers" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );
            int passengers = getInt();

            if (!validArea(from) || !validArea(to) || passengers <= 0) {
                ConsoleUI.error("Invalid Input! Please enter valid area numbers and passenger count.");
                ConsoleUI.pause();
                continue;
            }

            int farePerPerson = calculateBusFare(cabDistance[from][to]);
            showSummary("Bus", areas[from], areas[to], cabDistance[from][to], passengers, farePerPerson * passengers);
            return;
        }
    }

    // ================= CAB BOOKING =================

    public static void cabBooking() {

        while (true) {

            ConsoleUI.clear();
            ConsoleUI.doubleLine();
            ConsoleUI.title("🚕 CAB BOOKING");
            ConsoleUI.line();

            showAreas();

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Select Pickup Location" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );
            int from = getInt() - 1;

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Select Drop Location" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );
            int to = getInt() - 1;

            // ── Same location check ───────────────────────────────────────────
            if (validArea(from) && validArea(to) && from == to) {
                ConsoleUI.error("Oops! Your drop location can't be the same as your pickup point. Please pick a different destination!");
                ConsoleUI.pause();
                continue;
            }

            System.out.print(
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Passengers" + ConsoleUI.RESET +
                "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET
            );
            int passengers = getInt();

            if (!validArea(from) || !validArea(to) || passengers <= 0) {
                ConsoleUI.error("Invalid Input! Please enter valid area numbers and passenger count.");
                ConsoleUI.pause();
                continue;
            }

            int km   = cabDistance[from][to];
            int fare = 80 + (km * 14) + (passengers > 4 ? 100 : 0);
            showSummary("Cab", areas[from], areas[to], km, passengers, fare);
            return;
        }
    }

    // ================= SUMMARY =================

    public static void showSummary(String type, String from, String to, int distance, int people, int total) {

        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🎫 BOOKING SUMMARY");
        ConsoleUI.line();

        System.out.println();
        System.out.println(ConsoleUI.center("📍 From : " + from));
        System.out.println();
        System.out.println(ConsoleUI.center("🏁 To : " + to));
        System.out.println();
        System.out.println(ConsoleUI.center("🛣 Distance : " + distance + " KM"));
        System.out.println();
        System.out.println(ConsoleUI.center("👥 People : " + people));
        System.out.println();
        System.out.println(ConsoleUI.center("💰 Total Fare : ₹" + total));
        System.out.println();

        ConsoleUI.line();

        System.out.print(
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  ┌─ Confirm Booking? (yes/no)" + ConsoleUI.RESET +
            "\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
            "  └──➤ " + ConsoleUI.RESET
        );
        String confirm = sc.nextLine();
        Session.keepAlive();

        if (confirm.equalsIgnoreCase("yes")) {

            if (type.equals("Cab"))
                Animation.cabRide();
            else if (type.equals("Bus"))
                Animation.busRide();

            if (PaymentService.processPayment(
                    type + " – " + from + " -> " + to + " (" + people + " pax)", total)) {

                PaymentService.addBooking(
                    type,
                    from + " to " + to + " - " + people + " people",
                    total
                );
                ConsoleUI.success(type + " Booking Confirmed & Payment Done!");

            } else {
                ConsoleUI.error("Booking Cancelled — No payment made.");
                ConsoleUI.pause();
            }

        } else {
            ConsoleUI.error("Booking Cancelled!");
            ConsoleUI.pause();
        }
    }

    // ================= SHOW AREAS =================

    public static void showAreas() {
        System.out.println();
        for (int i = 0; i < areas.length; i++) {
            System.out.println(ConsoleUI.center("[" + (i + 1) + "] " + areas[i]));
        }
        System.out.println();
    }

    // ================= HELPER METHODS =================

    public static String getStationName(String code) {
        code = code.toUpperCase();
        try {
            char line = code.charAt(0);
            int  num  = Integer.parseInt(code.substring(1));
            if (line == 'R' && num >= 1 && num <= redLine.length)   
                return redLine[num - 1];
            if (line == 'B' && num >= 1 && num <= blueLine.length)  
                return blueLine[num - 1];
            if (line == 'G' && num >= 1 && num <= greenLine.length) 
                return greenLine[num - 1];
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static int getLine(String code) {
        code = code.toUpperCase();
        if (code.startsWith("R")) 
            return 1;
        if (code.startsWith("B")) 
            return 2;
        if (code.startsWith("G")) 
            return 3;
        return -1;
    }

    public static int extractIndex(String code) {
        try {
            return Integer.parseInt(code.substring(1)) - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int calculateMetroFare(int stops) {
        if (stops <= 3)  return 20;
        if (stops <= 6)  return 35;
        if (stops <= 10) return 50;
        return 65;
    }

    public static int calculateBusFare(int km) {
        if (km <= 0) return 35;
        return 35 + (km * 5);
    }

    public static boolean validArea(int index) {
        return index >= 0 && index < areas.length;
    }

    // ================= LOCAL HELPERS =================

    private static int getInt() {
        Session.keepAlive();
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}