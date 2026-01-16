package com.automatedattendance;

import java.util.List;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * EmailSender class to send attendance summary emails to receivers and the sender.
 * Uses SMTP with TLS security for email transmission.
 */
public class EmailSender {
    
    /**
     * Sends attendance summary email to all configured receivers and CC to sender
     * @param subject Subject of the email
     * @param body Body content of the email (attendance summary)
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmail(String subject, String body) {
        try {
            // Validate configuration
            if (!Config.isConfigValid()) {
                System.err.println("Email configuration is not valid. Please check Config.java");
                return false;
            }
            
            // Set up properties for SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", Config.getSmtpHost());
            props.put("mail.smtp.port", Config.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", Config.isSmtpTlsEnabled());
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        Config.getSenderEmail(), 
                        Config.getSenderAppPassword()
                    );
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Config.getSenderEmail()));
            
            // Set recipients (to: receivers, cc: sender)
            List<String> receiverEmails = Config.getReceiverEmails();
            InternetAddress[] toAddresses = new InternetAddress[receiverEmails.size()];
            for (int i = 0; i < receiverEmails.size(); i++) {
                toAddresses[i] = new InternetAddress(receiverEmails.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            
            // CC the sender
            message.setRecipients(Message.RecipientType.CC, 
                new InternetAddress[]{new InternetAddress(Config.getSenderEmail())});
            
            message.setSubject(subject);
            message.setText(body);
            
            // Send the message
            Transport.send(message);
            
            System.out.println("Email sent successfully to " + receiverEmails.size() + " receivers");
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends attendance summary email with HTML formatting
     * @param subject Subject of the email
     * @param body Body content of the email (attendance summary)
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendHtmlEmail(String subject, String htmlBody) {
        try {
            // Validate configuration
            if (!Config.isConfigValid()) {
                System.err.println("Email configuration is not valid. Please check Config.java");
                return false;
            }
            
            // Set up properties for SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", Config.getSmtpHost());
            props.put("mail.smtp.port", Config.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", Config.isSmtpTlsEnabled());
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        Config.getSenderEmail(), 
                        Config.getSenderAppPassword()
                    );
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Config.getSenderEmail()));
            
            // Set recipients (to: receivers, cc: sender)
            List<String> receiverEmails = Config.getReceiverEmails();
            InternetAddress[] toAddresses = new InternetAddress[receiverEmails.size()];
            for (int i = 0; i < receiverEmails.size(); i++) {
                toAddresses[i] = new InternetAddress(receiverEmails.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            
            // CC the sender
            message.setRecipients(Message.RecipientType.CC, 
                new InternetAddress[]{new InternetAddress(Config.getSenderEmail())});
            
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            // Send the message
            Transport.send(message);
            
            System.out.println("HTML email sent successfully to " + receiverEmails.size() + " receivers");
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends attendance summary email to specified recipients
     * @param subject Subject of the email
     * @param body Body content of the email (attendance summary)
     * @param recipients List of email addresses to send the email to
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmailToRecipients(String subject, String body, List<String> recipients) {
        try {
            // Validate configuration
            if (!Config.isConfigValid()) {
                System.err.println("Email configuration is not valid. Please check Config.java");
                return false;
            }
            
            // Validate recipients
            if (recipients == null || recipients.isEmpty()) {
                System.err.println("No recipients provided");
                return false;
            }
            
            // Set up properties for SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", Config.getSmtpHost());
            props.put("mail.smtp.port", Config.getSmtpPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", Config.isSmtpTlsEnabled());
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        Config.getSenderEmail(), 
                        Config.getSenderAppPassword()
                    );
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Config.getSenderEmail()));
            
            // Set recipients (to: provided recipients, cc: sender)
            InternetAddress[] toAddresses = new InternetAddress[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                toAddresses[i] = new InternetAddress(recipients.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            
            // CC the sender
            message.setRecipients(Message.RecipientType.CC, 
                new InternetAddress[]{new InternetAddress(Config.getSenderEmail())});
            
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            
            // Send the message
            Transport.send(message);
            
            System.out.println("Email sent successfully to " + recipients.size() + " receivers");
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}