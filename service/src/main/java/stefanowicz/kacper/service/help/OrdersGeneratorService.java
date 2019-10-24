package stefanowicz.kacper.service.help;

import stefanowicz.kacper.validators.CustomerValidator;
import stefanowicz.kacper.validators.ProductValidator;
import stefanowicz.kacper.converter.impl.CustomersJsonConverter;
import stefanowicz.kacper.converter.impl.OrdersJsonConverter;
import stefanowicz.kacper.converter.impl.ProductsJsonConverter;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Order;
import stefanowicz.kacper.model.Product;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OrdersGeneratorService {
    private final String customersFileName;
    private final String productsFileName;

    public OrdersGeneratorService(String customersFileName, String productsFileName){
        this.customersFileName = customersFileName;
        this.productsFileName = productsFileName;
    }

    private Set<Customer> getCustomers(){
        var customerValidator = new CustomerValidator();
        var customerCounter = new AtomicInteger(1);

        return new CustomersJsonConverter(customersFileName)
                .fromJson()
                .orElseThrow(() -> new AppException("could not convert customers from json file"))
                .stream()
                .filter(customer -> {
                    var errors = customerValidator.validate(customer);
                    if(customerValidator.hasErrors()){
                        System.out.println("-----------------------------------------");
                        System.out.println("--- Validation error for customer no. " + customerCounter.get() + " ---");
                        System.out.println("-----------------------------------------");
                        errors.forEach((k, v) -> System.out.println(k + ": " + v));
                    }
                    customerCounter.incrementAndGet();
                    return !customerValidator.hasErrors();
                })
                .collect(Collectors.toSet());
    }

    private Set<Product> getProducts(){
        var productValidator = new ProductValidator();
        var productCounter = new AtomicInteger(1);

        return new ProductsJsonConverter(productsFileName)
                .fromJson()
                .orElseThrow(() -> new AppException("could not convert customers from json file"))
                .stream()
                .filter(product -> {
                    var errors = productValidator.validate(product);
                    if(productValidator.hasErrors()){
                        System.out.println("-----------------------------------------");
                        System.out.println("--- Validation error for product no. " + productCounter.get() + " ---");
                        System.out.println("-----------------------------------------");
                        errors.forEach((k, v) -> System.out.println(k + ": " + v));
                    }
                    productCounter.incrementAndGet();
                    return !productValidator.hasErrors();
                })
                .collect(Collectors.toSet());
    }

    public void generateOrdersFile(final String ordersFileName){
        var ordersConverter = new OrdersJsonConverter(ordersFileName);

        Set<Customer> customers = getCustomers();
        Set<Product> products = getProducts();
        List<Order> orders = new ArrayList<>();

        customers.forEach(customer -> orders.addAll(getOrders(customer, products)));

        ordersConverter.toJson(orders);
    }

    private List<Order> getOrders(Customer customer, Set<Product> products){
        int customerOrders = new Random().nextInt(4 ) + 1;
        List<Order> customersOrders = new ArrayList<>();

        for (int i = 0; i< customerOrders; i++){
            customersOrders.add(
                    new Order(
                            customer,
                            new ArrayList<>(products).get(new Random().nextInt(products.size())),
                            new Random().nextInt(3) + 1,
                            getRandomDate()));
        }
        return customersOrders;
    }

    private LocalDate getRandomDate(){
        int range = 90;
        return LocalDate.now().plusDays(new Random().nextInt(range));
    }
}
