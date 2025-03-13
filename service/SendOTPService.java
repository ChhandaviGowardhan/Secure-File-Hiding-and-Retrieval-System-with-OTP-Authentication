package service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendOTPService {
    public static void sendOTP(String email, String genOTP) {
        // Sender's email and App Password
        final String from = "www.kdot3@gmail.com"; // ✅ Replace with your email
        final String appPassword = "vsjt swpw iinb blbp"; // ✅ Generate from Google App Passwords

        // SMTP Server details
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = new Properties();

        // Setup mail server
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // ✅ Use TLS for security
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // ✅ Port 587 for TLS

        // Create a session with authentication
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, appPassword);
            }
        });

        // Enable debugging
        session.setDebug(true);

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

            // Set Subject: header field
            message.setSubject("Your OTP for File Enc App");

            // Set message content
            message.setText("Your One Time Password (OTP) is: " + genOTP);

            System.out.println("Sending OTP...");
            // Send the message
            Transport.send(message);
            System.out.println("OTP sent successfully!");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
