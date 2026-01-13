package models;

public class Outlet {
    private String code;
    private String name;

    public Outlet(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}