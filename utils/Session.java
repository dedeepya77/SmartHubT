package utils;

/**
 * Shared session-activity tracker.
 *
 * Main's watchdog thread reads lastActivityMs to detect idle timeout.
 * Any service (Payment, Transport, Stay, Rental) calls Session.keepAlive()
 * on every user keypress to reset the idle clock.
 *
 * Kept in utils so it is accessible from both the default package (Main)
 * and all named packages (services.*, ui.*) without circular dependencies.
 */
public class Session {

    /** Milliseconds since epoch of the last user activity. Volatile for cross-thread visibility. */
    public static volatile long lastActivityMs = 0;

    /** Reset the idle clock. Call this on every user keypress anywhere in the app. */
    public static void keepAlive() {
        lastActivityMs = System.currentTimeMillis();
    }
}