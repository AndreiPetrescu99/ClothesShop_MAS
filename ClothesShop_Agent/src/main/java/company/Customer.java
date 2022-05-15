package company;

import java.util.Random;

public class Customer extends Agent
{
    States current_client_state;
    int products;

    public Customer(String name) {super(name);}

    @Override
    protected void setup()
    {
        //setting the current state to WAITING as the customer first is
        // waiting in line for a free cashier
        current_client_state = States.WAITING;
        System.out.println(current_client_state.toString());
        //optain the number of products that the customer has in basket
        products = getRandomNumber(1,5);
    }

    //function that generates a random number in a range
    private static int getRandomNumber(int min, int max)
    {
        Random random = new Random();
        return random.ints(min, max).findFirst().getAsInt();
    }

    @Override
    protected void action() throws InterruptedException
    {
        Random random = new Random();
        while (true)
        {
            Message message = new Message();
            message.setReceiver("Scheduler");
            message.setMessage(new Details(getName(), current_client_state));

            //waiting for cashier to be free
            Message reply = dispatcher.receive();
            while (reply == null)
            {
                reply = dispatcher.receive();
            }

            //cashier is free now
            //cum imi dau seama ca e free?
            current_client_state = States.IN_PROGRESS;
            products = getRandomNumber(1,5);
            message.setReceiver("Scheduler");
            message.setMessage(new Details(getName(), current_client_state));
            dispatcher.send(message);
            System.out.println("Number of products in the basket:" + products);

            //paying
            Thread.sleep(3000);

            //leaving
            current_client_state = States.SERVED;
            products = 0;
            System.out.println(current_client_state + "Bye!");
            Thread.sleep(5000);
        }
    }
}
