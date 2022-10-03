package org.bouncycastle.crypto.tls;

import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;

public interface TlsServer extends TlsPeer
{
    void init(final TlsServerContext p0);
    
    void notifyClientVersion(final ProtocolVersion p0) throws IOException;
    
    void notifyFallback(final boolean p0) throws IOException;
    
    void notifyOfferedCipherSuites(final int[] p0) throws IOException;
    
    void notifyOfferedCompressionMethods(final short[] p0) throws IOException;
    
    void processClientExtensions(final Hashtable p0) throws IOException;
    
    ProtocolVersion getServerVersion() throws IOException;
    
    int getSelectedCipherSuite() throws IOException;
    
    short getSelectedCompressionMethod() throws IOException;
    
    Hashtable getServerExtensions() throws IOException;
    
    Vector getServerSupplementalData() throws IOException;
    
    TlsCredentials getCredentials() throws IOException;
    
    CertificateStatus getCertificateStatus() throws IOException;
    
    TlsKeyExchange getKeyExchange() throws IOException;
    
    CertificateRequest getCertificateRequest() throws IOException;
    
    void processClientSupplementalData(final Vector p0) throws IOException;
    
    void notifyClientCertificate(final Certificate p0) throws IOException;
    
    NewSessionTicket getNewSessionTicket() throws IOException;
}
