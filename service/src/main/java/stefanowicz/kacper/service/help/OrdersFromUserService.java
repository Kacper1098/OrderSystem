package stefanowicz.kacper.service.help;

import stefanowicz.kacper.service.util.UserDataService;
import stefanowicz.kacper.validators.CustomerValidator;
import stefanowicz.kacper.validators.OrderValidator;
import stefanowicz.kacper.validators.ProductValidator;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Order;
import stefanowicz.kacper.model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrdersFromUserService {
    private List<Order> orderList = new ArrayList<>();

    public List<Order> getOrderList(){
        return this.orderList;
    }

    public OrdersFromUserService(){
        orderMenu();
    }

    public void orderMenu(){
        int option;
        do{
            try{
                option = printOptions();

                switch (option){
                    case 1 -> newOrder();
                    case 2 -> {
                        return;
                    }
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        System.exit(0);
                    }
                    default -> {
                        System.out.println("No such option");
                    }
                }
            }
            catch (AppException e ){
                System.out.println("---------------------------------------");
                System.out.println("------------------ EXCEPTION ----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------------");
            }
        }while(true);
    }

    public int printOptions(){
        System.out.println("1. New order.");
        System.out.println("2. Done.");
        System.out.println("0. Exit.");
        return UserDataService.getInt("Choose an option. ");
    }

    private Customer getCustomerFromUser(){
        var customerValidator = new CustomerValidator();
        Customer customer;
        do{
            customer = Customer
                    .builder()
                    .name(UserDataService.getString("Customers name: "))
                    .surname(UserDataService.getString("Customers surname: "))
                    .age(UserDataService.getInt("Customers age: "))
                    .email(UserDataService.getString("Customers email: " ))
                    .build();
            var customerErrors = customerValidator.validate(customer);
            if(customerValidator.hasErrors()){
                System.out.println("-----------------------------------");
                System.out.println("-- Validation error for customer --");
                System.out.println("-----------------------------------");
                customerErrors.forEach((k, v) -> System.out.println(k + ": " + v));
            }
        }while(customerValidator.hasErrors());
        return customer;
    }

    private Product getProductFromUser(){
        var productValidator = new ProductValidator();
        Product product;
        do{

            product = Product
                    .builder()
                    .name(UserDataService.getString("Products name: " ))
                    .price(UserDataService.getBigDecimal("Products price: "))
                    .category(UserDataService.getCategory())
                    .build();

            var productErrors = productValidator.validate(product);
            if(productValidator.hasErrors()){
                System.out.println("-----------------------------------");
                System.out.println("-- Validation error for product --");
                System.out.println("-----------------------------------");
                productErrors.forEach((k, v) -> System.out.println(k + ": " + v));
            }
        }while(productValidator.hasErrors());
        return product;
    }

    private void newOrder(){
        var orderValidator = new OrderValidator();
        Order order;
        do {
            order = Order
                    .builder()
                    .customer(getCustomerFromUser())
                    .product(getProductFromUser())
                    .quantity(UserDataService.getInt("Orders product quantity: "))
                    .orderDate(UserDataService.getDate("Orders date: "))
                    .build();
            try {
                var orderErrors = orderValidator.validate(order);
                if(orderValidator.hasErrors()){
                    System.out.println("-----------------------------------");
                    System.out.println("-- Validation error for order --");
                    System.out.println("-----------------------------------");
                    orderErrors.forEach((k, v) -> System.out.println(k + ": " + v));
                }
            } catch (Exception e) {
                throw new AppException("Order validation failed.");
            }
        }while(orderValidator.hasErrors());
        orderList.add(order);
    }
}
