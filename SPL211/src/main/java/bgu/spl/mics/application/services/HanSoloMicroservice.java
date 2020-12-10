package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadCastMe;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Collections;
import java.util.List;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private Ewoks ewoks;

    public HanSoloMicroservice(Ewoks ewoks) {
        super("Han");
        this.ewoks=ewoks;
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, event -> {
            List<Integer> serials= event.getSer();
            Collections.sort(serials);
            int counter=0;
            while (counter!=serials.size()){
                for (Integer ser: serials){
                    if (ewoks.getMyEwoks()[ser].isAvailable()) {
                        ewoks.getMyEwoks()[ser].acquire();
                        counter++;
                    }
                }
            }
            try {
                Thread.sleep(event.getDur());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ewoks.releaseAll(serials);
            this.complete(event, true);
        });
        subscribeBroadcast(BroadCastMe.class, broad->{
            this.terminate();
        });
    }
}
