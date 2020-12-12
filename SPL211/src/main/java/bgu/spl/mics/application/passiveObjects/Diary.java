package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    AtomicInteger totalAttacks;
    long HanSoloFinish;
    long C3POFinis;
    long R2D2Deactivate;
    long LeiaTerminate;
    long HanSoloTerminate;
    long C3POTerminate;
    long R2D2Terminate;
    long LandoTerminate;

    public AtomicInteger getNumberOfAttacks() {
        return totalAttacks;
    }

    public int getC3POFinish() {
        return (int) C3POFinis;
    }

    public int getHanSoloFinish() {
        return (int) HanSoloFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public void resetNumberAttacks() {
        totalAttacks.lazySet(0);
    }

    private static class DiaryHolder{
        private static Diary instance=new Diary();
    }

    private Diary(){
        totalAttacks=new AtomicInteger(0);
        HanSoloFinish=0;
        C3POFinis=0;
        R2D2Deactivate=0;
        LeiaTerminate=0;
        HanSoloTerminate=0;
        C3POTerminate=0;
        R2D2Terminate=0;
        LandoTerminate=0;
    }

    public static Diary getInstance(){return DiaryHolder.instance;}

    public void setFinishTime(String name, long time){
        switch (name){
            case "Han": HanSoloFinish=time;
            case "C3PO": C3POFinis=time;
        }
    }

    public void setTerminateTime(String name, long time){
        switch (name){
            case "Han": HanSoloTerminate=time;
            case "Leia": LeiaTerminate=time;
            case "C3PO": C3POTerminate=time;
            case "R2D2": R2D2Terminate=time;
            case "Lando": LandoTerminate=time;
        }
    }

    public void setR2D2Deactivate(long time){
        R2D2Deactivate=time;
    }

    public void setTotalAttacks(){
        totalAttacks.getAndIncrement();
    }

}
