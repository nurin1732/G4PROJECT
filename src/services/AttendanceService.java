package services;

import units.Attendance;
import units.Employee;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AttendanceService {

    private final String FILE = "data/attendance.csv";
    private ArrayList<Attendance> records = new ArrayList<>();

    public AttendanceService() {
        load();
    }

    private void load() {
        records.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                records.add(new Attendance(
                        p[0], p[1], p[2], p[3],
                        p.length > 4 ? p[4] : ""
                ));
            }
        } catch (IOException e) {
            System.out.println("Error finding attendance.csv");
        }
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("EmployeeID,EmployeeName,Date,ClockIn,ClockOut");
            for (Attendance a : records) {
                pw.println(a.getEmployeeId() + "," +
                        a.getEmployeeName() + "," +
                        a.getDate() + "," +
                        a.getClockIn() + "," +
                        (a.getClockOut() == null ? "" : a.getClockOut()));
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance.");
        }
    }

    public void clock(Employee emp) {

        load(); // reload latest data

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        for (Attendance a : records) {
            if (a.getEmployeeId().equals(emp.getId())
                && a.getDate().equals(today.toString())
                && (a.getClockOut() == null || a.getClockOut().isEmpty())) {

                // CLOCK OUT
                a.setClockOut(now.format(tf));
                save();

                LocalTime in = LocalTime.parse(a.getClockIn());
                double hours = Duration.between(in, now).toMinutes() / 60.0;

                System.out.println("\n=== Attendance Clock Out ===");
                System.out.println("Employee ID: " + emp.getId());
                System.out.println("Name: " + emp.getName());
                System.out.println("Clock Out Successful!");
                System.out.println("Date: " + today);
                System.out.println("Time: " + now.format(tf));
                System.out.printf("Total Hours Worked: %.1f hours\n", hours);
                return;
            }
        }

        // CLOCK IN
        Attendance rec = new Attendance(
            emp.getId(),
            emp.getName(),
            today.toString(),
            now.format(tf),
            ""
        );

        records.add(rec);
        save();

        System.out.println("\n=== Attendance Clock In ===");
        System.out.println("Employee ID: " + emp.getId());
        System.out.println("Name: " + emp.getName());
        System.out.println("Clock In Successful!");
        System.out.println("Date: " + today);
        System.out.println("Time: " + now.format(tf));
    }
}