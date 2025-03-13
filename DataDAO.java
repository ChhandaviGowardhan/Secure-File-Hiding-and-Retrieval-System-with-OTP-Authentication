package dao;

import db.MyConnection;
import model.Data;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataDAO {
    public static List<Data> getAllFiles(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM data WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        List<Data> files = new ArrayList<>();
        System.out.println("\nFetching all hidden files for user: " + email);
        System.out.println("-----------------------------------------");
        System.out.printf("%-5s | %-20s | %-30s\n", "ID", "File Name", "Path");
        System.out.println("-----------------------------------------");

        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new Data(id, name, path));

            System.out.printf("%-5d | %-20s | %-30s\n", id, name, path);
        }

        if (files.isEmpty()) {
            System.out.println("No hidden files found.");
        }

        System.out.println("-----------------------------------------\n");
        return files;
    }

    public static int hideFile(Data file) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO data(name, path, email, bin_data) VALUES(?,?,?,?)"
        );
        ps.setString(1, file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());

        File f = new File(file.getPath());

        if (!f.exists()) {
            System.out.println("❌ Error: File not found at " + file.getPath());
            return 0;
        }

        FileReader fr = new FileReader(f);
        ps.setCharacterStream(4, fr, f.length());
        int ans = ps.executeUpdate();
        fr.close();
        f.delete();

        if (ans > 0) {
            System.out.println("\n✅ File successfully hidden: " + file.getFileName());
        } else {
            System.out.println("\n❌ Error: Unable to hide the file.");
        }
        return ans;
    }

    public static void unhide(int id) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT path, bin_data FROM data WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("\n❌ Error: No file found with ID " + id);
            return;
        }

        String path = rs.getString("path");
        Clob c = rs.getClob("bin_data");

        Reader r = c.getCharacterStream();
        FileWriter fw = new FileWriter(path);
        int i;
        while ((i = r.read()) != -1) {
            fw.write((char) i);
        }
        fw.close();

        ps = connection.prepareStatement("DELETE FROM data WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();

        System.out.println("\n✅ File successfully restored to: " + path);
    }
}
