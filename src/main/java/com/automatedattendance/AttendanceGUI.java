package com.automatedattendance;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.UIManager;
import java.awt.Font;
import static javax.swing.SwingConstants.*;
import javax.swing.border.TitledBorder;

/**
 * Swing GUI for the Automated Attendance System.
 * Provides a user-friendly interface for Excel file upload and processing.
 */
public class AttendanceGUI extends JFrame {
    private JTextField fileTextField;
    private JTextField emailTextField;
    private JButton browseButton;
    private JButton processButton;
    private JTextArea logTextArea;
    private JScrollPane logScrollPane;
    private App attendanceApp;
    
    public AttendanceGUI() {
        this.attendanceApp = new App();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        setTitle("Automated Attendance System - Enhanced UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Configure fonts and styles
        Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
        
        fileTextField = new JTextField();
        fileTextField.setEditable(false);
        fileTextField.setFont(labelFont);
        
        emailTextField = new JTextField();
        emailTextField.setFont(labelFont);
        
        // Style the buttons
        browseButton = new JButton("Browse Excel File");
        browseButton.setFont(buttonFont);
        browseButton.setBackground(new Color(70, 130, 180)); // Steel blue
        browseButton.setForeground(Color.BLACK);
        browseButton.setFocusPainted(false);
        browseButton.setMargin(new Insets(8, 15, 8, 15));
        
        processButton = new JButton("Send Summary Email");
        processButton.setFont(buttonFont);
        processButton.setBackground(new Color(34, 139, 34)); // Forest green
        processButton.setForeground(Color.BLACK);
        processButton.setFocusPainted(false);
        processButton.setMargin(new Insets(8, 15, 8, 15));
        processButton.setEnabled(false); // Initially disabled until file is selected
        
        // Style the log area
        logTextArea = new JTextArea(15, 50);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logTextArea.setBackground(new Color(245, 245, 245)); // Light gray background
        logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Add styled initial log message
        logTextArea.append("🌟 Welcome to Automated Attendance System\n");
        logTextArea.append("📋 Please select an Excel file to process attendance.\n");
        logTextArea.append("✅ Required columns: P.no, Name, Status\n\n");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10)); // Add spacing between components
            
        // Create main container panel
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add padding
            
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180)); // Steel blue background
        JLabel headerLabel = new JLabel("📊 Automated Attendance Processing System", SwingConstants.CENTER);
        headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22)); // Increased font size
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.setPreferredSize(new Dimension(750, 70)); // Increase header panel size
            
        // Top panel for file selection
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Select Excel File", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        JLabel fileLabel = new JLabel("Attendance File: ");
        fileLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        topPanel.add(fileLabel, BorderLayout.WEST);
        topPanel.add(fileTextField, BorderLayout.CENTER);
        topPanel.add(browseButton, BorderLayout.EAST);
            
        // Middle panel for email input
        JPanel emailPanel = new JPanel(new BorderLayout(5, 5));
        emailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Email addresses (comma separated)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        JLabel emailLabel = new JLabel("Email Addresses (comma separated): ");
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailTextField, BorderLayout.CENTER);
            
        // Button panel for process button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(processButton);
            
        // Create upper panel combining file and email panels
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.add(topPanel);
        inputPanel.add(emailPanel);
            
        // Bottom panel for logs
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Processing Log", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        bottomPanel.add(logScrollPane, BorderLayout.CENTER);
            
        // Add components to main container
        mainContainer.add(inputPanel, BorderLayout.NORTH);
        mainContainer.add(buttonPanel, BorderLayout.CENTER);
            
        // Add all to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
            
        // Set preferred sizes
        topPanel.setPreferredSize(new Dimension(700, 80)); // Increased height
        emailPanel.setPreferredSize(new Dimension(700, 80)); // Increased height
        inputPanel.setPreferredSize(new Dimension(700, 180)); // Increased height
        bottomPanel.setPreferredSize(new Dimension(700, 250));
                
        // Set frame properties
        setPreferredSize(new Dimension(750, 650)); // Increased overall height
    }
    
    private void setupEventHandlers() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectExcelFile();
            }
        });
        
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processAttendanceFile();
            }
        });
    }
    
    private void setupWindow() {
        pack();
        setLocationRelativeTo(null); // Center the window
        setResizable(true);
        
        // Set window icon and appearance
        try {
            // Set system look and feel for better appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If system look and feel is not available, use default
        }
    }
    
    private void selectExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Excel File");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Excel Files (.xls, .xlsx)", "xls", "xlsx"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileTextField.setText(selectedFile.getAbsolutePath());
            processButton.setEnabled(true);
            
            // Log the file selection
            logTextArea.append("Selected file: " + selectedFile.getName() + "\n");
        }
    }
    
    private void processAttendanceFile() {
        String filePath = fileTextField.getText();
        if (filePath == null || filePath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select an Excel file first.", 
                "No File Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, 
                "File does not exist: " + filePath, 
                "File Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get recipient emails from the text field
        String emailText = emailTextField.getText();
        if (emailText == null || emailText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter at least one recipient email address.", 
                "No Recipients", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Parse the email addresses
        java.util.List<String> recipients = parseEmailAddresses(emailText);
        if (recipients.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No valid email addresses found. Please enter valid email addresses separated by commas.", 
                "Invalid Email Format", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Disable buttons during processing to prevent multiple clicks
        browseButton.setEnabled(false);
        processButton.setEnabled(false);
        
        // Add processing message to log
        logTextArea.append("Processing file: " + file.getName() + "\n");
        logTextArea.append("Sending email to: " + String.join(", ", recipients) + "\n");
        logTextArea.append("This may take a moment...\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        
        // Run processing in a separate thread to keep GUI responsive
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return attendanceApp.processAttendanceFile(filePath, recipients);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        logTextArea.append("Attendance processing completed successfully!\n");
                        JOptionPane.showMessageDialog(AttendanceGUI.this, 
                            "Attendance summary email sent successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        logTextArea.append("Attendance processing failed. Check logs for details.\n");
                        JOptionPane.showMessageDialog(AttendanceGUI.this, 
                            "Attendance processing failed. Check logs for details.", 
                            "Processing Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    logTextArea.append("Error during processing: " + e.getMessage() + "\n");
                    JOptionPane.showMessageDialog(AttendanceGUI.this, 
                        "Error during processing: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Re-enable buttons after processing
                    browseButton.setEnabled(true);
                    processButton.setEnabled(true);
                    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
                }
            }
        };
        
        worker.execute();
    }
    

    /**
     * Parses email addresses from a comma-separated string
     * @param emailText String containing email addresses separated by commas
     * @return List of valid email addresses
     */
    private java.util.List<String> parseEmailAddresses(String emailText) {
        java.util.List<String> emails = new java.util.ArrayList<>();
        if (emailText == null || emailText.trim().isEmpty()) {
            return emails;
        }
        
        String[] parts = emailText.split(",");
        for (String part : parts) {
            String email = part.trim();
            if (isValidEmail(email)) {
                emails.add(email);
            }
        }
        
        return emails;
    }
    
    /**
     * Validates if a string is a valid email address
     * @param email String to validate
     * @return true if the string is a valid email address, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Simple email validation using regex
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
    
    /**
     * Main method to launch the GUI application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // If system look and feel is not available, use default
                }
                
                new AttendanceGUI().setVisible(true);
            }
        });
    }
}