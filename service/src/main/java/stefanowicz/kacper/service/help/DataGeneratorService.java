package stefanowicz.kacper.service.help;

import com.github.javafaker.Faker;
import stefanowicz.kacper.converter.impl.CustomersJsonConverter;
import stefanowicz.kacper.converter.impl.ProductsJsonConverter;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.converter.impl.CategoriesJsonConverter;
import stefanowicz.kacper.converter.impl.CategoriesWithProductsJsonConverter;
import stefanowicz.kacper.help.CategoryWithProducts;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.model.enums.Category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class DataGeneratorService {
    private static final Faker faker = new Faker();
    private static final String PRODUCTS_FILENAME = "files/random/products.json";
    private static final String CATEGORIES_FILENAME = "files/random/categories.json";


    public  void generateNewFiles(int numberOfCustomers, int numberOfProducts){
        Set<Customer> customers = new HashSet<>();
        var customersConverter = new CustomersJsonConverter("files/customers.json");
        for (int i = 0; i < numberOfCustomers; i++) {
            customers.add(generateCustomer());
        }
        customersConverter.toJson(customers);
        Set<Product> products = new HashSet<>();
        var productsConverter = new ProductsJsonConverter("files/products.json");
        for (int i = 0; i < numberOfProducts; i++) {
            products.add(generateProduct());
        }
        productsConverter.toJson(products);
        System.out.println("----- DATA GENERATED SUCCESSFULLY -----");
    }

    private  String randomEmailDomain(){
        final String[] arr = {"yahoo.com", "hotmail.com" ,"gmail.com", "comcast.net", "msn.com", "aol.com", "ntlworld.com"};
        return arr[new Random().nextInt(arr.length)];
    }

    private  Customer generateCustomer(){
        Random rnd = new Random();
        String name = faker.name().firstName();
        String surName = faker.name().lastName();
        return Customer
                .builder()
                .name(name)
                .surname(surName)
                .age(rnd.nextInt(58) + 18)
                .email(name.toLowerCase() + "." + surName.toLowerCase() + "@"+ randomEmailDomain())
                .build();
    }

    private  Category getRandomCategory(){
        var converter = new CategoriesJsonConverter(CATEGORIES_FILENAME);
        List<Category> categories  = converter.fromJson().orElseThrow(() -> new AppException("Error while converting categories from json"));

        return categories.get(new Random().nextInt(categories.size()));
    }

    private  String getProductNameFromCategory(Category category){
        var converter = new CategoriesWithProductsJsonConverter(PRODUCTS_FILENAME);

        List<CategoryWithProducts> categoriesWithProducts = converter
                .fromJson()
                .orElseThrow(() -> new AppException("Error while converting categories with products from json"));

        CategoryWithProducts cwp1 = categoriesWithProducts
                .stream()
                .filter(cwp -> cwp.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new AppException("Error while filtering categories with products"));

        return cwp1.getProducts().get(new Random().nextInt(cwp1.getProducts().size()));
    }

    private  BigDecimal getPriceByCategory(Category category){
        return switch (category){
            case A ->  new BigDecimal(Math.random()).multiply(new BigDecimal(5)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(2));
            case B ->  new BigDecimal(Math.random()).multiply(new BigDecimal(50)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(10));
            case C ->  new BigDecimal(Math.random()).multiply(new BigDecimal(100)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(1000));
            default -> throw new AppException("Could not find given product category " + category);
        };
    }

    private Product generateProduct(){
        Category category = getRandomCategory();
        return Product
                .builder()
                .category(category)
                .name(getProductNameFromCategory(category))
                .price(getPriceByCategory(category))
                .build();
    }



}
