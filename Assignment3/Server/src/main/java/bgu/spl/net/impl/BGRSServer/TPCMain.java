package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.MessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class    TPCMain {
    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        Server.threadPerClient(
                port, //port
                () -> new MessagingProtocolImpl<>(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
