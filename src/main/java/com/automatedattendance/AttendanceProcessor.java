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
        sb.append("Attendance Summary Report\n");
        sb.append("=========================\n\n");
        sb.append("Total Students: ").append(summary.getTotalStudents()).append("\n");
        sb.append("Present: ").append(summary.getPresentCount()).append("\n");
        sb.append("Absent: ").append(summary.getAbsentCount()).append("\n");
        sb.append("Attendance Percentage: ").append(String.format("%.2f", summary.getAttendancePercentage())).append("%\n\n");
        
        // Add list of absent students if any
        if (summary.getAbsentCount() > 0) {
            sb.append("Absent Students:\n");
            sb.append("----------------\n");
            for (Student student : students) {
                if (student.isAbsent()) {
                    sb.append("P.no: ").append(student.getPNo())
                      .append(", Name: ").append(student.getName()).append("\n");
                }
            }
        }
        
        sb.append("\nGenerated on: ").append(new java.util.Date()).append("\n");
        
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