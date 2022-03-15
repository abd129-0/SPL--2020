package bgu.spl.mics.application.passiveObjects;

import java.util.List;
import java.util.Vector;
/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Vector<Ewok> EwoksArray;

    public Ewoks() { EwoksArray = new Vector<>();}

    private static class SingletoneHolder {
        private static Ewoks instance = new Ewoks();
    }

    public void setEwoksArray(int n){
        for(int i = 1; i <= n; i++)
             EwoksArray.add(new Ewok(i));
    }


    public synchronized boolean checkAndAcquire(List<Integer> serials) {
        for (Integer serial : serials) {
            if (!EwoksArray.get(serial - 1).isAvailable())
                return false;
        }
        for (Integer serial : serials) {
            acquireEwok(serial);
        }
        return true;
    }

    public void acquireEwok(int serial) {
        EwoksArray.get(serial-1).acquire();
    }

    public void releaseEwok(List<Integer> serials) {
        for(Integer serial: serials)
            EwoksArray.get(serial-1).release();
    }



    public static Ewoks getInstance() {
        return SingletoneHolder.instance;
    }
}