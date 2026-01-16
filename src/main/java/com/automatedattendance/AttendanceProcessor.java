package com.automatedattendance;

import java.util.List;

/**
 * AttendanceProcessor class to calculate attendance summaries from student data.
 * Computes total students, present count, and absent count.
 */
public class AttendanceProcessor {
    
    /**
     * Calculates attendance statistics from a list of students
     * @param students List of Student objects containing attendance data
     * @return AttendanceSummary object containing calculated statistics
     */
    public AttendanceSummary calculateAttendanceSummary(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return new AttendanceSummary(0, 0, 0, 0.0);
        }
        
        int totalStudents = students.size();
        int presentCount = 0;
        int absentCount = 0;
        
        for (Student student : students) {
            if (student.isPresent()) {
                presentCount++;
            } else if (student.isAbsent()) {
                absentCount++;
            }
            // If status is neither "Present" nor "Absent", it's considered invalid
            // and not counted in either category
        }
        
        // Calculate attendance percentage
        double attendancePercentage = totalStudents > 0 ? 
            (double) presentCount / totalStudents * 100 : 0.0;
        
        return new AttendanceSummary(totalStudents, presentCount, absentCount, attendancePercentage);
    }
    
    /**
     * Generates a formatted attendance summary string for email
     * @param students List of Student objects containing attendance data
     * @return Formatted string with attendance summary
     */
    public String generateSummaryText(List<Student> students) {
        AttendanceSummary summary = calculateAttendanceSummary(students);
        
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        sb.append("<head>\n");
        sb.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
        sb.append("    <title>Attendance Summary Report</title>\n");
        sb.append("</head>\n");
        sb.append("<body style=\"font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9;\">\n");
        sb.append("    <h2 style=\"color: #333; border-bottom: 2px solid #007cba; padding-bottom: 10px;\">Attendance Summary Report</h2>\n");
        sb.append("    <div style=\"margin-bottom: 20px; background-color: white; padding: 15px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);\">\n");
        sb.append("        <p><strong style=\"color: #333;\">Total Students:</strong> <span style=\"color: #666;\">").append(summary.getTotalStudents()).append("</span></p>\n");
        sb.append("        <p><strong style=\"color: #333;\">Present:</strong> <span style=\"color: #666;\">").append(summary.getPresentCount()).append("</span></p>\n");
        sb.append("        <p><strong style=\"color: #333;\">Absent:</strong> <span style=\"color: #666;\">").append(summary.getAbsentCount()).append("</span></p>\n");
        sb.append("        <p><strong style=\"color: #333;\">Attendance Percentage:</strong> <span style=\"color: #666;\">").append(String.format("%.2f", summary.getAttendancePercentage())).append("%</span></p>\n");
        sb.append("    </div>\n");
        
        // Add table of absent students if any
        if (summary.getAbsentCount() > 0) {
            sb.append("    <div style=\"font-weight: bold; margin-top: 15px; margin-bottom: 5px; color: #333;\"><strong>Absent Students:</strong></div>\n");
            sb.append("    <table style=\"border-collapse: collapse; width: 100%; margin-top: 10px; background-color: white; border: 1px solid #ccc;\">\n");
            sb.append("        <thead>\n");
            sb.append("            <tr style=\"background-color: #f2f2f2;\">\n");
            sb.append("                <th style=\"border: 1px solid #ddd; padding: 8px; text-align: left; font-weight: bold;\">P.No</th>\n");
            sb.append("                <th style=\"border: 1px solid #ddd; padding: 8px; text-align: left; font-weight: bold;\">Name</th>\n");
            sb.append("            </tr>\n");
            sb.append("        </thead>\n");
            sb.append("        <tbody>\n");
            for (Student student : students) {
                if (student.isAbsent()) {
                    sb.append("            <tr>\n");
                    sb.append("                <td style=\"border: 1px solid #ddd; padding: 8px;\">").append(student.getPNo()).append("</td>\n");
                    sb.append("                <td style=\"border: 1px solid #ddd; padding: 8px;\">").append(student.getName()).append("</td>\n");
                    sb.append("            </tr>\n");
                }
            }
            sb.append("        </tbody>\n");
            sb.append("    </table>\n");
        }
        
        sb.append("    <p style=\"margin-top: 20px; color: #666; font-style: italic;\"><em>Generated on: ").append(new java.util.Date()).append("</em></p>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        
        return sb.toString();
    }
    
    /**
     * Inner class to hold attendance summary statistics
     */
    public static class AttendanceSummary {
        private final int totalStudents;
        private final int presentCount;
        private final int absentCount;
        private final double attendancePercentage;
        
        public AttendanceSummary(int totalStudents, int presentCount, int absentCount, double attendancePercentage) {
            this.totalStudents = totalStudents;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.attendancePercentage = attendancePercentage;
        }
        
        // getters
        public int getTotalStudents() {
            return totalStudents;
        }
        
        public int getPresentCount() {
            return presentCount;
        }
        
        public int getAbsentCount() {
            return absentCount;
        }
        
        public double getAttendancePercentage() {
            return attendancePercentage;
        }
        
        @Override
        public String toString() {
            return "AttendanceSummary{" +
                    "totalStudents=" + totalStudents +
                    ", presentCount=" + presentCount +
                    ", absentCount=" + absentCount +
                    ", attendancePercentage=" + attendancePercentage +
                    '}';
        }
    }
}