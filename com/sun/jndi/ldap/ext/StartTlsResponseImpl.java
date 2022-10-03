package com.sun.jndi.ldap.ext;

import java.security.cert.Certificate;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import sun.security.util.HostnameChecker;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.OutputStream;
import java.io.InputStream;
import com.sun.jndi.ldap.Connection;
import javax.naming.ldap.StartTlsResponse;

public final class StartTlsResponseImpl extends StartTlsResponse
{
    private static final boolean debug = false;
    private static final int DNSNAME_TYPE = 2;
    private transient String hostname;
    private transient Connection ldapConnection;
    private transient InputStream originalInputStream;
    private transient OutputStream originalOutputStream;
    private transient SSLSocket sslSocket;
    private transient SSLSocketFactory defaultFactory;
    private transient SSLSocketFactory currentFactory;
    private transient String[] suites;
    private transient HostnameVerifier verifier;
    private transient boolean isClosed;
    private static final long serialVersionUID = -1126624615143411328L;
    
    public StartTlsResponseImpl() {
        this.hostname = null;
        this.ldapConnection = null;
        this.originalInputStream = null;
        this.originalOutputStream = null;
        this.sslSocket = null;
        this.defaultFactory = null;
        this.currentFactory = null;
        this.suites = null;
        this.verifier = null;
        this.isClosed = true;
    }
    
    @Override
    public void setEnabledCipherSuites(final String[] array) {
        this.suites = (String[])((array == null) ? null : ((String[])array.clone()));
    }
    
    @Override
    public void setHostnameVerifier(final HostnameVerifier verifier) {
        this.verifier = verifier;
    }
    
    @Override
    public SSLSession negotiate() throws IOException {
        return this.negotiate(null);
    }
    
    @Override
    public SSLSession negotiate(SSLSocketFactory defaultFactory) throws IOException {
        if (this.isClosed && this.sslSocket != null) {
            throw new IOException("TLS connection is closed.");
        }
        if (defaultFactory == null) {
            defaultFactory = this.getDefaultFactory();
        }
        final SSLSession session = this.startHandshake(defaultFactory).getSession();
        Object o = null;
        try {
            if (this.verify(this.hostname, session)) {
                this.isClosed = false;
                return session;
            }
        }
        catch (final SSLPeerUnverifiedException ex) {
            o = ex;
        }
        if (this.verifier != null && this.verifier.verify(this.hostname, session)) {
            this.isClosed = false;
            return session;
        }
        this.close();
        session.invalidate();
        if (o == null) {
            o = new SSLPeerUnverifiedException("hostname of the server '" + this.hostname + "' does not match the hostname in the server's certificate.");
        }
        throw o;
    }
    
    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.ldapConnection.replaceStreams(this.originalInputStream, this.originalOutputStream, false);
        this.sslSocket.close();
        this.isClosed = true;
    }
    
    public void setConnection(final Connection ldapConnection, final String s) {
        this.ldapConnection = ldapConnection;
        this.hostname = ((s == null || s.isEmpty()) ? ldapConnection.host : s);
        this.originalInputStream = ldapConnection.inStream;
        this.originalOutputStream = ldapConnection.outStream;
    }
    
    private SSLSocketFactory getDefaultFactory() throws IOException {
        if (this.defaultFactory != null) {
            return this.defaultFactory;
        }
        return this.defaultFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    }
    
    private SSLSocket startHandshake(final SSLSocketFactory currentFactory) throws IOException {
        if (this.ldapConnection == null) {
            throw new IllegalStateException("LDAP connection has not been set. TLS requires an existing LDAP connection.");
        }
        if (currentFactory != this.currentFactory) {
            this.sslSocket = (SSLSocket)currentFactory.createSocket(this.ldapConnection.sock, this.ldapConnection.host, this.ldapConnection.port, false);
            this.currentFactory = currentFactory;
        }
        if (this.suites != null) {
            this.sslSocket.setEnabledCipherSuites(this.suites);
        }
        try {
            this.sslSocket.startHandshake();
            this.ldapConnection.replaceStreams(this.sslSocket.getInputStream(), this.sslSocket.getOutputStream(), true);
        }
        catch (final IOException ex) {
            this.sslSocket.close();
            this.isClosed = true;
            throw ex;
        }
        return this.sslSocket;
    }
    
    private boolean verify(String substring, final SSLSession sslSession) throws SSLPeerUnverifiedException {
        if (substring != null && substring.startsWith("[") && substring.endsWith("]")) {
            substring = substring.substring(1, substring.length() - 1);
        }
        try {
            final HostnameChecker instance = HostnameChecker.getInstance((byte)2);
            if (sslSession.getCipherSuite().startsWith("TLS_KRB5")) {
                final Principal peerPrincipal = getPeerPrincipal(sslSession);
                if (!HostnameChecker.match(substring, peerPrincipal)) {
                    throw new SSLPeerUnverifiedException("hostname of the kerberos principal:" + peerPrincipal + " does not match the hostname:" + substring);
                }
            }
            else {
                final Certificate[] peerCertificates = sslSession.getPeerCertificates();
                if (!(peerCertificates[0] instanceof X509Certificate)) {
                    throw new SSLPeerUnverifiedException("Received a non X509Certificate from the server");
                }
                instance.match(substring, (X509Certificate)peerCertificates[0]);
            }
            return true;
        }
        catch (final SSLPeerUnverifiedException ex) {
            final String cipherSuite = sslSession.getCipherSuite();
            if (cipherSuite != null && cipherSuite.indexOf("_anon_") != -1) {
                return true;
            }
            throw ex;
        }
        catch (final CertificateException ex2) {
            throw (SSLPeerUnverifiedException)new SSLPeerUnverifiedException("hostname of the server '" + substring + "' does not match the hostname in the server's certificate.").initCause(ex2);
        }
    }
    
    private static Principal getPeerPrincipal(final SSLSession sslSession) throws SSLPeerUnverifiedException {
        Principal peerPrincipal;
        try {
            peerPrincipal = sslSession.getPeerPrincipal();
        }
        catch (final AbstractMethodError abstractMethodError) {
            peerPrincipal = null;
        }
        return peerPrincipal;
    }
}
