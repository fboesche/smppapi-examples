package ie.omk.smpp.examples;

import ie.omk.smpp.Session;
import ie.omk.smpp.message.Bind;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.APIMessages;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;


public abstract class ExampleBase implements SmppapiExample {
    
    protected APIMessages apiMessages = new APIMessages();
    
    private String hostname;
    private int port;
    private int addressTon;
    private int addressNpi;
    private String addressRange;
    private String systemType;
    private String systemId;
    private String password;
    
    public int getAddressNpi() {
        return addressNpi;
    }

    public void setAddressNpi(int addressNpi) {
        this.addressNpi = addressNpi;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    public int getAddressTon() {
        return addressTon;
    }

    public void setAddressTon(int addressTon) {
        this.addressTon = addressTon;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }
    
    protected abstract Logger getLog();
    
    protected Session createSession() throws UnknownHostException {
        return new Session(hostname, port);
    }

    protected void send(Session session, SMPPPacket packet) {
        try {
            session.sendPacket(packet);
        } catch (IOException x) {
            getLog().error("Unable to send packet {}", packet);
            getLog().error("Stack trace: ", x);
        }
    }
    
    protected Bind initBindReq(Bind bindRequest) {
        bindRequest.setAddressTon(getAddressTon());
        bindRequest.setAddressNpi(getAddressNpi());
        bindRequest.setAddressRange(getAddressRange());
        bindRequest.setSystemType(getSystemType());
        bindRequest.setSystemId(getSystemId());
        bindRequest.setPassword(getPassword());
        return bindRequest;
    }
}
