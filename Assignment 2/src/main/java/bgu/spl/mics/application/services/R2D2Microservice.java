package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;
    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, call -> {
            //when we get a call, what should we do?
            try {
                Thread.sleep(duration);
                Main.diary.setR2D2Deactivate(System.currentTimeMillis());
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
        Diary.getDiaryInstance().setR2D2Terminate(System.currentTimeMillis());
    }
}
