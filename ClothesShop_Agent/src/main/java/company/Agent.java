package company;

public abstract class Agent extends Thread
{
    Dispatcher dispatcher;

    public Agent() {}

    public Agent(String name)
    {
        setName(name);
    }

    @Override
    public void run()
    {
        super.run();
        dispatcher = Dispatcher.getInstance();
        setup();

        try {
            action();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected abstract void setup();
    protected abstract void action() throws InterruptedException;
}
