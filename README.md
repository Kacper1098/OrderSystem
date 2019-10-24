# OrdersSystem
OrdersSystem is maven multi-module project written in Java 13, that simulates ordering system.
It allows user to generate orders with products and customers that are being stored in JSON files 
or to insert each order directly from terminal.

From created orders user can:
* Calculate average product price in given date range 
* Designate the most expensive product for each category
* Send email for each client with their shopping list
* Designate date with most and least orders
* Designate details about customer that spent most money on shopping
* Calculate sum of all products price including discounts:
    * If order date is not later than two days after today's date, it gets 2% discount
    * If customer is not older than 25, it gets 3% discount
    * NOTE: Discounts do not add up
* Calculate quantity of customers that always have ordered at least X pieces of product.
  X value is inserted by user    
* Designate the most popular category
* Show juxtaposition with month and quantity of products ordered in this month
* Show juxtaposition with month and most popular category in this month

## Installation
```
    mvn clean install
    cd main
    mvn clean compile assembly::single 
```

## Usage 
Please make sure that _files_ directory is located in 
the same directory as _main-1.0-SNAPSHOT-jar-with-dependencies.jar_
```
    cd target
    java --enable-preview -cp main-1.0-SNAPSHOT-jar-with-dependencies.jar stefanowicz.kacper.main.App
```

