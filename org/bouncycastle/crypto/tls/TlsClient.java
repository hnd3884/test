package org.bouncycastle.crypto.tls;

import java.util.Vector;
import java.io.IOException;
import java.util.Hashtable;

public interface TlsClient extends TlsPeer
{
    void init(final TlsClientContext p0);
    
    TlsSession getSessionToResume();
    
    ProtocolVersion getClientHelloRecordLayerVersion();
    
    ProtocolVersion getClientVersion();
    
    boolean isFallback();
    
    int[] getCipherSuites();
    
    short[] getCompressionMethods();
    
    Hashtable getClientExtensions() throws IOException;
    
    void notifyServerVersion(final ProtocolVersion p0) throws IOException;
    
    void notifySessionID(final byte[] p0);
    
    void notifySelectedCipherSuite(final int p0);
    
    void notifySelectedCompressionMethod(final short p0);
    
    void processServerExtensions(final Hashtable p0) throws IOException;
    
    void processServerSupplementalData(final Vector p0) throws IOException;
    
    TlsKeyExchange getKeyExchange() throws IOException;
    
    TlsAuthentication getAuthentication() throws IOException;
    
    Vector getClientSupplementalData() throws IOException;
    
    void notifyNewSessionTicket(final NewSessionTicket p0) throws IOException;
}
