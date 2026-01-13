package services;

import models.*;
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

    // =============================
    // RECORD SALE
    // =============================
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
                        break; // exit quantity loop back to model input
                    }

                    break;
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

    // =============================
    // SAVE SALE
    // =============================
    private void saveSale(Sale sale) {
        File f = new File(SALES_FILE);
        boolean writeHeader = false;

        try {
            if (!f.exists() || f.length() == 0) writeHeader = true;

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

    // =============================
    // GENERATE RECEIPT
    // =============================
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


    // =============================
    // NEW: FILTER & SORT SALES HISTORY
    // =============================
    public void viewSalesHistory(Employee emp, Scanner sc) {
        System.out.println("\n=== Filter & Sort Sales History ===");

        // Select outlet
        System.out.println("1. View own outlet");
        System.out.println("2. View all outlets");
        System.out.print("> ");
        String outletChoice = sc.nextLine().trim();
        boolean allOutlets = outletChoice.equals("2");

        // Input date range
        LocalDate startDate, endDate;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            System.out.print("Enter start date (yyyy-MM-dd): ");
            startDate = LocalDate.parse(sc.nextLine().trim(), df);

            System.out.print("Enter end date (yyyy-MM-dd): ");
            endDate = LocalDate.parse(sc.nextLine().trim(), df);

            if (endDate.isBefore(startDate)) {
                System.out.println("End date cannot be before start date.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return;
        }

        // Sorting options
        System.out.println("\nSort by:");
        System.out.println("1. Date Ascending");
        System.out.println("2. Date Descending");
        System.out.println("3. Amount Ascending");
        System.out.println("4. Amount Descending");
        System.out.println("5. Customer Name A-Z");
        System.out.println("6. Customer Name Z-A");
        System.out.print("> ");
        String sortChoice = sc.nextLine().trim();

        // Read CSV and filter
        List<String[]> records = new ArrayList<>();
        double totalCumulative = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                for (int i = 0; i < p.length; i++) p[i] = p[i].replace("\"", "").trim();

                String dateStr = p[0];
                String outlet = p[2];
                String totalStr = p[6];

                LocalDate date;
                double total;
                try {
                    date = LocalDate.parse(dateStr, df);
                    total = Double.parseDouble(totalStr);
                } catch (Exception e) {
                    continue;
                }

                if (!allOutlets && !outlet.equalsIgnoreCase(emp.getOutlet())) continue;
                if (date.isBefore(startDate) || date.isAfter(endDate)) continue;

                records.add(p);
                totalCumulative += total;
            }
        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
            return;
        }

        if (records.isEmpty()) {
            System.out.println("No sales records found for the selected range.");
            return;
        }

        // Sorting
        records.sort((a, b) -> {
            try {
                switch (sortChoice) {
                    case "1": return LocalDate.parse(a[0], df).compareTo(LocalDate.parse(b[0], df));
                    case "2": return LocalDate.parse(b[0], df).compareTo(LocalDate.parse(a[0], df));
                    case "3": return Double.compare(Double.parseDouble(a[6]), Double.parseDouble(b[6]));
                    case "4": return Double.compare(Double.parseDouble(b[6]), Double.parseDouble(a[6]));
                    case "5": return a[4].compareToIgnoreCase(b[4]);
                    case "6": return b[4].compareToIgnoreCase(a[4]);
                    default: return 0;
                }
            } catch (Exception e) {
                return 0;
            }
        });

        // Display
        System.out.printf("\n%-12s %-6s %-8s %-30s %-15s %-8s %-12s\n",
                "Date", "Time", "Outlet", "Employee", "Customer", "Total", "Method");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (String[] r : records) {
            System.out.printf("%-12s %-6s %-8s %-30s %-15s RM%-7s %-12s\n",
                    r[0], r[1], r[2], r[3], r[4], r[6], r[7]);
        }

        System.out.printf("\nTotal cumulative sales in range: RM%.2f\n", totalCumulative);
    }
}