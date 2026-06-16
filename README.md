# 🚆 SmartHub – Unified Travel & Booking Console Application

SmartHub is a **console-based Java application** that brings multiple travel and booking services into one platform. Users can book transport, reserve hotels, rent vehicles, make payments, manage bookings, and view booking history through an interactive terminal interface.

Built entirely using **Core Java**, the project demonstrates strong Object-Oriented Programming concepts, multithreading, file handling, and input validation without using external libraries or frameworks.

---

## ✨ Features

### 👤 Authentication & Session Management
- User signup with OTP verification and Indian mobile number validation
- Strong password rules and confirm-password attempts
- Login with password masking and 3-attempt lockout
- Session token generation and auto logout after 2 minutes of inactivity

### 🚇 Travel & Booking Services
- Metro booking with interchange logic and fare calculation
- Bus and cab booking with distance-based pricing
- Hotel booking with multiple room types and member support
- Bike and car rentals with hourly pricing

### 💳 Payment Gateway
- UPI, Credit/Debit Card, and Net Banking support
- Input validation with retry mechanisms
- Receipt generation for successful payments

### 📜 Booking Management
- View booking history
- Cancel eligible bookings
- Track total spending with GST calculations
- Admin mode to view all users' bookings

### 🎨 Console Experience
- ANSI color-coded interface
- Loading animations and progress indicators
- Vehicle animations for enhanced user experience

---

## 🛠️ Tech Stack

- **Language:** Java
- **Architecture:** Console-Based Application
- **Persistence:** File-based storage (`users.txt`, `bookings.txt`)
- **Multithreading:** Raw `Thread` class
- **Libraries:** None (Pure Core Java)

---

## 🧠 OOP Concepts Demonstrated

- **Interface:** `Bookable` implemented by booking services
- **Abstraction:** `PaymentMethod` abstract class
- **Polymorphism:** Runtime selection of payment methods
- **Encapsulation:** Private fields with getters/setters in models
- **Method Overloading:** Multiple constructors in `Booking`
- **Multithreading:** User sessions and idle watchdog threads
- **File I/O:** Persistent storage using text files

---

## 📁 Folder Structure

```text
SmartHub/
├── Main.java
├── ui/
├── utils/
├── models/
└── services/
    └── payment/
```

## 🚀 Sample Flow

Signup → Login → Book Metro Ticket → Pay via UPI → Generate Receipt → View Booking History

---

## 🔮 Future Improvements

- Database integration
- Password hashing
- Email/SMS notifications
- GUI or web-based interface
- Unit testing with JUnit
- Role-based access control
