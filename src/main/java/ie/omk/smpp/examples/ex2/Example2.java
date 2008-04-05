package ie.omk.smpp.examples.ex2;

import ie.omk.smpp.Session;
import ie.omk.smpp.SessionState;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.examples.ExampleBase;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.util.AutoResponder;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An asynchronous receiver that uses an event adapter and an auto responder.
 * @version $Id:$
 */
public class Example2 extends ExampleBase {
    private static final Logger LOG = LoggerFactory.getLogger(Example2.class);

    private SMPPEventAdapter adapter = new Example2Adapter();
    private AutoResponder responder = new AutoResponder(true);
    
    public void run() throws Exception {
        Session session = createSession();
        session.addObserver(adapter);
        session.addObserver(responder);
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
