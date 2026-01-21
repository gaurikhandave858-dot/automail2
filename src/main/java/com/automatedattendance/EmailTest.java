package com.automatedattendance;

/**
 * Simple test class to verify email configuration and functionality
 */
public class EmailTest {
    public static void main(String[] args) {
        System.out.println("Testing email configuration...");
        
        // Check if configuration is valid
        if (!Config.isConfigValid()) {
            System.out.println("❌ Configuration is not valid. Please check Config.java");
            System.out.println("  - Ensure SENDER_EMAIL is set");
            System.out.println("  - Ensure SENDER_APP_PASSWORD is set");
            System.out.println("  - Ensure SMTP_HOST is set");
            System.out.println("  - Ensure RECEIVER_EMAILS is set");
            return;
        }
        
        System.out.println("✅ Configuration is valid");
        System.out.println("Sender Email: " + Config.getSenderEmail());
        System.out.println("SMTP Host: " + Config.getSmtpHost());
        System.out.println("SMTP Port: " + Config.getSmtpPort());
        System.out.println("Receiver Emails: " + Config.getReceiverEmails());
        
        // Try to send a test email
        System.out.println("\nTrying to send test email...");
        EmailSender emailSender = new EmailSender();
        
        String testSubject = "Test Email - Attendance System";
        String testBody = "<h2>Email Configuration Test</h2>" +
                         "<p>If you received this email, your Gmail configuration is working correctly.</p>" +
                         "<p>This is a test message from the Automated Attendance System.</p>" +
                         "<p>Time: " + new java.util.Date() + "</p>";
        
        boolean success = emailSender.sendHtmlEmail(testSubject, testBody);
        
        if (success) {
            System.out.println("✅ Test email sent successfully!");
        } else {
            System.out.println("❌ Failed to send test email.");
            System.out.println("Possible causes:");
            System.out.println("  - Invalid Gmail app password");
            System.out.println("  - 2-factor authentication not enabled");
            System.out.println("  - Network connectivity issues");
            System.out.println("  - Gmail security settings blocking access");
        }
    }
}