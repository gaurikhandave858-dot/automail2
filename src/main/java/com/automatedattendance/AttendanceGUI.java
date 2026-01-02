package com.automatedattendance;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Swing GUI for the Automated Attendance System.
 * Provides a user-friendly interface for Excel file upload and processing.
 */
public class AttendanceGUI extends JFrame {
    private JTextField fileTextField;
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
        setTitle("Automated Attendance System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        fileTextField = new JTextField();
        fileTextField.setEditable(false);
        
        browseButton = new JButton("Browse Excel File");
        processButton = new JButton("Send Summary Email");
        processButton.setEnabled(false); // Initially disabled until file is selected
        
        logTextArea = new JTextArea(15, 50);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logScrollPane = new JScrollPane(logTextArea);
        
        // Add initial log message
        logTextArea.append("Welcome to Automated Attendance System\n");
        logTextArea.append("Please select an Excel file to process attendance.\n");
        logTextArea.append("Required columns: P.no, Name, Status\n\n");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel for file selection
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("File Selection"));
        topPanel.add(new JLabel("Excel File: "), BorderLayout.WEST);
        topPanel.add(fileTextField, BorderLayout.CENTER);
        topPanel.add(browseButton, BorderLayout.EAST);
        
        // Center panel for process button
        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.add(processButton);
        
        // Bottom panel for logs
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Log"));
        bottomPanel.add(logScrollPane, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Set preferred sizes
        topPanel.setPreferredSize(new Dimension(600, 60));
        bottomPanel.setPreferredSize(new Dimension(600, 300));
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
        
        // Disable buttons during processing to prevent multiple clicks
        browseButton.setEnabled(false);
        processButton.setEnabled(false);
        
        // Add processing message to log
        logTextArea.append("Processing file: " + file.getName() + "\n");
        logTextArea.append("This may take a moment...\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        
        // Run processing in a separate thread to keep GUI responsive
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return attendanceApp.processAttendanceFile(filePath);
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