package com.adenki.smpp.examples.ex2;

import com.adenki.smpp.Session;
import com.adenki.smpp.event.SMPPEventAdapter;
import com.adenki.smpp.message.BindResp;
import com.adenki.smpp.message.DeliverSM;
import com.adenki.smpp.message.UnbindResp;
import com.adenki.smpp.util.APIMessages;

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
