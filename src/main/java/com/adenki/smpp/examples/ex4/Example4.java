package com.adenki.smpp.examples.ex4;

import com.adenki.smpp.Address;
import com.adenki.smpp.Session;
import com.adenki.smpp.encoding.DefaultAlphabetEncoding;
import com.adenki.smpp.examples.ExampleBase;
import com.adenki.smpp.message.Bind;
import com.adenki.smpp.message.BindResp;
import com.adenki.smpp.message.BindTransmitter;
import com.adenki.smpp.message.SubmitSM;
import com.adenki.smpp.message.SubmitSMResp;
import com.adenki.smpp.message.Unbind;
import com.adenki.smpp.message.UnbindResp;
import com.adenki.smpp.util.AutoResponder;
import com.adenki.smpp.util.SyncWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An example of the new synchronous behaviour.
 * @version $Id:$
 */
public class Example4 extends ExampleBase {
    private static final Logger LOG = LoggerFactory.getLogger(Example4.class);
    
    private AutoResponder responder = new AutoResponder(true);
    private SyncWrapper syncWrapper;
    
    public void run() throws Exception {
        Session session = createSession();
        session.addObserver(responder);
        syncWrapper = new SyncWrapper(session);
        BindResp bindResp = syncWrapper.bind(getBindRequest());
        if (bindResp.getCommandStatus() != 0) {
            LOG.error("Failed to bind: {}",
                    apiMessages.getPacketStatus(bindResp.getCommandStatus()));
            session.closeLink();
            return;
        }
        SubmitSM submitSm = new SubmitSM();
        submitSm.setDestination(new Address(0, 0, "+155578912"));
        submitSm.setMessage(new DefaultAlphabetEncoding().encode("Hello World"));
        SubmitSMResp submitSmResp = (SubmitSMResp) syncWrapper.sendPacket(submitSm);
        if (submitSmResp.getCommandStatus() != 0) {
            LOG.error("Could not submit a message: {}",
                    apiMessages.getPacketStatus(submitSmResp.getCommandStatus()));
        }
        UnbindResp unbindResp = (UnbindResp) syncWrapper.sendPacket(new Unbind());
        if (unbindResp.getCommandStatus() != 0) {
            LOG.error("Failed to unbind: {}",
                    apiMessages.getPacketStatus(unbindResp.getCommandStatus()));
        }
        session.closeLink();
    }
    
    @Override
    protected Logger getLog() {
        return LOG;
    }
    
    private Bind getBindRequest() {
        return initBindReq(new BindTransmitter());
    }
}
