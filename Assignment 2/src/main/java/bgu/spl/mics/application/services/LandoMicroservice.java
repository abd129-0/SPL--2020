package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(BombDestroyerEvent.class, call -> {
            //when we get a call, what should we do?
            try {
                Thread.sleep(duration);
                complete(call, true); //resolve the future
            } catch (Exception e){e.printStackTrace();}
        } );

        subscribeBroadcast(terminateBroadcast.class, call -> {
            terminate();
        });
        Main.downLatch.countDown();
    }

    @Override
    protected void close() {
        Diary.getDiaryInstance().setLandoTerminate(System.currentTimeMillis());
    }
}
