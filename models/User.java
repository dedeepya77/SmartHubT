package models;

// ================= MODEL: USER =================

public class User {

    private String username;
    private String password;
    private String mobile;

    public User(String username, String password, String mobile) {
        this.username = username;
        this.password = password;
        this.mobile   = mobile;
    }

    public String getUsername() { 
        return username; 
    }
    public String getPassword() { 
        return password; 
    }
    public String getMobile()   { 
        return mobile; 
    }

    // ── File serialization ─────────────
    public String toFileString() {
        return username + "|" + password + "|" + mobile;
    }

    public static User fromFileString(String line) {
        if (line == null || line.isEmpty()) 
            return null;
        String[] parts = line.split("\\|");
        if (parts.length < 3) 
            return null;
        return new User(parts[0], parts[1], parts[2]);
    }
}