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

    static { 
        loadUsers(); 
    }

    public static String getLoggedInUser() { 
        return loggedInUser; 
    }
    public static void logout() { 
        loggedInUser = null; 
    }

    public static boolean login() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("🔐 LOGIN");
        ConsoleUI.line();

        String username = getInput("Username");
        String password = getInput("Password");

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                Animation.loading("Authenticating");
                loggedInUser = username;
                showSuccess("Login Successful!");
                pause();
                return true;
            }
        }
        showError("Invalid Username or Password!");
        pause();
        return false;
    }

    public static void signup() {
        ConsoleUI.clear();
        ConsoleUI.doubleLine();
        ConsoleUI.title("📝 CREATE ACCOUNT");
        ConsoleUI.line();

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

        String password;
        while (true) {
            System.out.println();
            System.out.println(ConsoleUI.center(ConsoleUI.DIM + "Password must be 8+ chars with uppercase, lowercase, digit & special character" + ConsoleUI.RESET));
            System.out.println();
            password = getInput("Create Password");
            String passwordError = validatePassword(password);
            if (passwordError != null) { 
                showError(passwordError); 
                continue; 
            }
            String confirmPassword = getInput("Confirm Password");
            if (!confirmPassword.equals(password)) { 
                showError("Passwords do not match!"); 
                continue; 
            }
            break;
        }

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

        int otp = 1000 + (int)(Math.random() * 9000);
        System.out.println();
        showInfo("OTP Sent Successfully!");
        System.out.println();
        System.out.println(ConsoleUI.center(ConsoleUI.BRIGHT_YELLOW + "🔑 OTP : " + otp));
        System.out.println();

        int enteredOtp = getInt();
        if (enteredOtp != otp) { 
            showError("Incorrect OTP!"); 
            pause(); 
            return; 
        }

        Animation.loading("Creating Account");
        users.add(new User(username, password, mobile));
        saveUsers();
        showSuccess("Account Created Successfully!");
        pause();
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < 8) 
            return "Password must be at least 8 characters!";
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\\";
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }
        if (!hasUpper) return "Need uppercase letter!";
        if (!hasLower) return "Need lowercase letter!";
        if (!hasDigit) return "Need digit!";
        if (!hasSpecial) return "Need special character!";
        return null;
    }

    public static boolean usernameExists(String username) {
        for (User user : users) if (user.getUsername().equalsIgnoreCase(username)) return true;
        return false;
    }

    public static boolean mobileExists(String mobile) {
        for (User user : users) if (user.getMobile().equals(mobile)) return true;
        return false;
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.length() != 10) return false;
        char first = mobile.charAt(0);
        if (first != '6' && first != '7' && first != '8' && first != '9') return false;
        for (char c : mobile.toCharArray()) if (!Character.isDigit(c)) return false;
        return true;
    }

    public static void saveUsers() {
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
        } catch (Exception e) {    }
    }

    private static String getInput(String prompt) { 
        ConsoleUI.input(prompt); 
        return new java.util.Scanner(System.in).nextLine(); 
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

    private static void showSuccess(String msg) { 
        ConsoleUI.success(msg); 
    }
    
    private static void showError(String msg) { 
        ConsoleUI.error(msg); 
    }
    
    private static void showInfo(String msg) { 
        ConsoleUI.info(msg); 
    }
}