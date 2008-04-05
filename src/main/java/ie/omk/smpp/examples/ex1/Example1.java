package ie.omk.smpp.examples.ex1;

import ie.omk.smpp.Session;
import ie.omk.smpp.SessionState;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.SessionObserver;
import ie.omk.smpp.examples.ExampleBase;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.CommandId;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

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
