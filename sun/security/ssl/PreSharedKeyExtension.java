package sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import javax.crypto.Mac;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.io.IOException;
import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import javax.net.ssl.SSLPeerUnverifiedException;

final class PreSharedKeyExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeAbsence chOnLoadAbsence;
    static final HandshakeConsumer chOnTradeConsumer;
    static final HandshakeAbsence chOnTradAbsence;
    static final SSLStringizer chStringizer;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeAbsence shOnLoadAbsence;
    static final SSLStringizer shStringizer;
    
    private static boolean canRejoin(final ClientHello.ClientHelloMessage clientHelloMessage, final ServerHandshakeContext serverHandshakeContext, final SSLSessionImpl sslSessionImpl) {
        boolean b = sslSessionImpl.isRejoinable() && sslSessionImpl.getPreSharedKey() != null;
        if (b && sslSessionImpl.getProtocolVersion() != serverHandshakeContext.negotiatedProtocol) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.finest("Can't resume, incorrect protocol version", new Object[0]);
            }
            b = false;
        }
        if (serverHandshakeContext.localSupportedSignAlgs == null) {
            serverHandshakeContext.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, serverHandshakeContext.activeProtocols);
        }
        if (b && serverHandshakeContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
            try {
                sslSessionImpl.getPeerPrincipal();
            }
            catch (final SSLPeerUnverifiedException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, client authentication is required", new Object[0]);
                }
                b = false;
            }
            final Collection<SignatureScheme> localSupportedSignatureSchemes = sslSessionImpl.getLocalSupportedSignatureSchemes();
            if (b && !serverHandshakeContext.localSupportedSignAlgs.containsAll(localSupportedSignatureSchemes)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Can't resume. Session uses different signature algorithms", new Object[0]);
                }
                b = false;
            }
        }
        final String identificationProtocol = serverHandshakeContext.sslConfig.identificationProtocol;
        if (b && identificationProtocol != null) {
            final String identificationProtocol2 = sslSessionImpl.getIdentificationProtocol();
            if (!identificationProtocol.equalsIgnoreCase(identificationProtocol2)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, endpoint id algorithm does not match, requested: " + identificationProtocol + ", cached: " + identificationProtocol2, new Object[0]);
                }
                b = false;
            }
        }
        if (b && (!serverHandshakeContext.isNegotiable(sslSessionImpl.getSuite()) || !clientHelloMessage.cipherSuites.contains(sslSessionImpl.getSuite()))) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.finest("Can't resume, unavailable session cipher suite", new Object[0]);
            }
            b = false;
        }
        return b;
    }
    
    private static void checkBinder(final ServerHandshakeContext serverHandshakeContext, final SSLSessionImpl sslSessionImpl, final HandshakeHash handshakeHash, final byte[] array) throws IOException {
        final SecretKey preSharedKey = sslSessionImpl.getPreSharedKey();
        if (preSharedKey == null) {
            throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Session has no PSK");
        }
        if (!Arrays.equals(array, computeBinder(serverHandshakeContext, deriveBinderKey(serverHandshakeContext, preSharedKey, sslSessionImpl), sslSessionImpl, handshakeHash))) {
            throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Incorect PSK binder value");
        }
    }
    
    private static byte[] computeBinder(final HandshakeContext handshakeContext, final SecretKey secretKey, final SSLSessionImpl sslSessionImpl, final HandshakeHash handshakeHash) throws IOException {
        handshakeHash.determine(sslSessionImpl.getProtocolVersion(), sslSessionImpl.getSuite());
        handshakeHash.update();
        return computeBinder(handshakeContext, secretKey, sslSessionImpl, handshakeHash.digest());
    }
    
    private static byte[] computeBinder(final HandshakeContext handshakeContext, final SecretKey secretKey, final HandshakeHash handshakeHash, final SSLSessionImpl sslSessionImpl, final HandshakeContext handshakeContext2, final ClientHello.ClientHelloMessage clientHelloMessage, final CHPreSharedKeySpec chPreSharedKeySpec) throws IOException {
        new PartialClientHelloMessage(handshakeContext2, clientHelloMessage, chPreSharedKeySpec).write(new HandshakeOutStream(new SSLEngineOutputRecord(handshakeHash)));
        handshakeHash.determine(sslSessionImpl.getProtocolVersion(), sslSessionImpl.getSuite());
        handshakeHash.update();
        return computeBinder(handshakeContext, secretKey, sslSessionImpl, handshakeHash.digest());
    }
    
    private static byte[] computeBinder(final HandshakeContext handshakeContext, final SecretKey secretKey, final SSLSessionImpl sslSessionImpl, final byte[] array) throws IOException {
        try {
            final CipherSuite.HashAlg hashAlg = sslSessionImpl.getSuite().hashAlg;
            final SecretKey expand = new HKDF(hashAlg.name).expand(secretKey, SSLSecretDerivation.createHkdfInfo("tls13 finished".getBytes(), new byte[0], hashAlg.hashLength), hashAlg.hashLength, "TlsBinderKey");
            final String string = "Hmac" + hashAlg.name.replace("-", "");
            try {
                final Mac mac = JsseJce.getMac(string);
                mac.init(expand);
                return mac.doFinal(array);
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, (Throwable)ex);
            }
        }
        catch (final GeneralSecurityException ex2) {
            throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, ex2);
        }
    }
    
    private static SecretKey deriveBinderKey(final HandshakeContext handshakeContext, final SecretKey secretKey, final SSLSessionImpl sslSessionImpl) throws IOException {
        try {
            final CipherSuite.HashAlg hashAlg = sslSessionImpl.getSuite().hashAlg;
            final HKDF hkdf = new HKDF(hashAlg.name);
            return hkdf.expand(hkdf.extract(new byte[hashAlg.hashLength], secretKey, "TlsEarlySecret"), SSLSecretDerivation.createHkdfInfo("tls13 res binder".getBytes(), MessageDigest.getInstance(hashAlg.name).digest(new byte[0]), hashAlg.hashLength), hashAlg.hashLength, "TlsBinderKey");
        }
        catch (final GeneralSecurityException ex) {
            throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, ex);
        }
    }
    
    static {
        chNetworkProducer = new CHPreSharedKeyProducer();
        chOnLoadConsumer = new CHPreSharedKeyConsumer();
        chOnLoadAbsence = new CHPreSharedKeyOnLoadAbsence();
        chOnTradeConsumer = new CHPreSharedKeyUpdate();
        chOnTradAbsence = new CHPreSharedKeyOnTradeAbsence();
        chStringizer = new CHPreSharedKeyStringizer();
        shNetworkProducer = new SHPreSharedKeyProducer();
        shOnLoadConsumer = new SHPreSharedKeyConsumer();
        shOnLoadAbsence = new SHPreSharedKeyAbsence();
        shStringizer = new SHPreSharedKeyStringizer();
    }
    
    private static final class PskIdentity
    {
        final byte[] identity;
        final int obfuscatedAge;
        
        PskIdentity(final byte[] identity, final int obfuscatedAge) {
            this.identity = identity;
            this.obfuscatedAge = obfuscatedAge;
        }
        
        int getEncodedLength() {
            return 2 + this.identity.length + 4;
        }
        
        void writeEncoded(final ByteBuffer byteBuffer) throws IOException {
            Record.putBytes16(byteBuffer, this.identity);
            Record.putInt32(byteBuffer, this.obfuscatedAge);
        }
        
        @Override
        public String toString() {
            return "{" + Utilities.toHexString(this.identity) + "," + this.obfuscatedAge + "}";
        }
    }
    
    private static final class CHPreSharedKeySpec implements SSLExtension.SSLExtensionSpec
    {
        final List<PskIdentity> identities;
        final List<byte[]> binders;
        
        CHPreSharedKeySpec(final List<PskIdentity> identities, final List<byte[]> binders) {
            this.identities = identities;
            this.binders = binders;
        }
        
        CHPreSharedKeySpec(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 44) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient data (length=" + byteBuffer.remaining() + ")");
            }
            final int int16 = Record.getInt16(byteBuffer);
            if (int16 < 7) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient identities (length=" + int16 + ")");
            }
            this.identities = new ArrayList<PskIdentity>();
            PskIdentity pskIdentity;
            for (int i = 0; i < int16; i += pskIdentity.getEncodedLength()) {
                final byte[] bytes16 = Record.getBytes16(byteBuffer);
                if (bytes16.length < 1) {
                    throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient identity (length=" + bytes16.length + ")");
                }
                pskIdentity = new PskIdentity(bytes16, Record.getInt32(byteBuffer));
                this.identities.add(pskIdentity);
            }
            if (byteBuffer.remaining() < 35) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient binders data (length=" + byteBuffer.remaining() + ")");
            }
            final int int17 = Record.getInt16(byteBuffer);
            if (int17 < 33) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient binders (length=" + int17 + ")");
            }
            this.binders = new ArrayList<byte[]>();
            byte[] bytes17;
            for (int j = 0; j < int17; j += 1 + bytes17.length) {
                bytes17 = Record.getBytes8(byteBuffer);
                if (bytes17.length < 32) {
                    throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient binder entry (length=" + bytes17.length + ")");
                }
                this.binders.add(bytes17);
            }
        }
        
        int getIdsEncodedLength() {
            int n = 0;
            final Iterator<PskIdentity> iterator = this.identities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().getEncodedLength();
            }
            return n;
        }
        
        int getBindersEncodedLength() {
            int n = 0;
            final Iterator<byte[]> iterator = this.binders.iterator();
            while (iterator.hasNext()) {
                n += 1 + iterator.next().length;
            }
            return n;
        }
        
        byte[] getEncoded() throws IOException {
            final int idsEncodedLength = this.getIdsEncodedLength();
            final int bindersEncodedLength = this.getBindersEncodedLength();
            final byte[] array = new byte[4 + idsEncodedLength + bindersEncodedLength];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, idsEncodedLength);
            final Iterator<PskIdentity> iterator = this.identities.iterator();
            while (iterator.hasNext()) {
                iterator.next().writeEncoded(wrap);
            }
            Record.putInt16(wrap, bindersEncodedLength);
            final Iterator<byte[]> iterator2 = this.binders.iterator();
            while (iterator2.hasNext()) {
                Record.putBytes8(wrap, iterator2.next());
            }
            return array;
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"PreSharedKey\": '{'\n  \"identities\"    : \"{0}\",\n  \"binders\"       : \"{1}\",\n'}'", Locale.ENGLISH).format(new Object[] { Utilities.indent(this.identitiesString()), Utilities.indent(this.bindersString()) });
        }
        
        String identitiesString() {
            final StringBuilder sb = new StringBuilder();
            final Iterator<PskIdentity> iterator = this.identities.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString() + "\n");
            }
            return sb.toString();
        }
        
        String bindersString() {
            final StringBuilder sb = new StringBuilder();
            final Iterator<byte[]> iterator = this.binders.iterator();
            while (iterator.hasNext()) {
                sb.append("{" + Utilities.toHexString(iterator.next()) + "}\n");
            }
            return sb.toString();
        }
    }
    
    private static final class CHPreSharedKeyStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CHPreSharedKeySpec(null, byteBuffer).toString();
            }
            catch (final Exception ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class SHPreSharedKeySpec implements SSLExtension.SSLExtensionSpec
    {
        final int selectedIdentity;
        
        SHPreSharedKeySpec(final int selectedIdentity) {
            this.selectedIdentity = selectedIdentity;
        }
        
        SHPreSharedKeySpec(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient selected_identity (length=" + byteBuffer.remaining() + ")");
            }
            this.selectedIdentity = Record.getInt16(byteBuffer);
        }
        
        byte[] getEncoded() throws IOException {
            return new byte[] { (byte)(this.selectedIdentity >> 8 & 0xFF), (byte)(this.selectedIdentity & 0xFF) };
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"PreSharedKey\": '{'\n  \"selected_identity\"      : \"{0}\",\n'}'", Locale.ENGLISH).format(new Object[] { Utilities.byte16HexString(this.selectedIdentity) });
        }
    }
    
    private static final class SHPreSharedKeyStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new SHPreSharedKeySpec(null, byteBuffer).toString();
            }
            catch (final Exception ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHPreSharedKeyConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_PRE_SHARED_KEY)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable pre_shared_key extension", new Object[0]);
                }
                return;
            }
            CHPreSharedKeySpec chPreSharedKeySpec;
            try {
                chPreSharedKeySpec = new CHPreSharedKeySpec(serverHandshakeContext, byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            if (!serverHandshakeContext.handshakeExtensions.containsKey(SSLExtension.PSK_KEY_EXCHANGE_MODES)) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Client sent PSK but not PSK modes, or the PSK extension is not the last extension");
            }
            if (chPreSharedKeySpec.identities.size() != chPreSharedKeySpec.binders.size()) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "PSK extension has incorrect number of binders");
            }
            if (serverHandshakeContext.isResumption) {
                final SSLSessionContextImpl sslSessionContextImpl = (SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext();
                int n = 0;
                final Iterator<PskIdentity> iterator = chPreSharedKeySpec.identities.iterator();
                while (iterator.hasNext()) {
                    final SSLSessionImpl value = sslSessionContextImpl.get(iterator.next().identity);
                    if (value != null && canRejoin(clientHelloMessage, serverHandshakeContext, value)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.fine("Resuming session: ", value);
                        }
                        serverHandshakeContext.resumingSession = value;
                        serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_PRE_SHARED_KEY, new SHPreSharedKeySpec(n));
                        break;
                    }
                    ++n;
                }
                if (n == chPreSharedKeySpec.identities.size()) {
                    serverHandshakeContext.isResumption = false;
                    serverHandshakeContext.resumingSession = null;
                }
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_PRE_SHARED_KEY, chPreSharedKeySpec);
        }
    }
    
    private static final class CHPreSharedKeyUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.isResumption || serverHandshakeContext.resumingSession == null) {
                return;
            }
            final CHPreSharedKeySpec chPreSharedKeySpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_PRE_SHARED_KEY);
            final SHPreSharedKeySpec shPreSharedKeySpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.SH_PRE_SHARED_KEY);
            if (chPreSharedKeySpec == null || shPreSharedKeySpec == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Required extensions are unavailable");
            }
            final byte[] array = chPreSharedKeySpec.binders.get(shPreSharedKeySpec.selectedIdentity);
            final HandshakeHash copy = serverHandshakeContext.handshakeHash.copy();
            final ByteBuffer wrap = ByteBuffer.wrap(copy.removeLastReceived());
            wrap.position(4);
            ClientHello.ClientHelloMessage.readPartial(serverHandshakeContext.conContext, wrap);
            final int position = wrap.position();
            wrap.position(0);
            copy.receive(wrap, position);
            checkBinder(serverHandshakeContext, serverHandshakeContext.resumingSession, copy, array);
        }
    }
    
    static final class PartialClientHelloMessage extends SSLHandshake.HandshakeMessage
    {
        private final ClientHello.ClientHelloMessage msg;
        private final CHPreSharedKeySpec psk;
        
        PartialClientHelloMessage(final HandshakeContext handshakeContext, final ClientHello.ClientHelloMessage msg, final CHPreSharedKeySpec psk) {
            super(handshakeContext);
            this.msg = msg;
            this.psk = psk;
        }
        
        @Override
        SSLHandshake handshakeType() {
            return this.msg.handshakeType();
        }
        
        private int pskTotalLength() {
            return this.psk.getIdsEncodedLength() + this.psk.getBindersEncodedLength() + 8;
        }
        
        @Override
        int messageLength() {
            if (this.msg.extensions.get(SSLExtension.CH_PRE_SHARED_KEY) != null) {
                return this.msg.messageLength();
            }
            return this.msg.messageLength() + this.pskTotalLength();
        }
        
        @Override
        void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            this.msg.sendCore(handshakeOutStream);
            int length = this.msg.extensions.length();
            if (this.msg.extensions.get(SSLExtension.CH_PRE_SHARED_KEY) == null) {
                length += this.pskTotalLength();
            }
            handshakeOutStream.putInt16(length - 2);
            for (final SSLExtension sslExtension : SSLExtension.values()) {
                final byte[] value = this.msg.extensions.get(sslExtension);
                if (value != null) {
                    if (sslExtension != SSLExtension.CH_PRE_SHARED_KEY) {
                        handshakeOutStream.putInt16(sslExtension.id);
                        handshakeOutStream.putBytes16(value);
                    }
                }
            }
            handshakeOutStream.putInt16(SSLExtension.CH_PRE_SHARED_KEY.id);
            final byte[] encoded = this.psk.getEncoded();
            handshakeOutStream.putInt16(encoded.length);
            handshakeOutStream.write(encoded, 0, this.psk.getIdsEncodedLength() + 2);
        }
    }
    
    private static final class CHPreSharedKeyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.isResumption || clientHandshakeContext.resumingSession == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No session to resume.", new Object[0]);
                }
                return null;
            }
            if (!clientHandshakeContext.localSupportedSignAlgs.containsAll(clientHandshakeContext.resumingSession.getLocalSupportedSignatureSchemes())) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Existing session uses different signature algorithms", new Object[0]);
                }
                return null;
            }
            final SecretKey preSharedKey = clientHandshakeContext.resumingSession.getPreSharedKey();
            if (preSharedKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Existing session has no PSK.", new Object[0]);
                }
                return null;
            }
            if (clientHandshakeContext.pskIdentity == null) {
                clientHandshakeContext.pskIdentity = clientHandshakeContext.resumingSession.consumePskIdentity();
            }
            if (clientHandshakeContext.pskIdentity == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("PSK has no identity, or identity was already used", new Object[0]);
                }
                return null;
            }
            ((SSLSessionContextImpl)clientHandshakeContext.sslContext.engineGetClientSessionContext()).remove(clientHandshakeContext.resumingSession.getSessionId());
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Found resumable session. Preparing PSK message.", new Object[0]);
            }
            final ArrayList list = new ArrayList();
            list.add(new PskIdentity(clientHandshakeContext.pskIdentity, (int)(System.currentTimeMillis() - clientHandshakeContext.resumingSession.getTicketCreationTime()) + clientHandshakeContext.resumingSession.getTicketAgeAdd()));
            final byte[] access$1300 = computeBinder(clientHandshakeContext, deriveBinderKey(clientHandshakeContext, preSharedKey, clientHandshakeContext.resumingSession), clientHandshakeContext.handshakeHash.copy(), clientHandshakeContext.resumingSession, clientHandshakeContext, (ClientHello.ClientHelloMessage)handshakeMessage, this.createPskPrototype(clientHandshakeContext.resumingSession.getSuite().hashAlg.hashLength, list));
            final ArrayList list2 = new ArrayList();
            list2.add(access$1300);
            final CHPreSharedKeySpec chPreSharedKeySpec = new CHPreSharedKeySpec(list, list2);
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_PRE_SHARED_KEY, chPreSharedKeySpec);
            return chPreSharedKeySpec.getEncoded();
        }
        
        private CHPreSharedKeySpec createPskPrototype(final int n, final List<PskIdentity> list) {
            final ArrayList list2 = new ArrayList();
            final byte[] array = new byte[n];
            for (final PskIdentity pskIdentity : list) {
                list2.add(array);
            }
            return new CHPreSharedKeySpec(list, list2);
        }
    }
    
    private static final class CHPreSharedKeyOnLoadAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Handling pre_shared_key absence.", new Object[0]);
            }
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.resumingSession = null;
            serverHandshakeContext.isResumption = false;
        }
    }
    
    private static final class CHPreSharedKeyOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec() && (!serverHandshakeContext.handshakeExtensions.containsKey(SSLExtension.CH_SIGNATURE_ALGORITHMS) || !serverHandshakeContext.handshakeExtensions.containsKey(SSLExtension.CH_SUPPORTED_GROUPS))) {
                throw serverHandshakeContext.conContext.fatal(Alert.MISSING_EXTENSION, "No supported_groups or signature_algorithms extension when pre_shared_key extension is not present");
            }
        }
    }
    
    private static final class SHPreSharedKeyConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.handshakeExtensions.containsKey(SSLExtension.CH_PRE_SHARED_KEY)) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Server sent unexpected pre_shared_key extension");
            }
            final SHPreSharedKeySpec shPreSharedKeySpec = new SHPreSharedKeySpec(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Received pre_shared_key extension: ", shPreSharedKeySpec);
            }
            if (shPreSharedKeySpec.selectedIdentity != 0) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Selected identity index is not in correct range.");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Resuming session: ", clientHandshakeContext.resumingSession);
            }
        }
    }
    
    private static final class SHPreSharedKeyAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Handling pre_shared_key absence.", new Object[0]);
            }
            clientHandshakeContext.resumingSession = null;
            clientHandshakeContext.isResumption = false;
        }
    }
    
    private static final class SHPreSharedKeyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final SHPreSharedKeySpec shPreSharedKeySpec = ((ServerHandshakeContext)connectionContext).handshakeExtensions.get(SSLExtension.SH_PRE_SHARED_KEY);
            if (shPreSharedKeySpec == null) {
                return null;
            }
            return shPreSharedKeySpec.getEncoded();
        }
    }
}
