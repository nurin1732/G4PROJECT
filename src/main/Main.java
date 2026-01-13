package main;

import services.*;
import models.Employee;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        EmployeeService empSvc = new EmployeeService();

        while (true) {
            System.out.println("=== Employee Login ===");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine().trim();

            System.out.print("Enter Password: ");
            String pass = sc.nextLine().trim();

            Employee logged = empSvc.login(id, pass);

            if (logged == null) {
                System.out.println("Login Failed: Invalid User ID or Password.\n");
                continue;
            }

            System.out.println("\nLogin Successful!");
            System.out.println("Welcome, " + logged.getName() +
                    " (" + logged.getId() + ")");

            boolean logout = false;
            
            StockService stockService = new StockService();
            SalesService salesService = new SalesService(stockService);
            SearchService searchService = new SearchService(stockService);
            EditService editSvc = new EditService(stockService,empSvc);
            ReportService reportSvc = new ReportService();
            PerformanceService PerformanceSvc = new PerformanceService();

            while (!logout) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. Clock In / Clock Out");
                System.out.println("2. Stock Count");
                System.out.println("3. Stock In / Stock Out");
                System.out.println("4. Sales");
                System.out.println("5. Sales History");
                System.out.println("6. Search Information");
                System.out.println("7. Edit Information");
                System.out.println("8. View Sales Report");
                System.out.println("9. Register New Employee");
                System.out.println("10. View Employee Performance");
                System.out.println("0. Logout");
                System.out.print("> ");

                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1":
                        AttendanceService attSvc = new AttendanceService();
                        attSvc.clock(logged);
                        break;
                        
                    case "2":
                        new StockService().stockCount(logged, sc);
                        break;
                        
                    case "3":
                        stockService.stockInOut(logged, sc);
                        break;
                        
                    case "4":
                        salesService.recordSale(logged, sc);
                        break;
                        
                    case "5":
                        new SalesService(stockService).viewSalesHistory(logged, sc);
                        break;
                        
                    case "6":
                        new SearchService(stockService).search(logged, sc); 
                        break;
                        
                    case "7":
                        editSvc.editInformation(logged, sc);
                        break;
                        
                    case "8": 
                        reportSvc.viewSalesReport(logged, sc);
                        break;

                    case "9":
                        if (!logged.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("Only manager can register new employees.");
                        } else {
                            empSvc.registerEmployee(sc);
                        }
                        break;
                        
                    case "10":
                        if (!logged.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("Only manager can view employee performance.");
                            return;
                        } 
                        PerformanceSvc.viewEmployeePerformance(logged, sc);
                        break;

                    case "0":  
                        logout = true;
                        System.out.println("Logged out.\n");
                    break;
                        
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }
}