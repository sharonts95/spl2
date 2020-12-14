package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeIntEvent implements Event<Integer>{}
class ChangeIntBroadCast implements Broadcast{}

public class MessageBusImplTest {
    private MessageBusImpl messageBus;
    private MicroService m;
    private Integer [] Test;
    private ChangeIntEvent event;
    private ChangeIntBroadCast broad;
    private Future<Integer> future;

    @BeforeEach
    public void setUp(){
        messageBus=MessageBusImpl.getInstance();
        event=new ChangeIntEvent();
        broad=new ChangeIntBroadCast();
        Test = new Integer[1];
        Test[0] = 0;
        m= new MicroService("test") {
            @Override
            protected void initialize() {
                subscribeEvent(ChangeIntEvent.class, event->{
                    Test[0]=1; terminate();});
                subscribeBroadcast(ChangeIntBroadCast.class, broad->{
                    Test[0]=1; terminate();});
                future= messageBus.sendEvent(event);
            }
        };
        Thread t = new Thread(m);
        t.start();
        try
        {
            t.join();
        } catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

    @Test
    public void testSubscribeEvent(){
        assertEquals(1, Test[0]);
    }

    @Test
    public void testSubscribeBroadcast(){
        Integer [] Test = new Integer[2];
        Test[0] = 0;
        Test[1] = 0;
        MicroService m1 = new MicroService("test1")
        {
            @Override
            protected void initialize()
            {
                subscribeBroadcast(ChangeIntBroadCast.class, broad -> {Test[0] = 1; terminate();});
            }
        };
        MicroService m2 = new MicroService("test2")
        {
            @Override
            protected void initialize()
            {
                subscribeBroadcast(ChangeIntBroadCast.class, broad -> {Test[1] = 1; terminate();});
                messageBus.sendBroadcast(broad);
            }
        };
        Thread t1 = new Thread(m1);
        Thread t2 = new Thread(m2);
        t1.start();
        t2.start();
        try
        {
            t1.join();
            t2.join();
        } catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
        assertTrue(Test[0] == 1 & Test[1] == 1);
    }

    @Test
    public void testComplete(){
        messageBus.complete(event, 5);
        Integer resolved = future.get();
        assertEquals(resolved, 5);
    }
}
