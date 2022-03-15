package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.MessagingProtocolImpl;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;

import java.util.function.Supplier;

public class ReactorMain {
    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        int numberOfThreads = Integer.valueOf(args[1]);
        Server.reactor(
                numberOfThreads,
                port, //port
                () -> new MessagingProtocolImpl<>(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}