package stefanowicz.kacper.menu;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.help.LocalDateSerializer;
import stefanowicz.kacper.service.OrdersService;
import stefanowicz.kacper.service.util.UserDataService;
import stefanowicz.kacper.exception.AppException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MenuService {
    private OrdersService ordersService;
    private final String CUSTOMERS_FILENAME;
    private final String PRODUCTS_FILENAME;
    private final String ORDERS_FILENAME;

    public MenuService(String customersFilename, String productsFilename, String ordersFilename){
        this.CUSTOMERS_FILENAME = customersFilename;
        this.PRODUCTS_FILENAME = productsFilename;
        this.ORDERS_FILENAME = ordersFilename;
    }

    public void insertDataOption(){
        int option;
        do{
            try{
                option = printInsertDataOptions();
                switch (option){
                    case 1 -> {
                        var dataMenu = new DataMenuService();
                        dataMenu.dataMenu();
                        ordersService = new OrdersService(CUSTOMERS_FILENAME, PRODUCTS_FILENAME, ORDERS_FILENAME);
                        mainMenu();
                    }
                    case 2 -> {
                        ordersService = new OrdersService();
                        mainMenu();
                    }
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        return;
                    }
                    default -> System.out.println("No such option");
                }
            }
            catch (AppException e){
                System.out.println("---------------------------------------");
                System.out.println("------------------ EXCEPTION ----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------------");
            }

        }while(true);

    }

    private int printInsertDataOptions(){
        System.out.println("1. Load data from file.");
        System.out.println("2. Insert data from user.");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose insert data option.");
    }

    public void mainMenu(){
        int option;
        do{
            try{
                option = printMenu();
                switch (option){
                    case 1 -> option1();
                    case 2 -> option2();
                    case 3 -> option3();
                    case 4 -> option4();
                    case 5 -> option5();
                    case 6 -> option6();
                    case 7 -> option7();
                    case 8 -> option8();
                    case 9 -> option9();
                    case 10 -> option10();
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day!");
                        System.exit(0);
                    }
                }
            }
            catch (AppException e){
                System.out.println("---------------------------------------");
                System.out.println("------------------ EXCEPTION ----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------------");
            }
        }while(true);
    }

    private int printMenu(){
        System.out.println("1. Show average product price in given date range.");
        System.out.println("2. Most expensive product in each category.");
        System.out.println("3. Send email for each customers with products they bought.");
        System.out.println("4. Date with most and least orders.");
        System.out.println("5. Customer that spent most money on shopping.");
        System.out.println("6. Price of all orders after discount.");
        System.out.println("7. Number of clients that always have ordered quantity of product equal X.");
        System.out.println("8. Most popular category.");
        System.out.println("9. Qauntity of orders grouped by month.");
        System.out.println("10. Most popular category in each month.");
        System.out.println("0.Exit");
        return UserDataService.getInt("Choose an option: ");
    }



    private void option1(){
        BigDecimal avgPrice = ordersService.avgPriceInDateRange(
                UserDataService.getDate("From date: "),
                UserDataService.getDate("To date: "));
        System.out.println("Average product price in given date range-> " + avgPrice);
    }

    private void option2(){
        var mostExpensiveProductByCategory = ordersService.maxPriceGroupedByCategory();
        System.out.println(toJson(mostExpensiveProductByCategory));
    }

    private void option3(){
        ordersService.customersProducts()
                .forEach((customer, products) -> ordersService.sendEmail(customer.getEmail(), products));
    }

    private void option4(){
        var mostOrders = ordersService.mostOrdersDate();
        var leastOrders = ordersService.leastOrdersDate();
        System.out.println("Most orders");
        System.out.println(toJson(mostOrders));
        System.out.println("Least orders");
        System.out.println(toJson(leastOrders));
    }

    private void option5(){
        var mostMoneyCustomer = ordersService.spentMostMoney();
        System.out.println(toJson(mostMoneyCustomer));
    }

    private void option6(){
        BigDecimal afterDiscount = ordersService.ordersAfterDiscount();
        System.out.println("Price of all orders after dicsount: " + afterDiscount);
    }

    private void option7(){
        int quantity = UserDataService.getInt("Quantity: ");
        System.out.println("Number of clients that always have ordered quantity of product equal at least " + quantity + ": " +
                ordersService.orderedAtLeastProducts( quantity ));
    }

    private void option8(){
        var categoryEntry = ordersService.mostPopularCategory();
        System.out.println("Category: " + categoryEntry.getKey() + " -> Orders of this category: " + categoryEntry.getValue());
    }

    private void option9(){
        var montWithOrders = ordersService.monthWithOrdersQuantity();
        System.out.println(toJson(montWithOrders));
    }

    private void option10(){
        var mostPopular = ordersService.mostPopularCategoryInMonth();
        System.out.println(toJson(mostPopular));
    }

    private static <T> String toJson(T t){
        try{
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).setPrettyPrinting().create();
            return gson.toJson(t);
        }
        catch (Exception e){
            throw new AppException("to json conversion exception in menu service");
        }
    }
}
