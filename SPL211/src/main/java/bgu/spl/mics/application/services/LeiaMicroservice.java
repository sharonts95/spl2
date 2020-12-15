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
        // let all microservices register and subscribe
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector<Future> futures= new Vector<>(attacks.length);
        // send all the attacks as AttackEvents
        for (Attack attack : attacks){
            Future<Boolean> future = sendEvent(new AttackEvent(attack));
            futures.add(future);
        }
        // when this loop finish, Leia knows that HanSolo and 3CPO finish their attacks because get() method wait until the Future object resolved.
        for (Future future: futures){
            future.get();
        }
        //now Leia can informed R2D2 that he can act by sending him the DeactivasionEvent.
        Future<Boolean> R2D2future = sendEvent(new DeactivasionEvent());
        R2D2future.get();
        //R2D2 finish his act after get() method ends. now Leia can informed Lando that he can act by sending him the BombDestroyerEvent.
        Future<Boolean> LandoFuture = sendEvent(new BombDestroyerEvent());
        LandoFuture.get();
        //Lando finish his act after get() method ends.
        //now all microservices terminate by this broadcast.
        sendBroadcast(new BroadCastMe());
    }

}
