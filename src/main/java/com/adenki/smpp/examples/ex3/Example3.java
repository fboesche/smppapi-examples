package com.adenki.smpp.examples.ex3;

import com.adenki.smpp.Address;
import com.adenki.smpp.Session;
import com.adenki.smpp.encoding.AlphabetEncoding;
import com.adenki.smpp.encoding.DefaultAlphabetEncoding;
import com.adenki.smpp.encoding.Latin1Encoding;
import com.adenki.smpp.event.SMPPEvent;
import com.adenki.smpp.event.SessionObserver;
import com.adenki.smpp.examples.ExampleBase;
import com.adenki.smpp.message.BindTransmitter;
import com.adenki.smpp.message.CommandId;
import com.adenki.smpp.message.EnquireLink;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.SubmitSM;
import com.adenki.smpp.util.AutoResponder;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A transmitter example.
 * @version $Id:$
 */
public class Example3 extends ExampleBase implements SessionObserver {
    private static final Logger LOG = LoggerFactory.getLogger(Example3.class);
    
    private AutoResponder responder = new AutoResponder(true);
    
    public void run() throws Exception {
        Session session = createSession();
        session.addObserver(responder);
        bind(session);
        sendMessage(session,
                new Address(0, 0, "+123456789"),
                "Hi there!");
        session.sendPacket(new EnquireLink());
        sendMessage(session,
                new Address(0, 0, "+155522233"),
                "Caf\u00e9 au lait",
                new Latin1Encoding());
        synchronized (this) {
            // Send unbind and wait for the response packet.
            session.unbind();
            wait(15000);
        }
    }

    public void packetReceived(Session session, SMPPPacket packet) {
        LOG.info("Packet received: {}", packet);
        if (packet.getCommandId() == CommandId.UNBIND_RESP) {
            synchronized (this) {
                notifyAll();
            }
        }
    }
    
    public void update(Session session, SMPPEvent event) {
        LOG.info("Event received: {}", event);
    }
    
    @Override
    protected Logger getLog() {
        return LOG;
    }

    private void sendMessage(
            Session session,
            Address destination,
            String message) throws IOException {
        sendMessage(session, destination, message, new DefaultAlphabetEncoding());
    }
    
    private void sendMessage(
            Session session,
            Address destination,
            String message,
            AlphabetEncoding encoding) throws IOException {
        SubmitSM submitSm = new SubmitSM();
        submitSm.setDestination(destination);
        submitSm.setDataCoding(encoding.getDataCoding());
        submitSm.setMessage(encoding.encode(message));
        session.sendPacket(submitSm);
    }
    
    private void bind(Session session) throws IOException {
        BindTransmitter bt = new BindTransmitter();
        initBindReq(bt);
        session.bind(bt);
    }
}
