package company;
import java.util.*;

public class Dispatcher
{
    private static Dispatcher dispatcher = null;
    private static Map<String, Queue<Message>> messages;

    private Dispatcher()
    {
        System.out.println("Initializing message dispatcher");
        messages = new HashMap<>();
    }

    public synchronized static Dispatcher getInstance()
    {
        if(dispatcher == null)
        {
            dispatcher = new Dispatcher();
        }

        return dispatcher;
    }

    public synchronized void send(Message message)
    {
        String sender = Thread.currentThread().getName();
        message.setSender(sender);
        String receiver = message.getReceiver();

        if (messages.containsKey(receiver))
        {
            Queue<Message> queue = messages.get(receiver);
            queue.add(message);
            messages.replace(receiver, queue);
        }
        else
        {
            Queue<Message> queue = new LinkedList<>();
            queue.add(message);
            messages.put(receiver, queue);
        }
    }

    public synchronized Message receive()
    {
        String receiver = Thread.currentThread().getName();

        if (messages.containsKey(receiver))
        {
            Queue<Message> queue = messages.get(receiver);

            if(!queue.isEmpty())
            {
                Message message = queue.remove();
                messages.replace(receiver, queue);
                return message;
            }
            else { return null; }
        }
        else { return null; }
    }

}
