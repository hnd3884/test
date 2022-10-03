package org.openjsse.sun.security.ssl;

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
import java.util.Objects;
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
    
    private static boolean canRejoin(final ClientHello.ClientHelloMessage clientHello, final ServerHandshakeContext shc, final SSLSessionImpl s) {
        boolean result = s.isRejoinable() && s.getPreSharedKey() != null;
        if (result && s.getProtocolVersion() != shc.negotiatedProtocol) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.finest("Can't resume, incorrect protocol version", new Object[0]);
            }
            result = false;
        }
        if (shc.localSupportedSignAlgs == null) {
            shc.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, shc.activeProtocols);
        }
        if (result && shc.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
            try {
                s.getPeerPrincipal();
            }
            catch (final SSLPeerUnverifiedException e) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, client authentication is required", new Object[0]);
                }
                result = false;
            }
            final Collection<SignatureScheme> sessionSigAlgs = s.getLocalSupportedSignatureSchemes();
            if (result && !shc.localSupportedSignAlgs.containsAll(sessionSigAlgs)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Can't resume. Session uses different signature algorithms", new Object[0]);
                }
                result = false;
            }
        }
        final String identityAlg = shc.sslConfig.identificationProtocol;
        if (result && identityAlg != null) {
            final String sessionIdentityAlg = s.getIdentificationProtocol();
            if (!Objects.equals(identityAlg, sessionIdentityAlg)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Can't resume, endpoint id algorithm does not match, requested: " + identityAlg + ", cached: " + sessionIdentityAlg, new Object[0]);
                }
                result = false;
            }
        }
        if (result && (!shc.isNegotiable(s.getSuite()) || !clientHello.cipherSuites.contains(s.getSuite()))) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.finest("Can't resume, unavailable session cipher suite", new Object[0]);
            }
            result = false;
        }
        return result;
    }
    
    private static void checkBinder(final ServerHandshakeContext shc, final SSLSessionImpl session, final HandshakeHash pskBinderHash, final byte[] binder) throws IOException {
        final SecretKey psk = session.getPreSharedKey();
        if (psk == null) {
            throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Session has no PSK");
        }
        final SecretKey binderKey = deriveBinderKey(shc, psk, session);
        final byte[] computedBinder = computeBinder(shc, binderKey, session, pskBinderHash);
        if (!Arrays.equals(binder, computedBinder)) {
            throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Incorect PSK binder value");
        }
    }
    
    private static byte[] computeBinder(final HandshakeContext context, final SecretKey binderKey, final SSLSessionImpl session, final HandshakeHash pskBinderHash) throws IOException {
        pskBinderHash.determine(session.getProtocolVersion(), session.getSuite());
        pskBinderHash.update();
        final byte[] digest = pskBinderHash.digest();
        return computeBinder(context, binderKey, session, digest);
    }
    
    private static byte[] computeBinder(final HandshakeContext context, final SecretKey binderKey, final HandshakeHash hash, final SSLSessionImpl session, final HandshakeContext ctx, final ClientHello.ClientHelloMessage hello, final CHPreSharedKeySpec pskPrototype) throws IOException {
        final PartialClientHelloMessage partialMsg = new PartialClientHelloMessage(ctx, hello, pskPrototype);
        final SSLEngineOutputRecord record = new SSLEngineOutputRecord(hash);
        final HandshakeOutStream hos = new HandshakeOutStream(record);
        partialMsg.write(hos);
        hash.determine(session.getProtocolVersion(), session.getSuite());
        hash.update();
        final byte[] digest = hash.digest();
        return computeBinder(context, binderKey, session, digest);
    }
    
    private static byte[] computeBinder(final HandshakeContext context, final SecretKey binderKey, final SSLSessionImpl session, final byte[] digest) throws IOException {
        try {
            final CipherSuite.HashAlg hashAlg = session.getSuite().hashAlg;
            final HKDF hkdf = new HKDF(hashAlg.name);
            final byte[] label = "tls13 finished".getBytes();
            final byte[] hkdfInfo = SSLSecretDerivation.createHkdfInfo(label, new byte[0], hashAlg.hashLength);
            final SecretKey finishedKey = hkdf.expand(binderKey, hkdfInfo, hashAlg.hashLength, "TlsBinderKey");
            final String hmacAlg = "Hmac" + hashAlg.name.replace("-", "");
            try {
                final Mac hmac = JsseJce.getMac(hmacAlg);
                hmac.init(finishedKey);
                return hmac.doFinal(digest);
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                throw context.conContext.fatal(Alert.INTERNAL_ERROR, ex);
            }
        }
        catch (final GeneralSecurityException ex2) {
            throw context.conContext.fatal(Alert.INTERNAL_ERROR, ex2);
        }
    }
    
    private static SecretKey deriveBinderKey(final HandshakeContext context, final SecretKey psk, final SSLSessionImpl session) throws IOException {
        try {
            final CipherSuite.HashAlg hashAlg = session.getSuite().hashAlg;
            final HKDF hkdf = new HKDF(hashAlg.name);
            final byte[] zeros = new byte[hashAlg.hashLength];
            final SecretKey earlySecret = hkdf.extract(zeros, psk, "TlsEarlySecret");
            final byte[] label = "tls13 res binder".getBytes();
            final MessageDigest md = MessageDigest.getInstance(hashAlg.name);
            final byte[] hkdfInfo = SSLSecretDerivation.createHkdfInfo(label, md.digest(new byte[0]), hashAlg.hashLength);
            return hkdf.expand(earlySecret, hkdfInfo, hashAlg.hashLength, "TlsBinderKey");
        }
        catch (final GeneralSecurityException ex) {
            throw context.conContext.fatal(Alert.INTERNAL_ERROR, ex);
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
        
        void writeEncoded(final ByteBuffer m) throws IOException {
            Record.putBytes16(m, this.identity);
            Record.putInt32(m, this.obfuscatedAge);
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
        
        CHPreSharedKeySpec(final HandshakeContext context, final ByteBuffer m) throws IOException {
            if (m.remaining() < 44) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient data (length=" + m.remaining() + ")");
            }
            final int idEncodedLength = Record.getInt16(m);
            if (idEncodedLength < 7) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient identities (length=" + idEncodedLength + ")");
            }
            this.identities = new ArrayList<PskIdentity>();
            PskIdentity pskId;
            for (int idReadLength = 0; idReadLength < idEncodedLength; idReadLength += pskId.getEncodedLength()) {
                final byte[] id = Record.getBytes16(m);
                if (id.length < 1) {
                    throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient identity (length=" + id.length + ")");
                }
                final int obfuscatedTicketAge = Record.getInt32(m);
                pskId = new PskIdentity(id, obfuscatedTicketAge);
                this.identities.add(pskId);
            }
            if (m.remaining() < 35) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient binders data (length=" + m.remaining() + ")");
            }
            final int bindersEncodedLen = Record.getInt16(m);
            if (bindersEncodedLen < 33) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient binders (length=" + bindersEncodedLen + ")");
            }
            this.binders = new ArrayList<byte[]>();
            byte[] binder;
            for (int bindersReadLength = 0; bindersReadLength < bindersEncodedLen; bindersReadLength += 1 + binder.length) {
                binder = Record.getBytes8(m);
                if (binder.length < 32) {
                    throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient binder entry (length=" + binder.length + ")");
                }
                this.binders.add(binder);
            }
        }
        
        int getIdsEncodedLength() {
            int idEncodedLength = 0;
            for (final PskIdentity curId : this.identities) {
                idEncodedLength += curId.getEncodedLength();
            }
            return idEncodedLength;
        }
        
        int getBindersEncodedLength() {
            int binderEncodedLength = 0;
            for (final byte[] curBinder : this.binders) {
                binderEncodedLength += 1 + curBinder.length;
            }
            return binderEncodedLength;
        }
        
        byte[] getEncoded() throws IOException {
            final int idsEncodedLength = this.getIdsEncodedLength();
            final int bindersEncodedLength = this.getBindersEncodedLength();
            final int encodedLength = 4 + idsEncodedLength + bindersEncodedLength;
            final byte[] buffer = new byte[encodedLength];
            final ByteBuffer m = ByteBuffer.wrap(buffer);
            Record.putInt16(m, idsEncodedLength);
            for (final PskIdentity curId : this.identities) {
                curId.writeEncoded(m);
            }
            Record.putInt16(m, bindersEncodedLength);
            for (final byte[] curBinder : this.binders) {
                Record.putBytes8(m, curBinder);
            }
            return buffer;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"PreSharedKey\": '{'\n  \"identities\"    : \"{0}\",\n  \"binders\"       : \"{1}\",\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { Utilities.indent(this.identitiesString()), Utilities.indent(this.bindersString()) };
            return messageFormat.format(messageFields);
        }
        
        String identitiesString() {
            final StringBuilder result = new StringBuilder();
            for (final PskIdentity curId : this.identities) {
                result.append(curId.toString() + "\n");
            }
            return result.toString();
        }
        
        String bindersString() {
            final StringBuilder result = new StringBuilder();
            for (final byte[] curBinder : this.binders) {
                result.append("{" + Utilities.toHexString(curBinder) + "}\n");
            }
            return result.toString();
        }
    }
    
    private static final class CHPreSharedKeyStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CHPreSharedKeySpec(null, buffer).toString();
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
        
        SHPreSharedKeySpec(final HandshakeContext context, final ByteBuffer m) throws IOException {
            if (m.remaining() < 2) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid pre_shared_key extension: insufficient selected_identity (length=" + m.remaining() + ")");
            }
            this.selectedIdentity = Record.getInt16(m);
        }
        
        byte[] getEncoded() throws IOException {
            return new byte[] { (byte)(this.selectedIdentity >> 8 & 0xFF), (byte)(this.selectedIdentity & 0xFF) };
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"PreSharedKey\": '{'\n  \"selected_identity\"      : \"{0}\",\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { Utilities.byte16HexString(this.selectedIdentity) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class SHPreSharedKeyStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new SHPreSharedKeySpec(null, buffer).toString();
            }
            catch (final Exception ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHPreSharedKeyConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_PRE_SHARED_KEY)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable pre_shared_key extension", new Object[0]);
                }
                return;
            }
            CHPreSharedKeySpec pskSpec = null;
            try {
                pskSpec = new CHPreSharedKeySpec(shc, buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            if (!shc.handshakeExtensions.containsKey(SSLExtension.PSK_KEY_EXCHANGE_MODES)) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Client sent PSK but not PSK modes, or the PSK extension is not the last extension");
            }
            if (pskSpec.identities.size() != pskSpec.binders.size()) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "PSK extension has incorrect number of binders");
            }
            if (shc.isResumption) {
                final SSLSessionContextImpl sessionCache = (SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext();
                int idIndex = 0;
                for (final PskIdentity requestedId : pskSpec.identities) {
                    final SSLSessionImpl s = sessionCache.get(requestedId.identity);
                    if (s != null && canRejoin(clientHello, shc, s)) {
                        synchronized (sessionCache) {
                            final SSLSessionImpl rs = sessionCache.get(requestedId.identity);
                            if (rs == s) {
                                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                                    SSLLogger.fine("Resuming session: ", s);
                                }
                                sessionCache.remove(s.getSessionId());
                                shc.resumingSession = s;
                                shc.handshakeExtensions.put(SSLExtension.SH_PRE_SHARED_KEY, new SHPreSharedKeySpec(idIndex));
                                break;
                            }
                        }
                    }
                    ++idIndex;
                }
                if (idIndex == pskSpec.identities.size()) {
                    shc.isResumption = false;
                    shc.resumingSession = null;
                }
            }
            shc.handshakeExtensions.put(SSLExtension.CH_PRE_SHARED_KEY, pskSpec);
        }
    }
    
    private static final class CHPreSharedKeyUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.isResumption || shc.resumingSession == null) {
                return;
            }
            final CHPreSharedKeySpec chPsk = shc.handshakeExtensions.get(SSLExtension.CH_PRE_SHARED_KEY);
            final SHPreSharedKeySpec shPsk = shc.handshakeExtensions.get(SSLExtension.SH_PRE_SHARED_KEY);
            if (chPsk == null || shPsk == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Required extensions are unavailable");
            }
            final byte[] binder = chPsk.binders.get(shPsk.selectedIdentity);
            final HandshakeHash pskBinderHash = shc.handshakeHash.copy();
            final byte[] lastMessage = pskBinderHash.removeLastReceived();
            final ByteBuffer messageBuf = ByteBuffer.wrap(lastMessage);
            messageBuf.position(4);
            ClientHello.ClientHelloMessage.readPartial(shc.conContext, messageBuf);
            final int length = messageBuf.position();
            messageBuf.position(0);
            pskBinderHash.receive(messageBuf, length);
            checkBinder(shc, shc.resumingSession, pskBinderHash, binder);
        }
    }
    
    static final class PartialClientHelloMessage extends SSLHandshake.HandshakeMessage
    {
        private final ClientHello.ClientHelloMessage msg;
        private final CHPreSharedKeySpec psk;
        
        PartialClientHelloMessage(final HandshakeContext ctx, final ClientHello.ClientHelloMessage msg, final CHPreSharedKeySpec psk) {
            super(ctx);
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
        void send(final HandshakeOutStream hos) throws IOException {
            this.msg.sendCore(hos);
            int extsLen = this.msg.extensions.length();
            if (this.msg.extensions.get(SSLExtension.CH_PRE_SHARED_KEY) == null) {
                extsLen += this.pskTotalLength();
            }
            hos.putInt16(extsLen - 2);
            for (final SSLExtension ext : SSLExtension.values()) {
                final byte[] extData = this.msg.extensions.get(ext);
                if (extData != null) {
                    if (ext != SSLExtension.CH_PRE_SHARED_KEY) {
                        final int extID = ext.id;
                        hos.putInt16(extID);
                        hos.putBytes16(extData);
                    }
                }
            }
            final int extID2 = SSLExtension.CH_PRE_SHARED_KEY.id;
            hos.putInt16(extID2);
            final byte[] encodedPsk = this.psk.getEncoded();
            hos.putInt16(encodedPsk.length);
            hos.write(encodedPsk, 0, this.psk.getIdsEncodedLength() + 2);
        }
    }
    
    private static final class CHPreSharedKeyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.isResumption || chc.resumingSession == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No session to resume.", new Object[0]);
                }
                return null;
            }
            final Collection<SignatureScheme> sessionSigAlgs = chc.resumingSession.getLocalSupportedSignatureSchemes();
            if (!chc.localSupportedSignAlgs.containsAll(sessionSigAlgs)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Existing session uses different signature algorithms", new Object[0]);
                }
                return null;
            }
            final SecretKey psk = chc.resumingSession.getPreSharedKey();
            if (psk == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Existing session has no PSK.", new Object[0]);
                }
                return null;
            }
            if (chc.pskIdentity == null) {
                chc.pskIdentity = chc.resumingSession.consumePskIdentity();
            }
            if (chc.pskIdentity == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("PSK has no identity, or identity was already used", new Object[0]);
                }
                return null;
            }
            final SSLSessionContextImpl sessionCache = (SSLSessionContextImpl)chc.sslContext.engineGetClientSessionContext();
            sessionCache.remove(chc.resumingSession.getSessionId());
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Found resumable session. Preparing PSK message.", new Object[0]);
            }
            final List<PskIdentity> identities = new ArrayList<PskIdentity>();
            final int ageMillis = (int)(System.currentTimeMillis() - chc.resumingSession.getTicketCreationTime());
            final int obfuscatedAge = ageMillis + chc.resumingSession.getTicketAgeAdd();
            identities.add(new PskIdentity(chc.pskIdentity, obfuscatedAge));
            final SecretKey binderKey = deriveBinderKey(chc, psk, chc.resumingSession);
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final CHPreSharedKeySpec pskPrototype = this.createPskPrototype(chc.resumingSession.getSuite().hashAlg.hashLength, identities);
            final HandshakeHash pskBinderHash = chc.handshakeHash.copy();
            final byte[] binder = computeBinder(chc, binderKey, pskBinderHash, chc.resumingSession, chc, clientHello, pskPrototype);
            final List<byte[]> binders = new ArrayList<byte[]>();
            binders.add(binder);
            final CHPreSharedKeySpec pskMessage = new CHPreSharedKeySpec(identities, binders);
            chc.handshakeExtensions.put(SSLExtension.CH_PRE_SHARED_KEY, pskMessage);
            return pskMessage.getEncoded();
        }
        
        private CHPreSharedKeySpec createPskPrototype(final int hashLength, final List<PskIdentity> identities) {
            final List<byte[]> binders = new ArrayList<byte[]>();
            final byte[] binderProto = new byte[hashLength];
            for (final PskIdentity curId : identities) {
                binders.add(binderProto);
            }
            return new CHPreSharedKeySpec(identities, binders);
        }
    }
    
    private static final class CHPreSharedKeyOnLoadAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Handling pre_shared_key absence.", new Object[0]);
            }
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.resumingSession = null;
            shc.isResumption = false;
        }
    }
    
    private static final class CHPreSharedKeyOnTradeAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.negotiatedProtocol.useTLS13PlusSpec() && (!shc.handshakeExtensions.containsKey(SSLExtension.CH_SIGNATURE_ALGORITHMS) || !shc.handshakeExtensions.containsKey(SSLExtension.CH_SUPPORTED_GROUPS))) {
                throw shc.conContext.fatal(Alert.MISSING_EXTENSION, "No supported_groups or signature_algorithms extension when pre_shared_key extension is not present");
            }
        }
    }
    
    private static final class SHPreSharedKeyConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.handshakeExtensions.containsKey(SSLExtension.CH_PRE_SHARED_KEY)) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Server sent unexpected pre_shared_key extension");
            }
            final SHPreSharedKeySpec shPsk = new SHPreSharedKeySpec(chc, buffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Received pre_shared_key extension: ", shPsk);
            }
            if (shPsk.selectedIdentity != 0) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Selected identity index is not in correct range.");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Resuming session: ", chc.resumingSession);
            }
        }
    }
    
    private static final class SHPreSharedKeyAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Handling pre_shared_key absence.", new Object[0]);
            }
            chc.resumingSession = null;
            chc.isResumption = false;
        }
    }
    
    private static final class SHPreSharedKeyProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final SHPreSharedKeySpec psk = shc.handshakeExtensions.get(SSLExtension.SH_PRE_SHARED_KEY);
            if (psk == null) {
                return null;
            }
            return psk.getEncoded();
        }
    }
}
