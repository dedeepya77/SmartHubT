package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Booking {

    // ── How long after booking a user may cancel (2 days) ────────────────────
    public static final long CANCEL_WINDOW_MS = 2 * 24 * 60 * 60 * 1000L;

    private static final DateTimeFormatter DISPLAY_FMT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

    // Matches " - dd-MM-yyyy hh:mm am/pm" at the END of a details string
    private static final java.util.regex.Pattern LEGACY_DATE_PAT =
        java.util.regex.Pattern.compile(
            " - (\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2} (?:am|pm|AM|PM))$"
        );

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String  bookingId;
    private final String  type;
    private final String  details;       // date tail already stripped
    private final int     amount;
    private final long    bookingEpoch;  // millis since epoch; 0 = truly unknown
    private       boolean cancelled;

    // ── Constructor for NEW bookings ──────────────────────────────────────────
    public Booking(String type, String details, int amount) {
        this.bookingId    = generateId();
        this.type         = type;
        this.details      = details;
        this.amount       = amount;
        this.bookingEpoch = System.currentTimeMillis();
        this.cancelled    = false;
    }

    // ── Constructor used when loading from file ───────────────────────────────
    private Booking(String bookingId, String type, String details,
                    int amount, long bookingEpoch, boolean cancelled) {
        this.bookingId    = bookingId;
        this.type         = type;
        this.details      = details;
        this.amount       = amount;
        this.bookingEpoch = bookingEpoch;
        this.cancelled    = cancelled;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String  getBookingId() { return bookingId; }
    public String  getType()      { return type; }
    public String  getDetails()   { return details; }
    public int     getAmount()    { return amount; }
    public boolean isCancelled()  { return cancelled; }

    /** Human-readable booking timestamp derived from the epoch. */
    public String getTimestamp() {
        if (bookingEpoch == 0L) return "N/A (legacy record)";
        java.time.Instant instant = java.time.Instant.ofEpochMilli(bookingEpoch);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant,
            java.time.ZoneId.systemDefault());
        return ldt.format(DISPLAY_FMT);
    }

    /** Returns true if the booking is still within the cancellation window. */
    public boolean isCancellable() {
        if (bookingEpoch == 0L) return false;
        return !cancelled &&
               (System.currentTimeMillis() - bookingEpoch) <= CANCEL_WINDOW_MS;
    }

    /** Hours remaining in the cancellation window (0 if expired or epoch unknown). */
    public long hoursLeftToCancel() {
        if (bookingEpoch == 0L) return 0;
        long remaining = CANCEL_WINDOW_MS - (System.currentTimeMillis() - bookingEpoch);
        return remaining > 0 ? remaining / 3_600_000L : 0;
    }

    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    // ── Persistence ───────────────────────────────────────────────────────────

    public String toFileString() {
        return bookingId + "|" + type + "|" + details + "|"
             + amount + "|" + bookingEpoch + "|" + (cancelled ? "1" : "0");
    }

    public static Booking fromFileString(String line) {
        if (line == null || line.isBlank()) return null;
        try {
            String[] p = line.split("\\|", 6);
            String  id         = p[0];
            String  type       = p[1];
            String  rawDetails = p[2];
            int     amount     = Integer.parseInt(p[3]);
            long    epoch      = p.length > 4 ? Long.parseLong(p[4]) : 0L;
            boolean cancelled  = p.length > 5 && p[5].equals("1");

            String cleanDetails = rawDetails;
            java.util.regex.Matcher m = LEGACY_DATE_PAT.matcher(rawDetails);
            if (m.find()) {
                String dateStr = m.group(1);
                try {
                    LocalDateTime ldt = LocalDateTime.parse(dateStr, DISPLAY_FMT);
                    long parsedEpoch = ldt.atZone(java.time.ZoneId.systemDefault())
                                         .toInstant().toEpochMilli();
                    epoch = parsedEpoch;                          // always use the real date
                    cleanDetails = rawDetails.substring(0, m.start()); // strip date from display
                } catch (DateTimeParseException ignored) { }
            }

            return new Booking(id, type, cleanDetails, amount, epoch, cancelled);
        } catch (Exception e) {
            return null;
        }
    }

    // ── ID generation ─────────────────────────────────────────────────────────
    private static String generateId() {
        String hex = Integer.toHexString((int)(Math.random() * 0xFFFF)).toUpperCase();
        return "BK-" + hex + "-" + System.currentTimeMillis();
    }
}