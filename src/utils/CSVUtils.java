package utils;

import java.io.*;
import java.util.*;

public class CSVUtils {

    public static List<String[]> readAll(String path) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null)
                rows.add(line.split(","));
        } catch (IOException e) {
            System.out.println("Error reading " + path);
        }
        return rows;
    }

    public static void writeAll(String path, List<String[]> rows) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.out.println("Error writing " + path);
        }
    }

    public static void append(String path, String line) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("Error appending " + path);
        }
    }
}