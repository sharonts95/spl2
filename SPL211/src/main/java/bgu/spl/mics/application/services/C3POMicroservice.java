package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadCastMe;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Collections;
import java.util.List;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    private Ewoks ewoks;
    Diary diary;

    public C3POMicroservice() {
        super("C3PO");
        ewoks=Ewoks.getInstance();
        diary=Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, event -> {
            List<Integer> serials= event.getSer();
            Collections.sort(serials);
            int counter=0;
            while (counter!=serials.size()){// try to catch the ewoks.
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
            diary.setFinishTime("C3PO", System.currentTimeMillis());
            diary.setTotalAttacks();
        });
        subscribeBroadcast(BroadCastMe.class, broad->{
            this.terminate();
            diary.setTerminateTime("C3PO", System.currentTimeMillis());
        });
    }
}
