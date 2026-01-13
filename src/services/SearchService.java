package services;

import models.*;
import java.io.*;
import java.util.*;

public class SearchService {

    private final StockService stockService;
    private final String SALES_FILE = "data/sale.csv";
    private final String EMPLOYEE_FILE = "data/employees.csv";

    public SearchService(StockService stockService) {
        this.stockService = stockService;
    }

    public void search(Employee emp,Scanner sc) {
        System.out.println("\n=== Search Menu ===");
        System.out.println("1. Stock Information");
        System.out.println("2. Sales Information");
        System.out.print("> ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1": searchStock(sc); break;
            case "2": searchSales(emp,sc); break;
            default: System.out.println("Invalid choice.");
        }
    }

    // =======================
    // STOCK INFORMATION
    // =======================
    private void searchStock(Scanner sc) {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String query = sc.nextLine().trim().toLowerCase();

        System.out.println("Searching...");
        boolean found = false;

        for (Model m : stockService.getModels()) {
            if (m.getName().toLowerCase().contains(query)) {
                found = true;
                System.out.println("Model: " + m.getName());
                System.out.println("Unit Price: RM" + m.getPrice());
                System.out.println("Stock by Outlet:");

                // Show stock per outlet
                String[] outletNames = { "C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69" };
                for (int i = 0; i < 10; i++) {
                    System.out.print(outletNames[i] + ": " + m.getStockAt(i));
                    if (i < 9) System.out.print("  ");
                }
                System.out.println("\n");
            }
        }

        if (!found) System.out.println("No model found matching '" + query + "'.");
    }

    // =======================
    // SALES INFORMATION
    // =======================
    private void searchSales(Employee emp,Scanner sc) {
        System.out.println("\n=== Search Sales Information ===");
        System.out.println("Search by:");
        System.out.println("1. Date (yyyy-mm-dd)");
        System.out.println("2. Customer Name");
        System.out.println("3. Model Name");
        System.out.print("> ");
        String option = sc.nextLine().trim();

        String query = "";
        switch (option) {
            case "1":
                while (true) {
                    System.out.print("Enter date (yyyy-mm-dd): ");
                    query = sc.nextLine().trim();
                    if (query.matches("\\d{4}-\\d{2}-\\d{2}")) break;
                    System.out.println("Invalid date format.");
                }
                break;
            case "2":
                System.out.print("Enter customer name: ");
                query = sc.nextLine().trim().toLowerCase();
                break;
            case "3":
                System.out.print("Enter model name: ");
                query = sc.nextLine().trim().toLowerCase();
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        System.out.println("Searching...");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                for (int i = 0; i < p.length; i++) p[i] = p[i].replaceAll("^\"|\"$", "").trim();

                String date = p[0];
                String outlet = p[2];
                String employee = p[3];
                String customer = p[4];
                String items = p[5];
                String total = p[6];
                String method = p[7];
                String status = p[8];
                
                // Filter by outlet
                if (!outlet.equalsIgnoreCase(emp.getOutlet())) continue;

                boolean match = false;
                switch (option) {
                    case "1": // search by date
                        match = date.equals(query);
                        break;
                    case "2": // search by customer
                        match = customer.toLowerCase().contains(query);
                        break;
                    case "3": // search by model
                        match = items.toLowerCase().contains(query);
                        break;
                }

                if (match) {
                    found = true;
                    System.out.println("Sales Record Found:");
                    System.out.println("Date: " + date + " Time: " + p[1]);
                    System.out.println("Customer: " + customer);
                    System.out.println("Item(s): " + items.replace(";", "").replace(",", ""));
                    System.out.println("Total: RM" + total);
                    System.out.println("Transaction Method: " + method);
                    System.out.println("Employee: " + employee);
                    System.out.println("Status: " + status + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
        }

        if (!found) System.out.println("No sales record found for your search.");
    }
}