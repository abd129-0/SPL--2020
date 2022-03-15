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
    long C3POFinish;
    long R2D2Deactivate;
    long LeiaTerminate;
    long HanSoloTerminate;
    long C3POTerminate;
    long R2D2Terminate;
    long LandoTerminate;

    public Diary() {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        R2D2Terminate = 0;
        C3POTerminate = 0;
        LandoTerminate = 0;
    }

    public void resetNumberAttacks() {
        totalAttacks = new AtomicInteger(0);
    }


    private static class SingletoneHolder {
        private static Diary instance = new Diary();
    }

    public synchronized static Diary getDiaryInstance() {
        if (SingletoneHolder.instance == null) {
            SingletoneHolder.instance  = new Diary();
        }
        return SingletoneHolder.instance ;
    }
    //setters
    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }
    public void setC3PoFinish(long c3PoFinish) {
        C3POFinish = c3PoFinish;
    }
    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }
    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }
    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }
    public void setR2D2Deactivate(long r2D2Deactivate) {R2D2Deactivate = r2D2Deactivate;}
    public void setLandoTerminate(long landoTerminate) { LandoTerminate = landoTerminate;}
    public void setR2D2Terminate(long r2D2Terminate) { R2D2Terminate = r2D2Terminate; }



    //getters
    public long getHanSoloFinish(){return HanSoloFinish;}
    public long getC3PoFinish() {
        return C3POFinish;
    }
    public long getR2D2Terminate() { return R2D2Terminate; }
    public long getLandoTerminate() { return LandoTerminate; }
    public long getHanSoloTerminate() { return HanSoloTerminate; }
    public long getC3POTerminate() { return C3POTerminate;}
    public synchronized void setTotalAttacks() {
        totalAttacks.getAndIncrement();
    }
    public long getR2D2Deactivate() { return R2D2Deactivate;}
    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }



    public String output(String output) {
        output = "totalAttacks: " + totalAttacks + "," + "\n" +
                "HanSoloFinish: " + HanSoloFinish + "," + "\n" +
                "C3POFinish: " + C3POFinish + "," + "\n" +
                "R2D2Deactivate: " + R2D2Deactivate + "," + "\n" +
                "LeiaTerminate: " + LeiaTerminate + "," + "\n" +
                "HanSoloTerminate: " + HanSoloTerminate + "," + "\n" +
                "C3POTerminate: " + C3POTerminate + "," + "\n" +
                "R2D2Terminate: " + R2D2Terminate + "," + "\n" +
                "LandoTerminate: " + LandoTerminate;
        return output;
    }
}
