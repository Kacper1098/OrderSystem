package stefanowicz.kacper.main;

import stefanowicz.kacper.menu.MenuService;


public class App {
    public static void main(String[] args) {
        final String CUSTOMERS_FILENAME = "files/customers.json";
        final String PRODUCTS_FILENAME  = "files/products.json";
        final String ORDERS_FILENAME = "files/orders.json";
        MenuService menuService = new MenuService(CUSTOMERS_FILENAME, PRODUCTS_FILENAME, ORDERS_FILENAME);
        menuService.insertDataOption();
    }
}
