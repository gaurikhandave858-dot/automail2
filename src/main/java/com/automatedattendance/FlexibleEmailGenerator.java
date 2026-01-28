package com.automatedattendance;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FlexibleEmailGenerator class to create dynamic HTML emails that mirror
 * the structure of the source Excel file.
 */
public class FlexibleEmailGenerator {
    
    /**
     * Generates a dynamic HTML email based on the flexible attendance data
     * @param attendanceData The processed attendance data with original headers
     * @return HTML formatted email content
     */
    public String generateFlexibleEmail(FlexibleExcelReader.FlexibleAttendanceData attendanceData) {
        StringBuilder html = new StringBuilder();
        
        // HTML document structure
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append("        .container { max-width: 1000px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        html.append("        .header { text-align: center; margin-bottom: 30px; }\n");
        html.append("        .header h1 { color: #2c3e50; margin-bottom: 10px; }\n");
        html.append("        .summary-box { background-color: #ecf0f1; padding: 20px; border-radius: 5px; margin-bottom: 25px; }\n");
        html.append("        .shop-summary-box { background-color: #e8f5e8; padding: 20px; border-radius: 5px; margin-bottom: 25px; border-left: 4px solid #27ae60; }\n");
        html.append("        .shop-info { margin: 10px 0; }\n");
        html.append("        .management-insights { margin: 15px 0; padding: 10px; background-color: #f8f9fa; border-radius: 4px; }\n");
        html.append("        .summary-item { margin: 8px 0; font-size: 16px; }\n");
        html.append("        .summary-label { font-weight: bold; color: #34495e; }\n");
        html.append("        .summary-value { color: #2c3e50; margin-left: 10px; }\n");
        html.append("        .section-title { font-size: 20px; font-weight: bold; color: #2c3e50; margin: 25px 0 15px 0; padding-bottom: 8px; border-bottom: 2px solid #3498db; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin: 15px 0; background-color: white; }\n");
        html.append("        th { background-color: #3498db; color: white; padding: 12px; text-align: left; font-weight: bold; }\n");
        html.append("        td { padding: 10px; border: 1px solid #bdc3c7; }\n");
        html.append("        tr:nth-child(even) { background-color: #f8f9fa; }\n");
        html.append("        tr:hover { background-color: #e3f2fd; }\n");
        html.append("        .present { color: #27ae60; font-weight: bold; }\n");
        html.append("        .absent { color: #e74c3c; font-weight: bold; }\n");
        html.append("        .footer { margin-top: 30px; padding-top: 15px; border-top: 1px solid #bdc3c7; color: #7f8c8d; font-size: 14px; text-align: center; }\n");
        html.append("        .highlight { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        
        // Header
        html.append("        <div class=\"header\">\n");
        html.append("            <h1>📊 Attendance Summary Report</h1>\n");
        html.append("            <p>Generated from attendance data</p>\n");
        html.append("        </div>\n");
        
        // Process data to get statistics
        List<Student> students = attendanceData.getStudents();
        int totalStudents = students.size();
        int presentCount = 0;
        int absentCount = 0;
        
        for (Student student : students) {
            if (student.isPresent()) {
                presentCount++;
            } else if (student.isAbsent()) {
                absentCount++;
            }
        }
        
        double attendancePercentage = totalStudents > 0 ? (double) presentCount / totalStudents * 100 : 0.0;
        
        // Enhanced summary section with simplified information
        html.append("        <div class=\"summary-box\">\n");
        html.append("            <div class=\"summary-item\"><span class=\"summary-label\">Total Students:</span><span class=\"summary-value\">").append(totalStudents).append("</span></div>\n");
        html.append("            <div class=\"summary-item\"><span class=\"summary-label\">Present:</span><span class=\"summary-value present\">").append(presentCount).append("</span></div>\n");
        html.append("            <div class=\"summary-item\"><span class=\"summary-label\">Absent:</span><span class=\"summary-value absent\">").append(absentCount).append("</span></div>\n");
        html.append("            <div class=\"summary-item\"><span class=\"summary-label\">Attendance Rate:</span><span class=\"summary-value\">").append(String.format("%.1f", attendancePercentage)).append("%</span></div>\n");
        html.append("        </div>\n");
        
        // Absent employees summary section with all essential details
        if (absentCount > 0) {
            html.append("        <div class=\"section-title\">⚠️ Absent Employees Summary</div>\n");
            html.append("        <div class=\"highlight\">\n");
            html.append("            <p><strong>Total Absent: ").append(absentCount).append(" employees</strong></p>\n");
            html.append("            <table style=\"width: 100%; border-collapse: collapse; margin-top: 10px;\">\n");
            html.append("                <thead>\n");
            html.append("                    <tr style=\"background-color: #e74c3c; color: white;\">\n");
            html.append("                        <th style=\"padding: 8px; border: 1px solid #ddd;\">Name</th>\n");
            html.append("                        <th style=\"padding: 8px; border: 1px solid #ddd;\">Ticket/P.No</th>\n");
            html.append("                        <th style=\"padding: 8px; border: 1px solid #ddd;\">Trade/Shop/Department</th>\n");
            html.append("                    </tr>\n");
            html.append("                </thead>\n");
            html.append("                <tbody>\n");
            
            // Find the shop/dept column header
            List<String> originalHeaders = attendanceData.getOriginalHeaders();
            String pNoHeader = findEssentialHeader(originalHeaders, new String[]{"p.no", "ticket no", "ticket_no", "pno", "roll no", "id", "reg no"});
            String shopHeader = findEssentialHeader(originalHeaders, new String[]{"shop name", "shop", "department", "trade", "dept", "location", "branch", "unit", "workshop", "division", "area", "zone", "site", "center", "place"});
            
            List<Map<String, String>> rawData = attendanceData.getRawData();
            
            for (Student student : students) {
                if (student.isAbsent()) {
                    // Find the corresponding row in raw data to get shop/department info
                    String shopValue = "";
                    for (Map<String, String> row : rawData) {
                        String pNoValue = row.get(pNoHeader);
                        if (pNoValue != null && pNoValue.equals(student.getPNo())) {
                            if (shopHeader != null) {
                                shopValue = row.get(shopHeader);
                            }
                            break;
                        }
                    }
                    
                    html.append("                    <tr>\n");
                    html.append("                        <td style=\"padding: 8px; border: 1px solid #ddd;\"><strong>").append(escapeHtml(student.getName())).append("</strong></td>\n");
                    html.append("                        <td style=\"padding: 8px; border: 1px solid #ddd;\">").append(escapeHtml(student.getPNo())).append("</td>\n");
                    html.append("                        <td style=\"padding: 8px; border: 1px solid #ddd;\">").append(escapeHtml(shopValue != null ? shopValue : "N/A")).append("</td>\n");
                    html.append("                    </tr>\n");
                }
            }
            
            html.append("                </tbody>\n");
            html.append("            </table>\n");
            html.append("        </div>\n");
        }
        
        // Footer
        html.append("        <div class=\"footer\">\n");
        html.append("            <p>Report generated on: ").append(new java.util.Date()).append("</p>\n");
        html.append("            <p>This is an automated attendance summary report</p>\n");
        html.append("        </div>\n");
        
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * Finds the most relevant header from the list of original headers
     * based on the possible alternatives provided
     */
    private String findEssentialHeader(List<String> originalHeaders, String[] possibleHeaders) {
        for (String originalHeader : originalHeaders) {
            if (originalHeader != null) {
                String normalizedOriginal = normalizeHeader(originalHeader);
                for (String possibleHeader : possibleHeaders) {
                    if (normalizedOriginal.contains(possibleHeader) || possibleHeader.contains(normalizedOriginal)) {
                        return originalHeader;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Normalizes header names for comparison
     */
    private String normalizeHeader(String header) {
        if (header == null) return "";
        return header.toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]", " ")
                    .trim()
                    .replaceAll("\\s+", " ");
    }
    
    /**
     * Escapes HTML special characters for safe display
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
    
    /**
     * Extracts and summarizes shop/department information for management reporting
     */
    private String getShopNameSummary(FlexibleExcelReader.FlexibleAttendanceData attendanceData) {
        List<Map<String, String>> rawData = attendanceData.getRawData();
        if (rawData == null || rawData.isEmpty()) {
            return "";
        }
        
        // Look for shop/department related columns
        Set<String> shopNames = new HashSet<>();
        Set<String> departmentNames = new HashSet<>();
        
        // Check common shop/department column names
        String[] shopHeaders = {"shop name", "shop", "department", "dept", "branch", "location", "center", "unit", "workshop", "trade", "division", "area", "zone", "site", "place"};
        
        for (Map<String, String> row : rawData) {
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String header = entry.getKey();
                String value = entry.getValue();
                
                if (header != null && value != null && !value.trim().isEmpty()) {
                    String normalizedHeader = normalizeHeader(header);
                    
                    // Check if this is a shop/department column
                    for (String shopHeader : shopHeaders) {
                        if (normalizedHeader.contains(shopHeader)) {
                            shopNames.add(value.trim());
                            break;
                        }
                    }
                }
            }
        }
        
        // Generate summary HTML
        StringBuilder summary = new StringBuilder();
        
        if (!shopNames.isEmpty()) {
            summary.append("<div class=\"shop-info\">\n");
            summary.append("    <p><strong>Shop/Department(s):</strong> ");
            summary.append(String.join(", ", shopNames));
            summary.append("</p>\n");
            summary.append("</div>\n");
        }
        
        // Add additional management insights
        if (!shopNames.isEmpty() && rawData.size() > 0) {
            summary.append("<div class=\"management-insights\">\n");
            summary.append("    <p><strong>Management Summary:</strong></p>\n");
            summary.append("    <ul>\n");
            summary.append("        <li>Total workforce in shop(s): ").append(rawData.size()).append(" employees</li>\n");
            summary.append("        <li>Attendance rate: ").append(String.format("%.1f", 
                (double) rawData.stream().filter(row -> {
                    for (Map.Entry<String, String> entry : row.entrySet()) {
                        String header = entry.getKey();
                        String value = entry.getValue();
                        if (header != null && value != null) {
                            String normalizedHeader = normalizeHeader(header);
                            if (normalizedHeader.contains("status") || normalizedHeader.contains("attendance")) {
                                return value.toLowerCase().contains("present") || value.toLowerCase().equals("p");
                            }
                        }
                    }
                    return false;
                }).count() / rawData.size() * 100)).append("%</li>\n");
            summary.append("    </ul>\n");
            summary.append("</div>\n");
        }
        
        return summary.toString();
    }
}