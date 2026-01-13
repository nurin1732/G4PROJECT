package models;

public class Attendance {

    private String employeeId;
    private String employeeName;
    private String date;
    private String clockIn;
    private String clockOut;

    public Attendance(String employeeId, String employeeName,
                      String date, String clockIn, String clockOut) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDate() { return date; }
    public String getClockIn() { return clockIn; }
    public String getClockOut() { return clockOut; }

    public void setClockOut(String clockOut) {
        this.clockOut = clockOut;
    }
}