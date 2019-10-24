package stefanowicz.kacper.service.util;

import stefanowicz.kacper.model.enums.Category;
import stefanowicz.kacper.exception.AppException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public final class UserDataService {

    private UserDataService(){};

    private static Scanner sc = new Scanner(System.in);

    public static String getString( String message ){
        System.out.println(message);
        return sc.nextLine();
    }

    public static int getInt( String message ){
        System.out.println(message);

        String text = sc.nextLine();

        if( !text.matches("\\d+" )){
            throw new AppException("This is not int value!");
        }

        return Integer.parseInt(text);
    }

    public static BigDecimal getBigDecimal(String message){
        System.out.println(message);

        String text = sc.nextLine();
        if ( !text.matches("(\\d+\\.)?\\d+") && !text.matches("\\d+")){
            throw new AppException("This is not BigDecimal value");
        }
        return new BigDecimal(text);
    }

    public static Category getCategory(){
        var counter = new AtomicInteger(0);
        Arrays
                .stream(Category.values())
                .forEach(category -> System.out.println(counter.incrementAndGet() + ". " + category));
        int choice = getInt("Choose category: ");

        if( choice < 1 || choice > Category.values().length){
            throw new AppException("No category type with given number!");
        }
        return Category.values()[choice - 1];
    }

    public static LocalDate getDate(String message){
        System.out.println(message);
        int days = getInt("Day: ");
        int month = getInt("Month: ");
        int year = getInt("Year: ");

        try{
            return LocalDate.of(year, month, days);
        }
        catch (Exception e){
            throw new AppException("Date input exception - " + e.getMessage());
        }
    }
    public static void close(){
        if( sc != null ){
            sc.close();
            sc = null;
        }
    }
}
