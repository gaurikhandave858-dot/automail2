package com.automatedattendance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoggerUtil class for logging system activities including timestamps, 
 * summary, and email status for auditing purposes.
 */
public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    private static final String LOG_FILE_PATH = "attendance_system.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs an informational message
     * @param message The message to log
     */
    public static void logInfo(String message) {
        String formattedMessage = String.format("[%s] INFO: %s", 
            LocalDateTime.now().format(DATE_FORMATTER), message);
        logger.info(message);
        writeToFile(formattedMessage);
    }
    
    /**
     * Logs an error message
     * @param message The error message to log
     */
    public static void logError(String message) {
        String formattedMessage = String.format("[%s] ERROR: %s", 
            LocalDateTime.now().format(DATE_FORMATTER), message);
        logger.error(message);
        writeToFile(formattedMessage);
    }
    
    /**
     * Logs an error with exception details
     * @param message The error message to log
     * @param throwable The exception to log
     */
    public static void logError(String message, Throwable throwable) {
        String formattedMessage = String.format("[%s] ERROR: %s - %s", 
            LocalDateTime.now().format(DATE_FORMATTER), message, throwable.getMessage());
        logger.error(message, throwable);
        writeToFile(formattedMessage);
        
        // Also log the stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        writeToFile(sw.toString());
    }
    
    /**
     * Logs a warning message
     * @param message The warning message to log
     */
    public static void logWarning(String message) {
        String formattedMessage = String.format("[%s] WARNING: %s", 
            LocalDateTime.now().format(DATE_FORMATTER), message);
        logger.warn(message);
        writeToFile(formattedMessage);
    }
    
    /**
     * Logs attendance summary information
     * @param summary The attendance summary text to log
     */
    public static void logAttendanceSummary(String summary) {
        String formattedMessage = String.format("[%s] ATTENDANCE_SUMMARY:\n%s", 
            LocalDateTime.now().format(DATE_FORMATTER), summary);
        logger.info("Attendance summary logged");
        writeToFile(formattedMessage);
    }
    
    /**
     * Logs email sending status
     * @param subject The subject of the email
     * @param recipients List of recipients
     * @param isSuccess Whether the email was sent successfully
     * @param additionalInfo Additional information about the email sending process
     */
    public static void logEmailStatus(String subject, List<String> recipients, boolean isSuccess, String additionalInfo) {
        String status = isSuccess ? "SUCCESS" : "FAILED";
        String recipientList = String.join(", ", recipients);
        String formattedMessage = String.format("[%s] EMAIL_%s: Subject='%s', Recipients=[%s]%s%s", 
            LocalDateTime.now().format(DATE_FORMATTER), 
            status, 
            subject, 
            recipientList,
            additionalInfo != null ? ", Info='" : "",
            additionalInfo != null ? additionalInfo + "'" : "");
        
        if (isSuccess) {
            logger.info("Email sent: " + subject);
        } else {
            logger.error("Email failed: " + subject);
        }
        writeToFile(formattedMessage);
    }
    
    /**
     * Logs Excel file processing information
     * @param filePath Path of the processed Excel file
     * @param recordCount Number of records processed
     * @param additionalInfo Additional information about the processing
     */
    public static void logExcelProcessing(String filePath, int recordCount, String additionalInfo) {
        String formattedMessage = String.format("[%s] EXCEL_PROCESSED: File='%s', Records=%d%s%s", 
            LocalDateTime.now().format(DATE_FORMATTER), 
            filePath, 
            recordCount,
            additionalInfo != null ? ", Info='" : "",
            additionalInfo != null ? additionalInfo + "'" : "");
        
        logger.info("Excel file processed: " + filePath);
        writeToFile(formattedMessage);
    }
    
    /**
     * Writes a message to the log file
     * @param message The message to write to the file
     */
    private static void writeToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Reads the log file and returns its content
     * @return List of log entries as strings
     */
    public static List<String> readLog() {
        List<String> logEntries = new ArrayList<>();
        File logFile = new File(LOG_FILE_PATH);
        
        if (!logFile.exists()) {
            return logEntries;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logEntries.add(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to read log file: " + e.getMessage());
        }
        
        return logEntries;
    }
    
    /**
     * Clears the log file
     */
    public static void clearLog() {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, false)) {
            // Writing an empty string effectively clears the file
            writer.write("");
            writer.flush();
            logger.info("Log file cleared");
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }
}