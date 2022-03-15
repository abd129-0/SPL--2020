package bgu.spl.mics;


import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */


public class MessageBusImpl implements MessageBus {
    private Map<Class<? extends Event>, BlockingQueue<MicroService>> eventMessages;
    private ConcurrentHashMap<Event, Future> futuerMap;
    private Map<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastMessages;
    private Map<MicroService, BlockingQueue<Message>> msgQ;

    public Map<MicroService, BlockingQueue<Message>>  getMsgQ() {
        return msgQ;
    }

    private static class SingletoneHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public MessageBusImpl() {
        eventMessages = new ConcurrentHashMap<>();
        futuerMap = new ConcurrentHashMap<>();
        broadcastMessages = new ConcurrentHashMap<>();
        msgQ = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingletoneHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        eventMessages.putIfAbsent(type, new LinkedBlockingQueue<>());
        eventMessages.get(type).add(m);
    }

    @Override
    public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        broadcastMessages.putIfAbsent(type, new LinkedBlockingQueue<>());
        broadcastMessages.get(type).add(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T> void complete(Event<T> e, T result) {
        futuerMap.get(e).resolve(result);
    }


    @Override
    public synchronized void sendBroadcast(Broadcast b) {
        if (broadcastMessages.containsKey(b.getClass())) {
            for (MicroService ms : broadcastMessages.get(b.getClass())) {
              //  System.out.println("Sending broadcast to " + ms.getName());
                try {
                    msgQ.get(ms).put(b);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        synchronized (e.getClass()) {
            if (eventMessages.containsKey(e.getClass()) && !eventMessages.get(e.getClass()).isEmpty()) {
                MicroService micro = eventMessages.get(e.getClass()).poll();
                if(micro==null){
                    return null;
                }else {
                    try {
                        Future<T> future = new Future<>();
                        futuerMap.put(e, future);
                        msgQ.get(micro).add(e);
                        eventMessages.get(e.getClass()).add(micro);
                        return future;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }
            }
            return null;
        }
    }


    @Override
    public void register(MicroService m) {
        msgQ.put(m, new LinkedBlockingQueue<Message>());
    }

    @Override
    public void unregister(MicroService m) {
        //System.out.println("unregistered: " + m.getName());
        synchronized(eventMessages) {
            Iterator<Class<? extends Event>> itr = eventMessages.keySet().iterator();
            while (itr.hasNext()) {
                Class<? extends Event> tmp = itr.next();
                if (eventMessages.containsKey(tmp)) {
                    eventMessages.get(tmp).remove(m);
                }
            }
        }
        synchronized (broadcastMessages) {
            Iterator<Class<? extends Broadcast>> iter3 = broadcastMessages.keySet().iterator();
            while (iter3.hasNext()) {
                Class<? extends Broadcast> tmp3 = iter3.next();
                if (broadcastMessages.containsKey(tmp3)) {
                    broadcastMessages.get(tmp3).remove(m);
                }
            }
        }
        synchronized (msgQ) {
            msgQ.remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return msgQ.get(m).take();
    }

}
