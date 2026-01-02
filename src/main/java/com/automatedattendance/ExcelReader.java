package com.automatedattendance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ExcelReader class to read Excel files and validate attendance data.
 * Supports both .xls and .xlsx file formats.
 */
public class ExcelReader {
    
    /**
     * Reads an Excel file and converts it to a list of Student objects
     * @param filePath Path to the Excel file
     * @return List of Student objects containing attendance data
     * @throws IOException if there's an error reading the file
     */
    public List<Student> readExcelFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }
        
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = null;
        List<Student> students = new ArrayList<>();
        
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
            
            // Validate required columns
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("Excel file is empty or has no header row");
            }
            
            int pNoColIndex = findColumnIndex(headerRow, "P.no");
            int nameColIndex = findColumnIndex(headerRow, "Name");
            int statusColIndex = findColumnIndex(headerRow, "Status");
            int emailColIndex = findColumnIndex(headerRow, "Email");
            
            // Check if required columns exist
            if (pNoColIndex == -1 || nameColIndex == -1 || statusColIndex == -1) {
                throw new IOException("Missing required columns. Required: P.no, Name, Status");
            }
            
            // Process data rows (starting from row 1, since row 0 is header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // Skip empty rows
                
                String pNo = getCellValueAsString(row.getCell(pNoColIndex));
                String name = getCellValueAsString(row.getCell(nameColIndex));
                String status = getCellValueAsString(row.getCell(statusColIndex));
                String email = emailColIndex != -1 ? getCellValueAsString(row.getCell(emailColIndex)) : null;
                
                // Validate that required fields are not empty
                if (pNo == null || pNo.trim().isEmpty()) {
                    throw new IOException("Invalid data at row " + (i + 1) + ": P.no cannot be empty");
                }
                
                if (name == null || name.trim().isEmpty()) {
                    throw new IOException("Invalid data at row " + (i + 1) + ": Name cannot be empty");
                }
                
                if (status == null || status.trim().isEmpty()) {
                    throw new IOException("Invalid data at row " + (i + 1) + ": Status cannot be empty");
                }
                
                // Create and add Student object
                Student student = new Student(pNo, name, status, email);
                students.add(student);
            }
            
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
        
        return students;
    }
    
    /**
     * Finds the index of a column by its header name
     * @param headerRow The header row of the Excel sheet
     * @param columnName The name of the column to find
     * @return Index of the column, or -1 if not found
     */
    private int findColumnIndex(Row headerRow, String columnName) {
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue() != null && 
                cell.getStringCellValue().trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Column not found
    }
    
    /**
     * Gets the string value of a cell, handling different cell types
     * @param cell The cell to extract value from
     * @return String value of the cell, or null if cell is empty
     */
    private String getCellValueAsString(Cell cell) {
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
                return null;
            default:
                return null;
        }
    }
    
    /**
     * Validates if the Excel file has the required columns
     * @param filePath Path to the Excel file
     * @return true if required columns are present, false otherwise
     * @throws IOException if there's an error reading the file
     */
    public boolean validateExcelFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = null;
        
        try {
            // Determine if file is .xls or .xlsx
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                return false; // Unsupported format
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return false; // No header row
            }
            
            int pNoColIndex = findColumnIndex(headerRow, "P.no");
            int nameColIndex = findColumnIndex(headerRow, "Name");
            int statusColIndex = findColumnIndex(headerRow, "Status");
            
            // Check if required columns exist
            return pNoColIndex != -1 && nameColIndex != -1 && statusColIndex != -1;
            
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
    }
}