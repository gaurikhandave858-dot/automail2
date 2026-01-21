package com.automatedattendance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Manager to handle dynamic configuration updates
 * Allows updating credentials without rebuilding the application
 */
public class ConfigManager {
    
    private static final String CONFIG_FILE_PATH = "app_config.properties";
    private static ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        loadProperties();
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        File configFile = new File(CONFIG_FILE_PATH);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Could not load config file: " + e.getMessage());
                // Continue with defaults from Config.java
            }
        }
    }
    
    public String getSenderEmail() {
        String email = properties.getProperty("sender.email");
        return email != null ? email : Config.getSenderEmail();
    }
    
    public String getSenderAppPassword() {
        String password = properties.getProperty("sender.app.password");
        return password != null ? password : Config.getSenderAppPassword();
    }
    
    public String getSmtpHost() {
        String host = properties.getProperty("smtp.host");
        return host != null ? host : Config.getSmtpHost();
    }
    
    public int getSmtpPort() {
        String portStr = properties.getProperty("smtp.port");
        if (portStr != null) {
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                // Fall back to default
            }
        }
        return Config.getSmtpPort();
    }
    
    public boolean isSmtpTlsEnabled() {
        String tlsEnabled = properties.getProperty("smtp.tls.enabled");
        if (tlsEnabled != null) {
            return Boolean.parseBoolean(tlsEnabled);
        }
        return Config.isSmtpTlsEnabled();
    }
    
    public void updateSenderCredentials(String email, String password) {
        properties.setProperty("sender.email", email);
        properties.setProperty("sender.app.password", password);
        saveProperties();
    }
    
    private void saveProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Application Configuration - Updated: " + new java.util.Date());
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.err.println("Could not save config file: " + e.getMessage());
        }
    }
    
    // Method to check if the current configuration is valid
    public boolean isConfigValid() {
        String email = getSenderEmail();
        String password = getSenderAppPassword();
        String host = getSmtpHost();
        
        return email != null && !email.isEmpty() &&
               password != null && !password.isEmpty() &&
               host != null && !host.isEmpty();
    }
}