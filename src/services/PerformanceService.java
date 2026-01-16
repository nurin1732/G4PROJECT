package services;

import units.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceService {

    private final String SALES_FILE = "data/sale.csv";

    public void viewEmployeePerformance(Employee manager, java.util.Scanner sc) {

        System.out.println("\n=== Employee Performance Report ===");

        System.out.println("1. View Own Outlet");
        System.out.println("2. View All Outlets");
        System.out.print("> ");
        String input = sc.nextLine().trim();

        int outletChoice;
        if (input.equals("1")) {
            outletChoice = 1;
        } else if (input.equals("2")) {
            outletChoice = 2;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter month (yyyy-MM): ");
        String month = sc.nextLine().trim();
        if (!month.matches("\\d{4}-\\d{2}")) {
            System.out.println("Invalid month format.");
            return;
        }

        generatePerformanceReport(manager, outletChoice, month);
    }

    private void generatePerformanceReport(Employee manager, int outletChoice, String selectedMonth) {

        List<String> employeeNames = new ArrayList<>();
        List<Double> totalSalesList = new ArrayList<>();
        List<Integer> transactionCounts = new ArrayList<>();
        List<String> employeeOutlets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {

                String[] p = line.split(",", -1);
                for (int i = 0; i < p.length; i++)
                    p[i] = p[i].replace("\"", "").trim();

                String date = p[0];           // yyyy-MM-dd
                String outlet = p[2];         // C60, C61, etc
                String employee = p[3];       // Employee Name
                String totalStr = p[6];       // Total amount

                // Skip managers in calculations
                if (employee.equalsIgnoreCase(manager.getName())) continue; 

                // Outlet filter
                if (outletChoice == 1 && !outlet.equals(manager.getOutlet())) continue;

                // Month filter
                if (!date.startsWith(selectedMonth)) continue;

                double total = 0;
                try {
                    total = Double.parseDouble(totalStr);
                } catch (Exception ignored) {}

                // Check if employee already in list
                int idx = employeeNames.indexOf(employee);
                if (idx == -1) {
                    employeeNames.add(employee);
                    totalSalesList.add(total);
                    transactionCounts.add(1);
                    employeeOutlets.add(outlet);
                } else {
                    totalSalesList.set(idx, totalSalesList.get(idx) + total);
                    transactionCounts.set(idx, transactionCounts.get(idx) + 1);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
            return;
        }

        if (employeeNames.isEmpty()) {
            System.out.println("No sales records found for this month.");
            return;
        }

        // Sort by total sales descending
        for (int i = 0; i < totalSalesList.size() - 1; i++) {
            for (int j = i + 1; j < totalSalesList.size(); j++) {
                if (totalSalesList.get(j) > totalSalesList.get(i)) {
                    // swap totals
                    double tempTotal = totalSalesList.get(i);
                    totalSalesList.set(i, totalSalesList.get(j));
                    totalSalesList.set(j, tempTotal);

                    // swap counts
                    int tempCount = transactionCounts.get(i);
                    transactionCounts.set(i, transactionCounts.get(j));
                    transactionCounts.set(j, tempCount);

                    // swap names
                    String tempName = employeeNames.get(i);
                    employeeNames.set(i, employeeNames.get(j));
                    employeeNames.set(j, tempName);

                    // swap outlets
                    String tempOutlet = employeeOutlets.get(i);
                    employeeOutlets.set(i, employeeOutlets.get(j));
                    employeeOutlets.set(j, tempOutlet);
                }
            }
        }

        // Display
        System.out.printf("\n%-30s %-8s %-12s %-8s\n", "Employee", "Outlet", "Transactions", "Total Sales");
        System.out.println("------------------------------------------------------------");

        for (int i = 0; i < employeeNames.size(); i++) {
            System.out.printf("%-30s %-8s %-12d RM%.2f\n",
                    employeeNames.get(i),
                    employeeOutlets.get(i),
                    transactionCounts.get(i),
                    totalSalesList.get(i));
        }
    }
}