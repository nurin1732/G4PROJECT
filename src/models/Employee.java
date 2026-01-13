package models;

public class Employee {
    private String id;
    private String name;
    private String role;
    private String password;
    private String outletCode;

    public Employee(String id, String name, String role, String password, String outlet) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
        this.outletCode = outlet;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public String getOutlet() { return outletCode; }
}