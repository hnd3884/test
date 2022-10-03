package org.openjsse.javax.net.ssl;

public abstract class SSLEngine extends javax.net.ssl.SSLEngine
{
    protected SSLEngine() {
    }
    
    protected SSLEngine(final String peerHost, final int peerPort) {
        super(peerHost, peerPort);
    }
    
    public abstract boolean needUnwrapAgain();
}
