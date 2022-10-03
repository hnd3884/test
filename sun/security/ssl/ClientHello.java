package sun.security.ssl;

import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
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
    
    static {
        kickstartProducer = new ClientHelloKickstartProducer();
        handshakeConsumer = new ClientHelloConsumer();
        handshakeProducer = new ClientHelloProducer();
        t12HandshakeConsumer = new T12ClientHelloConsumer();
        t13HandshakeConsumer = new T13ClientHelloConsumer();
    }
    
    static final class ClientHelloMessage extends SSLHandshake.HandshakeMessage
    {
        final int clientVersion;
        final RandomCookie clientRandom;
        final SessionId sessionId;
        final int[] cipherSuiteIds;
        final List<CipherSuite> cipherSuites;
        final byte[] compressionMethod;
        final SSLExtensions extensions;
        private static final byte[] NULL_COMPRESSION;
        
        ClientHelloMessage(final HandshakeContext handshakeContext, final int clientVersion, final SessionId sessionId, final List<CipherSuite> cipherSuites, final SecureRandom secureRandom) {
            super(handshakeContext);
            this.clientVersion = clientVersion;
            this.clientRandom = new RandomCookie(secureRandom);
            this.sessionId = sessionId;
            this.cipherSuites = cipherSuites;
            this.cipherSuiteIds = getCipherSuiteIds(cipherSuites);
            this.extensions = new SSLExtensions(this);
            this.compressionMethod = ClientHelloMessage.NULL_COMPRESSION;
        }
        
        static void readPartial(final TransportContext transportContext, final ByteBuffer byteBuffer) throws IOException {
            Record.getInt16(byteBuffer);
            new RandomCookie(byteBuffer);
            Record.getBytes8(byteBuffer);
            Record.getBytes16(byteBuffer);
            Record.getBytes8(byteBuffer);
            if (byteBuffer.remaining() >= 2) {
                int i = Record.getInt16(byteBuffer);
                while (i > 0) {
                    final int int16 = Record.getInt16(byteBuffer);
                    final int int17 = Record.getInt16(byteBuffer);
                    i -= int17 + 4;
                    if (int16 == SSLExtension.CH_PRE_SHARED_KEY.id) {
                        if (i > 0) {
                            throw transportContext.fatal(Alert.ILLEGAL_PARAMETER, "pre_shared_key extension is not last");
                        }
                        Record.getBytes16(byteBuffer);
                    }
                    else {
                        byteBuffer.position(byteBuffer.position() + int17);
                    }
                }
            }
        }
        
        ClientHelloMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer, final SSLExtension[] array) throws IOException {
            super(handshakeContext);
            this.clientVersion = ((byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF));
            this.clientRandom = new RandomCookie(byteBuffer);
            this.sessionId = new SessionId(Record.getBytes8(byteBuffer));
            try {
                this.sessionId.checkLength(this.clientVersion);
            }
            catch (final SSLProtocolException ex) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, ex);
            }
            final byte[] bytes16 = Record.getBytes16(byteBuffer);
            if (bytes16.length == 0 || (bytes16.length & 0x1) != 0x0) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid ClientHello message");
            }
            this.cipherSuiteIds = new int[bytes16.length >> 1];
            for (int i = 0, n = 0; i < bytes16.length; this.cipherSuiteIds[n] = ((bytes16[i++] & 0xFF) << 8 | (bytes16[i] & 0xFF)), ++i, ++n) {}
            this.cipherSuites = getCipherSuites(this.cipherSuiteIds);
            this.compressionMethod = Record.getBytes8(byteBuffer);
            if (byteBuffer.hasRemaining()) {
                this.extensions = new SSLExtensions(this, byteBuffer, array);
            }
            else {
                this.extensions = new SSLExtensions(this);
            }
        }
        
        byte[] getHeaderBytes() {
            final HandshakeOutStream handshakeOutStream = new HandshakeOutStream(null);
            try {
                handshakeOutStream.putInt8((byte)(this.clientVersion >>> 8 & 0xFF));
                handshakeOutStream.putInt8((byte)(this.clientVersion & 0xFF));
                handshakeOutStream.write(this.clientRandom.randomBytes, 0, 32);
                handshakeOutStream.putBytes8(this.sessionId.getId());
                handshakeOutStream.putBytes16(this.getEncodedCipherSuites());
                handshakeOutStream.putBytes8(this.compressionMethod);
            }
            catch (final IOException ex) {}
            return handshakeOutStream.toByteArray();
        }
        
        private static int[] getCipherSuiteIds(final List<CipherSuite> list) {
            if (list != null) {
                final int[] array = new int[list.size()];
                int n = 0;
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    array[n++] = ((CipherSuite)iterator.next()).id;
                }
                return array;
            }
            return new int[0];
        }
        
        private static List<CipherSuite> getCipherSuites(final int[] array) {
            final LinkedList list = new LinkedList();
            for (int length = array.length, i = 0; i < length; ++i) {
                final CipherSuite value = CipherSuite.valueOf(array[i]);
                if (value != null) {
                    list.add(value);
                }
            }
            return (List<CipherSuite>)Collections.unmodifiableList((List<?>)list);
        }
        
        private List<String> getCipherSuiteNames() {
            final LinkedList list = new LinkedList();
            for (final int n : this.cipherSuiteIds) {
                list.add(CipherSuite.nameOf(n) + "(" + Utilities.byte16HexString(n) + ")");
            }
            return list;
        }
        
        private byte[] getEncodedCipherSuites() {
            final byte[] array = new byte[this.cipherSuiteIds.length << 1];
            int n = 0;
            for (final int n2 : this.cipherSuiteIds) {
                array[n++] = (byte)(n2 >> 8);
                array[n++] = (byte)n2;
            }
            return array;
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_HELLO;
        }
        
        public int messageLength() {
            return 38 + this.sessionId.length() + this.cipherSuiteIds.length * 2 + this.compressionMethod.length + this.extensions.length();
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            this.sendCore(handshakeOutStream);
            this.extensions.send(handshakeOutStream);
        }
        
        void sendCore(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt8((byte)(this.clientVersion >>> 8));
            handshakeOutStream.putInt8((byte)this.clientVersion);
            handshakeOutStream.write(this.clientRandom.randomBytes, 0, 32);
            handshakeOutStream.putBytes8(this.sessionId.getId());
            handshakeOutStream.putBytes16(this.getEncodedCipherSuites());
            handshakeOutStream.putBytes8(this.compressionMethod);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"ClientHello\": '{'\n  \"client version\"      : \"{0}\",\n  \"random\"              : \"{1}\",\n  \"session id\"          : \"{2}\",\n  \"cipher suites\"       : \"{3}\",\n  \"compression methods\" : \"{4}\",\n  \"extensions\"          : [\n{5}\n  ]\n'}'", Locale.ENGLISH).format(new Object[] { ProtocolVersion.nameOf(this.clientVersion), Utilities.toHexString(this.clientRandom.randomBytes), this.sessionId.toString(), this.getCipherSuiteNames().toString(), Utilities.toHexString(this.compressionMethod), Utilities.indent(Utilities.indent(this.extensions.toString())) });
        }
        
        static {
            NULL_COMPRESSION = new byte[] { 0 };
        }
    }
    
    private static final class ClientHelloKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeProducers.remove(SSLHandshake.CLIENT_HELLO.id);
            ProtocolVersion maximumActiveProtocol = clientHandshakeContext.maximumActiveProtocol;
            SessionId sessionId = new SessionId(new byte[0]);
            List<CipherSuite> list = clientHandshakeContext.activeCipherSuites;
            SSLSessionImpl value = ((SSLSessionContextImpl)clientHandshakeContext.sslContext.engineGetClientSessionContext()).get(clientHandshakeContext.conContext.transport.getPeerHost(), clientHandshakeContext.conContext.transport.getPeerPort());
            if (value != null) {
                if (!ClientHandshakeContext.allowUnsafeServerCertChange && value.isSessionResumption()) {
                    try {
                        clientHandshakeContext.reservedServerCerts = (X509Certificate[])value.getPeerCertificates();
                    }
                    catch (final SSLPeerUnverifiedException ex) {}
                }
                if (!value.isRejoinable()) {
                    value = null;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, the session is not rejoinable", new Object[0]);
                    }
                }
            }
            CipherSuite suite = null;
            if (value != null) {
                suite = value.getSuite();
                if (!clientHandshakeContext.isNegotiable(suite)) {
                    value = null;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, unavailable session cipher suite", new Object[0]);
                    }
                }
            }
            ProtocolVersion protocolVersion = null;
            if (value != null) {
                protocolVersion = value.getProtocolVersion();
                if (!clientHandshakeContext.isNegotiable(protocolVersion)) {
                    value = null;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, unavailable protocol version", new Object[0]);
                    }
                }
            }
            if (value != null && !protocolVersion.useTLS13PlusSpec() && SSLConfiguration.useExtendedMasterSecret) {
                final boolean available = clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_EXTENDED_MASTER_SECRET, protocolVersion);
                if (available && !value.useExtendedMasterSecret && !SSLConfiguration.allowLegacyResumption) {
                    value = null;
                }
                if (value != null && !ClientHandshakeContext.allowUnsafeServerCertChange) {
                    final String identificationProtocol = clientHandshakeContext.sslConfig.identificationProtocol;
                    if (identificationProtocol == null || identificationProtocol.isEmpty()) {
                        if (available) {
                            if (!value.useExtendedMasterSecret) {
                                value = null;
                            }
                        }
                        else {
                            value = null;
                        }
                    }
                }
            }
            final String identificationProtocol2 = clientHandshakeContext.sslConfig.identificationProtocol;
            if (value != null && identificationProtocol2 != null) {
                final String identificationProtocol3 = value.getIdentificationProtocol();
                if (!identificationProtocol2.equalsIgnoreCase(identificationProtocol3)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, endpoint id algorithm does not match, requested: " + identificationProtocol2 + ", cached: " + identificationProtocol3, new Object[0]);
                    }
                    value = null;
                }
            }
            if (value != null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Try resuming session", value);
                }
                if (!value.getProtocolVersion().useTLS13PlusSpec()) {
                    sessionId = value.getSessionId();
                }
                if (!maximumActiveProtocol.equals(protocolVersion)) {
                    maximumActiveProtocol = protocolVersion;
                    clientHandshakeContext.setVersion(protocolVersion);
                }
                if (!clientHandshakeContext.sslConfig.enableSessionCreation) {
                    if (!clientHandshakeContext.conContext.isNegotiated && !protocolVersion.useTLS13PlusSpec() && list.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                        list = Arrays.asList(suite, CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV);
                    }
                    else {
                        list = Arrays.asList(suite);
                    }
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("No new session is allowed, so try to resume the session cipher suite only", suite);
                    }
                }
                clientHandshakeContext.isResumption = true;
                clientHandshakeContext.resumingSession = value;
            }
            if (value == null) {
                if (!clientHandshakeContext.sslConfig.enableSessionCreation) {
                    throw new SSLHandshakeException("No new session is allowed and no existing session can be resumed");
                }
                if (maximumActiveProtocol.useTLS13PlusSpec() && SSLConfiguration.useCompatibilityMode) {
                    sessionId = new SessionId(true, clientHandshakeContext.sslContext.getSecureRandom());
                }
            }
            ProtocolVersion none = ProtocolVersion.NONE;
            for (final ProtocolVersion protocolVersion2 : clientHandshakeContext.activeProtocols) {
                if (none == ProtocolVersion.NONE || protocolVersion2.compare(none) < 0) {
                    none = protocolVersion2;
                }
            }
            if (!none.useTLS13PlusSpec() && clientHandshakeContext.conContext.secureRenegotiation && list.contains(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV)) {
                list = (List<CipherSuite>)new LinkedList(list);
                list.remove(CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV);
            }
            boolean b = false;
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                if (clientHandshakeContext.isNegotiable((CipherSuite)iterator2.next())) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                throw new SSLHandshakeException("No negotiable cipher suite");
            }
            ProtocolVersion tls12 = maximumActiveProtocol;
            if (tls12.useTLS13PlusSpec()) {
                tls12 = ProtocolVersion.TLS12;
            }
            final ClientHelloMessage initialClientHelloMsg = new ClientHelloMessage(clientHandshakeContext, tls12.id, sessionId, list, clientHandshakeContext.sslContext.getSecureRandom());
            clientHandshakeContext.clientHelloRandom = initialClientHelloMsg.clientRandom;
            clientHandshakeContext.clientHelloVersion = tls12.id;
            initialClientHelloMsg.extensions.produce(clientHandshakeContext, clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, clientHandshakeContext.activeProtocols));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ClientHello handshake message", initialClientHelloMsg);
            }
            initialClientHelloMsg.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            clientHandshakeContext.initialClientHelloMsg = initialClientHelloMsg;
            clientHandshakeContext.handshakeConsumers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            return null;
        }
    }
    
    private static final class ClientHelloProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final SSLHandshake handshakeType = handshakeMessage.handshakeType();
            if (handshakeType == null) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            switch (handshakeType) {
                case HELLO_REQUEST: {
                    try {
                        clientHandshakeContext.kickstart();
                    }
                    catch (final IOException ex) {
                        throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, ex);
                    }
                    return null;
                }
                case HELLO_RETRY_REQUEST: {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Produced ClientHello(HRR) handshake message", clientHandshakeContext.initialClientHelloMsg);
                    }
                    clientHandshakeContext.initialClientHelloMsg.write(clientHandshakeContext.handshakeOutput);
                    clientHandshakeContext.handshakeOutput.flush();
                    clientHandshakeContext.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t13Consumer);
                    clientHandshakeContext.handshakeConsumers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
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
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CLIENT_HELLO.id);
            if (!serverHandshakeContext.handshakeConsumers.isEmpty()) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No more handshake message allowed in a ClientHello flight");
            }
            final ClientHelloMessage clientHelloMessage = new ClientHelloMessage(serverHandshakeContext, byteBuffer, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ClientHello handshake message", clientHelloMessage);
            }
            serverHandshakeContext.clientHelloVersion = clientHelloMessage.clientVersion;
            this.onClientHello(serverHandshakeContext, clientHelloMessage);
        }
        
        private void onClientHello(final ServerHandshakeContext serverHandshakeContext, final ClientHelloMessage clientHelloMessage) throws IOException {
            clientHelloMessage.extensions.consumeOnLoad(serverHandshakeContext, new SSLExtension[] { SSLExtension.CH_SUPPORTED_VERSIONS });
            final SupportedVersionsExtension.CHSupportedVersionsSpec chSupportedVersionsSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_SUPPORTED_VERSIONS);
            ProtocolVersion negotiatedProtocol;
            if (chSupportedVersionsSpec != null) {
                negotiatedProtocol = this.negotiateProtocol(serverHandshakeContext, chSupportedVersionsSpec.requestedProtocols);
            }
            else {
                negotiatedProtocol = this.negotiateProtocol(serverHandshakeContext, clientHelloMessage.clientVersion);
            }
            serverHandshakeContext.negotiatedProtocol = negotiatedProtocol;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Negotiated protocol version: " + negotiatedProtocol.name, new Object[0]);
            }
            if (negotiatedProtocol.useTLS13PlusSpec()) {
                ClientHello.t13HandshakeConsumer.consume(serverHandshakeContext, clientHelloMessage);
            }
            else {
                ClientHello.t12HandshakeConsumer.consume(serverHandshakeContext, clientHelloMessage);
            }
        }
        
        private ProtocolVersion negotiateProtocol(final ServerHandshakeContext serverHandshakeContext, final int n) throws SSLException {
            int id = n;
            if (id > ProtocolVersion.TLS12.id) {
                id = ProtocolVersion.TLS12.id;
            }
            final ProtocolVersion selected = ProtocolVersion.selectedFrom(serverHandshakeContext.activeProtocols, id);
            if (selected == null || selected == ProtocolVersion.NONE || selected == ProtocolVersion.SSL20Hello) {
                throw serverHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "Client requested protocol " + ProtocolVersion.nameOf(n) + " is not enabled or supported in server context");
            }
            return selected;
        }
        
        private ProtocolVersion negotiateProtocol(final ServerHandshakeContext serverHandshakeContext, final int[] array) throws SSLException {
            for (final ProtocolVersion protocolVersion : serverHandshakeContext.activeProtocols) {
                if (protocolVersion == ProtocolVersion.SSL20Hello) {
                    continue;
                }
                for (final int n : array) {
                    if (n != ProtocolVersion.SSL20Hello.id) {
                        if (protocolVersion.id == n) {
                            return protocolVersion;
                        }
                    }
                }
            }
            throw serverHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "The client supported protocol versions " + Arrays.toString(ProtocolVersion.toStringArray(array)) + " are not accepted by server preferences " + serverHandshakeContext.activeProtocols);
        }
    }
    
    private static final class T12ClientHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHelloMessage clientHelloMessage = (ClientHelloMessage)handshakeMessage;
            if (serverHandshakeContext.conContext.isNegotiated) {
                if (!serverHandshakeContext.conContext.secureRenegotiation && !HandshakeContext.allowUnsafeRenegotiation) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (ServerHandshakeContext.rejectClientInitiatedRenego && !serverHandshakeContext.kickstartMessageDelivered) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Client initiated renegotiation is not allowed");
                }
            }
            if (clientHelloMessage.sessionId.length() != 0) {
                final SSLSessionImpl value = ((SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext()).get(clientHelloMessage.sessionId.getId());
                boolean isResumption = value != null && value.isRejoinable();
                if (!isResumption && SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, the existing session is not rejoinable", new Object[0]);
                }
                if (isResumption && value.getProtocolVersion() != serverHandshakeContext.negotiatedProtocol) {
                    isResumption = false;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Can't resume, not the same protocol version", new Object[0]);
                    }
                }
                if (isResumption && serverHandshakeContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                    try {
                        value.getPeerPrincipal();
                    }
                    catch (final SSLPeerUnverifiedException ex) {
                        isResumption = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, client authentication is required", new Object[0]);
                        }
                    }
                }
                if (isResumption) {
                    final CipherSuite suite = value.getSuite();
                    if (!serverHandshakeContext.isNegotiable(suite) || !clientHelloMessage.cipherSuites.contains(suite)) {
                        isResumption = false;
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, the session cipher suite is absent", new Object[0]);
                        }
                    }
                }
                if (isResumption) {
                    final CipherSuite suite2 = value.getSuite();
                    if (suite2.keyExchange == CipherSuite.KeyExchange.K_KRB5 || suite2.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                        final Principal localPrincipal = value.getLocalPrincipal();
                        Subject subject;
                        try {
                            subject = AccessController.doPrivileged((PrivilegedExceptionAction<Subject>)new PrivilegedExceptionAction<Subject>() {
                                @Override
                                public Subject run() throws Exception {
                                    return Krb5Helper.getServerSubject(serverHandshakeContext.conContext.acc);
                                }
                            });
                        }
                        catch (final PrivilegedActionException ex2) {
                            subject = null;
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                                SSLLogger.finest("Attempt to obtain subject failed!", new Object[0]);
                            }
                        }
                        if (subject != null) {
                            if (Krb5Helper.isRelated(subject, localPrincipal)) {
                                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                                    SSLLogger.finest("Subject can provide creds for princ", new Object[0]);
                                }
                            }
                            else {
                                isResumption = false;
                                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                                    SSLLogger.finest("Subject cannot provide creds for princ", new Object[0]);
                                }
                            }
                        }
                        else {
                            isResumption = false;
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                                SSLLogger.finest("Kerberos credentials are not present in the current Subject; check if  javax.security.auth.useSubjectCredsOnly system property has been set to false", new Object[0]);
                            }
                        }
                    }
                }
                final String identificationProtocol = serverHandshakeContext.sslConfig.identificationProtocol;
                if (isResumption && identificationProtocol != null) {
                    final String identificationProtocol2 = value.getIdentificationProtocol();
                    if (!identificationProtocol.equalsIgnoreCase(identificationProtocol2)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.finest("Can't resume, endpoint id algorithm does not match, requested: " + identificationProtocol + ", cached: " + identificationProtocol2, new Object[0]);
                        }
                        isResumption = false;
                    }
                }
                serverHandshakeContext.isResumption = isResumption;
                serverHandshakeContext.resumingSession = (isResumption ? value : null);
            }
            serverHandshakeContext.clientHelloRandom = clientHelloMessage.clientRandom;
            clientHelloMessage.extensions.consumeOnLoad(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO));
            if (!serverHandshakeContext.conContext.isNegotiated) {
                serverHandshakeContext.conContext.protocolVersion = serverHandshakeContext.negotiatedProtocol;
                serverHandshakeContext.conContext.outputRecord.setVersion(serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeProducers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            final SSLHandshake[] array = { SSLHandshake.SERVER_HELLO, SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_STATUS, SSLHandshake.SERVER_KEY_EXCHANGE, SSLHandshake.CERTIFICATE_REQUEST, SSLHandshake.SERVER_HELLO_DONE, SSLHandshake.FINISHED };
            for (int length = array.length, i = 0; i < length; ++i) {
                final HandshakeProducer handshakeProducer = serverHandshakeContext.handshakeProducers.remove(array[i].id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(connectionContext, clientHelloMessage);
                }
            }
        }
    }
    
    private static final class T13ClientHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHelloMessage clientHelloMessage = (ClientHelloMessage)handshakeMessage;
            if (serverHandshakeContext.conContext.isNegotiated) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Received unexpected renegotiation handshake message");
            }
            serverHandshakeContext.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t13Consumer);
            serverHandshakeContext.isResumption = true;
            clientHelloMessage.extensions.consumeOnLoad(serverHandshakeContext, new SSLExtension[] { SSLExtension.PSK_KEY_EXCHANGE_MODES, SSLExtension.CH_PRE_SHARED_KEY });
            clientHelloMessage.extensions.consumeOnLoad(serverHandshakeContext, serverHandshakeContext.sslConfig.getExclusiveExtensions(SSLHandshake.CLIENT_HELLO, Arrays.asList(SSLExtension.PSK_KEY_EXCHANGE_MODES, SSLExtension.CH_PRE_SHARED_KEY, SSLExtension.CH_SUPPORTED_VERSIONS)));
            if (!serverHandshakeContext.handshakeProducers.isEmpty()) {
                this.goHelloRetryRequest(serverHandshakeContext, clientHelloMessage);
            }
            else {
                this.goServerHello(serverHandshakeContext, clientHelloMessage);
            }
        }
        
        private void goHelloRetryRequest(final ServerHandshakeContext serverHandshakeContext, final ClientHelloMessage clientHelloMessage) throws IOException {
            final HandshakeProducer handshakeProducer = serverHandshakeContext.handshakeProducers.remove(SSLHandshake.HELLO_RETRY_REQUEST.id);
            if (handshakeProducer == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No HelloRetryRequest producer: " + serverHandshakeContext.handshakeProducers);
            }
            handshakeProducer.produce(serverHandshakeContext, clientHelloMessage);
            if (!serverHandshakeContext.handshakeProducers.isEmpty()) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "unknown handshake producers: " + serverHandshakeContext.handshakeProducers);
            }
        }
        
        private void goServerHello(final ServerHandshakeContext serverHandshakeContext, final ClientHelloMessage clientHelloMessage) throws IOException {
            serverHandshakeContext.clientHelloRandom = clientHelloMessage.clientRandom;
            if (!serverHandshakeContext.conContext.isNegotiated) {
                serverHandshakeContext.conContext.protocolVersion = serverHandshakeContext.negotiatedProtocol;
                serverHandshakeContext.conContext.outputRecord.setVersion(serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeProducers.put(SSLHandshake.SERVER_HELLO.id, SSLHandshake.SERVER_HELLO);
            final SSLHandshake[] array = { SSLHandshake.SERVER_HELLO, SSLHandshake.ENCRYPTED_EXTENSIONS, SSLHandshake.CERTIFICATE_REQUEST, SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_VERIFY, SSLHandshake.FINISHED };
            for (int length = array.length, i = 0; i < length; ++i) {
                final HandshakeProducer handshakeProducer = serverHandshakeContext.handshakeProducers.remove(array[i].id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(serverHandshakeContext, clientHelloMessage);
                }
            }
        }
    }
}
