# 🏠 SmartHub – Console Based Java Booking System

## 📌 Project Overview
SmartHub is a console-based Java application that simulates a real-world multi-service booking platform. It allows users to book transportation, rental services, and stays through a structured and modular system. The project is built using Object-Oriented Programming (OOP) principles and demonstrates clean architecture using service-based design.

---

## ✨ Features
- 🔐 User Authentication System (Login/Register)
- 🚕 Transport Booking Services
- 🏨 Stay Booking System
- 🏠 Rental Services
- 💳 Payment System Integration (UPI, Card, NetBanking)
- 📂 File Handling for Data Persistence
- 🧩 Modular Service-Based Architecture
- 🖥️ Console-Based User Interface

---

## 🛠️ Tech Stack
- Java (Core Java)
- Object-Oriented Programming (OOP)
- File Handling
- Console-Based UI

---

## 📁 Project Structure
SmartHubT/
│
├── Main.java
│
├── models/
│   ├── User.java
│   ├── Booking.java
│
├── services/
│   ├── AuthService.java
│   ├── RentalService.java
│   ├── StayService.java
│   ├── TransportService.java
│   ├── PaymentService.java
│   └── payment/
│       ├── PaymentMethod.java
│       ├── UpiPayment.java
│       ├── CardPayment.java
│       ├── NetBankingPayment.java
│
├── ui/
│   ├── ConsoleUI.java
│   ├── AsciiArt.java
│
├── utils/
│   ├── Animation.java
│
├── assets/
│   ├── ClassDiagram.png
│   ├── ERDiagram.png
│   ├── SystemArchitecture.png
│
├── users.txt
├── bookings.txt
