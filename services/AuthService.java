package services;

import java.io.*;
import java.util.ArrayList;

import models.User;
import ui.ConsoleUI;
import utils.Animation;

public class AuthService {
    private static ArrayList<User> users = new ArrayList<User>();
    private static final String FILE_NAME = "users.txt";
    private static String loggedInUser = null;

    // ── Session token ─────────────────────────────────────────────────────────
    // A random hex token is generated per session instead of just storing the
    // username string, which is a closer simulation of real auth systems.
    private static String sessionToken = null;

    static {
        loadUsers();
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static String getSessionToken() {
        return sessionToken;
    }

    public static void logout() {
        loggedInUser = null;
        sessionToken = null;
    }

    // =========================================================================
    //  LOGIN  –  3 attempts max
    // =========================================================================
    public static boolean login() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🔐 LOGIN");
        ConsoleUI.line();

        final int MAX_ATTEMPTS = 3;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            if (attempt > 1) {
                System.out.println();
                System.out.println(ConsoleUI.center(ConsoleUI.DIM +
                    "Attempt " + attempt + " of " + MAX_ATTEMPTS + ConsoleUI.RESET));
            }

            // ── Username: case-insensitive match ──────────────────────────────
            String username = getInput("Username");
            String password = getInput("Password");

            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)
                        && user.getPassword().equals(password)) {
                    Animation.loading("Authenticating");
                    loggedInUser = user.getUsername();   // store canonical casing
                    sessionToken = generateToken();
                    showSuccess("Login Successful!");
                    pause();
                    return true;
                }
            }

            showError("Invalid Username or Password!"
                + (attempt < MAX_ATTEMPTS ? " (" + (MAX_ATTEMPTS - attempt) + " attempt(s) left)" : ""));
        }

        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + ConsoleUI.BOLD +
            "Too many failed attempts. Please try again later." + ConsoleUI.RESET));
        System.out.println();
        pause();
        return false;
    }

    // =========================================================================
    //  SIGNUP
    // =========================================================================
    public static void signup() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("📝 CREATE ACCOUNT");
        ConsoleUI.line();

        // ── Username ──────────────────────────────────────────────────────────
        String username;
        while (true) {
            username = getInput("Create Username");
            if (username.length() < 4) {
                showError("Username must contain at least 4 characters!");
                continue;
            }
            if (usernameExists(username)) {
                showError("Username already exists!");
                continue;
            }
            break;
        }

        // ── Password  –  3 confirm-attempts max ───────────────────────────────
        String password = null;
        final int MAX_CONFIRM = 3;

        outer:
        while (true) {
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.DIM +
                "Password must be 8+ chars with uppercase, lowercase, digit & special character"
                + ConsoleUI.RESET));
            System.out.println();

            password = getInput("Create Password");
            String passwordError = validatePassword(password);
            if (passwordError != null) {
                showError(passwordError);
                continue;
            }

            // Up to 3 chances to confirm the password correctly
            for (int attempt = 1; attempt <= MAX_CONFIRM; attempt++) {
                String confirmPassword = getInput("Confirm Password (attempt " + attempt + "/" + MAX_CONFIRM + ")");
                if (confirmPassword.equals(password)) {
                    break outer;                // confirmed — leave both loops
                }
                showError("Passwords do not match!"
                    + (attempt < MAX_CONFIRM ? " (" + (MAX_CONFIRM - attempt) + " attempt(s) left)" : ""));
                if (attempt == MAX_CONFIRM) {
                    System.out.println();
                    System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + ConsoleUI.BOLD +
                        "Too many failed attempts. Returning to menu." + ConsoleUI.RESET));
                    System.out.println();
                    pause();
                    return;
                }
            }
        }

        // ── Mobile ────────────────────────────────────────────────────────────
        String mobile;
        while (true) {
            mobile = getInput("Mobile Number (10 digits, starts with 6/7/8/9)");
            if (!isValidMobile(mobile)) {
                showError("Invalid mobile number!");
                continue;
            }
            if (mobileExists(mobile)) {
                showError("Mobile number already registered!");
                continue;
            }
            break;
        }

        // ── OTP  –  3 total attempts (counting across resends) ────────────────
        int remainingAttempts = 3;
        int otp = generateOtp();
        showOtp(otp);

        while (remainingAttempts > 0) {
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.DIM +
                "Attempts remaining: " + remainingAttempts + ConsoleUI.RESET));
            System.out.println();

            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Enter OTP" + ConsoleUI.RESET + "\n" +
                ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET);
            int enteredOtp = getInt();
            remainingAttempts--;

            if (enteredOtp == otp) {
                // Success
                Animation.loading("Creating Account");
                users.add(new User(username, password, mobile));
                saveUsers();
                showSuccess("Account Created Successfully!");
                pause();
                return;
            }

            // Wrong OTP
            if (remainingAttempts == 0) {
                break;
            }

            showError("Incorrect OTP! (" + remainingAttempts + " attempt(s) remaining)");
            System.out.println();
            System.out.print("\n" + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  ┌─ Resend OTP? (yes/no)" + ConsoleUI.RESET + "\n" +
                ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD +
                "  └──➤ " + ConsoleUI.RESET);
            String resend = new java.util.Scanner(System.in).nextLine().trim();

            if (resend.equalsIgnoreCase("yes")) {
                otp = generateOtp();
                showOtp(otp);
            }
            // If they say no (or anything else), they still use a remaining attempt on the same OTP
        }

        // All 3 attempts exhausted without success
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_RED + ConsoleUI.BOLD +
            "Account creation failed. Exiting..." + ConsoleUI.RESET));
        System.out.println();
        ConsoleUI.doubleLine();
        System.exit(0);
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private static int generateOtp() {
        return 1000 + (int) (Math.random() * 9000);
    }

    private static void showOtp(int otp) {
        System.out.println();
        showInfo("OTP Sent Successfully!");
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "🔑 OTP : " + otp));
        System.out.println();
    }

    /** Generates a simple 32-char hex session token. */
    private static String generateToken() {
        StringBuilder sb = new StringBuilder(32);
        String hex = "0123456789abcdef";
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < 32; i++) {
            sb.append(hex.charAt(rng.nextInt(16)));
        }
        return sb.toString();
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < 8)
            return "Password must be at least 8 characters!";
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\\";
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))       hasUpper   = true;
            else if (Character.isLowerCase(c))  hasLower   = true;
            else if (Character.isDigit(c))      hasDigit   = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }
        if (!hasUpper)   return "Need uppercase letter!";
        if (!hasLower)   return "Need lowercase letter!";
        if (!hasDigit)   return "Need digit!";
        if (!hasSpecial) return "Need special character!";
        return null;
    }

    public static boolean usernameExists(String username) {
        for (User user : users)
            if (user.getUsername().equalsIgnoreCase(username)) return true;
        return false;
    }

    public static boolean mobileExists(String mobile) {
        for (User user : users)
            if (user.getMobile().equals(mobile)) return true;
        return false;
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.length() != 10) return false;
        char first = mobile.charAt(0);
        if (first != '6' && first != '7' && first != '8' && first != '9') return false;
        for (char c : mobile.toCharArray())
            if (!Character.isDigit(c)) return false;
        return true;
    }

    // synchronized so concurrent threads don't corrupt the file
    public static synchronized void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User user : users) {
                bw.write(user.toFileString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving users.");
        }
    }

    public static void loadUsers() {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user != null) users.add(user);
            }
        } catch (Exception e) { /* file may not exist yet */ }
    }

    // ── Console I/O wrappers ─────────────────────────────────────────────────

    private static String getInput(String prompt) {
        ConsoleUI.input(prompt);
        return new java.util.Scanner(System.in).nextLine();
    }

    private static int getInt() {
        try {
            return Integer.parseInt(new java.util.Scanner(System.in).nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    private static void pause()              { ConsoleUI.pause(); }
    private static void showSuccess(String m){ ConsoleUI.success(m); }
    private static void showError(String m)  { ConsoleUI.error(m); }
    private static void showInfo(String m)   { ConsoleUI.info(m); }
}