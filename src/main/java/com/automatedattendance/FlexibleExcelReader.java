package com.automatedattendance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FlexibleExcelReader class to read Excel files with intelligent column detection.
 * Automatically detects column headers and maps various naming conventions.
 */
public class FlexibleExcelReader {
    
    // Column name mappings for flexible detection
    private static final Map<String, String> COLUMN_MAPPINGS = new HashMap<>();
    
    static {
        // P.no / Student ID mappings
        COLUMN_MAPPINGS.put("p.no", "P.no");
        COLUMN_MAPPINGS.put("p no", "P.no");
        COLUMN_MAPPINGS.put("pno", "P.no");
        COLUMN_MAPPINGS.put("roll no", "P.no");
        COLUMN_MAPPINGS.put("roll number", "P.no");
        COLUMN_MAPPINGS.put("student id", "P.no");
        COLUMN_MAPPINGS.put("reg no", "P.no");
        COLUMN_MAPPINGS.put("registration no", "P.no");
        COLUMN_MAPPINGS.put("id", "P.no");
        
        // Name mappings
        COLUMN_MAPPINGS.put("name", "Name");
        COLUMN_MAPPINGS.put("full name", "Name");
        COLUMN_MAPPINGS.put("student name", "Name");
        COLUMN_MAPPINGS.put("candidate", "Name");
        COLUMN_MAPPINGS.put("participant", "Name");
        
        // Status mappings
        COLUMN_MAPPINGS.put("status", "Status");
        COLUMN_MAPPINGS.put("attendance", "Status");
        COLUMN_MAPPINGS.put("present", "Status");
        COLUMN_MAPPINGS.put("present absent", "Status");
        COLUMN_MAPPINGS.put("attendance status", "Status");
        
        // Email mappings
        COLUMN_MAPPINGS.put("email", "Email");
        COLUMN_MAPPINGS.put("email address", "Email");
        COLUMN_MAPPINGS.put("contact email", "Email");
    }
    
    /**
     * Reads an Excel file with flexible column detection
     * @param filePath Path to the Excel file
     * @return FlexibleAttendanceData containing processed data and metadata
     * @throws IOException if there's an error reading the file
     */
    public FlexibleAttendanceData readFlexibleExcelFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }
        
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = null;
        FlexibleAttendanceData attendanceData = new FlexibleAttendanceData();
        
        try {
            // Determine if file is .xls or .xlsx
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IOException("Unsupported file format. Only .xls and .xlsx files are supported.");
            }
            
            Sheet sheet = workbook.getSheetAt(0); // Get first sheet
            
            // Validate and process header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("Excel file is empty or has no header row");
            }
            
            // Detect column mappings
            Map<String, Integer> columnMappings = detectColumnMappings(headerRow);
            
            // Check if required columns exist
            System.out.println("Column mappings found: " + columnMappings);
            System.out.println("P.no mapping: " + columnMappings.get("P.no"));
            System.out.println("Name mapping: " + columnMappings.get("Name"));
            System.out.println("Status mapping: " + columnMappings.get("Status"));
            if (!columnMappings.containsKey("P.no") || !columnMappings.containsKey("Name") || !columnMappings.containsKey("Status")) {
                System.out.println("Missing required columns. P.no: " + columnMappings.containsKey("P.no") + 
                    ", Name: " + columnMappings.containsKey("Name") + 
                    ", Status: " + columnMappings.containsKey("Status"));
                throw new IOException("Missing required columns. Need identifiers for Student ID, Name, and Status");
            }
            
            // Store original headers for email generation
            attendanceData.setOriginalHeaders(extractHeaders(headerRow));
            attendanceData.setColumnMappings(columnMappings);
            
            // Process data rows
            List<Map<String, String>> rawData = new ArrayList<>();
            List<Student> students = new ArrayList<>();
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // Skip empty rows
                
                Map<String, String> rowData = new HashMap<>();
                Student student = new Student();
                
                // Process each mapped column
                for (Map.Entry<String, Integer> entry : columnMappings.entrySet()) {
                    String columnName = entry.getKey();
                    int columnIndex = entry.getValue();
                    
                    String cellValue = getCellValueAsString(row.getCell(columnIndex));
                    rowData.put(columnName, cellValue);
                    
                    // Set Student object properties
                    switch (columnName) {
                        case "P.no":
                            student.setPNo(cellValue);
                            break;
                        case "Name":
                            student.setName(cellValue);
                            break;
                        case "Status":
                            student.setStatus(normalizeStatus(cellValue));
                            break;
                        case "Email":
                            student.setEmail(cellValue);
                            break;
                    }
                }
                
                // Validate required fields
                if (student.getPNo() == null || student.getPNo().trim().isEmpty()) {
                    throw new IOException("Invalid data at row " + (i + 1) + ": Student ID cannot be empty");
                }
                
                if (student.getName() == null || student.getName().trim().isEmpty()) {
                    throw new IOException("Invalid data at row " + (i + 1) + ": Name cannot be empty");
                }
                
                if (student.getStatus() == null || student.getStatus().trim().isEmpty()) {
                    throw new IOException("Invalid data at row " + (i + 1) + ": Status cannot be empty");
                }
                
                rawData.add(rowData);
                students.add(student);
            }
            
            attendanceData.setRawData(rawData);
            attendanceData.setStudents(students);
            
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
        
        return attendanceData;
    }
    
    /**
     * Detects column mappings by analyzing header names
     */
    public Map<String, Integer> detectColumnMappings(Row headerRow) {
        Map<String, Integer> mappings = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerName = getCellValueAsString(cell);
                if (headerName != null && !headerName.trim().isEmpty()) {
                    String normalizedHeader = normalizeHeader(headerName.trim());
                    if (COLUMN_MAPPINGS.containsKey(normalizedHeader)) {
                        String standardName = COLUMN_MAPPINGS.get(normalizedHeader);
                        // Avoid overwriting if column already mapped (first occurrence takes precedence)
                        if (!mappings.containsKey(standardName)) {
                            mappings.put(standardName, i);
                        }
                    }
                }
            }
        }
        
        return mappings;
    }
    
    /**
     * Normalizes header names for comparison
     */
    private String normalizeHeader(String header) {
        return header.toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]", " ")
                    .trim()
                    .replaceAll("\\s+", " ");
    }
    
    /**
     * Normalizes status values to standard format
     */
    private String normalizeStatus(String status) {
        if (status == null) return "";
        
        String normalized = status.trim().toLowerCase();
        
        // Handle various present indicators
        if (normalized.equals("p") || normalized.equals("present") || normalized.equals("✓") || 
            normalized.equals("1") || normalized.equals("yes") || normalized.equals("attended") ||
            normalized.equals("present/absent") || normalized.contains("present")) {
            return "Present";
        }
        
        // Handle various absent indicators
        if (normalized.equals("a") || normalized.equals("absent") || normalized.equals("✗") ||
            normalized.equals("0") || normalized.equals("no") || normalized.equals("missing") ||
            normalized.contains("absent")) {
            return "Absent";
        }
        
        // Return original if not recognized
        return status.trim();
    }
    
    /**
     * Extracts original headers from the row
     */
    private List<String> extractHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = getCellValueAsString(cell);
            headers.add(headerValue != null ? headerValue : "");
        }
        return headers;
    }
    
    /**
     * Gets the string value of a cell, handling different cell types
     */
    public String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle numeric values
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
    
    /**
     * Validates if the Excel file can be processed with flexible reading
     */
    public boolean validateFlexibleExcelFile(String filePath) throws IOException {
        System.out.println("Starting validation for file: " + filePath);
        try {
            System.out.println("About to read file...");
            FlexibleAttendanceData data = readFlexibleExcelFile(filePath);
            System.out.println("File read successfully, checking data...");
            boolean isValid = data.getStudents() != null && !data.getStudents().isEmpty();
            System.out.println("Validation result: " + isValid + " (students: " + 
                (data.getStudents() != null ? data.getStudents().size() : "null") + ")");
            return isValid;
        } catch (Exception e) {
            System.out.println("Validation failed with exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Data class to hold flexible attendance data and metadata
     */
    public static class FlexibleAttendanceData {
        private List<String> originalHeaders;
        private Map<String, Integer> columnMappings;
        private List<Map<String, String>> rawData;
        private List<Student> students;
        
        // Getters and setters
        public List<String> getOriginalHeaders() { return originalHeaders; }
        public void setOriginalHeaders(List<String> originalHeaders) { this.originalHeaders = originalHeaders; }
        
        public Map<String, Integer> getColumnMappings() { return columnMappings; }
        public void setColumnMappings(Map<String, Integer> columnMappings) { this.columnMappings = columnMappings; }
        
        public List<Map<String, String>> getRawData() { return rawData; }
        public void setRawData(List<Map<String, String>> rawData) { this.rawData = rawData; }
        
        public List<Student> getStudents() { return students; }
        public void setStudents(List<Student> students) { this.students = students; }
    }
}