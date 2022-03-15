package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {


    private Attack[] attacks;
    private Future[] fuArray;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.fuArray = new Future[attacks.length];
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(terminateBroadcast.class, (terminateBroadcast finish) -> {
            this.terminate();
        });
        for (int i = 0; i < attacks.length; i++) {
            AttackEvent attack = new AttackEvent(attacks[i]);
            fuArray[i] = sendEvent(attack);
        }
        for (int i = 0; i < attacks.length; i++) {
            fuArray[i].get();
        }
        Future finish = sendEvent(new DeactivationEvent());
        finish.get();

        finish = sendEvent(new BombDestroyerEvent());
        finish.get();

        sendBroadcast(new terminateBroadcast());
    }

    @Override
    protected void close() {
        Diary.getDiaryInstance().setLeiaTerminate(System.currentTimeMillis());
    }
}
