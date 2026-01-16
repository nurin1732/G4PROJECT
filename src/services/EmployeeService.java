package services;

import units.Employee;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class EmployeeService {

    private final String FILE = "data/employee.csv";
    private ArrayList<Employee> employees = new ArrayList<>();

    public EmployeeService() {
        loadEmployees();
    }

    private void loadEmployees() {
        employees.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 5) continue;   
                employees.add(new Employee(p[0], p[1], p[2], p[3], p[4]));
            }
        } catch (IOException e) {
            System.out.println("Employee file not found.");
        }
    }

    public Employee login(String id, String pass) {
        for (Employee e : employees) {
            if (e.getId().equals(id) && e.getPassword().equals(pass)) {
                return e;
            }
        }
        return null;
    }

    public void registerEmployee(Scanner sc) {

        System.out.println("\n=== Register New Employee ===");

        System.out.print("Employee ID: ");
        String id = sc.nextLine().trim();

        if (employeeExists(id)) {
            System.out.println("Employee ID already exists.");
            return;
        }

        System.out.print("Employee Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Role (Manager / Full-time / Part-time): ");
        String role = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        String outlet;
        while (true) {
            System.out.print("Outlet (C60 - C69): ");
            outlet = sc.nextLine().trim().toUpperCase();
            if (outlet.matches("C6[0-9]")) break;
            System.out.println("Invalid outlet code.");
        }

        Employee emp = new Employee(id, name, role, password, outlet);

        saveEmployee(emp);
        employees.add(emp);

        System.out.println("Employee registered successfully.");
    }

    // =========================
    // SAVE TO CSV (FIXED)
    // =========================
    private void saveEmployee(Employee emp) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, true))) {

            pw.println();  // add a new row to keep data
            pw.print(emp.getId() + ",");
            pw.print(emp.getName() + ",");
            pw.print(emp.getRole() + ",");
            pw.print(emp.getPassword() + ",");
            pw.print(emp.getOutlet());

        } catch (IOException e) {
            System.out.println("Error saving employee.");
        }
    }

    //this is to ensure there is no duplication of id 
    private boolean employeeExists(String id) {
        for (Employee e : employees) {
            if (e.getId().equalsIgnoreCase(id)) return true;
        }
        return false;
    }
}