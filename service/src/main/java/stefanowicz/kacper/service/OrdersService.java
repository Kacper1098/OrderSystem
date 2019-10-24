package stefanowicz.kacper.service;

import com.github.javafaker.App;
import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;
import stefanowicz.kacper.converter.impl.OrdersJsonConverter;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.help.CustomerWithMoney;
import stefanowicz.kacper.help.DateOrdersQuantity;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Order;
import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.model.enums.Category;
import stefanowicz.kacper.service.help.OrdersGeneratorService;
import stefanowicz.kacper.service.help.EmailService;
import stefanowicz.kacper.service.help.OrdersFromUserService;
import stefanowicz.kacper.validators.OrderValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class OrdersService {
    private List<Order> orderList;

    public OrdersService(String customersFilename, String productsFilename, String ordersFilename) {
        OrdersGeneratorService ordersGeneratorService = new OrdersGeneratorService(customersFilename, productsFilename);
        ordersGeneratorService.generateOrdersFile(ordersFilename);
        orderList = getOrdersFromJson(ordersFilename);
    }

    public OrdersService() {
        OrdersFromUserService ordersFromUserService = new OrdersFromUserService();
        orderList = ordersFromUserService.getOrderList();
    }

    /**
     * @param ordersFilename Name of file from which orders should be loaded.
     * @return List of orders loaded from json file.
     */
    private List<Order> getOrdersFromJson(String ordersFilename) {
        var orderConverter = new OrdersJsonConverter(ordersFilename);
        var orderValidator = new OrderValidator();
        var orderCounter = new AtomicInteger(1);

        return orderConverter
                .fromJson()
                .orElseThrow(() -> new AppException("could not convert orders from json file"))
                .stream()
                .filter(order -> {
                    try {
                        var errors = orderValidator.validate(order);
                        if (orderValidator.hasErrors()) {
                            System.out.println("--------------------------------------");
                            System.out.println("-- Validation error for order no. " + orderCounter.get() + " --");
                            System.out.println("--------------------------------------");
                            errors.forEach((k, v) -> System.out.println(k + ": " + v));
                        }
                        orderCounter.incrementAndGet();
                        return !orderValidator.hasErrors();
                    } catch (Exception e) {
                        throw new AppException("order exception for order no. " + orderCounter.get());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * @param fromDate Date from which average price should be computed
     * @param toDate   Date to which average price should be computed
     * @return Average product price in given date range.
     */
    public BigDecimal avgPriceInDateRange(LocalDate fromDate, LocalDate toDate) {
        if(fromDate.compareTo(toDate) > 0){
            throw new AppException("OrdersService excepiton - fromDate cannot be greater than toDate");
        }

        List<Order> ordersInRange = orderList
                .stream()
                .filter(order -> order.getOrderDate().compareTo(fromDate) >= 0 && order.getOrderDate().compareTo(toDate) <= 0)
                .collect(Collectors.toList());

        if (ordersInRange.isEmpty()) {
            throw new AppException("There are'nt any orders in given date range");
        }

        return ordersInRange
                .stream()
                .collect(Collectors2.summarizingBigDecimal(o -> o.getProduct().getPrice().multiply(BigDecimal.valueOf(o.getQuantity()))))
                .getAverage();
    }

    /**
     * @return Map with product category as key and product with max price from this category as value.
     */
    public Map<Category, Product> maxPriceGroupedByCategory() {
        return orderList
                .stream()
                .collect(Collectors.groupingBy(order -> order.getProduct().getCategory(), Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(o -> o.getProduct().getPrice())),
                        maxPrice -> maxPrice
                                .orElseThrow(() -> new AppException("Could not find max price for category"))
                )))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getProduct(),
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    /**
     * @return Map with customer as key and list of products bought by this customer as value
     */
    public Map<Customer, List<Product>> customersProducts() {
        return orderList
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer, Collectors.mapping(Order::getProduct, Collectors.toList())));
    }

    /**
     * @return Date and quantity where there were most orders.
     */
    public DateOrdersQuantity mostOrdersDate() {
        var entry = orderList
                .stream()
                .collect(Collectors.groupingBy(Order::getOrderDate, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(() -> new AppException("Could not find most orders date"));

        return DateOrdersQuantity
                .builder()
                .date(entry.getKey())
                .quantity(entry.getValue())
                .build();
    }

    /**
     * @return Date and quantity where there were least orders.
     */
    public DateOrdersQuantity leastOrdersDate() {
        var entry = orderList
                .stream()
                .collect(Collectors.groupingBy(Order::getOrderDate, Collectors.counting()))
                .entrySet().stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(() -> new AppException("Could not find least orders date"));

        return DateOrdersQuantity
                .builder()
                .date(entry.getKey())
                .quantity(entry.getValue())
                .build();
    }

    /**
     * @return Map entry with customer who spent most money on shopping as key and money he spent as value.
     */
    public CustomerWithMoney spentMostMoney() {
        var entry = orderList
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Order::getCustomer,
                                Collectors.mapping(
                                        o -> o.getProduct().getPrice().multiply(new BigDecimal(o.getQuantity())),
                                        Collectors2.summarizingBigDecimal(o -> o))))
                .entrySet().stream()
                .max(Comparator.comparing(e -> e.getValue().getSum()))
                .orElseThrow(() -> new AppException("Error while looking for max value"));
        return CustomerWithMoney
                .builder()
                .customer(entry.getKey())
                .money(entry.getValue().getSum())
                .build();
    }

    /**
     * @return Price of all orders after discount
     */
    public BigDecimal ordersAfterDiscount() {
        return orderList
                .stream()
                .map(this::priceOfOrderAfterDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal priceOfOrderAfterDiscount(Order order){
        BigDecimal orderPrice = order.getProduct().getPrice().multiply(new BigDecimal(order.getQuantity()));
        LocalDate twoDaysBeforeOrderDate = order.getOrderDate().minusDays(2);

        if (order.getCustomer().getAge() < 25) {
            return orderPrice.subtract(orderPrice.multiply(new BigDecimal(0.3)));
        } else if (twoDaysBeforeOrderDate.isEqual(LocalDate.now()) || twoDaysBeforeOrderDate.isBefore(LocalDate.now())) {
            return orderPrice.subtract(orderPrice.multiply(new BigDecimal(0.2)));
        }

        return orderPrice;
    }

    /**
     * @param x Qauntity of products
     * @return Number of clients that always have ordered qunatity of products equal at least x.
     */
    public long orderedAtLeastProducts(int x) {
        return orderList
                .stream()
                .filter(order -> order.getQuantity() >= x)
                .map(Order::getCustomer)
                .distinct()
                .count();
    }

    /**
     * @return Map entry with category as key and quantity of orders from this category as value.
     */
    public Map.Entry<Category, Long> mostPopularCategory() {
        return orderList
                .stream()
                .collect(Collectors.groupingBy(order -> order.getProduct().getCategory(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new AppException("Error while looking for most popular category"));
    }

    /**
     * @return Map with month as key and quantity of orders in this month as value.
     */
    public Map<Month, Long> monthWithOrdersQuantity() {
        return orderList
                .stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().getMonth(), Collectors.counting()));
    }

    /**
     * @return Map with month as key and most popular category in this month as value.
     */
    public Map<Month, Category> mostPopularCategoryInMonth() {
        return orderList
                .stream()
                .collect(Collectors.groupingBy(
                        order -> order.getOrderDate().getMonth(),
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(o -> o.getProduct().getCategory(), Collectors.counting()),
                                counted -> counted.entrySet()
                                        .stream()
                                        .max(Comparator.comparing(Map.Entry::getValue))
                                        .orElseThrow()
                                        .getKey())
                ));
    }

    public String sendEmail(String customerEmail, List<Product> customerProducts) {
        if (customerEmail == null) {
            throw new AppException("customer email string is null");
        }
        var emailService = new EmailService();
        return emailService.send(customerEmail, "Your orders", ordersToHtml(customerProducts))
                ? "Email has been sent to ->" + customerEmail : "Email sending errors";
    }

    private String ordersToHtml(List<Product> customerProducts) {

        if (customerProducts == null) {
            throw new AppException("OrdersToHtml method exception - customer products are null");
        }
        return table().with(
                thead(
                        tr().with(
                                th("Name"),
                                th("Price"),
                                th("Category")
                        )
                ),
                tbody(
                        each(customerProducts, product -> tr(
                                td(product.getName()),
                                td(product.getPrice().toString()),
                                td(product.getCategory().toString())
                        ))
                )
        ).render();
    }
}
