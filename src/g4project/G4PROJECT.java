package g4project;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class G4PROJECT {
    
    public static class stockManager{
        
        public static void displayStockHeader(){
        LocalTime time=LocalTime.now();
        int hour=time.getHour();
        
        if(hour<12){
            System.out.println("=== Morning Stock Count ===");
        }else{
            System.out.println("==== Night Stock Count ===");
        }
    }
        
        public static void displayDateTime(){
            LocalDate date= LocalDate.now();
            LocalTime time=LocalTime.now();
            
            DateTimeFormatter dateFormat= DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormat= DateTimeFormatter.ofPattern("hh:mm a");
            
            System.out.println("Date: " + date.format(dateFormat));
            System.out.println("Time: " + time.format(timeFormat));
        }
    
    
    }
        public static void main(String[] args) {
        stockManager.displayStockHeader();
        stockManager.displayDateTime();
    }
        
    
    
    

}