package bgu.spl.mics;

import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class
MessageBusImplTest {
    MessageBusImpl bus;
    MicroService C3PO = new C3POMicroservice();
    MicroService han = new HanSoloMicroservice();
    BombDestroyerEvent e = new BombDestroyerEvent();
    terminateBroadcast b =new terminateBroadcast();

    @BeforeEach
    public void setUp() throws Exception {
        bus = MessageBusImpl.getInstance();
        bus.register(C3PO);
        bus.register(han);
    }

    @AfterEach
    public void tearDown(){
        bus.unregister(C3PO);
        bus.unregister(han);
    }

    @Test
    public void testSubscribeEvent(){
        bus.subscribeEvent(BombDestroyerEvent.class,C3PO);
        bus.sendEvent(e);
        assertTrue(bus.getMsgQ().get(C3PO).contains(e));
    }

    @Test
    public void testSubscribeBroadcast() {
        bus.subscribeBroadcast(terminateBroadcast.class,han);
        bus.sendBroadcast(b);
        assertTrue(bus.getMsgQ().get(han).contains(b));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testComplete(){
        bus.subscribeEvent(BombDestroyerEvent.class,C3PO);
        Future future = bus.sendEvent(e);
        bus.complete(e,true);
        assertFalse(bus.getMsgQ().get(C3PO).isEmpty());
        assertTrue(future.isDone());
    }

    @Test
    public void testSendBroadcast() {
        bus.subscribeBroadcast(terminateBroadcast.class,C3PO);
        bus.subscribeBroadcast(terminateBroadcast.class,han);
        bus.sendBroadcast(b);
        assertTrue(bus.getMsgQ().get(C3PO).contains(b));
        assertTrue(bus.getMsgQ().get(han).contains(b));
    }

    @Test
    public void testSendEvent() {
        bus.subscribeEvent(BombDestroyerEvent.class,C3PO);
        bus.sendEvent(e);
        assertTrue(bus.getMsgQ().get(C3PO).contains(e));
    }

    @Test
    public void testRegister() {
        assertTrue(bus.getMsgQ().containsKey(C3PO));
        assertTrue(bus.getMsgQ().containsKey(han));
    }


    @Test
    public void testAwaitMessage() throws InterruptedException {
        bus.subscribeEvent(BombDestroyerEvent.class,C3PO);
        bus.sendEvent(e);
        bus.awaitMessage(C3PO);
        assertEquals(bus.getMsgQ().get(C3PO).size(),0);
    }
}