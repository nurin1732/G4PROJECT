package units;

import java.util.List;

public class Sale {

    private String date;
    private String time;
    private String outlet;
    private String employee;
    private String customer;
    private List<SaleItem> items;
    private double total;
    private String method;
    private String status;

    public Sale(String date, String time, String outlet, String employee,
                String customer, List<SaleItem> items,
                double total, String method) {

        this.date = date;
        this.time = time;
        this.outlet = outlet;
        this.employee = employee;
        this.customer = customer;
        this.items = items;
        this.total = total;
        this.method = method;
        this.status = "Transaction verified";
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getOutlet() { return outlet; }
    public String getEmployee() { return employee; }
    public String getCustomer() { return customer; }
    public double getTotal() { return total; }
    public String getMethod() { return method; }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (SaleItem i : items)
            sb.append(i.getModel()).append(" x")
              .append(i.getQuantity()).append(";");

        return "\"" + date + "\"," +time + "," +outlet + "," +employee + "," +customer + "," +sb + "," +total + "," +method + "," +status;
    }
}