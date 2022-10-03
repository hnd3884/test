package org.openjsse.sun.security.ssl;

import java.util.Enumeration;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.security.Principal;
import java.util.Iterator;
import javax.security.cert.CertificateException;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import java.security.Permission;
import javax.net.ssl.SSLPermission;
import javax.net.ssl.SSLSessionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import javax.net.ssl.SNIServerName;
import java.util.Queue;
import java.util.List;
import java.util.Collection;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import org.openjsse.javax.net.ssl.ExtendedSSLSession;

final class SSLSessionImpl extends ExtendedSSLSession
{
    private final ProtocolVersion protocolVersion;
    private final SessionId sessionId;
    private X509Certificate[] peerCerts;
    private CipherSuite cipherSuite;
    private SecretKey masterSecret;
    final boolean useExtendedMasterSecret;
    private final long creationTime;
    private long lastUsedTime;
    private final String host;
    private final int port;
    private SSLSessionContextImpl context;
    private boolean invalidated;
    private X509Certificate[] localCerts;
    private PrivateKey localPrivateKey;
    private final Collection<SignatureScheme> localSupportedSignAlgs;
    private String[] peerSupportedSignAlgs;
    private boolean useDefaultPeerSignAlgs;
    private List<byte[]> statusResponses;
    private SecretKey resumptionMasterSecret;
    private SecretKey preSharedKey;
    private byte[] pskIdentity;
    private final long ticketCreationTime;
    private int ticketAgeAdd;
    private int negotiatedMaxFragLen;
    private int maximumPacketSize;
    private final Queue<SSLSessionImpl> childSessions;
    private boolean isSessionResumption;
    private static boolean defaultRejoinable;
    final SNIServerName serverNameIndication;
    private final List<SNIServerName> requestedServerNames;
    private BigInteger ticketNonceCounter;
    private final String identificationProtocol;
    private X500Principal[] certificateAuthorities;
    private final ConcurrentHashMap<SecureKey, Object> boundValues;
    private boolean acceptLargeFragments;
    
    SSLSessionImpl() {
        this.lastUsedTime = 0L;
        this.useDefaultPeerSignAlgs = false;
        this.ticketCreationTime = System.currentTimeMillis();
        this.negotiatedMaxFragLen = -1;
        this.childSessions = new ConcurrentLinkedQueue<SSLSessionImpl>();
        this.isSessionResumption = false;
        this.ticketNonceCounter = BigInteger.ONE;
        this.acceptLargeFragments = Utilities.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
        this.protocolVersion = ProtocolVersion.NONE;
        this.cipherSuite = CipherSuite.C_NULL;
        this.sessionId = new SessionId(false, null);
        this.host = null;
        this.port = -1;
        this.localSupportedSignAlgs = (Collection<SignatureScheme>)Collections.emptySet();
        this.serverNameIndication = null;
        this.requestedServerNames = Collections.emptyList();
        this.useExtendedMasterSecret = false;
        this.creationTime = System.currentTimeMillis();
        this.identificationProtocol = null;
        this.boundValues = new ConcurrentHashMap<SecureKey, Object>();
    }
    
    SSLSessionImpl(final HandshakeContext hc, final CipherSuite cipherSuite) {
        this(hc, cipherSuite, new SessionId(SSLSessionImpl.defaultRejoinable, hc.sslContext.getSecureRandom()));
    }
    
    SSLSessionImpl(final HandshakeContext hc, final CipherSuite cipherSuite, final SessionId id) {
        this(hc, cipherSuite, id, System.currentTimeMillis());
    }
    
    SSLSessionImpl(final HandshakeContext hc, final CipherSuite cipherSuite, final SessionId id, final long creationTime) {
        this.lastUsedTime = 0L;
        this.useDefaultPeerSignAlgs = false;
        this.ticketCreationTime = System.currentTimeMillis();
        this.negotiatedMaxFragLen = -1;
        this.childSessions = new ConcurrentLinkedQueue<SSLSessionImpl>();
        this.isSessionResumption = false;
        this.ticketNonceCounter = BigInteger.ONE;
        this.acceptLargeFragments = Utilities.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
        this.protocolVersion = hc.negotiatedProtocol;
        this.cipherSuite = cipherSuite;
        this.sessionId = id;
        this.host = hc.conContext.transport.getPeerHost();
        this.port = hc.conContext.transport.getPeerPort();
        this.localSupportedSignAlgs = (Collection<SignatureScheme>)((hc.localSupportedSignAlgs == null) ? Collections.emptySet() : Collections.unmodifiableCollection((Collection<?>)new ArrayList<Object>(hc.localSupportedSignAlgs)));
        this.serverNameIndication = hc.negotiatedServerName;
        this.requestedServerNames = Collections.unmodifiableList((List<? extends SNIServerName>)new ArrayList<SNIServerName>(hc.getRequestedServerNames()));
        if (hc.sslConfig.isClientMode) {
            this.useExtendedMasterSecret = (hc.handshakeExtensions.get(SSLExtension.CH_EXTENDED_MASTER_SECRET) != null && hc.handshakeExtensions.get(SSLExtension.SH_EXTENDED_MASTER_SECRET) != null);
        }
        else {
            this.useExtendedMasterSecret = (hc.handshakeExtensions.get(SSLExtension.CH_EXTENDED_MASTER_SECRET) != null && !hc.negotiatedProtocol.useTLS13PlusSpec());
        }
        this.creationTime = creationTime;
        this.identificationProtocol = hc.sslConfig.identificationProtocol;
        this.boundValues = new ConcurrentHashMap<SecureKey, Object>();
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Session initialized:  " + this, new Object[0]);
        }
    }
    
    SSLSessionImpl(final SSLSessionImpl baseSession, final SessionId newId) {
        this.lastUsedTime = 0L;
        this.useDefaultPeerSignAlgs = false;
        this.ticketCreationTime = System.currentTimeMillis();
        this.negotiatedMaxFragLen = -1;
        this.childSessions = new ConcurrentLinkedQueue<SSLSessionImpl>();
        this.isSessionResumption = false;
        this.ticketNonceCounter = BigInteger.ONE;
        this.acceptLargeFragments = Utilities.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
        this.protocolVersion = baseSession.getProtocolVersion();
        this.cipherSuite = baseSession.cipherSuite;
        this.sessionId = newId;
        this.host = baseSession.getPeerHost();
        this.port = baseSession.getPeerPort();
        this.localSupportedSignAlgs = (Collection<SignatureScheme>)((baseSession.localSupportedSignAlgs == null) ? Collections.emptySet() : baseSession.localSupportedSignAlgs);
        this.peerSupportedSignAlgs = baseSession.getPeerSupportedSignatureAlgorithms();
        this.serverNameIndication = baseSession.serverNameIndication;
        this.requestedServerNames = baseSession.getRequestedServerNames();
        this.masterSecret = baseSession.getMasterSecret();
        this.useExtendedMasterSecret = baseSession.useExtendedMasterSecret;
        this.creationTime = baseSession.getCreationTime();
        this.lastUsedTime = System.currentTimeMillis();
        this.identificationProtocol = baseSession.getIdentificationProtocol();
        this.localCerts = baseSession.localCerts;
        this.peerCerts = baseSession.peerCerts;
        this.statusResponses = baseSession.statusResponses;
        this.resumptionMasterSecret = baseSession.resumptionMasterSecret;
        this.context = baseSession.context;
        this.negotiatedMaxFragLen = baseSession.negotiatedMaxFragLen;
        this.maximumPacketSize = baseSession.maximumPacketSize;
        this.boundValues = baseSession.boundValues;
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Session initialized:  " + this, new Object[0]);
        }
    }
    
    void setMasterSecret(final SecretKey secret) {
        this.masterSecret = secret;
    }
    
    void setResumptionMasterSecret(final SecretKey secret) {
        this.resumptionMasterSecret = secret;
    }
    
    void setPreSharedKey(final SecretKey key) {
        this.preSharedKey = key;
    }
    
    void addChild(final SSLSessionImpl session) {
        this.childSessions.add(session);
    }
    
    void setTicketAgeAdd(final int ticketAgeAdd) {
        this.ticketAgeAdd = ticketAgeAdd;
    }
    
    void setPskIdentity(final byte[] pskIdentity) {
        this.pskIdentity = pskIdentity;
    }
    
    BigInteger incrTicketNonceCounter() {
        final BigInteger result = this.ticketNonceCounter;
        this.ticketNonceCounter = this.ticketNonceCounter.add(BigInteger.valueOf(1L));
        return result;
    }
    
    SecretKey getMasterSecret() {
        return this.masterSecret;
    }
    
    SecretKey getResumptionMasterSecret() {
        return this.resumptionMasterSecret;
    }
    
    synchronized SecretKey getPreSharedKey() {
        return this.preSharedKey;
    }
    
    synchronized SecretKey consumePreSharedKey() {
        try {
            return this.preSharedKey;
        }
        finally {
            this.preSharedKey = null;
        }
    }
    
    int getTicketAgeAdd() {
        return this.ticketAgeAdd;
    }
    
    String getIdentificationProtocol() {
        return this.identificationProtocol;
    }
    
    synchronized byte[] consumePskIdentity() {
        try {
            return this.pskIdentity;
        }
        finally {
            this.pskIdentity = null;
        }
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
    
    void setPeerSupportedSignatureAlgorithms(final Collection<SignatureScheme> signatureSchemes) {
        this.peerSupportedSignAlgs = SignatureScheme.getAlgorithmNames(signatureSchemes);
    }
    
    void setUseDefaultPeerSignAlgs() {
        this.useDefaultPeerSignAlgs = true;
        this.peerSupportedSignAlgs = new String[] { "SHA1withRSA", "SHA1withDSA", "SHA1withECDSA" };
    }
    
    SSLSessionImpl finish() {
        if (this.useDefaultPeerSignAlgs) {
            this.peerSupportedSignAlgs = new String[0];
        }
        return this;
    }
    
    void setStatusResponses(final List<byte[]> responses) {
        if (responses != null && !responses.isEmpty()) {
            this.statusResponses = responses;
        }
        else {
            this.statusResponses = Collections.emptyList();
        }
    }
    
    boolean isRejoinable() {
        return this.sessionId != null && this.sessionId.length() != 0 && !this.invalidated && this.isLocalAuthenticationValid();
    }
    
    @Override
    public synchronized boolean isValid() {
        return this.isRejoinable();
    }
    
    private boolean isLocalAuthenticationValid() {
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
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Negotiating session:  " + this, new Object[0]);
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
    
    void setCertificateAuthorities(final X500Principal[] certificateAuthorities) {
        this.certificateAuthorities = certificateAuthorities;
    }
    
    X500Principal[] getCertificateAuthorities() {
        return this.certificateAuthorities;
    }
    
    @Override
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        if (this.peerCerts == null) {
            throw new SSLPeerUnverifiedException("peer not authenticated");
        }
        return this.peerCerts.clone();
    }
    
    @Override
    public Certificate[] getLocalCertificates() {
        return (Certificate[])((this.localCerts == null) ? null : ((Certificate[])this.localCerts.clone()));
    }
    
    @Deprecated
    @Override
    public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
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
        if (this.peerCerts != null) {
            return this.peerCerts.clone();
        }
        throw new SSLPeerUnverifiedException("peer not authenticated");
    }
    
    @Override
    public List<byte[]> getStatusResponses() {
        if (this.statusResponses == null || this.statusResponses.isEmpty()) {
            return Collections.emptyList();
        }
        final List<byte[]> responses = new ArrayList<byte[]>(this.statusResponses.size());
        for (final byte[] respBytes : this.statusResponses) {
            responses.add(respBytes.clone());
        }
        return Collections.unmodifiableList((List<? extends byte[]>)responses);
    }
    
    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        if (this.peerCerts == null) {
            throw new SSLPeerUnverifiedException("peer not authenticated");
        }
        return this.peerCerts[0].getSubjectX500Principal();
    }
    
    @Override
    public Principal getLocalPrincipal() {
        return (this.localCerts == null || this.localCerts.length == 0) ? null : this.localCerts[0].getSubjectX500Principal();
    }
    
    public long getTicketCreationTime() {
        return this.ticketCreationTime;
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
        if (this.context != null) {
            this.context.remove(this.sessionId);
            this.context = null;
        }
        if (this.invalidated) {
            return;
        }
        this.invalidated = true;
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Invalidated session:  " + this, new Object[0]);
        }
        for (final SSLSessionImpl child : this.childSessions) {
            child.invalidate();
        }
    }
    
    @Override
    public void putValue(final String key, final Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("arguments can not be null");
        }
        final SecureKey secureKey = new SecureKey(key);
        final Object oldValue = this.boundValues.put(secureKey, value);
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
        return this.boundValues.get(secureKey);
    }
    
    @Override
    public void removeValue(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        final SecureKey secureKey = new SecureKey(key);
        final Object value = this.boundValues.remove(secureKey);
        if (value instanceof SSLSessionBindingListener) {
            final SSLSessionBindingEvent e = new SSLSessionBindingEvent(this, key);
            ((SSLSessionBindingListener)value).valueUnbound(e);
        }
    }
    
    @Override
    public String[] getValueNames() {
        final ArrayList<Object> v = new ArrayList<Object>();
        final Object securityCtx = SecureKey.getCurrentSecurityContext();
        final Enumeration<SecureKey> e = this.boundValues.keys();
        while (e.hasMoreElements()) {
            final SecureKey key = e.nextElement();
            if (securityCtx.equals(key.getSecurityContext())) {
                v.add(key.getAppKey());
            }
        }
        return v.toArray(new String[0]);
    }
    
    protected synchronized void expandBufferSizes() {
        this.acceptLargeFragments = true;
    }
    
    @Override
    public synchronized int getPacketBufferSize() {
        int packetSize = 0;
        if (this.negotiatedMaxFragLen > 0) {
            packetSize = this.cipherSuite.calculatePacketSize(this.negotiatedMaxFragLen, this.protocolVersion, this.protocolVersion.isDTLS);
        }
        if (this.maximumPacketSize > 0) {
            return (this.maximumPacketSize > packetSize) ? this.maximumPacketSize : packetSize;
        }
        if (packetSize != 0) {
            return packetSize;
        }
        if (this.protocolVersion.isDTLS) {
            return 16717;
        }
        return this.acceptLargeFragments ? 33093 : 16709;
    }
    
    @Override
    public synchronized int getApplicationBufferSize() {
        int fragmentSize = 0;
        if (this.maximumPacketSize > 0) {
            fragmentSize = this.cipherSuite.calculateFragSize(this.maximumPacketSize, this.protocolVersion, this.protocolVersion.isDTLS);
        }
        if (this.negotiatedMaxFragLen > 0) {
            return (this.negotiatedMaxFragLen > fragmentSize) ? this.negotiatedMaxFragLen : fragmentSize;
        }
        if (fragmentSize != 0) {
            return fragmentSize;
        }
        if (this.protocolVersion.isDTLS) {
            return 16384;
        }
        final int maxPacketSize = this.acceptLargeFragments ? 33093 : 16709;
        return maxPacketSize - 5;
    }
    
    synchronized void setNegotiatedMaxFragSize(final int negotiatedMaxFragLen) {
        this.negotiatedMaxFragLen = negotiatedMaxFragLen;
    }
    
    synchronized int getNegotiatedMaxFragSize() {
        return this.negotiatedMaxFragLen;
    }
    
    synchronized void setMaximumPacketSize(final int maximumPacketSize) {
        this.maximumPacketSize = maximumPacketSize;
    }
    
    synchronized int getMaximumPacketSize() {
        return this.maximumPacketSize;
    }
    
    @Override
    public String[] getLocalSupportedSignatureAlgorithms() {
        return SignatureScheme.getAlgorithmNames(this.localSupportedSignAlgs);
    }
    
    public Collection<SignatureScheme> getLocalSupportedSignatureSchemes() {
        return this.localSupportedSignAlgs;
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
        return this.requestedServerNames;
    }
    
    @Override
    public String toString() {
        return "Session(" + this.creationTime + "|" + this.getCipherSuite() + ")";
    }
    
    static {
        SSLSessionImpl.defaultRejoinable = true;
    }
}
