package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.security.cert.CertificateException;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import java.security.Permission;
import javax.net.ssl.SSLPermission;
import javax.net.ssl.SSLSessionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.security.Principal;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.ExtendedSSLSession;

final class SSLSessionImpl extends ExtendedSSLSession
{
    private static final byte compression_null = 0;
    private final ProtocolVersion protocolVersion;
    private final SessionId sessionId;
    private X509Certificate[] peerCerts;
    private byte compressionMethod;
    private CipherSuite cipherSuite;
    private SecretKey masterSecret;
    private final boolean useExtendedMasterSecret;
    private final long creationTime;
    private long lastUsedTime;
    private final String host;
    private final int port;
    private SSLSessionContextImpl context;
    private int sessionCount;
    private boolean invalidated;
    private X509Certificate[] localCerts;
    private PrivateKey localPrivateKey;
    private String[] localSupportedSignAlgs;
    private String[] peerSupportedSignAlgs;
    private List<SNIServerName> requestedServerNames;
    private Principal peerPrincipal;
    private Principal localPrincipal;
    private final String endpointIdentificationAlgorithm;
    private boolean isSessionResumption;
    private static volatile int counter;
    private static boolean defaultRejoinable;
    private static final Debug debug;
    private Hashtable<SecureKey, Object> table;
    private boolean acceptLargeFragments;
    
    SSLSessionImpl() {
        this(ProtocolVersion.NONE, CipherSuite.C_NULL, null, new SessionId(false, null), null, -1, false, null);
    }
    
    SSLSessionImpl(final ProtocolVersion protocolVersion, final CipherSuite cipherSuite, final Collection<SignatureAndHashAlgorithm> algorithms, final SecureRandom generator, final String host, final int port, final boolean useExtendedMasterSecret, final String endpointIdAlgorithm) {
        this(protocolVersion, cipherSuite, algorithms, new SessionId(SSLSessionImpl.defaultRejoinable, generator), host, port, useExtendedMasterSecret, endpointIdAlgorithm);
    }
    
    SSLSessionImpl(final ProtocolVersion protocolVersion, final CipherSuite cipherSuite, final Collection<SignatureAndHashAlgorithm> algorithms, final SessionId id, final String host, final int port, final boolean useExtendedMasterSecret, final String endpointIdAlgorithm) {
        this.creationTime = System.currentTimeMillis();
        this.lastUsedTime = 0L;
        this.isSessionResumption = false;
        this.table = new Hashtable<SecureKey, Object>();
        this.acceptLargeFragments = Debug.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
        this.protocolVersion = protocolVersion;
        this.sessionId = id;
        this.peerCerts = null;
        this.compressionMethod = 0;
        this.cipherSuite = cipherSuite;
        this.masterSecret = null;
        this.host = host;
        this.port = port;
        this.sessionCount = ++SSLSessionImpl.counter;
        this.localSupportedSignAlgs = SignatureAndHashAlgorithm.getAlgorithmNames(algorithms);
        this.useExtendedMasterSecret = useExtendedMasterSecret;
        this.endpointIdentificationAlgorithm = endpointIdAlgorithm;
        if (SSLSessionImpl.debug != null && Debug.isOn("session")) {
            System.out.println("%% Initialized:  " + this);
        }
    }
    
    void setMasterSecret(final SecretKey secret) {
        if (this.masterSecret == null) {
            this.masterSecret = secret;
            return;
        }
        throw new RuntimeException("setMasterSecret() error");
    }
    
    SecretKey getMasterSecret() {
        return this.masterSecret;
    }
    
    boolean getUseExtendedMasterSecret() {
        return this.useExtendedMasterSecret;
    }
    
    void setPeerCertificates(final X509Certificate[] peer) {
        if (this.peerCerts == null) {
            this.peerCerts = peer;
        }
    }
    
    void setLocalCertificates(final X509Certificate[] local) {
        this.localCerts = local;
    }
    
    void setLocalPrivateKey(final PrivateKey privateKey) {
        this.localPrivateKey = privateKey;
    }
    
    void setPeerSupportedSignatureAlgorithms(final Collection<SignatureAndHashAlgorithm> algorithms) {
        this.peerSupportedSignAlgs = SignatureAndHashAlgorithm.getAlgorithmNames(algorithms);
    }
    
    void setRequestedServerNames(final List<SNIServerName> requestedServerNames) {
        this.requestedServerNames = new ArrayList<SNIServerName>(requestedServerNames);
    }
    
    void setPeerPrincipal(final Principal principal) {
        if (this.peerPrincipal == null) {
            this.peerPrincipal = principal;
        }
    }
    
    void setLocalPrincipal(final Principal principal) {
        this.localPrincipal = principal;
    }
    
    String getEndpointIdentificationAlgorithm() {
        return this.endpointIdentificationAlgorithm;
    }
    
    boolean isRejoinable() {
        return this.sessionId != null && this.sessionId.length() != 0 && !this.invalidated && this.isLocalAuthenticationValid();
    }
    
    @Override
    public synchronized boolean isValid() {
        return this.isRejoinable();
    }
    
    boolean isLocalAuthenticationValid() {
        if (this.localPrivateKey != null) {
            try {
                this.localPrivateKey.getAlgorithm();
            }
            catch (final Exception e) {
                this.invalidate();
                return false;
            }
        }
        return true;
    }
    
    @Override
    public byte[] getId() {
        return this.sessionId.getId();
    }
    
    @Override
    public SSLSessionContext getSessionContext() {
        final SecurityManager sm;
        if ((sm = System.getSecurityManager()) != null) {
            sm.checkPermission(new SSLPermission("getSSLSessionContext"));
        }
        return this.context;
    }
    
    SessionId getSessionId() {
        return this.sessionId;
    }
    
    CipherSuite getSuite() {
        return this.cipherSuite;
    }
    
    void setSuite(final CipherSuite suite) {
        this.cipherSuite = suite;
        if (SSLSessionImpl.debug != null && Debug.isOn("session")) {
            System.out.println("%% Negotiating:  " + this);
        }
    }
    
    boolean isSessionResumption() {
        return this.isSessionResumption;
    }
    
    void setAsSessionResumption(final boolean flag) {
        this.isSessionResumption = flag;
    }
    
    @Override
    public String getCipherSuite() {
        return this.getSuite().name;
    }
    
    ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }
    
    @Override
    public String getProtocol() {
        return this.getProtocolVersion().name;
    }
    
    byte getCompression() {
        return this.compressionMethod;
    }
    
    @Override
    public int hashCode() {
        return this.sessionId.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SSLSessionImpl) {
            final SSLSessionImpl sess = (SSLSessionImpl)obj;
            return this.sessionId != null && this.sessionId.equals(sess.getSessionId());
        }
        return false;
    }
    
    @Override
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            throw new SSLPeerUnverifiedException("no certificates expected for Kerberos cipher suites");
        }
        if (this.peerCerts == null) {
            throw new SSLPeerUnverifiedException("peer not authenticated");
        }
        return this.peerCerts.clone();
    }
    
    @Override
    public Certificate[] getLocalCertificates() {
        return (Certificate[])((this.localCerts == null) ? null : ((Certificate[])this.localCerts.clone()));
    }
    
    @Override
    public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            throw new SSLPeerUnverifiedException("no certificates expected for Kerberos cipher suites");
        }
        if (this.peerCerts == null) {
            throw new SSLPeerUnverifiedException("peer not authenticated");
        }
        final javax.security.cert.X509Certificate[] certs = new javax.security.cert.X509Certificate[this.peerCerts.length];
        for (int i = 0; i < this.peerCerts.length; ++i) {
            byte[] der = null;
            try {
                der = this.peerCerts[i].getEncoded();
                certs[i] = javax.security.cert.X509Certificate.getInstance(der);
            }
            catch (final CertificateEncodingException e) {
                throw new SSLPeerUnverifiedException(e.getMessage());
            }
            catch (final CertificateException e2) {
                throw new SSLPeerUnverifiedException(e2.getMessage());
            }
        }
        return certs;
    }
    
    public X509Certificate[] getCertificateChain() throws SSLPeerUnverifiedException {
        if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            throw new SSLPeerUnverifiedException("no certificates expected for Kerberos cipher suites");
        }
        if (this.peerCerts != null) {
            return this.peerCerts.clone();
        }
        throw new SSLPeerUnverifiedException("peer not authenticated");
    }
    
    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            if (this.peerPrincipal == null) {
                throw new SSLPeerUnverifiedException("peer not authenticated");
            }
            return this.peerPrincipal;
        }
        else {
            if (this.peerCerts == null) {
                throw new SSLPeerUnverifiedException("peer not authenticated");
            }
            return this.peerCerts[0].getSubjectX500Principal();
        }
    }
    
    @Override
    public Principal getLocalPrincipal() {
        if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            return (this.localPrincipal == null) ? null : this.localPrincipal;
        }
        return (this.localCerts == null) ? null : this.localCerts[0].getSubjectX500Principal();
    }
    
    @Override
    public long getCreationTime() {
        return this.creationTime;
    }
    
    @Override
    public long getLastAccessedTime() {
        return (this.lastUsedTime != 0L) ? this.lastUsedTime : this.creationTime;
    }
    
    void setLastAccessedTime(final long time) {
        this.lastUsedTime = time;
    }
    
    public InetAddress getPeerAddress() {
        try {
            return InetAddress.getByName(this.host);
        }
        catch (final UnknownHostException e) {
            return null;
        }
    }
    
    @Override
    public String getPeerHost() {
        return this.host;
    }
    
    @Override
    public int getPeerPort() {
        return this.port;
    }
    
    void setContext(final SSLSessionContextImpl ctx) {
        if (this.context == null) {
            this.context = ctx;
        }
    }
    
    @Override
    public synchronized void invalidate() {
        this.invalidated = true;
        if (SSLSessionImpl.debug != null && Debug.isOn("session")) {
            System.out.println("%% Invalidated:  " + this);
        }
        if (this.context != null) {
            this.context.remove(this.sessionId);
            this.context = null;
        }
    }
    
    @Override
    public void putValue(final String key, final Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("arguments can not be null");
        }
        final SecureKey secureKey = new SecureKey(key);
        final Object oldValue = this.table.put(secureKey, value);
        if (oldValue instanceof SSLSessionBindingListener) {
            final SSLSessionBindingEvent e = new SSLSessionBindingEvent(this, key);
            ((SSLSessionBindingListener)oldValue).valueUnbound(e);
        }
        if (value instanceof SSLSessionBindingListener) {
            final SSLSessionBindingEvent e = new SSLSessionBindingEvent(this, key);
            ((SSLSessionBindingListener)value).valueBound(e);
        }
    }
    
    @Override
    public Object getValue(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        final SecureKey secureKey = new SecureKey(key);
        return this.table.get(secureKey);
    }
    
    @Override
    public void removeValue(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        final SecureKey secureKey = new SecureKey(key);
        final Object value = this.table.remove(secureKey);
        if (value instanceof SSLSessionBindingListener) {
            final SSLSessionBindingEvent e = new SSLSessionBindingEvent(this, key);
            ((SSLSessionBindingListener)value).valueUnbound(e);
        }
    }
    
    @Override
    public String[] getValueNames() {
        final Vector<Object> v = new Vector<Object>();
        final Object securityCtx = SecureKey.getCurrentSecurityContext();
        final Enumeration<SecureKey> e = this.table.keys();
        while (e.hasMoreElements()) {
            final SecureKey key = e.nextElement();
            if (securityCtx.equals(key.getSecurityContext())) {
                v.addElement(key.getAppKey());
            }
        }
        final String[] names = new String[v.size()];
        v.copyInto(names);
        return names;
    }
    
    protected synchronized void expandBufferSizes() {
        this.acceptLargeFragments = true;
    }
    
    @Override
    public synchronized int getPacketBufferSize() {
        return this.acceptLargeFragments ? 33305 : 16921;
    }
    
    @Override
    public synchronized int getApplicationBufferSize() {
        return this.getPacketBufferSize() - 5;
    }
    
    @Override
    public String[] getLocalSupportedSignatureAlgorithms() {
        if (this.localSupportedSignAlgs != null) {
            return this.localSupportedSignAlgs.clone();
        }
        return new String[0];
    }
    
    @Override
    public String[] getPeerSupportedSignatureAlgorithms() {
        if (this.peerSupportedSignAlgs != null) {
            return this.peerSupportedSignAlgs.clone();
        }
        return new String[0];
    }
    
    @Override
    public List<SNIServerName> getRequestedServerNames() {
        if (this.requestedServerNames != null && !this.requestedServerNames.isEmpty()) {
            return Collections.unmodifiableList((List<? extends SNIServerName>)this.requestedServerNames);
        }
        return Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return "[Session-" + this.sessionCount + ", " + this.getCipherSuite() + "]";
    }
    
    @Override
    protected void finalize() throws Throwable {
        final String[] names = this.getValueNames();
        for (int i = 0; i < names.length; ++i) {
            this.removeValue(names[i]);
        }
    }
    
    static {
        SSLSessionImpl.counter = 0;
        SSLSessionImpl.defaultRejoinable = true;
        debug = Debug.getInstance("ssl");
    }
}
