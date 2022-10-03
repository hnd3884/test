package sun.security.ssl;

import java.util.Enumeration;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import java.net.UnknownHostException;
import java.net.InetAddress;
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
import java.math.BigInteger;
import javax.net.ssl.SNIServerName;
import java.util.Queue;
import java.util.List;
import java.util.Collection;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.Principal;
import java.security.cert.X509Certificate;
import javax.net.ssl.ExtendedSSLSession;

final class SSLSessionImpl extends ExtendedSSLSession
{
    private final ProtocolVersion protocolVersion;
    private final SessionId sessionId;
    private X509Certificate[] peerCerts;
    private Principal peerPrincipal;
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
    private Principal localPrincipal;
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
    
    SSLSessionImpl(final HandshakeContext handshakeContext, final CipherSuite cipherSuite) {
        this(handshakeContext, cipherSuite, new SessionId(SSLSessionImpl.defaultRejoinable, handshakeContext.sslContext.getSecureRandom()));
    }
    
    SSLSessionImpl(final HandshakeContext handshakeContext, final CipherSuite cipherSuite, final SessionId sessionId) {
        this(handshakeContext, cipherSuite, sessionId, System.currentTimeMillis());
    }
    
    SSLSessionImpl(final HandshakeContext handshakeContext, final CipherSuite cipherSuite, final SessionId sessionId, final long creationTime) {
        this.lastUsedTime = 0L;
        this.useDefaultPeerSignAlgs = false;
        this.ticketCreationTime = System.currentTimeMillis();
        this.negotiatedMaxFragLen = -1;
        this.childSessions = new ConcurrentLinkedQueue<SSLSessionImpl>();
        this.isSessionResumption = false;
        this.ticketNonceCounter = BigInteger.ONE;
        this.acceptLargeFragments = Utilities.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
        this.protocolVersion = handshakeContext.negotiatedProtocol;
        this.cipherSuite = cipherSuite;
        this.sessionId = sessionId;
        this.host = handshakeContext.conContext.transport.getPeerHost();
        this.port = handshakeContext.conContext.transport.getPeerPort();
        this.localSupportedSignAlgs = (Collection<SignatureScheme>)((handshakeContext.localSupportedSignAlgs == null) ? Collections.emptySet() : Collections.unmodifiableCollection((Collection<?>)new ArrayList<Object>(handshakeContext.localSupportedSignAlgs)));
        this.serverNameIndication = handshakeContext.negotiatedServerName;
        this.requestedServerNames = Collections.unmodifiableList((List<? extends SNIServerName>)new ArrayList<SNIServerName>(handshakeContext.getRequestedServerNames()));
        if (handshakeContext.sslConfig.isClientMode) {
            this.useExtendedMasterSecret = (handshakeContext.handshakeExtensions.get(SSLExtension.CH_EXTENDED_MASTER_SECRET) != null && handshakeContext.handshakeExtensions.get(SSLExtension.SH_EXTENDED_MASTER_SECRET) != null);
        }
        else {
            this.useExtendedMasterSecret = (handshakeContext.handshakeExtensions.get(SSLExtension.CH_EXTENDED_MASTER_SECRET) != null && !handshakeContext.negotiatedProtocol.useTLS13PlusSpec());
        }
        this.creationTime = creationTime;
        this.identificationProtocol = handshakeContext.sslConfig.identificationProtocol;
        this.boundValues = new ConcurrentHashMap<SecureKey, Object>();
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Session initialized:  " + this, new Object[0]);
        }
    }
    
    SSLSessionImpl(final SSLSessionImpl sslSessionImpl, final SessionId sessionId) {
        this.lastUsedTime = 0L;
        this.useDefaultPeerSignAlgs = false;
        this.ticketCreationTime = System.currentTimeMillis();
        this.negotiatedMaxFragLen = -1;
        this.childSessions = new ConcurrentLinkedQueue<SSLSessionImpl>();
        this.isSessionResumption = false;
        this.ticketNonceCounter = BigInteger.ONE;
        this.acceptLargeFragments = Utilities.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
        this.protocolVersion = sslSessionImpl.getProtocolVersion();
        this.cipherSuite = sslSessionImpl.cipherSuite;
        this.sessionId = sessionId;
        this.host = sslSessionImpl.getPeerHost();
        this.port = sslSessionImpl.getPeerPort();
        this.localSupportedSignAlgs = (Collection<SignatureScheme>)((sslSessionImpl.localSupportedSignAlgs == null) ? Collections.emptySet() : sslSessionImpl.localSupportedSignAlgs);
        this.peerSupportedSignAlgs = sslSessionImpl.getPeerSupportedSignatureAlgorithms();
        this.serverNameIndication = sslSessionImpl.serverNameIndication;
        this.requestedServerNames = sslSessionImpl.getRequestedServerNames();
        this.masterSecret = sslSessionImpl.getMasterSecret();
        this.useExtendedMasterSecret = sslSessionImpl.useExtendedMasterSecret;
        this.creationTime = sslSessionImpl.getCreationTime();
        this.lastUsedTime = System.currentTimeMillis();
        this.identificationProtocol = sslSessionImpl.getIdentificationProtocol();
        this.localCerts = sslSessionImpl.localCerts;
        this.peerCerts = sslSessionImpl.peerCerts;
        this.localPrincipal = sslSessionImpl.localPrincipal;
        this.peerPrincipal = sslSessionImpl.peerPrincipal;
        this.statusResponses = sslSessionImpl.statusResponses;
        this.resumptionMasterSecret = sslSessionImpl.resumptionMasterSecret;
        this.context = sslSessionImpl.context;
        this.negotiatedMaxFragLen = sslSessionImpl.negotiatedMaxFragLen;
        this.maximumPacketSize = sslSessionImpl.maximumPacketSize;
        this.boundValues = sslSessionImpl.boundValues;
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Session initialized:  " + this, new Object[0]);
        }
    }
    
    void setMasterSecret(final SecretKey masterSecret) {
        this.masterSecret = masterSecret;
    }
    
    void setResumptionMasterSecret(final SecretKey resumptionMasterSecret) {
        this.resumptionMasterSecret = resumptionMasterSecret;
    }
    
    void setPreSharedKey(final SecretKey preSharedKey) {
        this.preSharedKey = preSharedKey;
    }
    
    void addChild(final SSLSessionImpl sslSessionImpl) {
        this.childSessions.add(sslSessionImpl);
    }
    
    void setTicketAgeAdd(final int ticketAgeAdd) {
        this.ticketAgeAdd = ticketAgeAdd;
    }
    
    void setPskIdentity(final byte[] pskIdentity) {
        this.pskIdentity = pskIdentity;
    }
    
    BigInteger incrTicketNonceCounter() {
        final BigInteger ticketNonceCounter = this.ticketNonceCounter;
        this.ticketNonceCounter = this.ticketNonceCounter.add(BigInteger.valueOf(1L));
        return ticketNonceCounter;
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
    
    void setPeerCertificates(final X509Certificate[] peerCerts) {
        if (this.peerCerts == null) {
            this.peerCerts = peerCerts;
        }
    }
    
    void setPeerPrincipal(final Principal peerPrincipal) {
        if (this.peerPrincipal == null) {
            this.peerPrincipal = peerPrincipal;
        }
    }
    
    void setLocalCertificates(final X509Certificate[] localCerts) {
        this.localCerts = localCerts;
    }
    
    void setLocalPrincipal(final Principal localPrincipal) {
        this.localPrincipal = localPrincipal;
    }
    
    void setLocalPrivateKey(final PrivateKey localPrivateKey) {
        this.localPrivateKey = localPrivateKey;
    }
    
    void setPeerSupportedSignatureAlgorithms(final Collection<SignatureScheme> collection) {
        this.peerSupportedSignAlgs = SignatureScheme.getAlgorithmNames(collection);
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
    
    void setStatusResponses(final List<byte[]> statusResponses) {
        if (statusResponses != null && !statusResponses.isEmpty()) {
            this.statusResponses = statusResponses;
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
            catch (final Exception ex) {
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
        final SecurityManager securityManager;
        if ((securityManager = System.getSecurityManager()) != null) {
            securityManager.checkPermission(new SSLPermission("getSSLSessionContext"));
        }
        return this.context;
    }
    
    SessionId getSessionId() {
        return this.sessionId;
    }
    
    CipherSuite getSuite() {
        return this.cipherSuite;
    }
    
    void setSuite(final CipherSuite cipherSuite) {
        this.cipherSuite = cipherSuite;
        if (SSLLogger.isOn && SSLLogger.isOn("session")) {
            SSLLogger.finest("Negotiating session:  " + this, new Object[0]);
        }
    }
    
    boolean isSessionResumption() {
        return this.isSessionResumption;
    }
    
    void setAsSessionResumption(final boolean isSessionResumption) {
        this.isSessionResumption = isSessionResumption;
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
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SSLSessionImpl) {
            final SSLSessionImpl sslSessionImpl = (SSLSessionImpl)o;
            return this.sessionId != null && this.sessionId.equals(sslSessionImpl.getSessionId());
        }
        return false;
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
        final javax.security.cert.X509Certificate[] array = new javax.security.cert.X509Certificate[this.peerCerts.length];
        for (int i = 0; i < this.peerCerts.length; ++i) {
            try {
                array[i] = javax.security.cert.X509Certificate.getInstance(this.peerCerts[i].getEncoded());
            }
            catch (final CertificateEncodingException ex) {
                throw new SSLPeerUnverifiedException(ex.getMessage());
            }
            catch (final CertificateException ex2) {
                throw new SSLPeerUnverifiedException(ex2.getMessage());
            }
        }
        return array;
    }
    
    public X509Certificate[] getCertificateChain() throws SSLPeerUnverifiedException {
        if (this.peerCerts != null) {
            return this.peerCerts.clone();
        }
        throw new SSLPeerUnverifiedException("peer not authenticated");
    }
    
    public List<byte[]> getStatusResponses() {
        if (this.statusResponses == null || this.statusResponses.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList list = new ArrayList(this.statusResponses.size());
        final Iterator<byte[]> iterator = this.statusResponses.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().clone());
        }
        return (List<byte[]>)Collections.unmodifiableList((List<?>)list);
    }
    
    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        if (this.peerCerts != null) {
            return this.peerCerts[0].getSubjectX500Principal();
        }
        if (this.peerPrincipal != null) {
            return this.peerPrincipal;
        }
        throw new SSLPeerUnverifiedException("peer not authenticated");
    }
    
    @Override
    public Principal getLocalPrincipal() {
        if (this.localCerts != null && this.localCerts.length != 0) {
            return this.localCerts[0].getSubjectX500Principal();
        }
        if (this.localPrincipal != null) {
            return this.localPrincipal;
        }
        return null;
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
    
    void setLastAccessedTime(final long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }
    
    public InetAddress getPeerAddress() {
        try {
            return InetAddress.getByName(this.host);
        }
        catch (final UnknownHostException ex) {
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
    
    void setContext(final SSLSessionContextImpl context) {
        if (this.context == null) {
            this.context = context;
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
        final Iterator<Object> iterator = this.childSessions.iterator();
        while (iterator.hasNext()) {
            iterator.next().invalidate();
        }
    }
    
    @Override
    public void putValue(final String s, final Object o) {
        if (s == null || o == null) {
            throw new IllegalArgumentException("arguments can not be null");
        }
        final SSLSessionBindingListener put = this.boundValues.put(new SecureKey(s), o);
        if (put instanceof SSLSessionBindingListener) {
            put.valueUnbound(new SSLSessionBindingEvent(this, s));
        }
        if (o instanceof SSLSessionBindingListener) {
            ((SSLSessionBindingListener)o).valueBound(new SSLSessionBindingEvent(this, s));
        }
    }
    
    @Override
    public Object getValue(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        return this.boundValues.get(new SecureKey(s));
    }
    
    @Override
    public void removeValue(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("argument can not be null");
        }
        final SSLSessionBindingListener remove = this.boundValues.remove(new SecureKey(s));
        if (remove instanceof SSLSessionBindingListener) {
            remove.valueUnbound(new SSLSessionBindingEvent(this, s));
        }
    }
    
    @Override
    public String[] getValueNames() {
        final ArrayList list = new ArrayList();
        final Object currentSecurityContext = SecureKey.getCurrentSecurityContext();
        final Enumeration<SecureKey> keys = this.boundValues.keys();
        while (keys.hasMoreElements()) {
            final SecureKey secureKey = keys.nextElement();
            if (currentSecurityContext.equals(secureKey.getSecurityContext())) {
                list.add(secureKey.getAppKey());
            }
        }
        return list.toArray(new String[0]);
    }
    
    protected synchronized void expandBufferSizes() {
        this.acceptLargeFragments = true;
    }
    
    @Override
    public synchronized int getPacketBufferSize() {
        int calculatePacketSize = 0;
        if (this.negotiatedMaxFragLen > 0) {
            calculatePacketSize = this.cipherSuite.calculatePacketSize(this.negotiatedMaxFragLen, this.protocolVersion);
        }
        if (this.maximumPacketSize > 0) {
            return (this.maximumPacketSize > calculatePacketSize) ? this.maximumPacketSize : calculatePacketSize;
        }
        if (calculatePacketSize != 0) {
            return calculatePacketSize;
        }
        return this.acceptLargeFragments ? 33093 : 16709;
    }
    
    @Override
    public synchronized int getApplicationBufferSize() {
        int calculateFragSize = 0;
        if (this.maximumPacketSize > 0) {
            calculateFragSize = this.cipherSuite.calculateFragSize(this.maximumPacketSize, this.protocolVersion);
        }
        if (this.negotiatedMaxFragLen > 0) {
            return (this.negotiatedMaxFragLen > calculateFragSize) ? this.negotiatedMaxFragLen : calculateFragSize;
        }
        if (calculateFragSize != 0) {
            return calculateFragSize;
        }
        return (this.acceptLargeFragments ? 33093 : 16709) - 5;
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
