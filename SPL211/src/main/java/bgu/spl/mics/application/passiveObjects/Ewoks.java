package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;

import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Ewok[] myEwoks;


    private Ewoks(int ewoks){
        myEwoks=new Ewok[ewoks+1];
        for (int i=1; i<ewoks+1; i++)
            myEwoks[i]=new Ewok(i);
    }

    private static class ewoksHolder{
        private static Ewoks instance= null;

        private ewoksHolder(int ewoks){ instance=new Ewoks(ewoks);}
    }

    public static Ewoks getInstance(){ return ewoksHolder.instance;}

    public static Ewoks getInstance(int ewoks){
        ewoksHolder eh=new ewoksHolder(ewoks);
        return ewoksHolder.instance;
    }

    public Ewok[] getMyEwoks(){return myEwoks;}

    public void releaseAll(List<Integer> serials){
        for (Integer i: serials){
            myEwoks[i].release();
        }
    }





}
