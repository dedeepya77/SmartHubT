package models;

// ================= MODEL: BOOKING =================

public class Booking {

    private static int counter = 1;

    private String bookingId;
    private String type;
    private String details;
    private int    amount;

    public Booking(String type, String details, int amount) {
        this.bookingId = "BK" + String.format("%04d", counter++);
        this.type      = type;
        this.details   = details;
        this.amount    = amount;
    }

    // Private constructor used only by fromFileString
    private Booking(String bookingId, String type, String details, int amount) {
        this.bookingId = bookingId;
        this.type      = type;
        this.details   = details;
        this.amount    = amount;
    }

    public String getBookingId() { 
        return bookingId; 
    }
    public String getType()      { 
        return type; 
    }
    public String getDetails()   { 
        return details; 
    }
    public int    getAmount()    { 
        return amount; 
    }

    // ── File serialization ───────────────────────────────────────────────────
    public String toFileString() {
        return bookingId + "|" + type + "|" + details + "|" + amount;
    }

    public static Booking fromFileString(String line) {
        if (line == null || line.isEmpty()) return null;
        String[] p = line.split("\\|", 4);
        if (p.length < 4) return null;
        try { return new Booking(p[0], p[1], p[2], Integer.parseInt(p[3])); }
        catch (Exception e) { return null; }
    }
}