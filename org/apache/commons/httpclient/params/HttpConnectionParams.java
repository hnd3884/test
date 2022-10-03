package org.apache.commons.httpclient.params;

public class HttpConnectionParams extends DefaultHttpParams
{
    public static final String SO_TIMEOUT = "http.socket.timeout";
    public static final String TCP_NODELAY = "http.tcp.nodelay";
    public static final String SO_SNDBUF = "http.socket.sendbuffer";
    public static final String SO_RCVBUF = "http.socket.receivebuffer";
    public static final String SO_LINGER = "http.socket.linger";
    public static final String CONNECTION_TIMEOUT = "http.connection.timeout";
    public static final String STALE_CONNECTION_CHECK = "http.connection.stalecheck";
    
    public int getSoTimeout() {
        return this.getIntParameter("http.socket.timeout", 0);
    }
    
    public void setSoTimeout(final int timeout) {
        this.setIntParameter("http.socket.timeout", timeout);
    }
    
    public void setTcpNoDelay(final boolean value) {
        this.setBooleanParameter("http.tcp.nodelay", value);
    }
    
    public boolean getTcpNoDelay() {
        return this.getBooleanParameter("http.tcp.nodelay", true);
    }
    
    public int getSendBufferSize() {
        return this.getIntParameter("http.socket.sendbuffer", -1);
    }
    
    public void setSendBufferSize(final int size) {
        this.setIntParameter("http.socket.sendbuffer", size);
    }
    
    public int getReceiveBufferSize() {
        return this.getIntParameter("http.socket.receivebuffer", -1);
    }
    
    public void setReceiveBufferSize(final int size) {
        this.setIntParameter("http.socket.receivebuffer", size);
    }
    
    public int getLinger() {
        return this.getIntParameter("http.socket.linger", -1);
    }
    
    public void setLinger(final int value) {
        this.setIntParameter("http.socket.linger", value);
    }
    
    public int getConnectionTimeout() {
        return this.getIntParameter("http.connection.timeout", 0);
    }
    
    public void setConnectionTimeout(final int timeout) {
        this.setIntParameter("http.connection.timeout", timeout);
    }
    
    public boolean isStaleCheckingEnabled() {
        return this.getBooleanParameter("http.connection.stalecheck", true);
    }
    
    public void setStaleCheckingEnabled(final boolean value) {
        this.setBooleanParameter("http.connection.stalecheck", value);
    }
}
