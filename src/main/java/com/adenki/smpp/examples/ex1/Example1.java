package com.adenki.smpp.examples.ex1;

import com.adenki.smpp.Session;
import com.adenki.smpp.SessionState;
import com.adenki.smpp.event.SMPPEvent;
import com.adenki.smpp.event.SessionObserver;
import com.adenki.smpp.examples.ExampleBase;
import com.adenki.smpp.message.BindReceiver;
import com.adenki.smpp.message.CommandId;
import com.adenki.smpp.message.DeliverSM;
import com.adenki.smpp.message.DeliverSMResp;
import com.adenki.smpp.message.EnquireLink;
import com.adenki.smpp.message.EnquireLinkResp;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.Unbind;
import com.adenki.smpp.message.UnbindResp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple asynchronous receiver that implements the session observer
 * interface.
 * @version $Id:$
 */
public class Example1 extends ExampleBase implements SessionObserver {
    private static final Logger LOG = LoggerFactory.getLogger(Example1.class);
    
    public void run() throws Exception {
        Session session = createSession();
        session.addObserver(this);
        bind(session);
        // Stay bound for 10 seconds, then initiate an unbind.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException x) {
            LOG.debug("Interrupted!");
        }
        session.unbind();
        try {
            long waitStart = System.currentTimeMillis();
            while (session.getState() != SessionState.UNBOUND) {
                if ((System.currentTimeMillis() - waitStart) > 15000) {
                    LOG.warn("Waiting 15 seconds for unbind! Aborting..");
                    break;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException x) {
            LOG.debug("Interrupted!");
        }
    }

    public void packetReceived(Session session, SMPPPacket packet) {
        SMPPPacket response;
        switch (packet.getCommandId()) {
        case CommandId.BIND_RECEIVER_RESP:
            if (packet.getCommandStatus() != 0) {
                String message =
                    apiMessages.getPacketStatus(packet.getCommandStatus());
                LOG.error("Bind failed: {}", message);
                System.exit(1);
            }
            break;
            
        case CommandId.DELIVER_SM:
            LOG.info("{}", packet);
            response = new DeliverSMResp((DeliverSM) packet);
            send(session, response);
            break;

        case CommandId.ENQUIRE_LINK:
            response = new EnquireLinkResp((EnquireLink) packet);
            send(session, response);
            break;

        case CommandId.UNBIND:
            LOG.info("SMSC requested an unbind. Responding..");
            response = new UnbindResp((Unbind) packet);
            send(session, response);
            break;
            
        case CommandId.UNBIND_RESP:
            LOG.info("Successfully unbound.");
            break;
            
        default:
            LOG.info("Packet received: ", packet);
        }
    }

    public void update(Session session, SMPPEvent event) {
        LOG.info("Received an event: {}", event);
    }

    @Override
    protected Logger getLog() {
        return LOG;
    }
    
    private void bind(Session session) throws IOException {
        BindReceiver br = new BindReceiver();
        initBindReq(br);
        session.bind(br);
    }
}
