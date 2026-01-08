/*Below is a working split-version of the program according to your exact tree.
✅ No HashMap / no advanced stuff — only ArrayList, loops, switch, basic file I/O, bubble sort, linear search.

> Put these files exactly under: src/ … with the same folder names (model, manager, util)




---

src/Main.java

import manager.*;
import model.*;
import util.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    static EmployeeManager employeeManager = new EmployeeManager();
    static StockManager stockManager = new StockManager();
    static SalesManager salesManager = new SalesManager();

    static Employee currentUser = null;

    public static void main(String[] args) {
        FileManager.ensureFolders();
        FileManager.bootstrapIfMissing();

        // Load all data
        employeeManager.load();
        stockManager.load();
        salesManager.load();

        while (true) {
            if (currentUser == null) {
                loginScreen();
            } else {
                mainMenu();
            }
        }
    }

    static void loginScreen() {
        System.out.println("\n=== Employee Login ===");
        String id = prompt("Enter User ID: ").trim();
        String pw = prompt("Enter Password: ");

        Employee e = employeeManager.login(id, pw);
        if (e == null) {
            System.out.println("Login Failed: Invalid User ID or Password.");
            return;
        }
        currentUser = e;
        System.out.println("Login Successful!");
        System.out.println("Welcome, " + currentUser.getName() + " (" + currentUser.getOutletCode() + ")");
    }

    static boolean isManager() {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase("Manager");
    }

    static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    static void mainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("User: " + currentUser.getName()
                + " | Role: " + currentUser.getRole()
                + " | Outlet: " + currentUser.getOutletCode());

        System.out.println("1. Attendance Clock In");
        System.out.println("2. Attendance Clock Out");
        System.out.println("3. Stock Management");
        System.out.println("4. Sales System");
        System.out.println("5. Search Information");
        System.out.println("6. Edit Information");
        System.out.println("7. Storage Save (manual)");
        System.out.println("8. Data Analytics (Extra)");
        System.out.println("9. Filter & Sort Sales History (Extra)");
        System.out.println("10. Auto Email Daily Sales Report (Extra - generates .eml)");
        if (isManager()) System.out.println("11. Register New Employee (Manager)");
        if (isManager()) System.out.println("12. Employee Performance Metrics (Manager)");
        System.out.println("0. Logout");

        int c = promptInt("Choose: ");
        switch (c) {
            case 1: employeeManager.clockIn(currentUser); break;
            case 2: employeeManager.clockOut(currentUser); break;
            case 3: stockMenu(); break;
            case 4: salesMenu(); break;
            case 5: searchMenu(); break;
            case 6: editMenu(); break;
            case 7:
                employeeManager.save();
                stockManager.save();
                salesManager.save();
                System.out.println("All data saved.");
                break;
            case 8: salesManager.analyticsMenu(); break;
            case 9: salesManager.filterSortMenu(); break;
            case 10: salesManager.autoEmailMenu(currentUser); break;
            case 11:
                if (isManager()) employeeManager.registerEmployee();
                else System.out.println("Access denied (manager only).");
                break;
            case 12:
                if (isManager()) salesManager.performanceMetrics(employeeManager.getEmployees());
                else System.out.println("Access denied (manager only).");
                break;
            case 0: logout(); break;
            default: System.out.println("Invalid choice.");
        }
    }

    static void stockMenu() {
        System.out.println("\n=== Stock Management ===");
        System.out.println("1. Morning Stock Count");
        System.out.println("2. Night Stock Count");
        System.out.println("3. Stock In (receipt)");
        System.out.println("4. Stock Out (receipt)");
        System.out.println("0. Back");
        int c = promptInt("Choose: ");
        if (c == 1) stockManager.dailyCount(currentUser, "Morning");
        else if (c == 2) stockManager.dailyCount(currentUser, "Night");
        else if (c == 3) stockManager.stockInOut(true, currentUser);
        else if (c == 4) stockManager.stockInOut(false, currentUser);
    }

    static void salesMenu() {
        System.out.println("\n=== Sales System ===");
        System.out.println("1. Record New Sale");
        System.out.println("0. Back");
        int c = promptInt("Choose: ");
        if (c == 1) salesManager.recordSale(currentUser, stockManager);
    }

    static void searchMenu() {
        System.out.println("\n=== Search Information ===");
        System.out.println("1. Search Stock (by model)");
        System.out.println("2. Search Sales (keyword)");
        System.out.println("0. Back");
        int c = promptInt("Choose: ");
        if (c == 1) stockManager.searchStock();
        else if (c == 2) salesManager.searchSales();
    }

    static void editMenu() {
        System.out.println("\n=== Edit Information ===");
        System.out.println("1. Edit Stock (set qty)");
        System.out.println("2. Edit Sales (by Sale ID)");
        System.out.println("0. Back");
        int c = promptInt("Choose: ");
        if (c == 1) stockManager.editStock(currentUser);
        else if (c == 2) salesManager.editSale();
    }

    static String prompt(String s) {
        System.out.print(s);
        return sc.nextLine();
    }

    static int promptInt(String s) {
        while (true) {
            try {
                return Integer.parseInt(prompt(s).trim());
            } catch (Exception e) {
                System.out.println("Invalid number.");
            }
        }
    }
}


---

src/model/Employee.java

package model;

public class Employee {
    private String id;
    private String name;
    private String role;
    private String password;
    private String outletCode;

    public Employee(String id, String name, String role, String password, String outletCode) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
        this.outletCode = outletCode;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public String getOutletCode() { return outletCode; }

    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
    public void setOutletCode(String outletCode) { this.outletCode = outletCode; }
}


---

src/model/WatchModel.java

package model;

public class WatchModel {
    private String modelCode;
    private String modelName;
    private double unitPrice;

    public WatchModel(String modelCode, String modelName, double unitPrice) {
        this.modelCode = modelCode;
        this.modelName = modelName;
        this.unitPrice = unitPrice;
    }

    public String getModelCode() { return modelCode; }
    public String getModelName() { return modelName; }
    public double getUnitPrice() { return unitPrice; }
}


---

src/model/Sale.java

package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Sale {
    public static class SaleLine {
        private String modelCode;
        private int qty;
        private double unitPrice;

        public SaleLine(String modelCode, int qty, double unitPrice) {
            this.modelCode = modelCode;
            this.qty = qty;
            this.unitPrice = unitPrice;
        }

        public String getModelCode() { return modelCode; }
        public int getQty() { return qty; }
        public double getUnitPrice() { return unitPrice; }
    }

    private String saleId;
    private LocalDateTime dateTime;
    private String outletCode;
    private String employeeId;
    private String employeeName;
    private String customerName;
    private String method;
    private double total;
    private ArrayList<SaleLine> items = new ArrayList<SaleLine>();

    public String getSaleId() { return saleId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getOutletCode() { return outletCode; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getCustomerName() { return customerName; }
    public String getMethod() { return method; }
    public double getTotal() { return total; }
    public ArrayList<SaleLine> getItems() { return items; }

    public void setSaleId(String saleId) { this.saleId = saleId; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setOutletCode(String outletCode) { this.outletCode = outletCode; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setMethod(String method) { this.method = method; }
    public void setTotal(double total) { this.total = total; }

    public void addLine(SaleLine line) { items.add(line); }
}


---

src/model/Attendance.java

package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    private String employeeId;
    private LocalDate date;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    public Attendance(String employeeId, LocalDate date, LocalDateTime clockIn, LocalDateTime clockOut) {
        this.employeeId = employeeId;
        this.date = date;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    public String getEmployeeId() { return employeeId; }
    public LocalDate getDate() { return date; }
    public LocalDateTime getClockIn() { return clockIn; }
    public LocalDateTime getClockOut() { return clockOut; }

    public void setClockOut(LocalDateTime clockOut) { this.clockOut = clockOut; }
}


---

src/manager/EmployeeManager.java

package manager;

import model.Attendance;
import model.Employee;
import util.DateTimeUtil;
import util.FileManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class EmployeeManager {
    private ArrayList<Employee> employees = new ArrayList<Employee>();
    private ArrayList<Attendance> attendanceLogs = new ArrayList<Attendance>();
    private Scanner sc = new Scanner(System.in);

    public ArrayList<Employee> getEmployees() { return employees; }

    public void load() {
        employees.clear();
        attendanceLogs.clear();

        ArrayList<String[]> empRows = FileManager.readCsv(FileManager.EMP_FILE);
        for (int i = 1; i < empRows.size(); i++) {
            String[] r = empRows.get(i);
            if (r.length < 5) continue;
            employees.add(new Employee(r[0], r[1], r[2], r[3], r[4]));
        }

        ArrayList<String[]> attRows = FileManager.readCsv(FileManager.ATTEND_FILE);
        for (int i = 1; i < attRows.size(); i++) {
            String[] r = attRows.get(i);
            if (r.length < 4) continue;
            LocalDate d = DateTimeUtil.parseDate(r[1]);
            LocalDateTime in = r[2].trim().isEmpty() ? null : DateTimeUtil.parseTimestamp(r[2]);
            LocalDateTime out = r[3].trim().isEmpty() ? null : DateTimeUtil.parseTimestamp(r[3]);
            attendanceLogs.add(new Attendance(r[0], d, in, out));
        }
    }

    public void save() {
        // employees
        StringBuilder sb = new StringBuilder();
        sb.append("employeeId,employeeName,role,password,outletCode\n");
        for (int i = 0; i < employees.size(); i++) {
            Employee e = employees.get(i);
            sb.append(FileManager.csv(e.getId())).append(",")
              .append(FileManager.csv(e.getName())).append(",")
              .append(FileManager.csv(e.getRole())).append(",")
              .append(FileManager.csv(e.getPassword())).append(",")
              .append(FileManager.csv(e.getOutletCode())).append("\n");
        }
        FileManager.writeText(FileManager.EMP_FILE, sb.toString());

        // attendance
        StringBuilder ab = new StringBuilder();
        ab.append("employeeId,date,clockIn,clockOut\n");
        for (int i = 0; i < attendanceLogs.size(); i++) {
            Attendance a = attendanceLogs.get(i);
            ab.append(FileManager.csv(a.getEmployeeId())).append(",")
              .append(DateTimeUtil.formatDate(a.getDate())).append(",")
              .append(a.getClockIn() == null ? "" : DateTimeUtil.formatTimestamp(a.getClockIn())).append(",")
              .append(a.getClockOut() == null ? "" : DateTimeUtil.formatTimestamp(a.getClockOut())).append("\n");
        }
        FileManager.writeText(FileManager.ATTEND_FILE, ab.toString());
    }

    public Employee login(String id, String pw) {
        for (int i = 0; i < employees.size(); i++) {
            Employee e = employees.get(i);
            if (e.getId().equals(id) && e.getPassword().equals(pw)) return e;
        }
        return null;
    }

    public void registerEmployee() {
        System.out.println("\n=== Register New Employee ===");
        String name = prompt("Enter Employee Name: ").trim();
        String id = prompt("Enter Employee ID: ").trim();

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equalsIgnoreCase(id)) {
                System.out.println("Error: Employee ID already exists.");
                return;
            }
        }

        String pw = prompt("Set Password: ");
        String role = prompt("Set Role (Part-time / Full-time / Manager): ").trim();
        String outlet = prompt("Outlet Code (e.g. C60): ").trim();

        employees.add(new Employee(id, name, role, pw, outlet));
        save();
        System.out.println("Employee successfully registered!");
    }

    public void clockIn(Employee currentUser) {
        LocalDate today = LocalDate.now();
        Attendance open = findOpenAttendance(currentUser.getId(), today);
        if (open != null && open.getClockOut() == null) {
            System.out.println("You already clocked in today.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        attendanceLogs.add(new Attendance(currentUser.getId(), today, now, null));
        save();

        System.out.println("\n=== Attendance Clock In ===");
        System.out.println("Clock In Successful!");
        System.out.println("Date: " + DateTimeUtil.formatDate(today));
        System.out.println("Time: " + DateTimeUtil.formatTime(now));
    }

    public void clockOut(Employee currentUser) {
        LocalDate today = LocalDate.now();
        Attendance open = findOpenAttendance(currentUser.getId(), today);
        if (open == null || open.getClockIn() == null) {
            System.out.println("No clock-in record found for today.");
            return;
        }
        if (open.getClockOut() != null) {
            System.out.println("You already clocked out today.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        open.setClockOut(now);
        save();

        double hours = Duration.between(open.getClockIn(), open.getClockOut()).toMinutes() / 60.0;

        System.out.println("\n=== Attendance Clock Out ===");
        System.out.println("Clock Out Successful!");
        System.out.println("Date: " + DateTimeUtil.formatDate(today));
        System.out.println("Time: " + DateTimeUtil.formatTime(now));
        System.out.println("Total Hours Worked: " + String.format(java.util.Locale.US, "%.1f", hours) + " hours");
    }

    private Attendance findOpenAttendance(String empId, LocalDate date) {
        for (int i = attendanceLogs.size() - 1; i >= 0; i--) {
            Attendance a = attendanceLogs.get(i);
            if (a.getEmployeeId().equals(empId) && a.getDate().equals(date)) {
                return a;
            }
        }
        return null;
    }

    private String prompt(String s) {
        System.out.print(s);
        return sc.nextLine();
    }
}


---

src/manager/StockManager.java

package manager;

import model.Employee;
import model.WatchModel;
import util.DateTimeUtil;
import util.FileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class StockManager {

    // simple record (no extra file in model tree)
    public static class StockRecord {
        public String outletCode;
        public String modelCode;
        public int qty;
        public StockRecord(String outletCode, String modelCode, int qty) {
            this.outletCode = outletCode; this.modelCode = modelCode; this.qty = qty;
        }
    }

    private ArrayList<WatchModel> models = new ArrayList<WatchModel>();
    private ArrayList<StockRecord> stock = new ArrayList<StockRecord>();
    private Scanner sc = new Scanner(System.in);

    public void load() {
        models.clear();
        stock.clear();

        ArrayList<String[]> mRows = FileManager.readCsv(FileManager.MODEL_FILE);
        for (int i = 1; i < mRows.size(); i++) {
            String[] r = mRows.get(i);
            if (r.length < 3) continue;
            models.add(new WatchModel(r[0], r[1], FileManager.toDouble(r[2])));
        }

        ArrayList<String[]> sRows = FileManager.readCsv(FileManager.STOCK_FILE);
        for (int i = 1; i < sRows.size(); i++) {
            String[] r = sRows.get(i);
            if (r.length < 3) continue;
            stock.add(new StockRecord(r[0], r[1], FileManager.toInt(r[2])));
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("outletCode,model,qty\n");
        for (int i = 0; i < stock.size(); i++) {
            StockRecord s = stock.get(i);
            sb.append(FileManager.csv(s.outletCode)).append(",")
              .append(FileManager.csv(s.modelCode)).append(",")
              .append(s.qty).append("\n");
        }
        FileManager.writeText(FileManager.STOCK_FILE, sb.toString());
    }

    public ArrayList<WatchModel> getModels() { return models; }

    public WatchModel findModel(String code) {
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getModelCode().equalsIgnoreCase(code)) return models.get(i);
        }
        return null;
    }

    public int getStockQty(String outletCode, String modelCode) {
        for (int i = 0; i < stock.size(); i++) {
            StockRecord s = stock.get(i);
            if (s.outletCode.equalsIgnoreCase(outletCode) && s.modelCode.equalsIgnoreCase(modelCode)) return s.qty;
        }
        // create if missing
        stock.add(new StockRecord(outletCode, modelCode, 0));
        return 0;
    }

    public void setStockQty(String outletCode, String modelCode, int qty) {
        for (int i = 0; i < stock.size(); i++) {
            StockRecord s = stock.get(i);
            if (s.outletCode.equalsIgnoreCase(outletCode) && s.modelCode.equalsIgnoreCase(modelCode)) {
                s.qty = qty;
                return;
            }
        }
        stock.add(new StockRecord(outletCode, modelCode, qty));
    }

    public void dailyCount(Employee user, String label) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n=== " + label + " Stock Count ===");
        System.out.println("Date: " + DateTimeUtil.formatDate(now.toLocalDate()));
        System.out.println("Time: " + DateTimeUtil.formatTime(now));

        int ok = 0, mismatch = 0;

        for (int i = 0; i < models.size(); i++) {
            String model = models.get(i).getModelCode();
            int counted = promptInt("Model: " + model + " – Counted: ");
            int recorded = getStockQty(user.getOutletCode(), model);
            System.out.println("Store Record: " + recorded);

            if (counted == recorded) {
                System.out.println("Stock tally correct.");
                ok++;
            } else {
                System.out.println("! Mismatch detected (" + Math.abs(counted - recorded) + ")");
                mismatch++;
            }
        }

        System.out.println("Tally Correct: " + ok);
        System.out.println("Mismatches: " + mismatch);
        if (mismatch > 0) System.out.println("Warning: Please verify stock.");
    }

    public void stockInOut(boolean isIn, Employee user) {
        String type = isIn ? "Stock In" : "Stock Out";
        LocalDateTime now = LocalDateTime.now();

        String from = prompt("From (Outlet Code / HQ): ").trim();
        String to = prompt("To (Outlet Code): ").trim();

        ArrayList<String> movedModel = new ArrayList<String>();
        ArrayList<Integer> movedQty = new ArrayList<Integer>();
        int totalQty = 0;

        while (true) {
            String model = prompt("Enter Model (blank to finish): ").trim();
            if (model.isEmpty()) break;

            WatchModel mi = findModel(model);
            if (mi == null) { System.out.println("Unknown model."); continue; }

            int qty = promptInt("Enter Quantity: ");
            if (qty <= 0) { System.out.println("Quantity must be > 0"); continue; }

            if (!isIn) {
                int have = getStockQty(from, model);
                if (have < qty) {
                    System.out.println("Error: Insufficient stock for " + model + " (have " + have + ")");
                    continue;
                }
            }

            movedModel.add(model);
            movedQty.add(qty);
            totalQty += qty;
        }

        if (movedModel.size() == 0) {
            System.out.println("No model entered. Cancelled.");
            return;
        }

        if (isIn) {
            for (int i = 0; i < movedModel.size(); i++) {
                String m = movedModel.get(i);
                int q = movedQty.get(i);
                setStockQty(to, m, getStockQty(to, m) + q);
            }
        } else {
            for (int i = 0; i < movedModel.size(); i++) {
                String m = movedModel.get(i);
                int q = movedQty.get(i);
                setStockQty(from, m, getStockQty(from, m) - q);
                setStockQty(to, m, getStockQty(to, m) + q);
            }
        }

        save();

        String file = FileManager.RECEIPT_DIR + "/receipts_" + DateTimeUtil.formatDate(now.toLocalDate()) + ".txt";
        StringBuilder rec = new StringBuilder();
        rec.append("=== ").append(type).append(" ===\n");
        rec.append("Date: ").append(DateTimeUtil.formatDate(now.toLocalDate())).append("\n");
        rec.append("Time: ").append(DateTimeUtil.formatTime(now)).append("\n");
        rec.append("From: ").append(from).append("\n");
        rec.append("To: ").append(to).append("\n");
        rec.append("Models:\n");
        for (int i = 0; i < movedModel.size(); i++) {
            rec.append("- ").append(movedModel.get(i)).append(" (Quantity: ").append(movedQty.get(i)).append(")\n");
        }
        rec.append("Total Quantity: ").append(totalQty).append("\n");
        rec.append("Employee in Charge: ").append(user.getName()).append(" (").append(user.getId()).append(")\n\n");

        FileManager.appendText(file, rec.toString());
        System.out.println("Model quantities updated successfully.");
        System.out.println("Receipt generated: " + file);
    }

    public void searchStock() {
        String model = prompt("Search Model Name: ").trim();
        WatchModel mi = findModel(model);
        if (mi == null) {
            System.out.println("Model not found.");
            return;
        }
        System.out.println("Model: " + mi.getModelCode());
        System.out.println("Unit Price: RM" + FileManager.money(mi.getUnitPrice()));

        // show by outlet based on stock file contents
        ArrayList<String> outlets = FileManager.loadOutletCodes();
        System.out.println("Stock by Outlet:");
        for (int i = 0; i < outlets.size(); i++) {
            String oc = outlets.get(i);
            System.out.println(oc + ": " + getStockQty(oc, mi.getModelCode()));
        }
    }

    public void editStock(Employee user) {
        String outlet = prompt("Outlet Code (blank = your outlet): ").trim();
        if (outlet.isEmpty()) outlet = user.getOutletCode();

        String model = prompt("Enter Model: ").trim();
        WatchModel mi = findModel(model);
        if (mi == null) {
            System.out.println("Model not found.");
            return;
        }

        int cur = getStockQty(outlet, mi.getModelCode());
        System.out.println("Current Stock: " + cur);
        int nv = promptInt("Enter New Stock Value: ");
        if (nv < 0) { System.out.println("Cannot be negative."); return; }

        setStockQty(outlet, mi.getModelCode(), nv);
        save();
        System.out.println("Stock information updated successfully.");
    }

    private String prompt(String s) { System.out.print(s); return sc.nextLine(); }

    private int promptInt(String s) {
        while (true) {
            try { return Integer.parseInt(prompt(s).trim()); }
            catch (Exception e) { System.out.println("Invalid number."); }
        }
    }
}


---

src/manager/SalesManager.java

package manager;

import model.Employee;
import model.Sale;
import model.WatchModel;
import util.DateTimeUtil;
import util.FileManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class SalesManager {
    private ArrayList<Sale> sales = new ArrayList<Sale>();
    private Scanner sc = new Scanner(System.in);

    public void load() {
        sales.clear();
        ArrayList<String[]> rows = FileManager.readCsv(FileManager.SALES_FILE);
        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r.length < 9) continue;

            Sale s = new Sale();
            s.setSaleId(r[0]);
            s.setDateTime(DateTimeUtil.parseTimestamp(r[1]));
            s.setOutletCode(r[2]);
            s.setEmployeeId(r[3]);
            s.setEmployeeName(r[4]);
            s.setCustomerName(r[5]);
            s.setMethod(r[6]);
            s.setTotal(FileManager.toDouble(r[7]));

            String items = r[8];
            if (!items.trim().isEmpty()) {
                String[] parts = items.split("\\|");
                for (int k = 0; k < parts.length; k++) {
                    String[] p = parts[k].split(":");
                    if (p.length >= 3) {
                        s.addLine(new Sale.SaleLine(p[0], FileManager.toInt(p[1]), FileManager.toDouble(p[2])));
                    }
                }
            }
            sales.add(s);
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("saleId,datetime,outletCode,employeeId,employeeName,customerName,method,total,items\n");
        for (int i = 0; i < sales.size(); i++) {
            Sale s = sales.get(i);
            sb.append(FileManager.csv(s.getSaleId())).append(",")
              .append(DateTimeUtil.formatTimestamp(s.getDateTime())).append(",")
              .append(FileManager.csv(s.getOutletCode())).append(",")
              .append(FileManager.csv(s.getEmployeeId())).append(",")
              .append(FileManager.csv(s.getEmployeeName())).append(",")
              .append(FileManager.csv(s.getCustomerName())).append(",")
              .append(FileManager.csv(s.getMethod())).append(",")
              .append(s.getTotal()).append(",")
              .append(FileManager.csv(encodeItems(s))).append("\n");
        }
        FileManager.writeText(FileManager.SALES_FILE, sb.toString());
    }

    private String encodeItems(Sale s) {
        String out = "";
        ArrayList<Sale.SaleLine> items = s.getItems();
        for (int i = 0; i < items.size(); i++) {
            Sale.SaleLine it = items.get(i);
            out += it.getModelCode() + ":" + it.getQty() + ":" + it.getUnitPrice();
            if (i < items.size() - 1) out += "|";
        }
        return out;
    }

    public void recordSale(Employee user, StockManager stockManager) {
        LocalDateTime now = LocalDateTime.now();
        Sale s = new Sale();
        s.setSaleId(generateSaleId(now));
        s.setDateTime(now);
        s.setOutletCode(user.getOutletCode());
        s.setEmployeeId(user.getId());
        s.setEmployeeName(user.getName());

        System.out.println("\n=== Record New Sale ===");
        System.out.println("Date: " + DateTimeUtil.formatDate(now.toLocalDate()));
        System.out.println("Time: " + DateTimeUtil.formatTime(now));

        String cust = prompt("Customer Name: ").trim();
        if (cust.isEmpty()) cust = "Walk-in Customer";
        s.setCustomerName(cust);

        while (true) {
            String model = prompt("Enter Model: ").trim();
            WatchModel mi = stockManager.findModel(model);
            if (mi == null) { System.out.println("Unknown model."); continue; }

            int qty = promptInt("Enter Quantity: ");
            if (qty <= 0) { System.out.println("Quantity must be > 0"); continue; }

            int have = stockManager.getStockQty(user.getOutletCode(), model);
            if (have < qty) { System.out.println("Insufficient stock. Have " + have); continue; }

            System.out.println("Unit Price: RM" + FileManager.money(mi.getUnitPrice()));
            s.addLine(new Sale.SaleLine(model, qty, mi.getUnitPrice()));

            String more = prompt("More items? (Y/N): ").trim();
            if (!more.equalsIgnoreCase("Y")) break;
        }

        String method = prompt("Enter transaction method: ").trim();
        if (method.isEmpty()) method = "Unknown";
        s.setMethod(method);

        // total + reduce stock
        double total = 0;
        for (int i = 0; i < s.getItems().size(); i++) {
            Sale.SaleLine it = s.getItems().get(i);
            total += it.getQty() * it.getUnitPrice();
            stockManager.setStockQty(user.getOutletCode(), it.getModelCode(),
                    stockManager.getStockQty(user.getOutletCode(), it.getModelCode()) - it.getQty());
        }
        s.setTotal(total);
        stockManager.save();

        sales.add(s);
        save();

        String receipt = FileManager.SALES_RECEIPT_DIR + "/sales_" + DateTimeUtil.formatDate(now.toLocalDate()) + ".txt";
        FileManager.appendText(receipt, buildSaleReceipt(s));
        System.out.println("Subtotal: RM" + FileManager.money(s.getTotal()));
        System.out.println("Transaction successful.");
        System.out.println("Receipt generated: " + receipt);
    }

    private String buildSaleReceipt(Sale s) {
        StringBuilder rec = new StringBuilder();
        rec.append("=== Sale Receipt ===\n");
        rec.append("Sale ID: ").append(s.getSaleId()).append("\n");
        rec.append("Date: ").append(DateTimeUtil.formatDate(s.getDateTime().toLocalDate())).append("\n");
        rec.append("Time: ").append(DateTimeUtil.formatTime(s.getDateTime())).append("\n");
        rec.append("Outlet: ").append(s.getOutletCode()).append("\n");
        rec.append("Employee: ").append(s.getEmployeeName()).append(" (").append(s.getEmployeeId()).append(")\n");
        rec.append("Customer: ").append(s.getCustomerName()).append("\n");
        rec.append("Items:\n");
        for (int i = 0; i < s.getItems().size(); i++) {
            Sale.SaleLine it = s.getItems().get(i);
            rec.append("- ").append(it.getModelCode())
               .append(" Qty: ").append(it.getQty())
               .append(" Unit: RM").append(FileManager.money(it.getUnitPrice()))
               .append(" Line: RM").append(FileManager.money(it.getQty() * it.getUnitPrice()))
               .append("\n");
        }
        rec.append("Method: ").append(s.getMethod()).append("\n");
        rec.append("Total: RM").append(FileManager.money(s.getTotal())).append("\n");
        rec.append("Status: Transaction verified.\n\n");
        return rec.toString();
    }

    private String generateSaleId(LocalDateTime now) {
        return "S" + now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + (sales.size() + 1);
    }

    public void searchSales() {
        String key = prompt("Keyword (date yyyy-mm-dd / customer / model): ").trim().toLowerCase();
        boolean found = false;

        for (int i = 0; i < sales.size(); i++) {
            Sale s = sales.get(i);
            if (saleMatches(s, key)) {
                found = true;
                System.out.println("\nSales Record Found:");
                System.out.println("Sale ID: " + s.getSaleId());
                System.out.println("Date: " + DateTimeUtil.formatDate(s.getDateTime().toLocalDate())
                        + " Time: " + DateTimeUtil.formatTime(s.getDateTime()));
                System.out.println("Customer: " + s.getCustomerName());
                System.out.println("Items: " + summarizeItems(s));
                System.out.println("Total: RM" + FileManager.money(s.getTotal()));
                System.out.println("Method: " + s.getMethod());
                System.out.println("Employee: " + s.getEmployeeName());
                System.out.println("Status: Transaction verified.");
            }
        }

        if (!found) System.out.println("No matching sales record found.");
    }

    private boolean saleMatches(Sale s, String key) {
        if (s.getSaleId().toLowerCase().contains(key)) return true;
        if (s.getCustomerName().toLowerCase().contains(key)) return true;
        if (s.getEmployeeName().toLowerCase().contains(key)) return true;
        if (DateTimeUtil.formatDate(s.getDateTime().toLocalDate()).contains(key)) return true;
        for (int i = 0; i < s.getItems().size(); i++) {
            if (s.getItems().get(i).getModelCode().toLowerCase().contains(key)) return true;
        }
        return false;
    }

    private String summarizeItems(Sale s) {
        String out = "";
        for (int i = 0; i < s.getItems().size(); i++) {
            Sale.SaleLine it = s.getItems().get(i);
            out += it.getModelCode() + " x" + it.getQty();
            if (i < s.getItems().size() - 1) out += ", ";
        }
        return out;
    }

    public void editSale() {
        String saleId = prompt("Enter Sale ID: ").trim();
        Sale s = findSaleById(saleId);
        if (s == null) { System.out.println("Sales record not found."); return; }

        System.out.println("\nSales Record Found:");
        System.out.println("Customer: " + s.getCustomerName());
        System.out.println("Method: " + s.getMethod());
        System.out.println("Total: RM" + FileManager.money(s.getTotal()));

        System.out.println("1. Customer Name");
        System.out.println("2. Transaction Method");
        System.out.println("3. Total (manual override)");
        int c = promptInt("> ");

        if (c == 1) {
            String nn = prompt("Enter New Customer Name: ").trim();
            if (!nn.isEmpty()) s.setCustomerName(nn);
        } else if (c == 2) {
            String nm = prompt("Enter New Transaction Method: ").trim();
            if (!nm.isEmpty()) s.setMethod(nm);
        } else if (c == 3) {
            double nt = promptDouble("Enter New Total: RM");
            if (nt >= 0) s.setTotal(nt);
        } else {
            System.out.println("Invalid option.");
            return;
        }

        String ok = prompt("Confirm Update? (Y/N): ").trim();
        if (!ok.equalsIgnoreCase("Y")) { System.out.println("Cancelled."); return; }

        save();
        System.out.println("Sales information updated successfully.");
    }

    private Sale findSaleById(String saleId) {
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).getSaleId().equalsIgnoreCase(saleId)) return sales.get(i);
        }
        return null;
    }

    // ===== EXTRA: ANALYTICS =====
    public void analyticsMenu() {
        System.out.println("\n=== Data Analytics ===");
        System.out.println("1. Total Sales Today");
        System.out.println("2. Total Sales This Week (last 7 days)");
        System.out.println("3. Total Sales This Month");
        System.out.println("4. Most Sold Model (all time)");
        System.out.println("5. Average Daily Revenue (days with sales)");
        System.out.println("0. Back");
        int c = promptInt("Choose: ");

        if (c == 1) totalSalesRange(LocalDate.now(), LocalDate.now());
        else if (c == 2) totalSalesRange(LocalDate.now().minusDays(6), LocalDate.now());
        else if (c == 3) totalSalesRange(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        else if (c == 4) mostSoldModel();
        else if (c == 5) avgDailyRevenue();
    }

    private void totalSalesRange(LocalDate start, LocalDate end) {
        double total = 0;
        for (int i = 0; i < sales.size(); i++) {
            LocalDate d = sales.get(i).getDateTime().toLocalDate();
            if (!d.isBefore(start) && !d.isAfter(end)) total += sales.get(i).getTotal();
        }
        System.out.println("Total sales from " + DateTimeUtil.formatDate(start) + " to " + DateTimeUtil.formatDate(end)
                + ": RM" + FileManager.money(total));
    }

    private void mostSoldModel() {
        ArrayList<String> modelCodes = new ArrayList<String>();
        ArrayList<Integer> counts = new ArrayList<Integer>();

        // accumulate
        for (int i = 0; i < sales.size(); i++) {
            Sale s = sales.get(i);
            for (int j = 0; j < s.getItems().size(); j++) {
                String m = s.getItems().get(j).getModelCode();
                int qty = s.getItems().get(j).getQty();
                int idx = indexOfString(modelCodes, m);
                if (idx < 0) {
                    modelCodes.add(m);
                    counts.add(qty);
                } else {
                    counts.set(idx, counts.get(idx) + qty);
                }
            }
        }

        if (modelCodes.size() == 0) {
            System.out.println("No sales yet.");
            return;
        }

        int best = 0;
        for (int i = 1; i < counts.size(); i++) {
            if (counts.get(i) > counts.get(best)) best = i;
        }

        System.out.println("Most sold model: " + modelCodes.get(best) + " (Qty: " + counts.get(best) + ")");
    }

    private void avgDailyRevenue() {
        if (sales.size() == 0) { System.out.println("No sales yet."); return; }

        ArrayList<LocalDate> days = new ArrayList<LocalDate>();
        ArrayList<Double> totals = new ArrayList<Double>();

        for (int i = 0; i < sales.size(); i++) {
            LocalDate d = sales.get(i).getDateTime().toLocalDate();
            int idx = indexOfDate(days, d);
            if (idx < 0) {
                days.add(d);
                totals.add(sales.get(i).getTotal());
            } else {
                totals.set(idx, totals.get(idx) + sales.get(i).getTotal());
            }
        }

        double sum = 0;
        for (int i = 0; i < totals.size(); i++) sum += totals.get(i);
        double avg = sum / totals.size();

        System.out.println("Average daily revenue (" + totals.size() + " day(s) with sales): RM" + FileManager.money(avg));
    }

    // ===== EXTRA: FILTER & SORT SALES (bubble sort) =====
    public void filterSortMenu() {
        System.out.println("\n=== Filter & Sort Sales History ===");
        LocalDate start = promptDate("Start date (yyyy-mm-dd): ");
        LocalDate end = promptDate("End date (yyyy-mm-dd): ");
        if (end.isBefore(start)) { System.out.println("Invalid range."); return; }

        ArrayList<Sale> filtered = new ArrayList<Sale>();
        for (int i = 0; i < sales.size(); i++) {
            LocalDate d = sales.get(i).getDateTime().toLocalDate();
            if (!d.isBefore(start) && !d.isAfter(end)) filtered.add(sales.get(i));
        }

        double cum = 0;
        for (int i = 0; i < filtered.size(); i++) cum += filtered.get(i).getTotal();
        System.out.println("Found " + filtered.size() + " transaction(s). Cumulative total: RM" + FileManager.money(cum));

        System.out.println("Sort by:");
        System.out.println("1. Date Asc");
        System.out.println("2. Date Desc");
        System.out.println("3. Amount Low->High");
        System.out.println("4. Amount High->Low");
        System.out.println("5. Customer A->Z");
        int mode = promptInt("Choose: ");

        bubbleSortSales(filtered, mode);
        printSalesTable(filtered);
    }

    private void bubbleSortSales(ArrayList<Sale> list, int mode) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (shouldSwap(list.get(j), list.get(j + 1), mode)) {
                    Sale tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
            }
        }
    }

    private boolean shouldSwap(Sale a, Sale b, int mode) {
        if (mode == 1) return a.getDateTime().isAfter(b.getDateTime());
        if (mode == 2) return a.getDateTime().isBefore(b.getDateTime());
        if (mode == 3) return a.getTotal() > b.getTotal();
        if (mode == 4) return a.getTotal() < b.getTotal();
        if (mode == 5) return a.getCustomerName().compareToIgnoreCase(b.getCustomerName()) > 0;
        return false;
    }

    private void printSalesTable(ArrayList<Sale> list) {
        System.out.println("\n--- Sales History ---");
        System.out.printf("%-22s %-8s %-18s %-12s %-10s %-18s%n",
                "DateTime", "Outlet", "Customer", "Method", "Total", "Employee");
        System.out.println("--------------------------------------------------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            Sale s = list.get(i);
            System.out.printf("%-22s %-8s %-18s %-12s %-10s %-18s%n",
                    DateTimeUtil.formatTimestamp(s.getDateTime()),
                    s.getOutletCode(),
                    cut(s.getCustomerName(), 18),
                    cut(s.getMethod(), 12),
                    FileManager.money(s.getTotal()),
                    cut(s.getEmployeeName(), 18));
        }
        System.out.println();
    }

    // ===== EXTRA: EMPLOYEE PERFORMANCE (manager) =====
    public void performanceMetrics(ArrayList<Employee> employees) {
        System.out.println("\n=== Employee Performance Metrics ===");
        LocalDate start = promptDate("Start date (yyyy-mm-dd): ");
        LocalDate end = promptDate("End date (yyyy-mm-dd): ");
        if (end.isBefore(start)) { System.out.println("Invalid range."); return; }

        double[] totals = new double[employees.size()];
        int[] tx = new int[employees.size()];
        for (int i = 0; i < employees.size(); i++) { totals[i] = 0; tx[i] = 0; }

        for (int i = 0; i < sales.size(); i++) {
            Sale s = sales.get(i);
            LocalDate d = s.getDateTime().toLocalDate();
            if (!d.isBefore(start) && !d.isAfter(end)) {
                int idx = employeeIndex(employees, s.getEmployeeId());
                if (idx >= 0) {
                    totals[idx] += s.getTotal();
                    tx[idx] += 1;
                }
            }
        }

        // bubble sort by totals desc using index array
        int[] idxs = new int[employees.size()];
        for (int i = 0; i < idxs.length; i++) idxs[i] = i;

        for (int i = 0; i < idxs.length - 1; i++) {
            for (int j = 0; j < idxs.length - i - 1; j++) {
                if (totals[idxs[j]] < totals[idxs[j + 1]]) {
                    int tmp = idxs[j];
                    idxs[j] = idxs[j + 1];
                    idxs[j + 1] = tmp;
                }
            }
        }

        System.out.printf("%-20s %-10s %-15s %-10s%n", "Employee", "ID", "Total Sales", "Tx Count");
        System.out.println("---------------------------------------------------------------");
        for (int k = 0; k < idxs.length; k++) {
            int i = idxs[k];
            Employee e = employees.get(i);
            System.out.printf("%-20s %-10s %-15s %-10d%n",
                    cut(e.getName(), 20),
                    e.getId(),
                    FileManager.money(totals[i]),
                    tx[i]);
        }
    }

    private int employeeIndex(ArrayList<Employee> employees, String empId) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equalsIgnoreCase(empId)) return i;
        }
        return -1;
    }

    // ===== EXTRA: AUTO EMAIL (no SMTP) =====
    public void autoEmailMenu(Employee user) {
        System.out.println("\n=== Auto Email Daily Sales Report (Draft .eml) ===");
        LocalDate date = promptDate("Report date (yyyy-mm-dd): ");

        double total = 0;
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).getDateTime().toLocalDate().equals(date)) total += sales.get(i).getTotal();
        }

        String receiptFile = FileManager.SALES_RECEIPT_DIR + "/sales_" + DateTimeUtil.formatDate(date) + ".txt";
        String to = prompt("Send to (email): ").trim();
        if (to.isEmpty()) { System.out.println("Cancelled."); return; }

        String subject = "Daily Sales Report - " + DateTimeUtil.formatDate(date);
        String body = "Sales summary for " + DateTimeUtil.formatDate(date) + "\n" +
                "Outlet: " + user.getOutletCode() + "\n" +
                "Total sales amount: RM" + FileManager.money(total) + "\n\n" +
                "Attach this file when sending:\n" + receiptFile + "\n";

        String eml = "To: " + to + "\n" +
                "Subject: " + subject + "\n" +
                "Content-Type: text/plain; charset=UTF-8\n\n" +
                body;

        String out = FileManager.EMAIL_OUTBOX_DIR + "/draft_" + DateTimeUtil.formatDate(date) + ".eml";
        FileManager.writeText(out, eml);

        System.out.println("Draft created: " + out);
        System.out.println("Open the .eml file and send manually.");
    }

    // ===== small helpers =====
    private int indexOfString(ArrayList<String> list, String s) {
        for (int i = 0; i < list.size(); i++) if (list.get(i).equalsIgnoreCase(s)) return i;
        return -1;
    }

    private int indexOfDate(ArrayList<LocalDate> list, LocalDate d) {
        for (int i = 0; i < list.size(); i++) if (list.get(i).equals(d)) return i;
        return -1;
    }

    private String cut(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 1) + "…";
    }

    private String prompt(String s) { System.out.print(s); return sc.nextLine(); }

    private int promptInt(String s) {
        while (true) {
            try { return Integer.parseInt(prompt(s).trim()); }
            catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    private double promptDouble(String s) {
        while (true) {
            try { return Double.parseDouble(prompt(s).trim()); }
            catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    private LocalDate promptDate(String s) {
        while (true) {
            try { return LocalDate.parse(prompt(s).trim(), java.time.format.DateTimeFormatter.ISO_LOCAL_DATE); }
            catch (Exception e) { System.out.println("Invalid date. Use yyyy-mm-dd"); }
        }
    }
}


---

src/util/FileManager.java

package util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Locale;

public class FileManager {

    public static final String DATA_DIR = "data";
    public static final String RECEIPT_DIR = "receipts";
    public static final String SALES_RECEIPT_DIR = "sales_receipts";
    public static final String EMAIL_OUTBOX_DIR = "email_outbox";

    public static final String EMP_FILE = DATA_DIR + "/employees.csv";
    public static final String OUTLET_FILE = DATA_DIR + "/outlets.csv";
    public static final String MODEL_FILE = DATA_DIR + "/models.csv";
    public static final String STOCK_FILE = DATA_DIR + "/stock.csv";         // outletCode,model,qty
    public static final String ATTEND_FILE = DATA_DIR + "/attendance.csv";   // employeeId,date,clockIn,clockOut
    public static final String SALES_FILE = DATA_DIR + "/sales.csv";         // sale rows

    public static void ensureFolders() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(RECEIPT_DIR));
            Files.createDirectories(Paths.get(SALES_RECEIPT_DIR));
            Files.createDirectories(Paths.get(EMAIL_OUTBOX_DIR));
        } catch (IOException e) {
            System.out.println("Cannot create folders: " + e.getMessage());
            System.exit(1);
        }
    }

    // Creates starter CSVs if missing (you can replace with your lecturer CSVs)
    public static void bootstrapIfMissing() {
        try {
            if (!Files.exists(Paths.get(OUTLET_FILE))) {
                writeText(OUTLET_FILE,
                        "outletCode,outletName\n" +
                        "C60,KLCC\n" +
                        "MV1,MidValley\n");
            }
            if (!Files.exists(Paths.get(MODEL_FILE))) {
                writeText(MODEL_FILE,
                        "model,modelName,unitPrice\n" +
                        "DW2300-1,Daniel Watch 2300-1,399\n" +
                        "DW2300-2,Daniel Watch 2300-2,349\n");
            }
            if (!Files.exists(Paths.get(EMP_FILE))) {
                writeText(EMP_FILE,
                        "employeeId,employeeName,role,password,outletCode\n" +
                        "M0001,Store Manager,Manager,admin123,C60\n" +
                        "C6001,Tan Guan Han,Full-time,a2b1c0,C60\n");
            }
            if (!Files.exists(Paths.get(STOCK_FILE))) {
                // Fill with qty=1 for every outlet x model
                ArrayList<String> outletCodes = loadOutletCodes();
                ArrayList<String> modelCodes = loadModelCodes();

                StringBuilder sb = new StringBuilder();
                sb.append("outletCode,model,qty\n");
                for (int i = 0; i < outletCodes.size(); i++) {
                    for (int j = 0; j < modelCodes.size(); j++) {
                        sb.append(outletCodes.get(i)).append(",")
                          .append(modelCodes.get(j)).append(",")
                          .append("1\n");
                    }
                }
                writeText(STOCK_FILE, sb.toString());
            }
            if (!Files.exists(Paths.get(ATTEND_FILE))) {
                writeText(ATTEND_FILE, "employeeId,date,clockIn,clockOut\n");
            }
            if (!Files.exists(Paths.get(SALES_FILE))) {
                writeText(SALES_FILE, "saleId,datetime,outletCode,employeeId,employeeName,customerName,method,total,items\n");
            }
        } catch (Exception e) {
            System.out.println("Bootstrap failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public static ArrayList<String> loadOutletCodes() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String[]> rows = readCsv(OUTLET_FILE);
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i).length >= 1) list.add(rows.get(i)[0]);
        }
        return list;
    }

    public static ArrayList<String> loadModelCodes() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String[]> rows = readCsv(MODEL_FILE);
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i).length >= 1) list.add(rows.get(i)[0]);
        }
        return list;
    }

    public static void writeText(String path, String content) {
        try {
            Files.writeString(Paths.get(path), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Write failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void appendText(String path, String content) {
        try {
            Files.writeString(Paths.get(path), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Append failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public static ArrayList<String[]> readCsv(String path) {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line;
            while ((line = br.readLine()) != null) rows.add(parseCsvLine(line));
        } catch (IOException e) {
            rows.add(new String[]{}); // fallback
        }
        return rows;
    }

    // Basic CSV parsing with quotes
    public static String[] parseCsvLine(String line) {
        ArrayList<String> out = new ArrayList<String>();
        StringBuilder cur = new StringBuilder();
        boolean inQ = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQ) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"'); i++;
                    } else inQ = false;
                } else cur.append(c);
            } else {
                if (c == '"') inQ = true;
                else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
        }
        out.add(cur.toString());

        String[] arr = new String[out.size()];
        for (int i = 0; i < out.size(); i++) arr[i] = out.get(i);
        return arr;
    }

    public static String csv(String s) {
        if (s == null) return "";
        boolean need = s.contains(",") || s.contains("\"") || s.contains("\n");
        if (!need) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    public static int toInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    public static double toDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0; }
    }

    public static String money(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}


---

src/util/DateTimeUtil.java

package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(LocalDate d) { return d.format(DATE); }
    public static String formatTime(LocalDateTime dt) { return dt.toLocalTime().format(TIME); }
    public static String formatTimestamp(LocalDateTime dt) { return dt.format(TS); }

    public static LocalDate parseDate(String s) { return LocalDate.parse(s.trim(), DATE); }
    public static LocalDateTime parseTimestamp(String s) { return LocalDateTime.parse(s.trim(), TS); }
}


---

Compile & Run

From the folder that contains src/:

javac -d out src/Main.java src/model/*.java src/manager/*.java src/util/*.java
java -cp out Main


---

If you paste your exact CSV headers/content from your lecturer’s provided files, I can adjust FileManager.bootstrapIfMissing() and the loaders so it matches 100% (still using only prerequisites).*/