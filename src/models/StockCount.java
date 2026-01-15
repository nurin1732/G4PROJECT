package models;
public class StockCount {
    private String date;
    private String time;
    private String type;
    private String model;
    private int counted;
    private int recorded;
    private String employeeId;

    public StockCount(String d, String t, String ty, String m, int c, int r, String e) {
        date = d;
        time = t;
        type = ty;
        model = m;
        counted = c;
        recorded = r;
        employeeId = e;
    }

    public String toCSV() {
        return date + "," + time + "," + type + "," + model + "," +
               counted + "," + recorded + "," + employeeId;
    }
}
