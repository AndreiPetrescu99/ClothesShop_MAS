package company;

import java.util.Map;

public class Cashier extends Agent
{
    public Map<String, States> cashiers_state;

    public Cashier(String name) {super(name);}

    @Override
    protected void setup()
    {
        System.out.println("My name is:" + getName());
    }

    @Override
    protected void action() throws InterruptedException
    {
        Thread.sleep(500);
        while (true)
        {
            //anunta scheduler ca este free
            Message message = new Message();
            message.setReceiver("Scheduler");
            message.setMessage("I am free");
            dispatcher.send(message);

            //primeste client
            //schimbarea starii de la free la busy a casierului

            //asteapta client
            Message reply = dispatcher.receive();
            while (reply == null)
            {
                reply = dispatcher.receive();
            }

            //Trebuie anuntat Scheduleru ca acu e busy
            message.setReceiver("Scheduler");
            message.setMessage("busy");
            dispatcher.send(message);

            //serveste clientul
            Details client_details = (Details) reply.getMessage();
            String client_name = client_details.getId();
            System.out.println(getName() + ": serving " + client_name);
            Thread.sleep(5000);

            //trebuie schimbata starea clientului
            Message client_message = new Message();
            client_message.setReceiver(client_name);
            client_message.setMessage("Thank you for your choice!");
            dispatcher.send(client_message);

        }
    }
}
