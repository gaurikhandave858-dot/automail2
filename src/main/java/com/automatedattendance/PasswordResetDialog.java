package com.automatedattendance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog to allow users to update email credentials when authentication fails
 */
public class PasswordResetDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean confirmed = false;
    
    public PasswordResetDialog(Frame parent) {
        super(parent, "Update Email Credentials", true);
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        setSize(400, 200);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel emailLabel = new JLabel("Email Address:");
        emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("App Password:");
        passwordField = new JPasswordField(20);
        
        // Load current values if available
        ConfigManager configManager = ConfigManager.getInstance();
        emailField.setText(configManager.getSenderEmail());
    }
    
    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email Address:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("App Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Update Credentials");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> updateCredentials());
        cancelButton.addActionListener(e -> cancelDialog());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void updateCredentials() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Both email and password are required.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate email format (basic check)
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update the configuration
        ConfigManager configManager = ConfigManager.getInstance();
        configManager.updateSenderCredentials(email, password);
        
        confirmed = true;
        dispose();
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    private void cancelDialog() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public boolean isPasswordUpdated() {
        return confirmed;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame();
            PasswordResetDialog dialog = new PasswordResetDialog(frame);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                System.out.println("Credentials updated successfully!");
            } else {
                System.out.println("Credential update cancelled.");
            }
        });
    }
}