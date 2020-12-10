package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BroadCastMe;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
       subscribeEvent(BombDestroyerEvent.class, event->{
           try {
               Thread.sleep(duration);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           complete(event, true);
       });
        subscribeBroadcast(BroadCastMe.class, broad->{
            this.terminate();
        });
    }
}
