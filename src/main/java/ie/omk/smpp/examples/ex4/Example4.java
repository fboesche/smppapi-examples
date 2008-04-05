package ie.omk.smpp.examples.ex4;

import ie.omk.smpp.Address;
import ie.omk.smpp.Session;
import ie.omk.smpp.encoding.DefaultAlphabetEncoding;
import ie.omk.smpp.examples.ExampleBase;
import ie.omk.smpp.message.Bind;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.util.AutoResponder;
import ie.omk.smpp.util.SyncWrapper;

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
