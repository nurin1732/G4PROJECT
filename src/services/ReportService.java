package services;

import units.Employee;
import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;

public class ReportService {

    private final String SALES_FILE = "data/sale.csv";

    public void viewSalesReport(Employee emp, Scanner sc) {

        while (true) {
            System.out.println("\n=== Sales Report ===");
            System.out.println("1. Daily Sales Report");
            System.out.println("2. Weekly Sales Report");
            System.out.println("3. Monthly Sales Report");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = sc.nextLine().trim();
            if (choice.equals("0")) return;

            if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                System.out.println("Invalid choice.");
                continue;
            }

            System.out.println("\nView for:");
            System.out.println("1. My Outlet Only");
            System.out.println("2. All Outlets");
            System.out.print("> ");
            String outletChoice = sc.nextLine().trim();

            boolean allOutlets;
            if (outletChoice.equals("1")) {
                allOutlets = false;
            } else if (outletChoice.equals("2")) {
                allOutlets = true;
            } else {
                System.out.println("Invalid choice.");
                continue;
            }

            generateReport(emp, choice, allOutlets);
        }
    }

    // CORE REPORT LOGIC
    private void generateReport(Employee emp, String mode, boolean allOutlets) {

        String today = LocalDate.now().toString();
        String weekPrefix = today.substring(0, 7);   // yyyy-MM
        String monthPrefix = today.substring(0, 7);  // yyyy-MM

        double totalSales = 0;

        String[] models = new String[100];
        int[] quantities = new int[100];
        int modelCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {

                String[] p = line.split(",", -1);
                for (int i = 0; i < p.length; i++)
                    p[i] = p[i].replace("\"", "").trim();

                String date = p[0];
                String outlet = p[2];
                String items = p[5];
                double total = Double.parseDouble(p[6]);

                // Outlet filter
                if (!allOutlets && !outlet.equals(emp.getOutlet())) continue;

                // Time filter
                boolean match = false;
                if (mode.equals("1") && date.equals(today)) match = true;
                if (mode.equals("2") && date.startsWith(weekPrefix)) match = true;
                if (mode.equals("3") && date.startsWith(monthPrefix)) match = true;

                if (!match) continue;

                totalSales += total;

                // Parse sold items
                String[] itemArr = items.split(";");
                for (String it : itemArr) {
                    if (it.isEmpty()) continue;

                    String[] part = it.trim().split(" x");
                    String model = part[0];
                    int qty = Integer.parseInt(part[1]);

                    int idx = findModelIndex(models, modelCount, model);
                    if (idx == -1) {
                        models[modelCount] = model;
                        quantities[modelCount] = qty;
                        modelCount++;
                    } else {
                        quantities[idx] += qty;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading sales file.");
            return;
        }

        // OUTPUT
        System.out.println("\n----------------------------");
        System.out.println("Total Sales: RM" + totalSales);

        if (modelCount == 0) {
            System.out.println("No sales data found.");
            return;
        }

        // Sort by quantity (descending)
        for (int i = 0; i < modelCount - 1; i++) {
            for (int j = i + 1; j < modelCount; j++) {
                if (quantities[j] > quantities[i]) {
                    int tq = quantities[i];
                    quantities[i] = quantities[j];
                    quantities[j] = tq;

                    String tm = models[i];
                    models[i] = models[j];
                    models[j] = tm;
                }
            }
        }

        System.out.println("\nTop 3 Best-Selling Products:");
        for (int i = 0; i < modelCount && i < 3; i++) {
            System.out.println((i + 1) + ". " + models[i] + " (" + quantities[i] + " units)");
        }
        System.out.println("----------------------------");
    }

    // HELPER
    private int findModelIndex(String[] models, int count, String model) {
        for (int i = 0; i < count; i++) {
            if (models[i].equals(model)) return i;
        }
        return -1;
    }
    
    
   public double getTodayTotalSales(Employee emp, boolean allOutlets) {

        String today = LocalDate.now().toString();
        double totalSales = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {

                String[] p = line.split(",", -1);
                for (int i = 0; i < p.length; i++)
                    p[i] = p[i].replace("\"", "").trim();

                String date = p[0];
                String outlet = p[2];
                double total = Double.parseDouble(p[6]);

                if (!date.equals(today)) continue;
                if (!allOutlets && !outlet.equals(emp.getOutlet())) continue;

                totalSales += total;
            }

        } catch (Exception e) {
            System.out.println("Error calculating today's sales.");
        }

        return totalSales;
    } 
}