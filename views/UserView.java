package views;

import dao.DataDAO;
import model.Data;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserView {
    private String email;

    public UserView(String email) {
        this.email = email;
    }

    public void home() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n==============================");
            System.out.println("💡 Welcome, " + this.email + "!");
            System.out.println("==============================");
            System.out.println("1️⃣  Show hidden files");
            System.out.println("2️⃣  Hide a new file");
            System.out.println("3️⃣  Unhide a file");
            System.out.println("0️⃣  Exit");
            System.out.print("👉 Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> showHiddenFiles();
                case 2 -> hideNewFile(sc);
                case 3 -> unhideFile(sc);
                case 0 -> {
                    System.out.println("👋 Exiting the application. Goodbye!");
                    return;  // This will properly exit the loop
                }
                default -> System.out.println("❌ Invalid choice! Please select a valid option.");
            }
        }
    }

    private void showHiddenFiles() {
        try {
            List<Data> files = DataDAO.getAllFiles(this.email);
            if (files.isEmpty()) {
                System.out.println("📁 No hidden files found.");
            } else {
                System.out.println("\n🔒 Hidden Files:");
                System.out.println("ID    | File Name");
                System.out.println("-------------------");
                for (Data file : files) {
                    System.out.printf("%-5d | %s%n", file.getId(), file.getFileName());
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching files. Please try again.");
        }
    }

    private void hideNewFile(Scanner sc) {
        System.out.print("📂 Enter the file path: ");
        String path = sc.nextLine();
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("❌ File does not exist. Please enter a valid path.");
            return;
        }

        Data newFile = new Data(0, file.getName(), path, this.email);
        try {
            DataDAO.hideFile(newFile);
            System.out.println("✅ File '" + file.getName() + "' is now hidden.");
        } catch (SQLException | IOException e) {
            System.err.println("❌ Failed to hide the file. Please try again.");
        }
    }

    private void unhideFile(Scanner sc) {
        try {
            List<Data> files = DataDAO.getAllFiles(this.email);
            if (files.isEmpty()) {
                System.out.println("📂 No hidden files available to unhide.");
                return;
            }

            System.out.println("\n🔓 Hidden Files:");
            System.out.println("ID    | File Name");
            System.out.println("-------------------");
            for (Data file : files) {
                System.out.printf("%-5d | %s%n", file.getId(), file.getFileName());
            }

            System.out.print("🔍 Enter the ID of the file to unhide: ");
            int id = Integer.parseInt(sc.nextLine());

            boolean isValidID = files.stream().anyMatch(file -> file.getId() == id);
            if (isValidID) {
                DataDAO.unhide(id);
                System.out.println("✅ File with ID " + id + " has been unhidden.");
            } else {
                System.out.println("❌ Invalid ID! Please enter a correct file ID.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("❌ Error while unhiding the file. Please try again.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter a numeric ID.");
        }
    }
}
