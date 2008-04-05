package ie.omk.smpp.examples.ex2;

import ie.omk.smpp.Session;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.util.APIMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic extension of the SMPPEventAdapter.
 * @version $Id:$
 */
public class Example2Adapter extends SMPPEventAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(Example2Adapter.class);
    private APIMessages apiMessages = new APIMessages();
    
    @Override
    public void bindResponse(Session session, BindResp packet) {
        if (packet.getCommandStatus() != 0) {
            String message =
                apiMessages.getPacketStatus(packet.getCommandStatus());
            LOG.error("Bind failed: {}", message);
            System.exit(1);
        }
    }
    
    @Override
    public void deliverSM(Session session, DeliverSM packet) {
        LOG.info("{}", packet);
        // No need to send a response as the auto responder will do it.
    }
    
    @Override
    public void unbindResponse(Session arg0, UnbindResp arg1) {
        LOG.info("Successfully unbound.");
    }
}
