package com.automatedattendance;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    public static final Map<String, String> COLUMN_MAPPINGS = new HashMap<>();
    static {
        // P.No related headers - include more variations
        COLUMN_MAPPINGS.put("p.no", "P.no");
        COLUMN_MAPPINGS.put("p no", "P.no");
        COLUMN_MAPPINGS.put("pno", "P.no");
        COLUMN_MAPPINGS.put("ticket no", "P.no");
        COLUMN_MAPPINGS.put("ticket_no", "P.no");
        COLUMN_MAPPINGS.put("roll no", "P.no");
        COLUMN_MAPPINGS.put("id", "P.no");
        COLUMN_MAPPINGS.put("reg no", "P.no");
        
        // Name related headers
        COLUMN_MAPPINGS.put("name", "Name");
        COLUMN_MAPPINGS.put("full name", "Name");
        COLUMN_MAPPINGS.put("employee name", "Name");
        COLUMN_MAPPINGS.put("student name", "Name");
        
        // Status related headers - include more variations
        COLUMN_MAPPINGS.put("status", "Status");
        COLUMN_MAPPINGS.put("attendance", "Status");
        COLUMN_MAPPINGS.put("present/absent", "Status");
        COLUMN_MAPPINGS.put("present absent", "Status");
        COLUMN_MAPPINGS.put("attendance status", "Status");
        COLUMN_MAPPINGS.put("present", "Status");
        COLUMN_MAPPINGS.put("absent", "Status");
        COLUMN_MAPPINGS.put("attendance status", "Status");
        
        // Shop/Department related headers
        COLUMN_MAPPINGS.put("shop", "Shop");
        COLUMN_MAPPINGS.put("department", "Shop");
        COLUMN_MAPPINGS.put("trade", "Shop");
        COLUMN_MAPPINGS.put("dept", "Shop");
        COLUMN_MAPPINGS.put("location", "Shop");
        COLUMN_MAPPINGS.put("branch", "Shop");
        COLUMN_MAPPINGS.put("unit", "Shop");
        COLUMN_MAPPINGS.put("shop name", "Shop");
        COLUMN_MAPPINGS.put("workshop", "Shop");
        COLUMN_MAPPINGS.put("division", "Shop");
        COLUMN_MAPPINGS.put("area", "Shop");
        COLUMN_MAPPINGS.put("zone", "Shop");
        COLUMN_MAPPINGS.put("site", "Shop");
        COLUMN_MAPPINGS.put("center", "Shop");
        COLUMN_MAPPINGS.put("place", "Shop");
    }
    
    /**
     * Reads an Excel file with flexible column detection
     * @param filePath Path to the Excel file
     * @return FlexibleAttendanceData containing processed data and metadata
     * @throws IOException if there's an error reading the file
     */
    public static FlexibleAttendanceData readFlexibleExcelFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;
        
        try {
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IOException("Unsupported file format. Please use .xls or .xlsx files.");
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            if (!rowIterator.hasNext()) {
                throw new IOException("Excel file is empty");
            }
            
            Row headerRow = rowIterator.next();
            Map<String, Integer> columnMappings = detectColumnMappings(headerRow);
            List<String> originalHeaders = getOriginalHeaders(headerRow);
            
            // Check if required columns are present (P.no, Name, Status)
            boolean hasPNo = columnMappings.containsKey("P.no");
            boolean hasName = columnMappings.containsKey("Name");
            boolean hasStatus = columnMappings.containsKey("Status");
            
            System.out.println("P.no mapping: " + columnMappings.get("P.no"));
            System.out.println("Name mapping: " + columnMappings.get("Name"));
            System.out.println("Status mapping: " + columnMappings.get("Status"));
            System.out.println("Missing required columns. P.no: " + hasPNo + ", Name: " + hasName + ", Status: " + hasStatus);
            
            if (!hasPNo || !hasName || !hasStatus) {
                throw new IOException("Missing required columns. Need identifiers for Student ID, Name, and Status");
            }
            
            // Continue with reading the data...
            List<Map<String, String>> rawData = new ArrayList<>();
            List<Student> students = new ArrayList<>();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new HashMap<>();
                Student student = new Student();
                
                // Store all columns by their original header names
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    String headerName = getCellValueAsString(headerRow.getCell(i));
                    if (headerName != null) {
                        String cellValue = getCellValueAsString(cell);
                        rowData.put(headerName, cellValue);
                        
                        // Set Student object properties based on column mappings
                        for (Map.Entry<String, Integer> mappingEntry : columnMappings.entrySet()) {
                            if (mappingEntry.getValue() == i) { // If this column index matches the mapping
                                String mappedName = mappingEntry.getKey();
                                switch (mappedName) {
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
                        }
                    }
                }
                
                // Validate required fields
                if (student.getPNo() != null && !student.getPNo().trim().isEmpty() &&
                    student.getName() != null && !student.getName().trim().isEmpty() &&
                    student.getStatus() != null && !student.getStatus().trim().isEmpty()) {
                    students.add(student);
                }
                
                rawData.add(rowData);
            }
            
            return new FlexibleAttendanceData(originalHeaders, columnMappings, rawData, students);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    /**
     * Detects column mappings based on header row
     */
    public static Map<String, Integer> detectColumnMappings(Row headerRow) {
        Map<String, Integer> columnMappings = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = getCellValueAsString(cell);
            
            if (headerValue != null) {
                String normalizedHeader = normalizeHeader(headerValue);
                
                // Check against our predefined mappings
                for (Map.Entry<String, String> mapping : COLUMN_MAPPINGS.entrySet()) {
                    if (normalizedHeader.contains(mapping.getKey())) {
                        // Only map if this column type hasn't been mapped yet (first match wins)
                        if (!columnMappings.containsKey(mapping.getValue())) {
                            columnMappings.put(mapping.getValue(), i);
                        }
                    }
                }
            }
        }
        
        return columnMappings;
    }
    
    /**
     * Normalizes header names for comparison
     */
    public static String normalizeHeader(String header) {
        if (header == null) return "";
        return header.toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]", " ")
                    .trim()
                    .replaceAll("\\s+", " ");
    }
    
    /**
     * Gets the original headers from the header row
     */
    public static List<String> getOriginalHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = getCellValueAsString(cell);
            headers.add(headerValue);
        }
        return headers;
    }
    
    /**
     * Normalizes status values to standard format
     */
    public static String normalizeStatus(String status) {
        if (status == null) return "unknown";
        
        String lowerStatus = status.toLowerCase().trim();
        
        // Positive indicators
        if (lowerStatus.equals("present") || lowerStatus.equals("p") || lowerStatus.equals("yes") || 
            lowerStatus.contains("present") || lowerStatus.contains("here") || lowerStatus.contains("active")) {
            return "present";
        }
        // Negative indicators
        else if (lowerStatus.equals("absent") || lowerStatus.equals("a") || lowerStatus.equals("no") || 
                 lowerStatus.contains("absent") || lowerStatus.contains("leave") || lowerStatus.contains("off") ||
                 lowerStatus.contains("sick") || lowerStatus.contains("holiday") || lowerStatus.contains("off")) {
            return "absent";
        }
        
        // Return original if not recognized
        return status.trim();
    }
    
    /**
     * Gets cell value as string, handling different cell types
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle numeric values - convert to string without decimal places if it's a whole number
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
                return "";
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
        
        public FlexibleAttendanceData() {}
        
        public FlexibleAttendanceData(List<String> originalHeaders, Map<String, Integer> columnMappings, List<Map<String, String>> rawData) {
            this.originalHeaders = originalHeaders;
            this.columnMappings = columnMappings;
            this.rawData = rawData;
        }
        
        public FlexibleAttendanceData(List<String> originalHeaders, Map<String, Integer> columnMappings, List<Map<String, String>> rawData, List<Student> students) {
            this.originalHeaders = originalHeaders;
            this.columnMappings = columnMappings;
            this.rawData = rawData;
            this.students = students;
        }
        
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