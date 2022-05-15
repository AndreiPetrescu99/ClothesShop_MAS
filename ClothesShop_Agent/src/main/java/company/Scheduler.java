package company;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Agent
{
    //adding details of the customer & cashier
    BlockingQueue<Details> customers_queue;
    public Map<String, States> cashiers_state;

    public Scheduler(String name)
    {
        super(name);
    }

    @Override
    protected void setup()
    {
        System.out.println("Scheduler ON");
        //o structura care implementeaza Queue
        customers_queue = new LinkedBlockingQueue<>();
        cashiers_state = new HashMap<>();
    }

    private synchronized String selectCashier()
    {

        for(String key:cashiers_state.keySet())
        {
            if(cashiers_state.get(key) == States.FREE)
            {
                return key;
            }
        }

        return null;
    }

    private class HandleCustomerQueue extends Thread
    {
        public HandleCustomerQueue(String name) {setName(name);}

        @Override
        public void run()
        {
            while (true)
            {
                if (!customers_queue.isEmpty())
                {
                    //scoate un client din coada + customer retine referinta
                    Details customer = customers_queue.remove();

                    String cashier = selectCashier();
                    while (cashier == null)
                    {
                        cashier = selectCashier();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //System.out.println("Found a free cashier");

                    Message message = new Message();
                    //casierul primeste mesaj ca o sa aiba un client
                    message.setReceiver(cashier);
                    message.setMessage(customer);
                    dispatcher.send(message);
                }
            }
        }
    }

    private synchronized void updateCustomerQueue(Details customer)
    {
        //System.out.println("Updating queue");
        customers_queue.add(customer);
    }

    private synchronized void updateCashier(String cashier, States state){

        if (cashiers_state.containsKey(cashier))
        {
            cashiers_state.replace(cashier, state);
        }
        else
        {
            cashiers_state.put(cashier, state);
        }
    }

    private class HandleMessages extends Thread
    {
        public HandleMessages(String name) {setName(name);}

        @Override
        public void run()
        {
            while(true)
            {
                //primeste msg de la dispatcher
                Message msg = dispatcher.receive();

                if (msg != null)
                {
                    if (msg.getSender().contains("Cashier"))
                    {
                        String msgStr = (String)  msg.getMessage();
                        String sender = msg.getSender();

                        if (msgStr.contains("free"))
                        {
                            //System.out.println(sender + " e FREE");
                            //verifica daca avem in Map daca avem casier
                            updateCashier(sender, States.FREE);
                        }
                        else
                        {
                            //System.out.println(sender + " e BUSY");
                            updateCashier(sender, States.BUSY);
                        }
                    }
                    else
                    {
                        String sender = msg.getSender();
                        Details new_customer = new Details(sender, States.WAITING);
                        updateCustomerQueue(new_customer);
                    }
                }
            }
        }

    }

    @Override
    protected void action() throws InterruptedException
    {
        new HandleCustomerQueue(getName()).start();
        new HandleMessages(getName()).start();
    }
}
