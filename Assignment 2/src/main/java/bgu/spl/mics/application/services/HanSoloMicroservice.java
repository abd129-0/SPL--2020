package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Ewoks ewoks;

    public HanSoloMicroservice() {
        super("Han");
        ewoks = Ewoks.getInstance();
    }




    protected void initialize() {
        subscribeEvent(AttackEvent.class, attack -> {
            boolean acquiredEwoks = false;
            while (!acquiredEwoks) {
                acquiredEwoks = ewoks.checkAndAcquire(attack.getAttack().getSerials());
            }
            try {
                Thread.sleep(attack.getAttack().getDuration());
            } catch (Exception ex) {}
            Diary diary = Diary.getDiaryInstance();
            diary.setHanSoloFinish(System.currentTimeMillis());
            ewoks.releaseEwok(attack.getAttack().getSerials());
            complete(attack, true);
            diary.setTotalAttacks();
        });
        subscribeBroadcast(terminateBroadcast.class, callback -> {
            terminate();
        });
        Main.downLatch.countDown();
    }

    @Override
    protected void close() {
        Diary.getDiaryInstance().setHanSoloTerminate(System.currentTimeMillis());
    }
}
