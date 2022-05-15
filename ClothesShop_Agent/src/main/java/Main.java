import company.Cashier;
import company.Customer;
import company.Details;
import company.Scheduler;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args)
    {
        System.out.println("Hello world!");
        List<Customer> customers = new ArrayList<>();

        for (int i=0;i<5;i++)
        {
            String name = "Customer:" + i;
            customers.add(new Customer(name));
        }

        Scheduler scheduler = new Scheduler("Scheduler");
        Cashier cashier = new Cashier("Cashier");
        cashier.start();
        scheduler.start();

        for (Customer c:customers)
        {
            c.start();
        }
    }
}
