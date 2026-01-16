package main;

import services.*;
import units.Employee;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        EmployeeService empSvc = new EmployeeService();

        while (true) {
            
            System.out.println("====== Employee Login ======");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine().trim();

            System.out.print("Enter Password: ");
            String pass = sc.nextLine().trim();

            Employee loggedIn = empSvc.login(id, pass);

            if (loggedIn == null) {
                System.out.println("Login failed : Invalid User ID or Password\n");
                continue;
            }

            System.out.println("\nLogin Successful!");
            System.out.println("Welcome, " + loggedIn.getName() + " (" + loggedIn.getOutlet() + ")");

            boolean logout = false;
            
            AttendanceService attSvc = new AttendanceService();
            StockService stockSvc = new StockService();
            SalesService salesSvc = new SalesService(stockSvc);
            EditService editSvc = new EditService(stockSvc,empSvc);
            ReportService reportSvc = new ReportService();
            PerformanceService PerformanceSvc = new PerformanceService();
            EmailService emailSvc = new EmailService();

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
                        attSvc.clock(loggedIn);
                        break;
                        
                    case "2":
                        new StockService().stockCount(loggedIn, sc);
                        break;
                        
                    case "3":
                        stockSvc.stockInOut(loggedIn, sc);
                        break;
                        
                    case "4":
                        salesSvc.recordSale(loggedIn, sc);
                        break;
                        
                    case "5":
                        new SalesService(stockSvc).viewSalesHistory(loggedIn, sc);
                        break;
                        
                    case "6":
                        new SearchService(stockSvc).search(loggedIn, sc); 
                        break;
                        
                    case "7":
                        editSvc.editInformation(loggedIn, sc);
                        break;
                        
                    case "8": 
                        reportSvc.viewSalesReport(loggedIn, sc);
                        break;

                    case "9":
                        if (!loggedIn.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("Only manager can register new employees.");
                        } else {
                            empSvc.registerEmployee(sc);
                        }
                        break;
                        
                    case "10":
                        if (!loggedIn.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("Only manager can view employee performance.");
                            return;
                        } 
                        PerformanceSvc.viewEmployeePerformance(loggedIn, sc);
                        break;

                    case "0":  
                        System.out.println("Are you sure you want to log out? (Y/N)");
                        String confirm = sc.nextLine().trim();
                        
                        if(confirm.equalsIgnoreCase("Y")){
                            logout = true;
                                if (loggedIn.getRole().equalsIgnoreCase("Manager")) {
                                    double totalSalesToday = reportSvc.getTodayTotalSales(loggedIn, true);
                                    emailSvc.sendDailySalesReport(loggedIn, totalSalesToday);
                                }
                            System.out.println("Logged out.\n");
                        }else{
                            System.out.println("Log out cancelled.");
                        }
                        break;
                        
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
                        
                    
            
        }
    }
}