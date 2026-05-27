package utils;

import ui.ConsoleUI;

public class Animation {

    // ================= LOADING =================
    
    public static void loading(String text) {

        final int barWidth = 36;

        for (int i = 0; i <= barWidth; i++) {

            String filled = ConsoleUI.BRIGHT_GREEN + ConsoleUI.repeat('█', i)            + ConsoleUI.RESET;
            String empty  = ConsoleUI.DIM          + ConsoleUI.repeat('░', barWidth - i) + ConsoleUI.RESET;

            int    pct  = (i * 100) / barWidth;
            String pStr = (pct < 10 ? "  " : pct < 100 ? " " : "") + pct + "%";

            String bar =
                    ConsoleUI.BRIGHT_CYAN + text + "  " + ConsoleUI.RESET +
                    ConsoleUI.BRIGHT_CYAN + "❮" + ConsoleUI.RESET +
                    filled + empty +
                    ConsoleUI.BRIGHT_CYAN + "❯  " + ConsoleUI.RESET +
                    ConsoleUI.BRIGHT_CYAN + pStr + ConsoleUI.RESET;

            System.out.print("\r" + ConsoleUI.center(bar));

            sleep(45);
        }

        System.out.println("\n");
    }

    // ================= METRO ANIMATION =================
    

    public static void metroTrain() {

        vehicleBar(
                "🚇  Booking Metro Ticket",   // label
                "🚇",                          // vehicle emoji
                ConsoleUI.BRIGHT_CYAN,         // fill colour
                ConsoleUI.BRIGHT_CYAN          // frame / text colour
        );
    }

    // ================= BUS ANIMATION =================

    public static void busRide() {

        vehicleBar(
                "🚌  Booking Bus Ticket",
                "🚌",
                ConsoleUI.BRIGHT_YELLOW,
                ConsoleUI.BRIGHT_YELLOW
        );
    }

    // ================= CAB ANIMATION =================

    public static void cabRide() {

        vehicleBar(
                "🚗  Booking Cab Ride",
                "🚗",
                ConsoleUI.BRIGHT_RED,
                ConsoleUI.BRIGHT_RED
        );
    }

    // ─── shared vehicle-bar renderer ────────────────────────────────────────────


    private static void vehicleBar(
            String label,
            String vehicle,
            String fillColor,
            String frameColor
    ) {

        final int BAR = 36;   // bar width in columns  (excludes vehicle cols)

        final int VEHICLE_COLS = 2;

        for (int i = 0; i <= BAR; i++) {

            String filled = fillColor + ConsoleUI.repeat('█', i) + ConsoleUI.RESET;

            int emptyCount = BAR - i;
            String empty  = ConsoleUI.DIM + ConsoleUI.repeat('░', emptyCount) + ConsoleUI.RESET;

            int    pct  = (i * 100) / BAR;
            String pStr = (pct < 10 ? "  " : pct < 100 ? " " : "") + pct + "%";

            String bar =
                    frameColor + label + "  " + ConsoleUI.RESET +
                    frameColor + "❮" + ConsoleUI.RESET +
                    filled +
                    fillColor + vehicle + ConsoleUI.RESET +
                    empty +
                    frameColor + "❯  " + ConsoleUI.RESET +
                    frameColor + pStr + ConsoleUI.RESET;

            System.out.print("\r" + ConsoleUI.center(bar));

            sleep(45);
        }

        System.out.println("\n");
    }

    // ================= HOTEL ANIMATION =================

    public static void hotelLoading() {

        stageAnimation(
                new String[]{
                    "🏨  Checking Rooms",
                    "🛏  Preparing Suite",
                    "✨  Cleaning Room",
                    "🧾  Generating Booking",
                    "✅  Booking Confirmed!"
                },
                ConsoleUI.BRIGHT_PURPLE,
                ConsoleUI.BRIGHT_GREEN
        );
    }

    // ================= PAYMENT ANIMATION =================

    public static void paymentProcessing() {

        stageAnimation(
                new String[]{
                    "💳  Connecting Bank",
                    "🔐  Securing Transaction",
                    "📡  Verifying Payment",
                    "🧾  Generating Receipt",
                    "✅  Payment Successful!"
                },
                ConsoleUI.BRIGHT_GREEN,
                ConsoleUI.BRIGHT_GREEN
        );
    }

    // ─── shared stage renderer ───────────────────────────────────────────────────

    private static void stageAnimation(
            String[] stages,
            String   borderColor,
            String   doneColor
    ) {

        final int PREFIX_COLS = 7;   // "│  ├── "
        final int SUFFIX_COLS = 3;   // "..."
        final int SIDE_PAD    = 4;

        int maxLabel = 0;

        for (String s : stages) {

            int w = ConsoleUI.visualWidth(s);

            if (w > maxLabel) maxLabel = w;
        }

        int boxInner = PREFIX_COLS + maxLabel + SUFFIX_COLS + SIDE_PAD * 2;
        int margin   = (ConsoleUI.WIDTH - boxInner - 2) / 2;

        if (margin < 0) margin = 0;

        String pad = ConsoleUI.repeat(' ', margin);

        System.out.println(
                pad + borderColor +
                "┌" + ConsoleUI.repeat('─', boxInner) + "┐" +
                ConsoleUI.RESET
        );

        for (int i = 0; i < stages.length; i++) {

            boolean last      = (i == stages.length - 1);
            String  connector = last ? "└── " : "├── ";
            String  rowColor  = last ? doneColor : ConsoleUI.BRIGHT_CYAN;
            int     labelVW   = ConsoleUI.visualWidth(stages[i]);
            int     usedCols  = 3 + 4 + labelVW + (last ? 0 : SUFFIX_COLS);
            int     rightPad  = boxInner - usedCols;

            if (rightPad < 0) rightPad = 0;

            System.out.print(
                    pad +
                    borderColor + "│  " + ConsoleUI.RESET +
                    rowColor    + connector + stages[i] + ConsoleUI.RESET
            );

            if (!last) {

                for (int d = 0; d < 3; d++) {
                    sleep(200);
                    System.out.print(ConsoleUI.DIM + "." + ConsoleUI.RESET);
                }

                System.out.print(ConsoleUI.repeat(' ', Math.max(rightPad - 3, 0)));

            } else {

                System.out.print(ConsoleUI.repeat(' ', rightPad));
            }

            System.out.println();
            sleep(350);
        }

        System.out.println(
                pad + doneColor +
                ConsoleUI.repeat('─', boxInner + 2) +
                ConsoleUI.RESET
        );

        System.out.println();
    }

    // ================= BLINKING TITLE =================

    public static void blinkingTitle(String text) {

        int vw = ConsoleUI.visualWidth(text);

        for (int i = 0; i < 3; i++) {

            System.out.print(
                    "\r" +
                    ConsoleUI.center(
                            ConsoleUI.BOLD + ConsoleUI.BRIGHT_CYAN + text + ConsoleUI.RESET
                    )
            );

            sleep(380);

            System.out.print("\r" + ConsoleUI.repeat(' ', ConsoleUI.WIDTH));

            sleep(220);
        }

        System.out.println(
                "\r" +
                ConsoleUI.center(
                        ConsoleUI.BOLD + ConsoleUI.BRIGHT_CYAN + text + ConsoleUI.RESET
                )
        );

        System.out.println(
                ConsoleUI.center(
                        ConsoleUI.DIM + ConsoleUI.BRIGHT_CYAN +
                        ConsoleUI.repeat('─', vw) +
                        ConsoleUI.RESET
                )
        );

        System.out.println();
    }

    // ================= SLEEP =================

    public static void sleep(int ms) {

        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            // ignored
        }
    }
}