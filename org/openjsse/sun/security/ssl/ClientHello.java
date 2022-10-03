package org.openjsse.sun.security.ssl;

import javax.net.ssl.SSLException;
import java.util.Collection;
import javax.net.ssl.SSLHandshakeException;
import java.util.Arrays;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;
import javax.net.ssl.SSLProtocolException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.List;

final class ClientHello
{
    static final SSLProducer kickstartProducer;
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    private static final HandshakeConsumer t12HandshakeConsumer;
    private static final HandshakeConsumer t13HandshakeConsumer;
    private static final HandshakeConsumer d12HandshakeConsumer;
    private static final HandshakeConsumer d13HandshakeConsumer;
    
    static {
        kickstartProducer = new ClientHelloKickstartProducer();
        handshakeConsumer = new ClientHelloConsumer();
        handshakeProducer = new ClientHelloProducer();
        t12HandshakeConsumer = new T12ClientHelloConsumer();
        t13HandshakeConsumer = new T13ClientHelloConsumer();
        d12HandshakeConsumer = new D12ClientHelloConsumer();
        d13HandshakeConsumer = new D13ClientHelloConsumer();
    }
    
    static final class ClientHelloMessage extends SSLHandshake.HandshakeMessage
    {
        private final boolean isDTLS;
        final int clientVersion;
        final RandomCookie clientRandom;
        final SessionId sessionId;
        private byte[] cookie;
        final int[] cipherSuiteIds;
        final List<CipherSuite> cipherSuites;
        final byte[] compressionMethod;
        final SSLExtensions extensions;
        private static final byte[] NULL_COMPRESSION;
        
        ClientHelloMessage(final HandshakeContext handshakeContext, final int clientVersion, final SessionId sessionId, final List<CipherSuite> cipherSuites, final SecureRandom generator) {
            super(handshakeContext);
            this.isDTLS = handshakeContext.sslContext.isDTLS();
            this.clientVersion = clientVersion;
            this.clientRandom = new RandomCookie(generator);
            this.sessionId = sessionId;
            if (this.isDTLS) {
                this.cookie = new byte[0];
            }
            else {
                this.cookie = null;
            }
            this.cipherSuites = cipherSuites;
            this.cipherSuiteIds = getCipherSuiteIds(cipherSuites);
            this.extensions = new SSLExtensions(this);
            this.compressionMethod = ClientHelloMessage.NULL_COMPRESSION;
        }
        
        static void readPartial(final TransportContext tc, final ByteBuffer m) throws IOException {
            final boolean isDTLS = tc.sslContext.isDTLS();
            Record.getInt16(m);
            new RandomCookie(m);
            Record.getBytes8(m);
            if (isDTLS) {
                Record.getBytes8(m);
            }
            Record.getBytes16(m);
            Record.getBytes8(m);
            if (m.remaining() >= 2) {
                int remaining = Record.getInt16(m);
                while (remaining > 0) {
                    final int id = Record.getInt16(m);
                    final int extLen = Record.getInt16(m);
                    remaining -= extLen + 4;
                    if (id == SSLExtension.CH_PRE_SHARED_KEY.id) {
                        if (remaining > 0) {
                            throw tc.fatal(Alert.ILLEGAL_PARAMETER, "pre_shared_key extension is not last");
                        }
                        Record.getBytes16(m);
                    }
                    else {
                        m.position(m.position() + extLen);
                    }
                }
            }
        }
        
        ClientHelloMessage(final HandshakeContext handshakeContext, final ByteBuffer m, final SSLExtension[] supportedExtensions) throws IOException {
            super(handshakeContext);
            this.isDTLS = handshakeContext.sslContext.isDTLS();
            this.clientVersion = ((m.get() & 0xFF) << 8 | (m.get() & 0xFF));
            this.clientRandom = new RandomCookie(m);
            this.sessionId = new SessionId(Record.getBytes8(m));
            try {
                this.sessionId.checkLength(this.clientVersion);
            }
            catch (final SSLProtocolException ex) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, ex);
            }
            if (this.isDTLS) {
                this.cookie = Record.getBytes8(m);
            }
            else {
                this.cookie = null;
            }
            final byte[] encodedIds = Record.getBytes16(m);
            if (encodedIds.length == 0 || (encodedIds.length & 0x1) != 0x0) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid ClientHello message");
            }
            this.cipherSuiteIds = new int[encodedIds.length >> 1];
            for (int i = 0, j = 0; i < encodedIds.length; this.cipherSuiteIds[j] = ((encodedIds[i++] & 0xFF) << 8 | (encodedIds[i] & 0xFF)), ++i, ++j) {}
            this.cipherSuites = getCipherSuites(this.cipherSuiteIds);
            this.compressionMethod = Record.getBytes8(m);
            if (m.hasRemaining()) {
                this.extensions = new SSLExtensions(this, m, supportedExtensions);
            }
            else {
                this.extensions = new SSLExtensions(this);
            }
        }
        
        void setHelloCookie(final byte[] cookie) {
            this.cookie = cookie;
        }
        
        byte[] getHelloCookieBytes() {
            final HandshakeOutStream hos = new HandshakeOutStream(null);
            try {
                hos.putInt8((byte)(this.clientVersion >>> 8 & 0xFF));
                hos.putInt8((byte)(this.clientVersion & 0xFF));
                hos.write(this.clientRandom.randomBytes, 0, 32);
                hos.putBytes8(this.sessionId.getId());
                hos.putBytes16(this.getEncodedCipherSuites());
                hos.putBytes8(this.compressionMethod);
                this.extensions.send(hos);
            }
            catch (final IOException ex) {}
            return hos.toByteArray();
        }
        
        byte[] getHeaderBytes() {
            final HandshakeOutStream hos = new HandshakeOutStream(null);
            try {
                hos.putInt8((byte)(this.clientVersion >>> 8 & 0xFF));
                hos.putInt8((byte)(this.clientVersion & 0xFF));
                hos.write(this.clientRandom.randomBytes, 0, 32);
                hos.putBytes8(this.sessionId.getId());
                hos.putBytes16(this.getEncodedCipherSuites());
                hos.putBytes8(this.compressionMethod);
            }
            catch (final IOException ex) {}
            return hos.toByteArray();
        }
        
        private static int[] getCipherSuiteIds(final List<CipherSuite> cipherSuites) {
            if (cipherSuites != null) {
                final int[] ids = new int[cipherSuites.size()];
                int i = 0;
                for (final CipherSuite cipherSuite : cipherSuites) {
                    ids[i++] = cipherSuite.id;
                }
                return ids;
            }
            return new int[0];
        }
        
        private static List<CipherSuite> getCipherSuites(final int[] ids) {
            final List<CipherSuite> cipherSuites = new LinkedList<CipherSuite>();
            for (final int id : ids) {
                final CipherSuite cipherSuite = CipherSuite.valueOf(id);
                if (cipherSuite != null) {
                    cipherSuites.add(cipherSuite);
                }
            }
            return Collections.unmodifiableList((List<? extends CipherSuite>)cipherSuites);
        }
        
        private List<String> getCipherSuiteNames() {
            final List<String> names = new LinkedList<String>();
            for (final int id : this.cipherSuiteIds) {
                names.add(CipherSuite.nameOf(id) + "(" + Utilities.byte16HexString(id) + ")");
            }
            return names;
        }
        
        private byte[] getEncodedCipherSuites() {
            final byte[] encoded = new byte[this.cipherSuiteIds.length << 1];
            int i = 0;
            for (final int id : this.cipherSuiteIds) {
                encoded[i++] = (byte)(id >> 8);
                encoded[i++] = (byte)id;
            }
            return encoded;
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_HELLO;
        }
        
        public int messageLength() {
            return 38 + this.sessionId.length() + (this.isDTLS ? (1 + this.cookie.length) : 0) + this.cipherSuiteIds.length * 2 + this.compressionMethod.length + this.extensions.length();
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            this.sendCore(hos);
            this.extensions.send(hos);
        }
        
        void sendCore(final HandshakeOutStream hos) throws IOException {
            hos.putInt8((byte)(this.clientVersion >>> 8));
            hos.putInt8((byte)this.clientVersion);
            hos.write(this.clientRandom.randomBytes, 0, 32);
            hos.putBytes8(this.sessionId.getId());
            if (this.isDTLS) {
                hos.putBytes8(this.cookie);
            }
            hos.putBytes16(this.getEncodedCipherSuites());
            hos.putBytes8(this.compressionMethod);
        }
        
        @Override
        public String toString() {
            if (this.isDTLS) {
                final MessageFormat messageFormat = new MessageFormat("\"ClientHello\": '{'\n  \"client version\"      : \"{0}\",\n  \"random\"              : \"{1}\",\n  \"session id\"          : \"{2}\",\n  \"cookie\"              : \"{3}\",\n  \"cipher suites\"       : \"{4}\",\n  \"compression methods\" : \"{5}\",\n  \"extensions\"          : [\n{6}\n  ]\n'}'", Locale.ENGLISH);
                final Object[] messageFields = { ProtocolVersion.nameOf(this.clientVersion), Utilities.toHexString(this.clientRandom.randomBytes), this.sessionId.toString(), Utilities.toHexString(this.cookie), this.getCipherSuiteNames().toString(), Utilities.toHexString(this.compressionMethod), Utilities.indent(Utilities.indent(this.extensions.toString())) };
                return messageFormat.format(messageFields);
            }
            final MessageFormat messageFormat = new MessageFormat("\"ClientHello\": '{'\n  \"client version\"      : \"{0}\",\n  \"random\"              : \"{1}\",\n  \"session id\"          : \"{2}\",\n  \"cipher suites\"       : \"{3}\",\n  \"compression methods\" : \"{4}\",\n  \"extensions\"          : [\n{5}\n  ]\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { ProtocolVersion.nameOf(this.clientVersion), Utilities.toHexString(this.clientRandom.randomBytes), this.sessionId.toString(), this.getCipherSuiteNames().toString(), Utilities.toHexString(this.compressionMethod), Utilities.indent(Utilities.indent(this.extensions.toString())) };
            return messageFormat.format(messageFields);
        }
        
        static {
            NULL_COMPRESSION = new byte[] { 0 };
        }
    }
    
    private static final class ClientHelloKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeProducers.remove(SSLHandshake.CLIENT_HELLO.id);
            ProtocolVersion maxProtocolVersion = chc.maximumActiveProtocol;
            SessionId sessionId = new SessionId(new byte[0]);
            List<CipherSuite> cipherSuites = chc.activeCipherSuites;
            final SSLSessionContextImpl ssci = (SSLSessionContextImpl)chc.sslContext.engineGetClientSessionContext();
            SSLSessionImpl session = ssci.get(chc.conContext.transport.getPeerHost(), chc.conContext.transport.getPeerPort());
            if (session != null) {
                if (!ClientHandshakeContext.allowUnsafeServerCertChange && session.isSessionResumption()) {
                    try {
                        chc.reservedServerCerts = (X509Certificate[])session.getPeerCertificates();
                    }
                    catch (final SSLPeerUnverifiedException ex) {}
                }
                if (!session.isRejoinable()) {
                    session = null;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, the session is not rejoinable", new Object[0]);
                    }
                }
            }
            CipherSuite sessionSuite = null;
            if (session != null) {
                sessionSuite = session.getSuite();
                if (!chc.isNegotiable(sessionSuite)) {
                    session = null;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, unavailable session cipher suite", new Object[0]);
                    }
                }
            }
            ProtocolVersion sessionVersion = null;
            if (session != null) {
                sessionVersion = session.getProtocolVersion();
                if (!chc.isNegotiable(sessionVersion)) {
                    session = null;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, unavailable protocol version", new Object[0]);
                    }
                }
            }
            if (session != null && !sessionVersion.useTLS13PlusSpec() && SSLConfiguration.useExtendedMasterSecret) {
                final boolean isEmsAvailable = chc.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET, sessionVersion);
                if (isEmsAvailable && !session.useExtendedMasterSecret && !SSLConfiguration.allowLegacyResumption) {
                    session = null;
                }
                if (session != null && !ClientHandshakeContext.allowUnsafeServerCertChange) {
                    final String identityAlg = chc.sslConfig.identificationProtocol;
                    if (identityAlg == null || identityAlg.length() == 0) {
                        if (isEmsAvailable) {
                            if (!session.useExtendedMasterSecret) {
                                session = null;
                            }
                        }
                        else {
                            session = null;
                        }
                    }
                }
            }
            final String identityAlg2 = chc.sslConfig.identificationProtocol;
            if (session != null && identityAlg2 != null) {
                final String sessionIdentityAlg = session.getIdentificationProtocol();
                if (!identityAlg2.equals(sessionIdentityAlg)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, endpoint id algorithm does not match, requested: " + identityAlg2 + ", cached: " + sessionIdentityAlg, new Object[0]);
                    }
                    session = null;
                }
            }
            if (session != null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Try resuming session", session);
                }
                if (!session.getProtocolVersion().useTLS13PlusSpec()) {
                    sessionId = session.getSessionId();
                }
                if (!maxProtocolVersion.equals(sessionVersion)) {
                    maxProtocolVersion = sessionVersion;
                    chc.setVersion(sessionVersion);
                }
                if (!chc.sslConfig.enableSessionCreation) {
                    if (!chc.conContext.isNegotiated && !sessionVersion.useTLS13PlusSpec() && cipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                        cipherSuites = Arrays.asList(sessionSuite, CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV);
                    }
                    else {
                        cipherSuites = Arrays.asList(sessionSuite);
                    }
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("No new session is allowed, so try to resume the session cipher suite only", sessionSuite);
                    }
                }
                chc.isResumption = true;
                chc.resumingSession = session;
            }
            if (session == null) {
                if (!chc.sslConfig.enableSessionCreation) {
                    throw new SSLHandshakeException("No new session is allowed and no existing session can be resumed");
                }
                if (maxProtocolVersion.useTLS13PlusSpec() && SSLConfiguration.useCompatibilityMode) {
                    sessionId = new SessionId(true, chc.sslContext.getSecureRandom());
                }
            }
            ProtocolVersion minimumVersion = ProtocolVersion.NONE;
            for (final ProtocolVersion pv : chc.activeProtocols) {
                if (minimumVersion == ProtocolVersion.NONE || pv.compare(minimumVersion) < 0) {
                    minimumVersion = pv;
                }
            }
            if (!minimumVersion.useTLS13PlusSpec() && chc.conContext.secureRenegotiation && cipherSuites.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                cipherSuites = new LinkedList<CipherSuite>(cipherSuites);
                cipherSuites.remove(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV);
            }
            boolean negotiable = false;
            for (final CipherSuite suite : cipherSuites) {
                if (chc.isNegotiable(suite)) {
                    negotiable = true;
                    break;
                }
            }
            if (!negotiable) {
                throw new SSLHandshakeException("No negotiable cipher suite");
            }
            ProtocolVersion clientHelloVersion = maxProtocolVersion;
            if (clientHelloVersion.useTLS13PlusSpec()) {
                if (clientHelloVersion.isDTLS) {
                    clientHelloVersion = ProtocolVersion.DTLS12;
                }
                else {
                    clientHelloVersion = ProtocolVersion.TLS12;
                }
            }
            final ClientHelloMessage chm = new ClientHelloMessage(chc, clientHelloVersion.id, sessionId, cipherSuites, chc.sslContext.getSecureRandom());
            chc.clientHelloRandom = chm.clientRandom;
            chc.clientHelloVersion = clientHelloVersion.id;
            final SSLExtension[] extTypes = chc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, chc.activeProtocols);
            chm.extensions.produce(chc, extTypes);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ClientHello handshake message", chm);
            }
            chm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            chc.initialClientHelloMsg = chm;
            chc.handshakeConsumers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            if (chc.sslContext.isDTLS() && !minimumVersion.useTLS13PlusSpec()) {
                chc.handshakeConsumers.put(SSLHandshake.HELLO_VERIFY_REQUEST.id, SSLHandshake.HELLO_VERIFY_REQUEST);
            }
            return null;
        }
    }
    
    private static final class ClientHelloProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final SSLHandshake ht = message.handshakeType();
            if (ht == null) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            switch (ht) {
                case HELLO_REQUEST: {
                    try {
                        chc.kickstart();
                    }
                    catch (final IOException ioe) {
                        throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, ioe);
                    }
                    return null;
                }
                case HELLO_VERIFY_REQUEST: {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Produced ClientHello(cookie) handshake message", chc.initialClientHelloMsg);
                    }
                    chc.initialClientHelloMsg.write(chc.handshakeOutput);
                    chc.handshakeOutput.flush();
                    chc.handshakeConsumers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
                    ProtocolVersion minimumVersion = ProtocolVersion.NONE;
                    for (final ProtocolVersion pv : chc.activeProtocols) {
                        if (minimumVersion == ProtocolVersion.NONE || pv.compare(minimumVersion) < 0) {
                            minimumVersion = pv;
                        }
                    }
                    if (chc.sslContext.isDTLS() && !minimumVersion.useTLS13PlusSpec()) {
                        chc.handshakeConsumers.put(SSLHandshake.HELLO_VERIFY_REQUEST.id, SSLHandshake.HELLO_VERIFY_REQUEST);
                    }
                    return null;
                }
                case HELLO_RETRY_REQUEST: {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Produced ClientHello(HRR) handshake message", chc.initialClientHelloMsg);
                    }
                    chc.initialClientHelloMsg.write(chc.handshakeOutput);
                    chc.handshakeOutput.flush();
                    chc.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t13Consumer);
                    chc.handshakeConsumers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
                    return null;
                }
                default: {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        }
    }
    
    private static final class ClientHelloConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.handshakeConsumers.remove(SSLHandshake.CLIENT_HELLO.id);
            if (!shc.handshakeConsumers.isEmpty()) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No more handshake message allowed in a ClientHello flight");
            }
            final SSLExtension[] enabledExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO);
            final ClientHelloMessage chm = new ClientHelloMessage(shc, message, enabledExtensions);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ClientHello handshake message", chm);
            }
            shc.clientHelloVersion = chm.clientVersion;
            this.onClientHello(shc, chm);
        }
        
        private void onClientHello(final ServerHandshakeContext context, final ClientHelloMessage clientHello) throws IOException {
            final SSLExtension[] extTypes = { SSLExtension.CH_SUPPORTED_VERSIONS };
            clientHello.extensions.consumeOnLoad(context, extTypes);
            final SupportedVersionsExtension.CHSupportedVersionsSpec svs = context.handshakeExtensions.get(SSLExtension.CH_SUPPORTED_VERSIONS);
            ProtocolVersion negotiatedProtocol;
            if (svs != null) {
                negotiatedProtocol = this.negotiateProtocol(context, svs.requestedProtocols);
            }
            else {
                negotiatedProtocol = this.negotiateProtocol(context, clientHello.clientVersion);
            }
            context.negotiatedProtocol = negotiatedProtocol;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Negotiated protocol version: " + negotiatedProtocol.name, new Object[0]);
            }
            if (negotiatedProtocol.isDTLS) {
                if (negotiatedProtocol.useTLS13PlusSpec()) {
                    ClientHello.d13HandshakeConsumer.consume(context, clientHello);
                }
                else {
                    ClientHello.d12HandshakeConsumer.consume(context, clientHello);
                }
            }
            else if (negotiatedProtocol.useTLS13PlusSpec()) {
                ClientHello.t13HandshakeConsumer.consume(context, clientHello);
            }
            else {
                ClientHello.t12HandshakeConsumer.consume(context, clientHello);
            }
        }
        
        private ProtocolVersion negotiateProtocol(final ServerHandshakeContext context, final int clientHelloVersion) throws SSLException {
            int chv = clientHelloVersion;
            if (context.sslContext.isDTLS()) {
                if (chv < ProtocolVersion.DTLS12.id) {
                    chv = ProtocolVersion.DTLS12.id;
                }
            }
            else if (chv > ProtocolVersion.TLS12.id) {
                chv = ProtocolVersion.TLS12.id;
            }
            final ProtocolVersion pv = ProtocolVersion.selectedFrom(context.activeProtocols, chv);
            if (pv == null || pv == ProtocolVersion.NONE || pv == ProtocolVersion.SSL20Hello) {
                throw context.conContext.fatal(Alert.PROTOCOL_VERSION, "Client requested protocol " + ProtocolVersion.nameOf(clientHelloVersion) + " is not enabled or supported in server context");
            }
            return pv;
        }
        
        private ProtocolVersion negotiateProtocol(final ServerHandshakeContext context, final int[] clientSupportedVersions) throws SSLException {
            for (final ProtocolVersion spv : context.activeProtocols) {
                if (spv == ProtocolVersion.SSL20Hello) {
                    continue;
                }
                for (final int cpv : clientSupportedVersions) {
                    if (cpv != ProtocolVersion.SSL20Hello.id) {
                        if (spv.id == cpv) {
                            return spv;
                        }
                    }
                }
            }
            throw context.conContext.fatal(Alert.PROTOCOL_VERSION, "The client supported protocol versions " + Arrays.toString(ProtocolVersion.toStringArray(clientSupportedVersions)) + " are not accepted by server preferences " + context.activeProtocols);
        }
    }
    
    private static final class T12ClientHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHelloMessage clientHello = (ClientHelloMessage)message;
            if (shc.conContext.isNegotiated) {
                if (!shc.conContext.secureRenegotiation && !HandshakeContext.allowUnsafeRenegotiation) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (ServerHandshakeContext.rejectClientInitiatedRenego && !shc.kickstartMessageDelivered) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Client initiated renegotiation is not allowed");
                }
            }
            if (clientHello.sessionId.length() != 0) {
                final SSLSessionImpl previous = ((SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext()).get(clientHello.sessionId.getId());
                boolean resumingSession = previous != null && previous.isRejoinable();
                if (!resumingSession && SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, the existing session is not rejoinable", new Object[0]);
                }
                if (resumingSession) {
                    final ProtocolVersion sessionProtocol = previous.getProtocolVersion();
                    if (sessionProtocol != shc.negotiatedProtocol) {
                        resumingSession = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, not the same protocol version", new Object[0]);
                        }
                    }
                }
                if (resumingSession && shc.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                    try {
                        previous.getPeerPrincipal();
                    }
                    catch (final SSLPeerUnverifiedException e) {
                        resumingSession = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, client authentication is required", new Object[0]);
                        }
                    }
                }
                if (resumingSession) {
                    final CipherSuite suite = previous.getSuite();
                    if (!shc.isNegotiable(suite) || !clientHello.cipherSuites.contains(suite)) {
                        resumingSession = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, the session cipher suite is absent", new Object[0]);
                        }
                    }
                }
                final String identityAlg = shc.sslConfig.identificationProtocol;
                if (resumingSession && identityAlg != null) {
                    final String sessionIdentityAlg = previous.getIdentificationProtocol();
                    if (!identityAlg.equals(sessionIdentityAlg)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, endpoint id algorithm does not match, requested: " + identityAlg + ", cached: " + sessionIdentityAlg, new Object[0]);
                        }
                        resumingSession = false;
                    }
                }
                shc.isResumption = resumingSession;
                shc.resumingSession = (resumingSession ? previous : null);
            }
            shc.clientHelloRandom = clientHello.clientRandom;
            final SSLExtension[] extTypes = shc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO);
            clientHello.extensions.consumeOnLoad(shc, extTypes);
            if (!shc.conContext.isNegotiated) {
                shc.conContext.protocolVersion = shc.negotiatedProtocol;
                shc.conContext.outputRecord.setVersion(shc.negotiatedProtocol);
            }
            shc.handshakeProducers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.SERVER_HELLO, SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_STATUS, SSLHandshake.SERVER_KEY_EXCHANGE, SSLHandshake.CERTIFICATE_REQUEST, SSLHandshake.SERVER_HELLO_DONE, SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = shc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(context, clientHello);
                }
            }
        }
    }
    
    private static final class T13ClientHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHelloMessage clientHello = (ClientHelloMessage)message;
            if (shc.conContext.isNegotiated) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Received unexpected renegotiation handshake message");
            }
            shc.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t13Consumer);
            shc.isResumption = true;
            SSLExtension[] extTypes = { SSLExtension.PSK_KEY_EXCHANGE_MODES, SSLExtension.CH_PRE_SHARED_KEY };
            clientHello.extensions.consumeOnLoad(shc, extTypes);
            extTypes = shc.sslConfig.getExclusiveExtensions(SSLHandshake.CLIENT_HELLO, Arrays.asList(SSLExtension.PSK_KEY_EXCHANGE_MODES, SSLExtension.CH_PRE_SHARED_KEY, SSLExtension.CH_SUPPORTED_VERSIONS));
            clientHello.extensions.consumeOnLoad(shc, extTypes);
            if (!shc.handshakeProducers.isEmpty()) {
                this.goHelloRetryRequest(shc, clientHello);
            }
            else {
                this.goServerHello(shc, clientHello);
            }
        }
        
        private void goHelloRetryRequest(final ServerHandshakeContext shc, final ClientHelloMessage clientHello) throws IOException {
            final HandshakeProducer handshakeProducer = shc.handshakeProducers.remove(SSLHandshake.HELLO_RETRY_REQUEST.id);
            if (handshakeProducer == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No HelloRetryRequest producer: " + shc.handshakeProducers);
            }
            handshakeProducer.produce(shc, clientHello);
            if (!shc.handshakeProducers.isEmpty()) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "unknown handshake producers: " + shc.handshakeProducers);
            }
        }
        
        private void goServerHello(final ServerHandshakeContext shc, final ClientHelloMessage clientHello) throws IOException {
            shc.clientHelloRandom = clientHello.clientRandom;
            if (!shc.conContext.isNegotiated) {
                shc.conContext.protocolVersion = shc.negotiatedProtocol;
                shc.conContext.outputRecord.setVersion(shc.negotiatedProtocol);
            }
            shc.handshakeProducers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.SERVER_HELLO, SSLHandshake.ENCRYPTED_EXTENSIONS, SSLHandshake.CERTIFICATE_REQUEST, SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_VERIFY, SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = shc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(shc, clientHello);
                }
            }
        }
    }
    
    private static final class D12ClientHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHelloMessage clientHello = (ClientHelloMessage)message;
            if (shc.conContext.isNegotiated) {
                if (!shc.conContext.secureRenegotiation && !HandshakeContext.allowUnsafeRenegotiation) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (ServerHandshakeContext.rejectClientInitiatedRenego && !shc.kickstartMessageDelivered) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Client initiated renegotiation is not allowed");
                }
            }
            if (clientHello.sessionId.length() != 0) {
                final SSLSessionImpl previous = ((SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext()).get(clientHello.sessionId.getId());
                boolean resumingSession = previous != null && previous.isRejoinable();
                if (!resumingSession && SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, the existing session is not rejoinable", new Object[0]);
                }
                if (resumingSession) {
                    final ProtocolVersion sessionProtocol = previous.getProtocolVersion();
                    if (sessionProtocol != shc.negotiatedProtocol) {
                        resumingSession = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, not the same protocol version", new Object[0]);
                        }
                    }
                }
                if (resumingSession && shc.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                    try {
                        previous.getPeerPrincipal();
                    }
                    catch (final SSLPeerUnverifiedException e) {
                        resumingSession = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, client authentication is required", new Object[0]);
                        }
                    }
                }
                if (resumingSession) {
                    final CipherSuite suite = previous.getSuite();
                    if (!shc.isNegotiable(suite) || !clientHello.cipherSuites.contains(suite)) {
                        resumingSession = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, the session cipher suite is absent", new Object[0]);
                        }
                    }
                }
                shc.isResumption = resumingSession;
                shc.resumingSession = (resumingSession ? previous : null);
            }
            final HelloCookieManager hcm = shc.sslContext.getHelloCookieManager(ProtocolVersion.DTLS10);
            if (!shc.isResumption && !hcm.isCookieValid(shc, clientHello, clientHello.cookie)) {
                shc.handshakeProducers.put(SSLHandshake.HELLO_VERIFY_REQUEST.id, SSLHandshake.HELLO_VERIFY_REQUEST);
                SSLHandshake.HELLO_VERIFY_REQUEST.produce(context, clientHello);
                return;
            }
            shc.clientHelloRandom = clientHello.clientRandom;
            final SSLExtension[] extTypes = shc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO);
            clientHello.extensions.consumeOnLoad(shc, extTypes);
            if (!shc.conContext.isNegotiated) {
                shc.conContext.protocolVersion = shc.negotiatedProtocol;
                shc.conContext.outputRecord.setVersion(shc.negotiatedProtocol);
            }
            shc.handshakeProducers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.SERVER_HELLO, SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_STATUS, SSLHandshake.SERVER_KEY_EXCHANGE, SSLHandshake.CERTIFICATE_REQUEST, SSLHandshake.SERVER_HELLO_DONE, SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = shc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(context, clientHello);
                }
            }
        }
    }
    
    private static final class D13ClientHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
