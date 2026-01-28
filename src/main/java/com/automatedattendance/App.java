package com.automatedattendance;

import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Main controller class that manages the workflow:
 * upload → process → email → log
 */
public class App {
    
    private ExcelReader excelReader;
    private FlexibleExcelReader flexibleExcelReader;
    private AttendanceProcessor attendanceProcessor;
    private EmailSender emailSender;
    private FlexibleEmailGenerator flexibleEmailGenerator;
    
    public App() {
        this.excelReader = new ExcelReader();
        this.flexibleExcelReader = new FlexibleExcelReader();
        this.attendanceProcessor = new AttendanceProcessor();
        this.emailSender = new EmailSender();
        this.flexibleEmailGenerator = new FlexibleEmailGenerator();
    }
    
    /**
     * Processes an Excel file using flexible column detection and sends detailed tabular email
     * @param excelFilePath Path to the Excel file containing attendance data
     * @param recipients List of email addresses to send the summary to
     * @return true if the process completes successfully, false otherwise
     */
    public boolean processFlexibleAttendanceFile(String excelFilePath, List<String> recipients) {
        LoggerUtil.logInfo("Starting flexible attendance processing for file: " + excelFilePath);
        
        try {
            // 1. Validate Excel file with flexible reader
            LoggerUtil.logInfo("Validating Excel file with flexible reader: " + excelFilePath);
            if (!flexibleExcelReader.validateFlexibleExcelFile(excelFilePath)) {
                String errorMsg = "Excel file validation failed with flexible reader.";
                LoggerUtil.logError(errorMsg);
                System.err.println(errorMsg);
                return false;
            }
            LoggerUtil.logInfo("Excel file validation successful with flexible reader");
            
            // 2. Read Excel file with flexible column detection
            LoggerUtil.logInfo("Reading Excel file with flexible detection: " + excelFilePath);
            FlexibleExcelReader.FlexibleAttendanceData attendanceData = flexibleExcelReader.readFlexibleExcelFile(excelFilePath);
            List<Student> students = attendanceData.getStudents();
            
            LoggerUtil.logExcelProcessing(excelFilePath, students.size(), 
                "Successfully read " + students.size() + " student records with flexible detection");
            LoggerUtil.logInfo("Successfully read " + students.size() + " student records from Excel file");
            
            // 3. Generate flexible HTML email
            LoggerUtil.logInfo("Generating flexible tabular email");
            String htmlEmailContent = flexibleEmailGenerator.generateFlexibleEmail(attendanceData);
            LoggerUtil.logInfo("Flexible email content generated successfully");
            
            // 4. Send email
            LoggerUtil.logInfo("Sending flexible attendance summary email");
            
            // Try to send email with authentication failure handling
            boolean emailSent = attemptToSendEmail(Config.getEmailSubject(), htmlEmailContent, recipients);
            
            // Log email status
            LoggerUtil.logEmailStatus(Config.getEmailSubject(), recipients, emailSent, 
                emailSent ? "Flexible email sent successfully" : "Failed to send flexible email");
            
            if (emailSent) {
                LoggerUtil.logInfo("Flexible attendance summary email sent successfully to " + 
                    recipients.size() + " receivers");
                return true;
            } else {
                LoggerUtil.logError("Failed to send flexible attendance summary email");
                return false;
            }
            
        } catch (IOException e) {
            String errorMsg = "Error processing Excel file with flexible reader: " + e.getMessage();
            LoggerUtil.logError(errorMsg, e);
            System.err.println(errorMsg);
            return false;
        } catch (Exception e) {
            String errorMsg = "Unexpected error during flexible attendance processing: " + e.getMessage();
            LoggerUtil.logError(errorMsg, e);
            System.err.println(errorMsg);
            return false;
        }
    }
    
    /**
     * Processes an Excel file and sends attendance summary email
     * @param excelFilePath Path to the Excel file containing attendance data
     * @return true if the process completes successfully, false otherwise
     */
    public boolean processAttendanceFile(String excelFilePath) {
        // Use default recipients from Config
        return processAttendanceFile(excelFilePath, Config.getReceiverEmails());
    }
    
    /**
     * Processes an Excel file and sends attendance summary email to specified recipients
     * @param excelFilePath Path to the Excel file containing attendance data
     * @param recipients List of email addresses to send the summary to
     * @return true if the process completes successfully, false otherwise
     */
    public boolean processAttendanceFile(String excelFilePath, List<String> recipients) {
        LoggerUtil.logInfo("Starting attendance processing for file: " + excelFilePath);
        
        try {
            // 1. Validate Excel file
            LoggerUtil.logInfo("Validating Excel file: " + excelFilePath);
            if (!excelReader.validateExcelFile(excelFilePath)) {
                String errorMsg = "Excel file validation failed. Required columns (P.no, Name, Status) are missing.";
                LoggerUtil.logError(errorMsg);
                System.err.println(errorMsg);
                return false;
            }
            LoggerUtil.logInfo("Excel file validation successful");
            
            // 2. Read Excel file
            LoggerUtil.logInfo("Reading Excel file: " + excelFilePath);
            List<Student> students = excelReader.readExcelFile(excelFilePath);
            LoggerUtil.logExcelProcessing(excelFilePath, students.size(), 
                "Successfully read " + students.size() + " student records");
            LoggerUtil.logInfo("Successfully read " + students.size() + " student records from Excel file");
            
            // 3. Process attendance
            LoggerUtil.logInfo("Processing attendance data");
            String summaryText = attendanceProcessor.generateSummaryText(students);
            LoggerUtil.logAttendanceSummary(summaryText);
            LoggerUtil.logInfo("Attendance processing completed");
            
            // 4. Send email
            LoggerUtil.logInfo("Sending attendance summary email");
            
            // Try to send email with authentication failure handling
            boolean emailSent = attemptToSendEmail(Config.getEmailSubject(), summaryText, recipients);
            
            // Log email status
            LoggerUtil.logEmailStatus(Config.getEmailSubject(), recipients, emailSent, 
                emailSent ? "Email sent successfully" : "Failed to send email");
            
            if (emailSent) {
                LoggerUtil.logInfo("Attendance summary email sent successfully to " + 
                    recipients.size() + " receivers");
                return true;
            } else {
                LoggerUtil.logError("Failed to send attendance summary email");
                return false;
            }
            
        } catch (IOException e) {
            String errorMsg = "Error processing Excel file: " + e.getMessage();
            LoggerUtil.logError(errorMsg, e);
            System.err.println(errorMsg);
            return false;
        } catch (Exception e) {
            String errorMsg = "Unexpected error during attendance processing: " + e.getMessage();
            LoggerUtil.logError(errorMsg, e);
            System.err.println(errorMsg);
            return false;
        }
    }
    
    /**
     * Attempts to send email and handles authentication failures by prompting user to update credentials
     */
    private boolean attemptToSendEmail(String subject, String body, List<String> recipients) {
        try {
            return emailSender.sendEmailToRecipients(subject, body, recipients);
        } catch (Exception e) {
            // Check if the error is related to authentication
            if (e.getMessage() != null && (e.getMessage().contains("535") || 
                e.getMessage().contains("Authentication failed") || 
                e.getMessage().contains("Username and Password not accepted"))) {
                
                // Show the password reset dialog synchronously
                JFrame parentFrame = new JFrame();
                parentFrame.setAlwaysOnTop(true);
                PasswordResetDialog dialog = new PasswordResetDialog(parentFrame);
                dialog.setVisible(true);
                
                if (dialog.isPasswordUpdated()) {
                    int option = JOptionPane.showConfirmDialog(parentFrame,
                        "Password updated successfully! Would you like to retry sending the email?",
                        "Retry Email Sending",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        // Retry the email sending process
                        try {
                            Thread.sleep(1000); // Brief pause before retry
                            boolean retryResult = emailSender.sendEmailToRecipients(subject, body, recipients);
                            if (retryResult) {
                                JOptionPane.showMessageDialog(parentFrame,
                                    "Email sent successfully with new credentials!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                                return true; // Return true to indicate success
                            } else {
                                JOptionPane.showMessageDialog(parentFrame,
                                    "Failed to send email with new credentials.",
                                    "Email Sending Failed",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception retryException) {
                            JOptionPane.showMessageDialog(parentFrame,
                                "Failed to send email even with new credentials: " + retryException.getMessage(),
                                "Email Sending Failed",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        return false; // Return false after failed retry
                    }
                }
            }
            // Return false to indicate sending failed
            return false;
        }
    }
    
    /**
     * Main method to run the application
     * @param args Command line arguments - first argument should be the Excel file path
     */
    public static void main(String[] args) {
        App app = new App();
        
        // Check if Excel file path is provided as command line argument
        if (args.length == 0) {
            System.out.println("Usage: java -jar automated-attendance-system.jar <excel-file-path>");
            System.out.println("Or run with a default file path for testing purposes.");
            
            // For demonstration purposes, you can set a default file path here
            // This is just for testing - in production, the file path should be provided as an argument
            String defaultFilePath = "attendance.xlsx"; // Change this to your test file path
            System.out.println("Using default file path for demonstration: " + defaultFilePath);
            
            boolean success = app.processAttendanceFile(defaultFilePath);
            if (success) {
                System.out.println("Attendance processing completed successfully!");
            } else {
                System.out.println("Attendance processing failed. Check logs for details.");
            }
        } else {
            String excelFilePath = args[0];
            System.out.println("Processing attendance file: " + excelFilePath);
            
            boolean success = app.processAttendanceFile(excelFilePath);
            if (success) {
                System.out.println("Attendance processing completed successfully!");
            } else {
                System.out.println("Attendance processing failed. Check logs for details.");
            }
        }
    }
}