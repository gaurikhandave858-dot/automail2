package com.automatedattendance;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class to store email settings and receiver information
 * for the Automated Attendance System.
 */
public class Config {
    
    // Email sender configuration
    private static final String SENDER_EMAIL = "gaurikhandave858@gmail.com";
    private static final String SENDER_APP_PASSWORD = "xyflmraxlagbwiej";
    
    // SMTP configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final boolean SMTP_TLS_ENABLED = true;
    
    // Email receivers - list of email addresses to send attendance summaries to
    private static final List<String> RECEIVER_EMAILS = Arrays.asList(
        "tusharkhandave16@gmail.com",
        "bhumikamote05@gmail.com",
        "choudharyjinal934@gmail.com" ,
        "golemegha06@gmail.com",
        "sairajkatkar18@outlook.com" ,
        "atharvaaa1903@gmail.com");
    
    // Email subject
    private static final String EMAIL_SUBJECT = "Attendance Summary Report";
    
    // Getter methods
    public static String getSenderEmail() {
        return SENDER_EMAIL;
    }
    
    public static String getSenderAppPassword() {
        return SENDER_APP_PASSWORD;
    }
    
    public static String getSmtpHost() {
        return SMTP_HOST;
    }
    
    public static int getSmtpPort() {
        return SMTP_PORT;
    }
    
    public static boolean isSmtpTlsEnabled() {
        return SMTP_TLS_ENABLED;
    }
    
    public static List<String> getReceiverEmails() {
        return RECEIVER_EMAILS;
    }
    
    public static String getEmailSubject() {
        return EMAIL_SUBJECT;
    }
    
    /**
     * Method to validate if configuration is properly set
     * @return true if all required configurations are set, false otherwise
     */
    public static boolean isConfigValid() {
        return SENDER_EMAIL != null && !SENDER_EMAIL.isEmpty() &&
               SENDER_APP_PASSWORD != null && !SENDER_APP_PASSWORD.isEmpty() &&
               SMTP_HOST != null && !SMTP_HOST.isEmpty() &&
               RECEIVER_EMAILS != null && !RECEIVER_EMAILS.isEmpty();
    }
}