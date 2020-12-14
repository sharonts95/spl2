package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BroadCastMe;
import bgu.spl.mics.application.messages.DeactivasionEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.Vector;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		diary=Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(BroadCastMe.class, broad->{
            this.terminate();
            diary.setTerminateTime("Leia", System.currentTimeMillis());
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Future> futures= new Vector<>(attacks.length);
        for (Attack attack : attacks){
            Future<Boolean> future = sendEvent(new AttackEvent(attack));
            futures.add(future);
        }
        for (Future future: futures){
            future.get();
        }
        Future<Boolean> R2D2future = sendEvent(new DeactivasionEvent());
        R2D2future.get();
        Future<Boolean> LandoFuture = sendEvent(new BombDestroyerEvent());
        LandoFuture.get();
        sendBroadcast(new BroadCastMe());
    }

}
