# Automated Attendance System

A Java-based application designed to simplify attendance management by automatically processing attendance records from Excel files and sending summary emails to designated receivers.

## Features

- Read attendance data from .xls and .xlsx Excel files
- Validate Excel files for required columns (P.no, Name, Status)
- Calculate total students, present count, and absent count
- Generate attendance summary in HTML format with properly formatted tables
- Send summary emails via SMTP with TLS security
- Send email to dynamic recipients and a copy to the sender
- Maintain logs including timestamp, summary, and email status
- Support processing of 500+ records per upload
- User-friendly Swing GUI for easy interaction with dynamic email recipients

## System Requirements

### Hardware Requirements
- Processor: Intel i3 or higher
- RAM: 4 GB minimum
- Storage: 500 MB free disk space
- Network: Internet access for sending emails

### Software Requirements
- Java JDK 17 or higher
- Maven (for building the project)

## Setup Instructions

1. Clone or download the project
2. Update the configuration in `Config.java` with your email settings:
   - `SENDER_EMAIL`: Your Gmail address
   - `SENDER_APP_PASSWORD`: Your Gmail App Password (not your regular password)
3. Build the project using Maven: `mvn clean package`

## How to Use

### Command Line
1. Run the application with: `java -jar target/automated-attendance-system-1.0.0.jar <path-to-excel-file>`
2. The system will process the Excel file and send the attendance summary via email

### GUI Application
1. Run the GUI application with: `java -cp target/automated-attendance-system-1.0.0.jar com.automatedattendance.AttendanceGUI`
2. Click "Browse Excel File" to select your attendance Excel file
3. Enter recipient email addresses in the "Email(s)" field (separated by commas)
4. Click "Send Summary Email" to process the file and send the summary

## Configuration

Edit the `Config.java` file to set up your email configuration:

```java
// Email sender configuration
private static final String SENDER_EMAIL = "your-email@gmail.com";
private static final String SENDER_APP_PASSWORD = "your-app-password";

// Default placeholder for email receivers (will be updated dynamically via GUI)
private static List<String> RECEIVER_EMAILS = Arrays.asList(
    "example@example.com"
);

// Setter method to update receiver emails dynamically
public static void setReceiverEmails(List<String> emails) {
    RECEIVER_EMAILS = emails;
}
```

### Setting up Gmail App Password

1. Enable 2-Factor Authentication on your Google account
2. Go to Google Account settings
3. Navigate to Security > 2-Step Verification > App passwords
4. Generate a new app password for "Mail"
5. Use this app password in the configuration

## Excel File Format

The Excel file must contain the following required columns:
- `P.no`: Student identifier
- `Name`: Student name
- `Status`: Attendance status (Present/Absent)

Optional column:
- `Email`: Student email address

## GUI Features

The Swing GUI now includes:
- File selection panel to choose Excel files
- Email recipient input field to enter dynamic email addresses
- Process button to run the attendance processing
- Log panel to view processing status

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/automatedattendance/
│           ├── Config.java           # Email configuration
│           ├── Student.java          # Student data model
│           ├── ExcelReader.java      # Excel file processing
│           ├── AttendanceProcessor.java # Attendance calculations and HTML email generation
│           ├── EmailSender.java      # Email sending functionality
│           ├── LoggerUtil.java       # Logging functionality
│           ├── App.java              # Main application controller
│           ├── AttendanceGUI.java    # Swing GUI with dynamic email recipients

└── test/
    └── java/
```

## Dependencies

- Apache POI: For Excel file processing
- Jakarta Mail: For sending emails
- SLF4J & Logback: For logging
- JUnit: For testing

## Security Considerations

- Uses Gmail App Password instead of main account password
- Validates all inputs to prevent malformed data
- Does not store email passwords in logs
- Uses TLS/SSL for email transmission