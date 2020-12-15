package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadCastMe;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import java.util.Collections;
import java.util.List;


public abstract class attackingMicroServices extends MicroService {
    private Ewoks ewoks;
    private Diary diary;

    public attackingMicroServices(String name){
        super(name);
        ewoks=Ewoks.getInstance();
        diary=Diary.getInstance();
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
            diary.setFinishTime(this.getName(), System.currentTimeMillis());
            diary.setTotalAttacks();
        });
        subscribeBroadcast(BroadCastMe.class, broad->{
            this.terminate();
            diary.setTerminateTime(this.getName(), System.currentTimeMillis());
        });
    }
}
