# SmartHub — Implementation Changes

## Files Modified
Only **two files** were changed. Drop them into your project and everything else stays the same.

| File | What changed |
|------|-------------|
| `services/AuthService.java` | Login attempts, OTP attempts, confirm-password attempts, session token, case-insensitive login, `synchronized saveUsers()` |
| `Main.java` | Session thread, idle-timeout watchdog thread |

---

## Change 1 — Login: 3 attempts max (`AuthService.java`)

```java
// login() — loop runs at most MAX_ATTEMPTS (3) times
for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
    ...
    if (credentials match) { return true; }
    showError("Invalid..." + remainingHint);
}
// Falls through here only after 3 failures
System.out.println("Too many failed attempts. Please try again later.");
return false;
```

- On each wrong attempt the error message shows how many tries are left.
- After the 3rd failure the method prints the message and returns `false` — the auth menu loop then shows itself again (no `System.exit`, just returns to menu as specified).
- **Bonus:** Login is now **case-insensitive** for the username (`equalsIgnoreCase`). Passwords remain case-sensitive. The stored canonical casing is used as `loggedInUser`.

---

## Change 2 — Confirm Password: 3 attempts max (`AuthService.java`)

```java
for (int attempt = 1; attempt <= MAX_CONFIRM; attempt++) {
    String confirmPassword = getInput("Confirm Password (attempt N/3)");
    if (confirmPassword.equals(password)) { break outer; }   // success
    showError("Passwords do not match! (N left)");
    if (attempt == MAX_CONFIRM) {
        System.out.println("Too many failed attempts. Returning to menu.");
        return;
    }
}
```

- If the user enters the wrong confirmation 3 times they are sent back to the menu **without** saving any account.
- The password-strength prompt only appears once (before the confirm loop), not on every retry.

---

## Change 3 — OTP: 3 total attempts across all resends (`AuthService.java`)

```java
int remainingAttempts = 3;      // global counter, not reset on resend
int otp = generateOtp();
showOtp(otp);

while (remainingAttempts > 0) {
    int entered = readOtp();
    remainingAttempts--;

    if (entered == otp) {
        // create account & return
    }

    if (remainingAttempts == 0) break;   // exit loop → System.exit below

    showError("Incorrect OTP! (N remaining)");
    askResend();                         // if yes, generate new OTP — counter NOT reset
}

System.out.println("Account creation failed. Exiting...");
System.exit(0);
```

Key points:
- `remainingAttempts` is **decremented before** checking for resend, so resending doesn't grant extra tries.
- A resend just replaces the current OTP value — the counter keeps ticking.
- After the 3rd failure the program prints the failure message and calls `System.exit(0)` as specified.

---

## Change 4 — Multithreading (`Main.java`)

### Session thread

```java
Thread sessionThread = new Thread(() -> homeMenu(), "session-alice");
sessionThread.start();
sessionThread.join();   // main thread blocks here until session ends
```

`homeMenu()` (and everything the user does while logged in) now runs inside a dedicated thread named `session-<username>`.

### Idle-timeout watchdog (bonus improvement #1)

```java
Thread watchdog = new Thread(() -> {
    Thread.sleep(120_000);          // 2 minutes
    if (sessionThread.isAlive()) {
        AuthService.logout();
        sessionThread.interrupt();  // kicks the session out
    }
});
watchdog.setDaemon(true);           // won't block JVM exit
watchdog.start();
```

- If the user is idle for 2 minutes the watchdog interrupts the session thread and auto-logs them out.
- The watchdog is cancelled (`.interrupt()`) when the session ends normally.

### `synchronized saveUsers()` (`AuthService.java`)

```java
public static synchronized void saveUsers() { ... }
```

The `synchronized` keyword means only one thread at a time can write `users.txt`. If two users somehow complete signup at the same instant, neither write will corrupt the other. You should apply the same keyword to `PaymentService.saveBookings()` / `PaymentService.loadBookings()` — the pattern is identical.

---

## Bonus improvement — Session token (`AuthService.java`)

```java
private static String sessionToken = null;

private static String generateToken() {
    // 32 random hex characters, e.g. "a3f9c1d2..."
}
```

On successful login a 32-character hex token is generated and stored as `sessionToken`. It is cleared on logout.  
The home menu title shows the first 8 characters of the token as a visual indicator:

```
🚇 SMART HUB  |  👤 alice  |  🔑 a3f9c1d2…
```

---

## How to apply `synchronized` to `PaymentService` (not included — same pattern)

In `PaymentService.java`, find your `saveBookings()` and `loadBookings()` methods and add the keyword:

```java
public static synchronized void saveBookings() { ... }
public static synchronized void loadBookings() { ... }
```

That's all — Java's built-in monitor lock handles the rest.

---

## Summary table

| Requirement | Where | How |
|-------------|-------|-----|
| Login 3 attempts | `AuthService.login()` | `for` loop, counter |
| OTP 3 total attempts | `AuthService.signup()` | single `remainingAttempts` counter |
| OTP resend | `AuthService.signup()` | prompt after wrong OTP; new OTP generated, counter unchanged |
| OTP fail → exit | `AuthService.signup()` | `System.exit(0)` after loop |
| Confirm password 3 attempts | `AuthService.signup()` | inner `for` loop |
| Confirm fail → return | `AuthService.signup()` | `return` after 3rd mismatch |
| Session thread | `Main.startUserSession()` | `new Thread(() -> homeMenu())` |
| Idle timeout (2 min) | `Main.startUserSession()` | daemon watchdog thread |
| `synchronized` save | `AuthService.saveUsers()` | `synchronized` keyword |
| Case-insensitive login | `AuthService.login()` | `equalsIgnoreCase` |
| Session token | `AuthService` | random 32-char hex, cleared on logout |