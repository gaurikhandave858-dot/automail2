package com.automatedattendance;

/**
 * Student class to represent attendance data from Excel files.
 * Contains P.no, Name, Status, and optional Email fields.
 */
public class Student {
    private String pNo;      // P.no - student identifier
    private String name;     // Name - student name
    private String status;   // Status - attendance status (Present/Absent)
    private String email;    // Email - optional email field
    
    // Default constructor
    public Student() {
    }
    
    // Constructor with required fields
    public Student(String pNo, String name, String status) {
        this.pNo = pNo;
        this.name = name;
        this.status = status;
    }
    
    // Constructor with all fields
    public Student(String pNo, String name, String status, String email) {
        this.pNo = pNo;
        this.name = name;
        this.status = status;
        this.email = email;
    }
    
    // Getters
    public String getPNo() {
        return pNo;
    }
    
    public String getName() {
        return name;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getEmail() {
        return email;
    }
    
    // Setters
    public void setPNo(String pNo) {
        this.pNo = pNo;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Checks if the student's attendance status is marked as present
     * @return true if status is "Present" (case-insensitive), false otherwise
     */
    public boolean isPresent() {
        return status != null && status.trim().equalsIgnoreCase("Present");
    }
    
    /**
     * Checks if the student's attendance status is marked as absent
     * @return true if status is "Absent" (case-insensitive), false otherwise
     */
    public boolean isAbsent() {
        return status != null && status.trim().equalsIgnoreCase("Absent");
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "pNo='" + pNo + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Student student = (Student) o;
        
        if (pNo != null ? !pNo.equals(student.pNo) : student.pNo != null) return false;
        if (name != null ? !name.equals(student.name) : student.name != null) return false;
        if (status != null ? !status.equals(student.status) : student.status != null) return false;
        return email != null ? email.equals(student.email) : student.email == null;
    }
    
    @Override
    public int hashCode() {
        int result = pNo != null ? pNo.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}