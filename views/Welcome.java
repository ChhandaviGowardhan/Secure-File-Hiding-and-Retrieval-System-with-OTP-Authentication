package views;

import dao.UserDAO;
import model.User;
import service.GenerateOTP;
import service.SendOTPService;
import service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Welcome {
    private final BufferedReader br;

    public Welcome() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    public void welcomeScreen() {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("🚀 Welcome to the App! 🚀");
            System.out.println("=============================");
            System.out.println("1️⃣  Login");
            System.out.println("2️⃣  Signup");
            System.out.println("0️⃣  Exit");
            System.out.print("👉 Enter your choice: ");

            try {
                int choice = Integer.parseInt(br.readLine());
                switch (choice) {
                    case 1 -> login();
                    case 2 -> signUp();
                    case 0 -> {
                        System.out.println("👋 Exiting the application. Goodbye!");
                        return;  // This will properly exit the loop
                    }
                    default -> System.out.println("❌ Invalid choice! Please enter 0, 1, or 2.");
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a numeric value.");
            }
        }
    }

    private void login() {
        try {
            System.out.print("\n📧 Enter email: ");
            String email = br.readLine().trim();

            if (UserDAO.isExists(email)) {
                String genOTP = GenerateOTP.getOTP();
                SendOTPService.sendOTP(email, genOTP);

                System.out.print("🔢 Enter the OTP sent to your email: ");
                String otp = br.readLine().trim();

                if (otp.equals(genOTP)) {
                    System.out.println("✅ OTP Verified! Logging in...");
                    new UserView(email).home();
                } else {
                    System.out.println("❌ Wrong OTP! Please try again.");
                }
            } else {
                System.out.println("❌ User not found! Please sign up first.");
            }
        } catch (IOException e) {
            System.out.println("❌ Input error! Please try again.");
        } catch (SQLException e) {
            System.out.println("❌ Database error! Unable to verify user.");
        }
    }

    private void signUp() {
        try {
            System.out.print("\n👤 Enter your name: ");
            String name = br.readLine().trim();

            System.out.print("📧 Enter email: ");
            String email = br.readLine().trim();

            if (UserDAO.isExists(email)) {
                System.out.println("⚠️ User already exists! Try logging in instead.");
                return;
            }

            String genOTP = GenerateOTP.getOTP();
            SendOTPService.sendOTP(email, genOTP);

            System.out.print("🔢 Enter the OTP sent to your email: ");
            String otp = br.readLine().trim();

            if (otp.equals(genOTP)) {
                User user = new User(name, email);
                int response = UserService.saveUser(user);
                if (response == 0) {
                    System.out.println("✅ User registered successfully! You can now log in.");
                } else {
                    System.out.println("❌ Registration failed! Please try again.");
                }
            } else {
                System.out.println("❌ Wrong OTP! Please try again.");
            }
        } catch (IOException e) {
            System.out.println("❌ Input error! Please try again.");
        } catch (SQLException e) {
            System.out.println("❌ Database error! Unable to register user.");
        }
    }
}
