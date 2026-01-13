package services;

import models.Outlet;
import utils.CSVUtils;
import java.util.ArrayList;
import java.util.List;

public class OutletService {

    private static final String FILE = "data/outlets.csv";
    private List<Outlet> outlets = new ArrayList<>();

    public OutletService() {
        load();
    }

    private void load() {
        List<String[]> rows = CSVUtils.readAll(FILE);
        for (int i = 1; i < rows.size(); i++) { // skip header
            String[] r = rows.get(i);
            outlets.add(new Outlet(r[0], r[1]));
        }
    }

    // Linear search (as required by project)
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

    public void displayAllOutlets() {
        for (Outlet o : outlets) {
            System.out.println(o.getCode() + " - " + o.getName());
        }
    }
}