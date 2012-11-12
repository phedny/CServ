package nl.limesco.cserv.ant;

public class NetstatLine {

    private String m_protocol;
    
    private int m_receiveQueue;
    
    private int m_sendQueue;
    
    private String m_localAddress;
    
    private String m_foreignAddress;
    
    private String m_state;

    public String getProtocol() {
        return m_protocol;
    }

    public void setProtocol(String protocol) {
        m_protocol = protocol;
    }

    public int getReceiveQueue() {
        return m_receiveQueue;
    }

    public void setReceiveQueue(int receiveQueue) {
        m_receiveQueue = receiveQueue;
    }

    public int getSendQueue() {
        return m_sendQueue;
    }

    public void setSendQueue(int sendQueue) {
        m_sendQueue = sendQueue;
    }

    public String getLocalAddress() {
        return m_localAddress;
    }

    public void setLocalAddress(String localAddress) {
        m_localAddress = localAddress;
    }

    public String getForeignAddress() {
        return m_foreignAddress;
    }

    public void setForeignAddress(String foreignAddress) {
        m_foreignAddress = foreignAddress;
    }

    public String getState() {
        return m_state;
    }

    public void setState(String state) {
        m_state = state;
    }
    
}
