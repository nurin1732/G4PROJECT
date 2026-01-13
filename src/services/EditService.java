package services;

import models.*;
import java.io.*;
import java.util.*;

public class EditService {

    private final StockService stockService;
    private final EmployeeService empService;
    private final String SALES_FILE = "data/sale.csv";

    public EditService(StockService stockService, EmployeeService empService) {
        this.stockService = stockService;
        this.empService = empService;
    }
    
    // ========================
    // WRAPPER: Edit Information
    // ========================
    public void editInformation(Employee emp, Scanner sc) {
        System.out.println("\n=== Edit Menu ===");
        System.out.println("1. Edit Stock Information");
        System.out.println("2. Edit Sales Information");
        System.out.print("> ");
        String editChoice = sc.nextLine().trim();

        switch (editChoice) {
            case "1":
                editStock(emp, sc);
                break;
            case "2":
                editSales(emp, sc);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // ========================
    // EDIT STOCK
    // ========================
    public void editStock(Employee emp, Scanner sc) {
        System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name: ");
        String modelName = sc.nextLine().trim();

        Model model = stockService.findModel(modelName);

        if (model == null) {
            System.out.println("Model not found.");
            return;
        }

        int outletIndex = Integer.parseInt(emp.getOutlet().substring(1)) - 60;
        int currentStock = model.getStockAt(outletIndex);
        System.out.println("Current Stock: " + currentStock);

        int newStock;
        while (true) {
            System.out.print("Enter New Stock Value: ");
            try {
                newStock = Integer.parseInt(sc.nextLine());
                if (newStock >= 0) break;
            } catch (Exception ignored) {}
            System.out.println("Invalid number.");
        }

        System.out.print("Confirm update? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            model.setStockAt(outletIndex, newStock);
            stockService.save();
            System.out.println("Stock information updated successfully.");
        } else {
            System.out.println("Stock update canceled.");
        }
    }

    // ========================
    // EDIT SALES
    // ========================
    public void editSales(Employee emp, Scanner sc) {
        System.out.println("\n=== Edit Sales Information ===");
        System.out.print("Enter Transaction Date (yyyy-MM-dd): ");
        String dateQuery = sc.nextLine().trim();

        System.out.print("Enter Customer Name: ");
        String customerQuery = sc.nextLine().trim().toLowerCase();

        List<String[]> salesData = new ArrayList<>();
        boolean found = false;

        // Load sales CSV
        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {
            String header = br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",", -1);
                for (int i = 0; i < record.length; i++)
                    record[i] = record[i].replaceAll("^\"|\"$", "").trim();

                String recordDate = record[0];
                String outlet = record[2];
                String customer = record[4];

                // Only allow edits for the employee's outlet
                if (!outlet.equalsIgnoreCase(emp.getOutlet())) {
                    salesData.add(record); // keep unchanged
                    continue;
                }

                if (recordDate.equals(dateQuery) && customer.toLowerCase().contains(customerQuery)) {
                    found = true;
                    System.out.println("Sales Record Found:");
                    System.out.println("Model(s) & Quantity: " + record[5]);
                    System.out.println("Total: RM" + record[6]);
                    System.out.println("Transaction Method: " + record[7]);

                    System.out.println("Select number to edit:");
                    System.out.println("1. Customer Name 2. Model/Quantity 3. Total 4. Transaction Method");
                    System.out.print("> ");
                    String choice = sc.nextLine().trim();

                    switch (choice) {
                        case "1":
                            System.out.print("Enter New Customer Name: ");
                            record[4] = sc.nextLine().trim();
                            break;
                        case "2":
                            System.out.print("Enter New Model(s) & Quantity (e.g., DW2300-1 x2;SW2400-4 x1;): ");
                            record[5] = sc.nextLine().trim();
                            break;
                        case "3":
                            System.out.print("Enter New Total: ");
                            record[6] = sc.nextLine().trim();
                            break;
                        case "4":
                            System.out.print("Enter New Transaction Method: ");
                            record[7] = sc.nextLine().trim();
                            break;
                        default:
                            System.out.println("Invalid choice. Skipping edit.");
                    }

                    System.out.print("Confirm update? (Y/N): ");
                    String confirm = sc.nextLine().trim();
                    if (confirm.equalsIgnoreCase("Y")) {
                        System.out.println("Sales information updated successfully.");
                    } else {
                        System.out.println("Update canceled.");
                    }
                }

                salesData.add(record);
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("No sales record found for that date and customer at your outlet.");
            return;
        }

        // Save updated CSV (only quote date)
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE))) {
            pw.println("Date,Time,Outlet,Employee,Customer,Items,Total,Method,Status");
            for (String[] record : salesData) {
                String line = "\"" + record[0] + "\""; // only date in quotes
                for (int i = 1; i < record.length; i++)
                    line += "," + record[i];
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving sales file: " + e.getMessage());
        }
    }
}