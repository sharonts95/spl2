package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
    private Attack attackEvent;

    public AttackEvent(Attack attackEvent){
        this.attackEvent=attackEvent;
    }

    public Attack getAttackEvent(){
        return this.attackEvent;
    }

    public List<Integer> getSer(){
        return this.attackEvent.getSerials();
    }

    public int getDur(){
       return this.attackEvent.getDuration();
    }


}
