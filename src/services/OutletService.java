package services;

import units.Outlet;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OutletService {

    private static final String FILE = "data/outlets.csv";
    private List<Outlet> outlets = new ArrayList<>();

    public OutletService() {
        load();
    }

    private void load() {
        outlets.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] r = line.split(",", -1); // split by comma
                if (r.length >= 2) {
                    outlets.add(new Outlet(r[0].trim(), r[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading outlets file: " + e.getMessage());
        }
    }

    // Linear search 
    public Outlet findByCode(String code) {
        for (Outlet o : outlets) {
            if (o.getCode().equals(code)) {
                return o;
            }
        }
        return null;
    }

    public boolean isValidOutlet(String code) {
        return findByCode(code) != null;
    }
}