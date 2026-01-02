package com.automatedattendance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * SampleDataGenerator class to create a sample Excel file for testing the attendance system.
 */
public class SampleDataGenerator {
    
    public static void main(String[] args) {
        createSampleExcelFile("sample_attendance.xlsx");
    }
    
    /**
     * Creates a sample Excel file with attendance data for testing
     * @param fileName Name of the Excel file to create
     */
    public static void createSampleExcelFile(String fileName) {
        // Sample student data
        List<Student> students = Arrays.asList(
            new Student("001", "John Smith", "Present", "john.smith@example.com"),
            new Student("002", "Emma Johnson", "Present", "emma.johnson@example.com"),
            new Student("003", "Michael Brown", "Absent", "michael.brown@example.com"),
            new Student("004", "Sarah Davis", "Present", "sarah.davis@example.com"),
            new Student("005", "Robert Wilson", "Absent", "robert.wilson@example.com"),
            new Student("006", "Jennifer Taylor", "Present", "jennifer.taylor@example.com"),
            new Student("007", "William Anderson", "Present", "william.anderson@example.com"),
            new Student("008", "Lisa Martinez", "Absent", "lisa.martinez@example.com"),
            new Student("009", "David Thompson", "Present", "david.thompson@example.com"),
            new Student("010", "Karen Garcia", "Present", "karen.garcia@example.com")
        );
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("P.no");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Status");
            headerRow.createCell(3).setCellValue("Email");
            
            // Create data rows
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                Row row = sheet.createRow(i + 1);
                
                row.createCell(0).setCellValue(student.getPNo());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getStatus());
                row.createCell(3).setCellValue(student.getEmail());
            }
            
            // Auto-size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
                System.out.println("Sample Excel file created: " + fileName);
            }
            
        } catch (IOException e) {
            System.err.println("Error creating sample Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}