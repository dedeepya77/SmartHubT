package ui;

import java.util.Scanner;

public class ConsoleUI {

    // ================= ANSI COLORS =================

    public static final String RESET = "\u001B[0m";

    public static final String BLACK  = "\u001B[30m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN   = "\u001B[36m";
    public static final String WHITE  = "\u001B[37m";

    // ================= BRIGHT COLORS =================

    public static final String BRIGHT_RED    = "\u001B[91m";
    public static final String BRIGHT_GREEN  = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE   = "\u001B[94m";
    public static final String BRIGHT_PURPLE = "\u001B[95m";
    public static final String BRIGHT_CYAN   = "\u001B[96m";

    public static final String DIM    = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";

    // ================= STYLES =================

    public static final String BOLD  = "\u001B[1m";
    public static final String BLINK = "\u001B[5m";

    // ================= WIDTH =================

    public static final int WIDTH = 156;

    // ================= CLEAR =================

    public static void clear() {

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // ================= VISUAL WIDTH =================

    public static int visualWidth(String text) {

        String plain = removeAnsi(text);

        int cols = 0;
        int i    = 0;

        while (i < plain.length()) {

            char hi = plain.charAt(i);

            if (Character.isHighSurrogate(hi)
                    && i + 1 < plain.length()
                    && Character.isLowSurrogate(plain.charAt(i + 1))) {

                int cp = Character.toCodePoint(hi, plain.charAt(i + 1));
                cols  += wideCodePoint(cp) ? 2 : 1;
                i     += 2;

            } else {

                cols += wideChar(hi) ? 2 : 1;
                i    += 1;
            }
        }

        return cols;
    }

    private static boolean wideChar(char c) {

        int cp = c;

        return (cp >= 0x1100 && cp <= 0x115F)   // Hangul Jamo korean
            || (cp >= 0x2E80 && cp <= 0x303E)   // CJK Radicals / Kangxi chinese
            || (cp >= 0x3041 && cp <= 0x33FF)   // Hiragana … CJK compat japanese
            || (cp >= 0x3400 && cp <= 0x4DBF)   // CJK ext A mix of the top 3
            || (cp >= 0x4E00 && cp <= 0x9FFF)   // CJK unified chinese
            || (cp >= 0xA000 && cp <= 0xA4CF)   // Yi chinese
            || (cp >= 0xAC00 && cp <= 0xD7AF)   // Hangul syllables  korean
            || (cp >= 0xF900 && cp <= 0xFAFF)   // CJK compat ideographs 
            || (cp >= 0xFE10 && cp <= 0xFE1F)   // Vertical forms ︐, ︑, etc
            || (cp >= 0xFE30 && cp <= 0xFE6F)   // CJK compat forms︰, ︱, etc
            || (cp >= 0xFF01 && cp <= 0xFF60)   // Fullwidth Latin
            || (cp >= 0xFFE0 && cp <= 0xFFE6);  // Fullwidth signs
    }

    private static boolean wideCodePoint(int cp) {

        return (cp >= 0x1F004 && cp <= 0x1F9FF)   // Emoji / misc symbols
            || (cp >= 0x20000 && cp <= 0x2FA1F);  // CJK ext B–F
    }

    // ================= CENTER =================

    public static String center(String text) {

        int vw  = visualWidth(text);
        int pad = (WIDTH - vw) / 2;

        if (pad < 0) pad = 0;

        return repeat(' ', pad) + text;
    }

    // ================= CENTER IN BOX =================

    public static String centerInBox(String text, int innerWidth) {

        int vw    = visualWidth(text);
        int spare = innerWidth - vw;

        if (spare < 0) spare = 0;

        int left  = spare / 2;
        int right = spare - left;

        return repeat(' ', left) + text + repeat(' ', right);
    }

    // ================= REMOVE ANSI =================

    public static String removeAnsi(String text) {

        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    // ================= LINE =================

    public static void line() {

        int side = (WIDTH - 3) / 2;

        System.out.println(
                BRIGHT_CYAN + DIM + repeat('─', side) + RESET +
                BRIGHT_CYAN + " ◆ " + RESET +
                BRIGHT_CYAN + DIM + repeat('─', side) + RESET
        );
    }

    // ================= DOUBLE LINE =================

    public static void doubleLine() {

        System.out.println(
                BRIGHT_BLUE + BOLD +
                "╔" + repeat('═', WIDTH - 2) + "╗" +
                RESET
        );
    }

    // ================= TITLE =================

    public static void title(String text) {

        // inner = columns between  │_  _│  (WIDTH - 2 borders - 2 spaces)
        int inner = WIDTH - 4;
        int vw    = visualWidth(text);
        int spare = inner - vw;

        if (spare < 0) spare = 0;

        int left  = spare / 2;
        int right = spare - left;

        System.out.println();

        System.out.println(
                BRIGHT_CYAN + "┌" + repeat('─', WIDTH - 2) + "┐" + RESET
        );

        System.out.println(
                BRIGHT_CYAN + "│ " + RESET +
                BOLD + BRIGHT_CYAN +
                repeat(' ', left) + text + repeat(' ', right) +
                RESET +
                BRIGHT_CYAN + " │" + RESET
        );

        System.out.println(
                BRIGHT_CYAN + "└" + repeat('─', WIDTH - 2) + "┘" + RESET
        );

        System.out.println();
    }

    // ================= SUCCESS =================

    public static void success(String text) {

        String label = "  ✔  " + text + "  ";
        int    vw    = visualWidth(label);
        int    inner = Math.max(vw, 32);
        int    pad   = (WIDTH - inner - 4) / 2;

        if (pad < 0) pad = 0;

        String margin = repeat(' ', pad);
        String filler = repeat(' ', inner - vw);

        System.out.println();
        System.out.println(margin + BRIGHT_GREEN + "╔" + repeat('═', inner + 2) + "╗" + RESET);
        System.out.println(margin + BRIGHT_GREEN + "║ " + RESET + BOLD + BRIGHT_GREEN + label + filler + RESET + BRIGHT_GREEN + " ║" + RESET);
        System.out.println(margin + BRIGHT_GREEN + "╚" + repeat('═', inner + 2) + "╝" + RESET);
        System.out.println();
    }

    // ================= ERROR =================

    public static void error(String text) {

        String label = "  ✘  " + text + "  ";
        int    vw    = visualWidth(label);
        int    inner = Math.max(vw, 32);
        int    pad   = (WIDTH - inner - 4) / 2;

        if (pad < 0) pad = 0;

        String margin = repeat(' ', pad);
        String filler = repeat(' ', inner - vw);

        System.out.println();
        System.out.println(margin + BRIGHT_RED + "╔" + repeat('═', inner + 2) + "╗" + RESET);
        System.out.println(margin + BRIGHT_RED + "║ " + RESET + BOLD + BRIGHT_RED + label + filler + RESET + BRIGHT_RED + " ║" + RESET);
        System.out.println(margin + BRIGHT_RED + "╚" + repeat('═', inner + 2) + "╝" + RESET);
        System.out.println();
    }

    // ================= INFO =================

    public static void info(String text) {

        String label = "  ➜  " + text + "  ";
        int    vw    = visualWidth(label);
        int    inner = Math.max(vw, 32);
        int    pad   = (WIDTH - inner - 4) / 2;

        if (pad < 0) pad = 0;

        String margin = repeat(' ', pad);
        String filler = repeat(' ', inner - vw);

        System.out.println();
        System.out.println(margin + BRIGHT_CYAN + "┌" + repeat('─', inner + 2) + "┐" + RESET);
        System.out.println(margin + BRIGHT_CYAN + "│ " + RESET + BRIGHT_CYAN + label + filler + RESET + BRIGHT_CYAN + " │" + RESET);
        System.out.println(margin + BRIGHT_CYAN + "└" + repeat('─', inner + 2) + "┘" + RESET);
        System.out.println();
    }

    // ================= INPUT =================

    public static void input(String text) {

        System.out.print(
                "\n" +
                BRIGHT_YELLOW + BOLD + "  ┌─ " + text + RESET +
                "\n" +
                BRIGHT_YELLOW + BOLD + "  └──➤ " + RESET
        );
    }

    // ================= PAUSE =================

    public static void pause() {

        System.out.println();
        System.out.println(
                center(
                        DIM + BRIGHT_CYAN + "· · ·  press " + RESET +
                        BRIGHT_CYAN + BOLD + "Enter" + RESET +
                        DIM + BRIGHT_CYAN + " to continue  · · ·" + RESET
                )
        );
        new Scanner(System.in).nextLine();
    }

    // ================= REPEAT =================

    public static String repeat(char ch, int count) {

        if (count <= 0) return "";

        StringBuilder sb = new StringBuilder(count);

        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }

        return sb.toString();
    }
}