package ui;

public class AsciiArt {

    // ================= SPLASH SCREEN =================

    public static void splash() {

        ConsoleUI.clear();

        String c = ConsoleUI.BRIGHT_CYAN;
        String p = ConsoleUI.BRIGHT_PURPLE;
        String y = ConsoleUI.BRIGHT_YELLOW;
        String g = ConsoleUI.BRIGHT_GREEN;
        String b = ConsoleUI.BRIGHT_BLUE;
        String d = ConsoleUI.DIM;
        String r = ConsoleUI.RESET;
        String B = ConsoleUI.BOLD;

        final int IW = ConsoleUI.WIDTH - 2;

        System.out.println();

        System.out.println(b + B + "╔" + ConsoleUI.repeat('═', ConsoleUI.WIDTH - 2) + "╗" + r);

        frameBlank(b, r, IW);

        String icons = p + B + "🏙   🚇   🚌   🚕   🏨   ✈   🏖   🎫   🛒   🌆" + r;
        System.out.println(b + "║" + r + ConsoleUI.centerInBox(icons, IW) + b + "║" + r);

        frameBlank(b, r, IW);

        System.out.println(b + "╠" + ConsoleUI.repeat('─', ConsoleUI.WIDTH - 2) + "╣" + r);

        frameBlank(b, r, IW);

        String[] logo = {
            "███████╗███╗   ███╗ █████╗ ██████╗ ████████╗    ██╗  ██╗██╗   ██╗██████╗ ",
            "██╔════╝████╗ ████║██╔══██╗██╔══██╗╚══██╔══╝    ██║  ██║██║   ██║██╔══██╗",
            "███████╗██╔████╔██║███████║██████╔╝   ██║       ███████║██║   ██║██████╔╝",
            "╚════██║██║╚██╔╝██║██╔══██║██╔══██╗   ██║       ██╔══██║██║   ██║██╔══██╗",
            "███████║██║ ╚═╝ ██║██║  ██║██║  ██║   ██║       ██║  ██║╚██████╔╝██████╔╝",
            "╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝       ╚═╝  ╚═╝ ╚═════╝ ╚═════╝ "
        };

        for (String line : logo) {

            String styled = c + B + line + r;
            System.out.println(b + "║" + r + ConsoleUI.centerInBox(styled, IW) + b + "║" + r);
            sleep(75);
        }

        frameBlank(b, r, IW);

        String tagline = y + B + "✦  TRAVEL   ✦  STAY   ✦  RENT   ✦  EXPLORE  ✦" + r;
        System.out.println(b + "║" + r + ConsoleUI.centerInBox(tagline, IW) + b + "║" + r);

        frameBlank(b, r, IW);

        String subtitle = d + g + "Your All-In-One Smart Travel Companion" + r;
        System.out.println(b + "║" + r + ConsoleUI.centerInBox(subtitle, IW) + b + "║" + r);

        frameBlank(b, r, IW);

        //logo
        System.out.println(b + "╠" + ConsoleUI.repeat('─', ConsoleUI.WIDTH - 2) + "╣" + r);

        frameBlank(b, r, IW);

        loading();

        frameBlank(b, r, IW);

        // bottom border 
        System.out.println(b + B + "╚" + ConsoleUI.repeat('═', ConsoleUI.WIDTH - 2) + "╝" + r);

        sleep(800);
    }

    private static void frameBlank(String b, String r, int innerWidth) {

        System.out.println(b + "║" + r + ConsoleUI.repeat(' ', innerWidth) + b + "║" + r);
    }

    // ================= LOADING BAR =================

    public static void loading() {

        String c = ConsoleUI.BRIGHT_CYAN;
        String g = ConsoleUI.BRIGHT_GREEN;
        String b = ConsoleUI.BRIGHT_BLUE;
        String d = ConsoleUI.DIM;
        String r = ConsoleUI.RESET;

        final int IW       = ConsoleUI.WIDTH - 2;   // frame inner width
        final int barWidth = 40;

        for (int i = 0; i <= barWidth; i++) {

            String filled = g  + ConsoleUI.repeat('█', i)             + r;
            String empty  = d  + ConsoleUI.repeat('░', barWidth - i)  + r;

            int percent = (i * 100) / barWidth;

            String pStr = (percent < 10 ? "  " : percent < 100 ? " " : "") + percent + "%";

            String bar =
                    c + "  Loading SmartHub  " + r +
                    c + "❮" + r +
                    filled + empty +
                    c + "❯  " + r +
                    c + pStr + r;

            System.out.print(
                    "\r" +
                    b + "║" + r +
                    ConsoleUI.centerInBox(bar, IW) +
                    b + "║" + r
            );

            sleep(55);
        }

        System.out.println();
    }

    // ================= SLEEP =================

    public static void sleep(int ms) {

        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }
}