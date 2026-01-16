package services;

import units.Sale;
import units.Employee;
import units.SaleItem;
import units.Model;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SalesService {

    private final String SALES_FILE = "data/sale.csv";
    private final StockService stockService;

    public SalesService(StockService stockService) {
        this.stockService = stockService;
        initFile();
    }

    private void initFile() {
        File f = new File(SALES_FILE);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Date,Time,Outlet,Employee,Customer,Items,Total,Method,Status");
            } catch (IOException e) {
                System.out.println("Error initializing sales file.");
            }
        }
    }

    public void recordSale(Employee emp, Scanner sc) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        String date = LocalDate.now().format(df);
        String time = LocalTime.now().format(tf);

        System.out.println("\n=== Record New Sale ===");
        System.out.print("Customer Name: ");
        String customer = sc.nextLine().trim();

        List<SaleItem> items = new ArrayList<>();
        double total = 0;

        while (true) {
            System.out.print("Enter Model: ");
            String model = sc.nextLine().trim();

            Model m = stockService.findModel(model);
            if (m == null) {
                System.out.println("Model not found.");
                continue;
            }

            int outletIndex = Integer.parseInt(emp.getOutlet().substring(1)) - 60;

            int qty = 0;
            boolean validItem = true;

            while (true) {
                System.out.print("Enter Quantity: ");
                try {
                    qty = Integer.parseInt(sc.nextLine());
                    if (qty <= 0) {
                        System.out.println("Quantity must be greater than 0.");
                        continue;
                    }
                    if (qty > m.getStockAt(outletIndex)) {
                        System.out.println("Invalid or insufficient stock.");
                        validItem = false;
                        break;
                    }
                    break; // valid quantity
                } catch (Exception e) {
                    System.out.println("Invalid number.");
                }
            }
            if (!validItem) continue;

            m.setStockAt(outletIndex, m.getStockAt(outletIndex) - qty);
            stockService.save();

            SaleItem item = new SaleItem(m.getName(), qty, m.getPrice());
            items.add(item);
            total += item.getSubtotal();

            System.out.print("Add more items? (Y/N): ");
            if (!sc.nextLine().equalsIgnoreCase("Y")) break;
        }

        String method;
        while (true) {
            System.out.print("Enter transaction method: ");
            method = sc.nextLine().trim();
            if (!method.isEmpty()) break;
        }

        Sale sale = new Sale(date, time, emp.getOutlet(), emp.getName(), customer, items, total, method);
        saveSale(sale);
        generateReceipt(emp, sale, items);

        System.out.println("Transaction successful.");
        System.out.println("Total: RM" + total);
    }

    private void saveSale(Sale sale) {
        File f = new File(SALES_FILE);
        boolean writeHeader = false;

        try {
            if (!f.exists() || f.length() == 0) {
                f.createNewFile();
                writeHeader = true;
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
                if (writeHeader) {
                    pw.println("Date,Time,Outlet,Employee,Customer,Items,Total,Method,Status");
                }
                pw.println(sale.toCSV());
            }

        } catch (IOException e) {
            System.out.println("Error writing sales file: " + e.getMessage());
        }
    }

    private void generateReceipt(Employee emp, Sale sale, List<SaleItem> items) {
        String folderPath = "data/SalesReceipt";
        new File(folderPath).mkdirs();

        String fileName = emp.getOutlet() + "_sales_" + sale.getDate() + ".txt";
        File file = new File(folderPath, fileName);

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println("=== Sale Receipt ===");
            pw.println("Date: " + sale.getDate());
            pw.println("Time: " + sale.getTime());
            pw.println("Customer: " + sale.getCustomer());
            pw.println("Employee: " + sale.getEmployee());
            pw.println("Items:");
            for (SaleItem i : items)
                pw.println("- " + i.getModel() + " x" + i.getQuantity() + " RM" + i.getSubtotal());
            pw.println("Total: RM" + sale.getTotal());
            pw.println("Method: " + sale.getMethod());
            pw.println("----------------------\n");
        } catch (IOException e) {
            System.out.println("Error writing sales receipt.");
        }
    }

    public void viewSalesHistory(Employee emp, Scanner sc) {

    List<String[]> records = new ArrayList<>();
    double cumulativeTotal = 0;

    // ===== Outlet filter =====
    System.out.println("\n=== Sales History ===");
    System.out.println("1. View own outlet");
    System.out.println("2. View all outlets");
    System.out.print("Choose option: ");
    int outletChoice = Integer.parseInt(sc.nextLine());

    // ===== Date filter =====
    System.out.println("\n=== Date Filter ===");
    System.out.println("1. Today");
    System.out.println("2. Specific day");
    System.out.println("3. Range of days");
    System.out.print("Choose option: ");
    int dateChoice = Integer.parseInt(sc.nextLine());

    LocalDate startDate = null;
    LocalDate endDate = null;

        switch (dateChoice) {
            case 1:
                startDate = endDate = LocalDate.now();
                break;
            case 2:
                System.out.print("Enter date (yyyy-MM-dd): ");
                startDate = endDate = LocalDate.parse(sc.nextLine());
                break;
            case 3:
                System.out.print("Start date (yyyy-MM-dd): ");
                startDate = LocalDate.parse(sc.nextLine());
                System.out.print("End date (yyyy-MM-dd): ");
                endDate = LocalDate.parse(sc.nextLine());
                break;
            default:
                break;
        }

    // ===== Read & filter file =====
    try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {
        br.readLine(); // skip header
        String line;

        while ((line = br.readLine()) != null) {
            String[] p = line.split(",", -1);
            for (int i = 0; i < p.length; i++)
                p[i] = p[i].replace("\"", "").trim();

            LocalDate saleDate = LocalDate.parse(p[0]);
            String outlet = p[2];

            // Outlet filter
            if (outletChoice == 1 && !outlet.equals(emp.getOutlet())) continue;

            // Date filter
            if (saleDate.isBefore(startDate) || saleDate.isAfter(endDate)) continue;

            records.add(p);
            cumulativeTotal += Double.parseDouble(p[6]);
        }

    } catch (IOException e) {
        System.out.println("Error reading sales file.");
        return;
    }

    if (records.isEmpty()) {
        System.out.println("No records found.");
        return;
    }

    // ===== Sorting =====
    System.out.println("\n=== Sort Options ===");
    System.out.println("1. Date Ascending");
    System.out.println("2. Date Descending");
    System.out.println("3. Amount Low → High");
    System.out.println("4. Amount High → Low");
    System.out.println("5. Customer A → Z");
    System.out.print("Choose option: ");
    int sortChoice = Integer.parseInt(sc.nextLine());

    switch (sortChoice) {
        case 1 -> records.sort(Comparator.comparing(r -> LocalDate.parse(r[0])));
        case 2 -> records.sort((a, b) -> LocalDate.parse(b[0]).compareTo(LocalDate.parse(a[0])));
        case 3 -> records.sort(Comparator.comparingDouble(r -> Double.parseDouble(r[6])));
        case 4 -> records.sort((a, b) -> Double.compare(Double.parseDouble(b[6]), Double.parseDouble(a[6])));
        case 5 -> records.sort(Comparator.comparing(r -> r[4]));
    }

    // ===== Display table =====
        System.out.printf("\n%-12s %-6s %-30s %-15s RM%-8s %-12s\n", "Date", "Time", "Employee", "Customer", "Total", "Method");
        System.out.println("-----------------------------------------------------------------------------------");
        for (String[] r : records) {
            System.out.printf("%-12s %-6s %-30s %-15s RM%-8s %-12s\n",
                    r[0], r[1], r[3], r[4], r[6], r[7]);
        }

        System.out.println("\nTotal Sales: RM" + cumulativeTotal);
    }
}