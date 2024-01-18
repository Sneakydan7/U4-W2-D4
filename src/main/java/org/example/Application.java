package org.example;

import com.github.javafaker.Faker;
import org.example.Classi.Customer;
import org.example.Classi.Order;
import org.example.Classi.Product;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {

        List<Order> orders = new ArrayList<>();


        Supplier<Long> randomIdSupplier = () -> {
            Random rndm = new Random();
            return rndm.nextLong(50000, 60000);
        };

        Supplier<Double> randomPrice = () -> {
            Random rndm = new Random();
            return rndm.nextDouble(50, 150);
        };

        Supplier<String> bookTitlesFaker = () -> {
            Faker faker = new Faker();
            return faker.book().title();
        };


        Supplier<Long> randomOrderIdSupplier = () -> {
            Random rndm = new Random();
            return rndm.nextLong(0, 500);
        };

        LocalDate today = LocalDate.now();

        Supplier<Integer> deliveryDate = () -> {
            Random rndm = new Random();
            return (rndm.nextInt(2, 5));
        };

        Supplier<Integer> randomTier = () -> {
            Random rndm = new Random();
            return rndm.nextInt(1, 5);
        };


        Supplier<String> customerName = () -> {
            Faker faker = new Faker();
            return faker.name().fullName();
        };


        Supplier<Customer> customerSupplier = () -> new Customer(randomIdSupplier.get(), customerName.get(), randomTier.get());
        Supplier<List<Product>> productListSupplier = () -> {
            List<Product> products = new ArrayList<>();
            Supplier<Product> productSupplier = () -> new Product(randomIdSupplier.get(), bookTitlesFaker.get(), "Books", randomPrice.get());

            for (int i = 0; i < 2; i++) {
                products.add(productSupplier.get());
            }
            return products;

        };


        Supplier<Order> orderSupplier = () -> new Order(randomOrderIdSupplier.get(), "Pending", today, today.plusDays(deliveryDate.get()), productListSupplier.get(), customerSupplier.get());


        for (int i = 0; i < 10; i++) {
            orders.add(orderSupplier.get());

        }
        ;


        orders.forEach(System.out::println);

        System.out.println("-----------------------------ES1------------------------------------------------------");


        Map<Customer, List<Order>> ordersForCustomer = orders.stream().collect(Collectors.groupingBy(Order::getCustomer));
        ordersForCustomer.forEach((customer, ordersList) ->
                System.out.println("Lista di ordini di: " + customer + ":" + ordersList));

        System.out.println("-------------------------------ES2------------------------------------------------------");


        Map<Customer, Double> totalPrices = orders.stream().collect(Collectors.groupingBy(Order::getCustomer, Collectors.summingDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum())));
        totalPrices.forEach((customer, total) -> System.out.println("Totale ordine di " + customer + ":" + total));


        System.out.println("-----------------------------ES3---------------------------------------------------------");
        productListSupplier.get().stream().sorted(Comparator.comparing(Product::getPrice, Comparator.reverseOrder())).forEach(System.out::println);

        System.out.println("-----------------------------ES4---------------------------------------------------------");
        OptionalDouble averageOfOrders = orders.stream().flatMap(order -> order.getProducts().stream()).mapToDouble(Product::getPrice).average();
        System.out.println("Media dei prodotti di tutti gli ordini" + averageOfOrders);


    }


}
