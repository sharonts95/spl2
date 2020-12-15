package bgu.spl.mics;

import bgu.spl.mics.application.messages.BroadCastMe;
import bgu.spl.mics.application.messages.DeactivasionEvent;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
Changes:
fields:
Now that we implement all messages' classes and the class MicroService, we replace all "dummy" classes with the real assignment classes.
We use DeactivasionEvent class to test how the events operates and BroadCastMe class to test how broadcasts operates.
The Integer array "Test" helps us to follow the methods' performance.
We use two Microservices (m1, m2).
setUp():
The initialize methods' of m1 and m2 were implemented in a way that suits our tests.
testEventOperation():
We create one thread that use m1's initialize method-> subscribe to event and then send this event.
testBroadOperation():
We create two threads that use m1's and m2's initialize methods'-> subscribe to broadcast and send this broadcast.
testComplete():
We create one thread that use m1's initialize method, then we use the complete method to resolve the event and check if we get the expected result.
*/

public class MessageBusImplTest {

        private MessageBusImpl Mbus;
        private MicroService m1;
        private MicroService m2;
        private DeactivasionEvent event;
        private BroadCastMe broad;
        private Integer [] Test;
        private Future<Boolean> future;

        @BeforeEach
        public void setUp() {
            Test = new Integer[2];
            Test[0] = 0;
            Test[1] = 0;
            event = new DeactivasionEvent();
            broad = new BroadCastMe();
            Mbus = MessageBusImpl.getInstance();
            m1 = new MicroService("test1") {
                @Override
                protected void initialize() {
                    subscribeBroadcast(broad.getClass(), broad -> {
                        Test[0] = 1;
                        terminate();
                    });
                    subscribeEvent(event.getClass(), event -> {
                        Test[0] = 1;
                        terminate();
                    });
                    future= Mbus.sendEvent(event);
                }
            };
            m2 = new MicroService("test2") {
                @Override
                protected void initialize() {
                    subscribeBroadcast(broad.getClass(), broad -> {
                        Test[1] = 1;
                        terminate();
                    });
                    Mbus.sendBroadcast(broad);
                }
            };
        }


        @Test
        public void testEventOperation(){
            Thread t1=new Thread(m1);
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(1, Test[0]);
        }

        @Test
        public void testBroadOperation(){
            Thread t1=new Thread(m1);
            Thread t2=new Thread(m2);
            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(Test[0]==1 & Test[1]==1);
        }

        @Test
        public void testComplete(){
            Boolean result=false;
            Thread t1=new Thread(m1);
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Mbus.complete(event, true);
            result=future.get();
            assertEquals(result, true);
        }
}
