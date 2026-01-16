package services;

import units.Model;
import units.Employee;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockService {

    private final String MODEL_FILE = "data/models.csv";
    private ArrayList<Model> models = new ArrayList<>();
    private OutletService outletSvc; 

    public StockService() {
        outletSvc = new OutletService(); // load outlets once
        loadModels();
    }

    // Load models from CSV
    private void loadModels() {
        models.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int[] stocks = new int[10];
                for (int i = 0; i < 10; i++)
                    stocks[i] = Integer.parseInt(p[i + 2]);
                models.add(new Model(p[0], Double.parseDouble(p[1]), stocks));
            }
        } catch (Exception e) {
            System.out.println("Error loading model file: " + e.getMessage());
        }
    }

    // Save models to CSV
    private void saveModels() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MODEL_FILE))) {
            pw.println("Model,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69");
            for (Model m : models) {
                pw.print(m.getName() + "," + m.getPrice());
                for (int i = 0; i < 10; i++)
                    pw.print("," + m.getStockAt(i));
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving model file: " + e.getMessage());
        }
    }

    // Stock count (does NOT edit CSV)
    public void stockCount(Employee emp, Scanner sc) {
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(tf);

        String type;
        while (true) {
            System.out.print("Enter count type (Morning/Night): ");
            type = sc.nextLine().trim();
            if (type.equalsIgnoreCase("Morning") || type.equalsIgnoreCase("Night")) break;
            System.out.println("Invalid type.");
        }

        int outletIndex = Integer.parseInt(emp.getOutlet().substring(1)) - 60;

        int correct = 0, mismatch = 0;
        ArrayList<String> mismatchModels = new ArrayList<>();

        System.out.println("\n=== " + type + " Stock Count ===");
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);

        for (Model m : models) {
            int counted;
            while (true) {
                System.out.print("Model: " + m.getName() + " – Counted: ");
                try {
                    counted = Integer.parseInt(sc.nextLine());
                    if (counted >= 0) break;
                } catch (Exception e) {}
                System.out.println("Invalid number.");
            }

            int recorded = m.getStockAt(outletIndex);
            System.out.println("Store Record: " + recorded);

            if (counted == recorded) {
                System.out.println("Stock tally correct.");
                correct++;
            } else {
                System.out.println("! Mismatch detected (" + Math.abs(counted - recorded) + " unit difference)");
                mismatch++;
                mismatchModels.add(m.getName());
            }
        }

        System.out.println("\nTotal Models Checked: " + models.size());
        System.out.println("Tally Correct: " + correct);
        System.out.println("Mismatches: " + mismatch);
        System.out.println(type + " stock count completed.");

        if (mismatch > 0) {
            System.out.println("Warning: Please verify stock for the following models:");
            for (String mm : mismatchModels) System.out.println("- " + mm);
        }
    }

    // Stock IN / OUT with HQ or between outlets
    public void stockInOut(Employee emp, Scanner sc) {

        DateTimeFormatter dateF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm");
        String date = LocalDate.now().format(dateF);
        String time = LocalTime.now().format(timeF);

        String type;
        while (true) {
            System.out.print("Enter transaction type (In/Out): ");
            type = sc.nextLine().trim();
            if (type.equalsIgnoreCase("In") || type.equalsIgnoreCase("Out")) break;
            System.out.println("Invalid type.");
        }

        String fromOutlet = "";
        String toOutlet = "";
        String currentOutlet = emp.getOutlet();

        OutletService outletSvc = new OutletService();

    // Determine FROM / TO outlet
        if (type.equalsIgnoreCase("In")) {

            System.out.println("Is this stock coming from HQ or another outlet?");
            System.out.println("1. HQ (Service Center)");
            System.out.println("2. Another Outlet");
            System.out.print("> ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                fromOutlet = "HQ";
                toOutlet = currentOutlet;
            } else {
                while (true) {
                    System.out.print("Enter source outlet code: ");
                    fromOutlet = sc.nextLine().trim();
                    if (outletSvc.isValidOutlet(fromOutlet)
                            && !fromOutlet.equalsIgnoreCase(currentOutlet)) {
                        break;
                    }
                    System.out.println("Invalid outlet code.");
                }
                toOutlet = currentOutlet;
            }

        } else { // OUT
            fromOutlet = currentOutlet;
            while (true) {
                System.out.print("Enter destination outlet code: ");
                toOutlet = sc.nextLine().trim();
                if (outletSvc.isValidOutlet(toOutlet)
                        && !toOutlet.equalsIgnoreCase(currentOutlet)) {
                    break;
                }
                System.out.println("Invalid outlet code.");
            }
        }

    // Enter models
        ArrayList<String> modelNames = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        int totalQty = 0;

        while (true) {
            System.out.print("Enter Model Name: ");
            String modelName = sc.nextLine().trim();

            Model found = null;
            for (Model m : models) {
                if (m.getName().equalsIgnoreCase(modelName)) {
                    found = m;
                    break;
                }
            }

            if (found == null) {
                System.out.println("Model not found.");
                continue;
            }

            int qty;
            while (true) {
                System.out.print("Enter Quantity: ");
                try {
                    qty = Integer.parseInt(sc.nextLine());
                    if (qty > 0) break;
                } catch (Exception e) {}
                System.out.println("Invalid quantity.");
            }

            int sourceIndex = -1;
            int destIndex = Integer.parseInt(toOutlet.substring(1)) - 60;

            if (!fromOutlet.equals("HQ")) {
                sourceIndex = Integer.parseInt(fromOutlet.substring(1)) - 60;
            }

        // STOCK LOGIC
            if (type.equalsIgnoreCase("In")) {

                if (fromOutlet.equals("HQ")) {
                // IN from HQ
                    found.setStockAt(destIndex,
                            found.getStockAt(destIndex) + qty);
                } else {
                // IN from another outlet
                    if (found.getStockAt(sourceIndex) < qty) {
                        System.out.println("Not enough stock at source outlet.");
                        continue;
                    }
                    found.setStockAt(sourceIndex,
                            found.getStockAt(sourceIndex) - qty);
                    found.setStockAt(destIndex,
                            found.getStockAt(destIndex) + qty);
                }

            } else {
            // OUT to another outlet
                if (found.getStockAt(sourceIndex) < qty) {
                    System.out.println("Not enough stock to transfer.");
                    continue;
                }
                found.setStockAt(sourceIndex,
                        found.getStockAt(sourceIndex) - qty);
                found.setStockAt(destIndex,
                        found.getStockAt(destIndex) + qty);
            }

            modelNames.add(found.getName());
            quantities.add(qty);
            totalQty += qty;

            System.out.print("Add more models? (Y/N): ");
            if (!sc.nextLine().equalsIgnoreCase("Y")) break;
        }

        saveModels();
        loadModels();

    // Receipt
        try {
            File folder = new File("data/StockReceipt");
            if (!folder.exists()) folder.mkdirs();

            String fileName = currentOutlet+ "_receipts_" + date + ".txt";

            PrintWriter pw = new PrintWriter(new FileWriter(
                    new File(folder, fileName), true));

            pw.println("=== Stock " + type.toUpperCase() + " ===");
            pw.println("Date: " + date);
            pw.println("Time: " + time);
            pw.println("From: " + fromOutlet);
            pw.println("To: " + toOutlet);
            pw.println("Handled by: " + emp.getName());
            pw.println("Models:");

            for (int i = 0; i < modelNames.size(); i++) {
                pw.println("- " + modelNames.get(i) +
                        " (Quantity: " + quantities.get(i) + ")");
            }

            pw.println("Total Quantity: " + totalQty);
            pw.println("----------------------------\n");
            pw.close();

            System.out.println("Stock transaction completed.");
            System.out.println("Receipt: data/StockReceipt/" + fileName);

        } catch (IOException e) {
            System.out.println("Error generating receipt.");
        }
    }
    
    //methods used by sale system
    public void save() {
        saveModels();
        loadModels();
    }
    
    public Model findModel(String name) {
        for (Model m : models) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
    
    // methods used by search function
    public List<Model> getModels() {
        return models;
    }
}