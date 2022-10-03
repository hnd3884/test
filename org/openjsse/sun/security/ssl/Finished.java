package org.openjsse.sun.security.ssl;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Mac;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.internal.spec.TlsPrfParameterSpec;
import javax.crypto.SecretKey;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.MessageDigest;
import java.nio.ByteBuffer;
import java.io.IOException;

final class Finished
{
    static final SSLConsumer t12HandshakeConsumer;
    static final HandshakeProducer t12HandshakeProducer;
    static final SSLConsumer t13HandshakeConsumer;
    static final HandshakeProducer t13HandshakeProducer;
    
    static {
        t12HandshakeConsumer = new T12FinishedConsumer();
        t12HandshakeProducer = new T12FinishedProducer();
        t13HandshakeConsumer = new T13FinishedConsumer();
        t13HandshakeProducer = new T13FinishedProducer();
    }
    
    private static final class FinishedMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] verifyData;
        
        FinishedMessage(final HandshakeContext context) throws IOException {
            super(context);
            final VerifyDataScheme vds = VerifyDataScheme.valueOf(context.negotiatedProtocol);
            byte[] vd = null;
            try {
                vd = vds.createVerifyData(context, false);
            }
            catch (final IOException ioe) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Failed to generate verify_data", ioe);
            }
            this.verifyData = vd;
        }
        
        FinishedMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            int verifyDataLen = 12;
            if (context.negotiatedProtocol == ProtocolVersion.SSL30) {
                verifyDataLen = 36;
            }
            else if (context.negotiatedProtocol.useTLS13PlusSpec()) {
                verifyDataLen = context.negotiatedCipherSuite.hashAlg.hashLength;
            }
            if (m.remaining() != verifyDataLen) {
                throw context.conContext.fatal(Alert.DECODE_ERROR, "Inappropriate finished message: need " + verifyDataLen + " but remaining " + m.remaining() + " bytes verify_data");
            }
            m.get(this.verifyData = new byte[verifyDataLen]);
            final VerifyDataScheme vd = VerifyDataScheme.valueOf(context.negotiatedProtocol);
            byte[] myVerifyData;
            try {
                myVerifyData = vd.createVerifyData(context, true);
            }
            catch (final IOException ioe) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Failed to generate verify_data", ioe);
            }
            if (!MessageDigest.isEqual(myVerifyData, this.verifyData)) {
                throw context.conContext.fatal(Alert.DECRYPT_ERROR, "The Finished message cannot be verified.");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.FINISHED;
        }
        
        public int messageLength() {
            return this.verifyData.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.write(this.verifyData);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"Finished\": '{'\n  \"verify data\": '{'\n{0}\n  '}''}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encode(this.verifyData), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    enum VerifyDataScheme
    {
        SSL30("kdf_ssl30", (VerifyDataGenerator)new S30VerifyDataGenerator()), 
        TLS10("kdf_tls10", (VerifyDataGenerator)new T10VerifyDataGenerator()), 
        TLS12("kdf_tls12", (VerifyDataGenerator)new T12VerifyDataGenerator()), 
        TLS13("kdf_tls13", (VerifyDataGenerator)new T13VerifyDataGenerator());
        
        final String name;
        final VerifyDataGenerator generator;
        
        private VerifyDataScheme(final String name, final VerifyDataGenerator verifyDataGenerator) {
            this.name = name;
            this.generator = verifyDataGenerator;
        }
        
        static VerifyDataScheme valueOf(final ProtocolVersion protocolVersion) {
            switch (protocolVersion) {
                case SSL30: {
                    return VerifyDataScheme.SSL30;
                }
                case TLS10:
                case TLS11:
                case DTLS10: {
                    return VerifyDataScheme.TLS10;
                }
                case TLS12:
                case DTLS12: {
                    return VerifyDataScheme.TLS12;
                }
                case TLS13: {
                    return VerifyDataScheme.TLS13;
                }
                default: {
                    return null;
                }
            }
        }
        
        public byte[] createVerifyData(final HandshakeContext context, final boolean isValidation) throws IOException {
            if (this.generator != null) {
                return this.generator.createVerifyData(context, isValidation);
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private static final class S30VerifyDataGenerator implements VerifyDataGenerator
    {
        @Override
        public byte[] createVerifyData(final HandshakeContext context, final boolean isValidation) throws IOException {
            final HandshakeHash handshakeHash = context.handshakeHash;
            final SecretKey masterSecretKey = context.handshakeSession.getMasterSecret();
            final boolean useClientLabel = (context.sslConfig.isClientMode && !isValidation) || (!context.sslConfig.isClientMode && isValidation);
            return handshakeHash.digest(useClientLabel, masterSecretKey);
        }
    }
    
    private static final class T10VerifyDataGenerator implements VerifyDataGenerator
    {
        @Override
        public byte[] createVerifyData(final HandshakeContext context, final boolean isValidation) throws IOException {
            final HandshakeHash handshakeHash = context.handshakeHash;
            final SecretKey masterSecretKey = context.handshakeSession.getMasterSecret();
            final boolean useClientLabel = (context.sslConfig.isClientMode && !isValidation) || (!context.sslConfig.isClientMode && isValidation);
            String tlsLabel;
            if (useClientLabel) {
                tlsLabel = "client finished";
            }
            else {
                tlsLabel = "server finished";
            }
            try {
                final byte[] seed = handshakeHash.digest();
                final String prfAlg = "SunTlsPrf";
                final CipherSuite.HashAlg hashAlg = CipherSuite.HashAlg.H_NONE;
                final TlsPrfParameterSpec spec = new TlsPrfParameterSpec(masterSecretKey, tlsLabel, seed, 12, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
                final KeyGenerator kg = JsseJce.getKeyGenerator(prfAlg);
                kg.init(spec);
                final SecretKey prfKey = kg.generateKey();
                if (!"RAW".equals(prfKey.getFormat())) {
                    throw new ProviderException("Invalid PRF output, format must be RAW. Format received: " + prfKey.getFormat());
                }
                final byte[] finished = prfKey.getEncoded();
                return finished;
            }
            catch (final GeneralSecurityException e) {
                throw new RuntimeException("PRF failed", e);
            }
        }
    }
    
    private static final class T12VerifyDataGenerator implements VerifyDataGenerator
    {
        @Override
        public byte[] createVerifyData(final HandshakeContext context, final boolean isValidation) throws IOException {
            final CipherSuite cipherSuite = context.negotiatedCipherSuite;
            final HandshakeHash handshakeHash = context.handshakeHash;
            final SecretKey masterSecretKey = context.handshakeSession.getMasterSecret();
            final boolean useClientLabel = (context.sslConfig.isClientMode && !isValidation) || (!context.sslConfig.isClientMode && isValidation);
            String tlsLabel;
            if (useClientLabel) {
                tlsLabel = "client finished";
            }
            else {
                tlsLabel = "server finished";
            }
            try {
                final byte[] seed = handshakeHash.digest();
                final String prfAlg = "SunTls12Prf";
                final CipherSuite.HashAlg hashAlg = cipherSuite.hashAlg;
                final TlsPrfParameterSpec spec = new TlsPrfParameterSpec(masterSecretKey, tlsLabel, seed, 12, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
                final KeyGenerator kg = JsseJce.getKeyGenerator(prfAlg);
                kg.init(spec);
                final SecretKey prfKey = kg.generateKey();
                if (!"RAW".equals(prfKey.getFormat())) {
                    throw new ProviderException("Invalid PRF output, format must be RAW. Format received: " + prfKey.getFormat());
                }
                final byte[] finished = prfKey.getEncoded();
                return finished;
            }
            catch (final GeneralSecurityException e) {
                throw new RuntimeException("PRF failed", e);
            }
        }
    }
    
    private static final class T13VerifyDataGenerator implements VerifyDataGenerator
    {
        private static final byte[] hkdfLabel;
        private static final byte[] hkdfContext;
        
        @Override
        public byte[] createVerifyData(final HandshakeContext context, final boolean isValidation) throws IOException {
            final CipherSuite.HashAlg hashAlg = context.negotiatedCipherSuite.hashAlg;
            final SecretKey secret = isValidation ? context.baseReadSecret : context.baseWriteSecret;
            final SSLBasicKeyDerivation kdf = new SSLBasicKeyDerivation(secret, hashAlg.name, T13VerifyDataGenerator.hkdfLabel, T13VerifyDataGenerator.hkdfContext, hashAlg.hashLength);
            final AlgorithmParameterSpec keySpec = new SSLBasicKeyDerivation.SecretSizeSpec(hashAlg.hashLength);
            final SecretKey finishedSecret = kdf.deriveKey("TlsFinishedSecret", keySpec);
            final String hmacAlg = "Hmac" + hashAlg.name.replace("-", "");
            try {
                final Mac hmac = JsseJce.getMac(hmacAlg);
                hmac.init(finishedSecret);
                return hmac.doFinal(context.handshakeHash.digest());
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                throw new ProviderException("Failed to generate verify_data", ex);
            }
        }
        
        static {
            hkdfLabel = "tls13 finished".getBytes();
            hkdfContext = new byte[0];
        }
    }
    
    private static final class T12FinishedProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            if (hc.sslConfig.isClientMode) {
                return this.onProduceFinished((ClientHandshakeContext)context, message);
            }
            return this.onProduceFinished((ServerHandshakeContext)context, message);
        }
        
        private byte[] onProduceFinished(final ClientHandshakeContext chc, final SSLHandshake.HandshakeMessage message) throws IOException {
            chc.handshakeHash.update();
            final FinishedMessage fm = new FinishedMessage(chc);
            ChangeCipherSpec.t10Producer.produce(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Finished handshake message", fm);
            }
            fm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            if (chc.conContext.secureRenegotiation) {
                chc.conContext.clientVerifyData = fm.verifyData;
            }
            if (!chc.isResumption) {
                chc.conContext.consumers.put(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
                chc.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
                chc.conContext.inputRecord.expectingFinishFlight();
            }
            else {
                if (chc.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)chc.sslContext.engineGetClientSessionContext()).put(chc.handshakeSession);
                }
                chc.conContext.conSession = chc.handshakeSession.finish();
                chc.conContext.protocolVersion = chc.negotiatedProtocol;
                chc.handshakeFinished = true;
                if (!chc.sslContext.isDTLS()) {
                    chc.conContext.finishHandshake();
                }
            }
            return null;
        }
        
        private byte[] onProduceFinished(final ServerHandshakeContext shc, final SSLHandshake.HandshakeMessage message) throws IOException {
            shc.handshakeHash.update();
            final FinishedMessage fm = new FinishedMessage(shc);
            ChangeCipherSpec.t10Producer.produce(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Finished handshake message", fm);
            }
            fm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            if (shc.conContext.secureRenegotiation) {
                shc.conContext.serverVerifyData = fm.verifyData;
            }
            if (shc.isResumption) {
                shc.conContext.consumers.put(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
                shc.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
                shc.conContext.inputRecord.expectingFinishFlight();
            }
            else {
                if (shc.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext()).put(shc.handshakeSession);
                }
                shc.conContext.conSession = shc.handshakeSession.finish();
                shc.conContext.protocolVersion = shc.negotiatedProtocol;
                shc.handshakeFinished = true;
                if (!shc.sslContext.isDTLS()) {
                    shc.conContext.finishHandshake();
                }
            }
            return null;
        }
    }
    
    private static final class T12FinishedConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            hc.handshakeConsumers.remove(SSLHandshake.FINISHED.id);
            if (hc.conContext.consumers.containsKey(ContentType.CHANGE_CIPHER_SPEC.id)) {
                throw hc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Missing ChangeCipherSpec message");
            }
            if (hc.sslConfig.isClientMode) {
                this.onConsumeFinished((ClientHandshakeContext)context, message);
            }
            else {
                this.onConsumeFinished((ServerHandshakeContext)context, message);
            }
        }
        
        private void onConsumeFinished(final ClientHandshakeContext chc, final ByteBuffer message) throws IOException {
            final FinishedMessage fm = new FinishedMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming server Finished handshake message", fm);
            }
            if (chc.conContext.secureRenegotiation) {
                chc.conContext.serverVerifyData = fm.verifyData;
            }
            if (!chc.isResumption) {
                if (chc.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)chc.sslContext.engineGetClientSessionContext()).put(chc.handshakeSession);
                }
                chc.conContext.conSession = chc.handshakeSession.finish();
                chc.conContext.protocolVersion = chc.negotiatedProtocol;
                chc.handshakeFinished = true;
                if (!chc.sslContext.isDTLS()) {
                    chc.conContext.finishHandshake();
                }
            }
            else {
                chc.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = chc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(chc, fm);
                }
            }
        }
        
        private void onConsumeFinished(final ServerHandshakeContext shc, final ByteBuffer message) throws IOException {
            if (!shc.isResumption && shc.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE_VERIFY.id)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected Finished handshake message");
            }
            final FinishedMessage fm = new FinishedMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming client Finished handshake message", fm);
            }
            if (shc.conContext.secureRenegotiation) {
                shc.conContext.clientVerifyData = fm.verifyData;
            }
            if (shc.isResumption) {
                if (shc.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext()).put(shc.handshakeSession);
                }
                shc.conContext.conSession = shc.handshakeSession.finish();
                shc.conContext.protocolVersion = shc.negotiatedProtocol;
                shc.handshakeFinished = true;
                if (!shc.sslContext.isDTLS()) {
                    shc.conContext.finishHandshake();
                }
            }
            else {
                shc.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = shc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(shc, fm);
                }
            }
        }
    }
    
    private static final class T13FinishedProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            if (hc.sslConfig.isClientMode) {
                return this.onProduceFinished((ClientHandshakeContext)context, message);
            }
            return this.onProduceFinished((ServerHandshakeContext)context, message);
        }
        
        private byte[] onProduceFinished(final ClientHandshakeContext chc, final SSLHandshake.HandshakeMessage message) throws IOException {
            chc.handshakeHash.update();
            final FinishedMessage fm = new FinishedMessage(chc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Finished handshake message", fm);
            }
            fm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            if (chc.conContext.secureRenegotiation) {
                chc.conContext.clientVerifyData = fm.verifyData;
            }
            final SSLKeyDerivation kd = chc.handshakeKeyDerivation;
            if (kd == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
            if (kdg == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
            }
            try {
                final SecretKey writeSecret = kd.deriveKey("TlsClientAppTrafficSecret", null);
                final SSLKeyDerivation writeKD = kdg.createKeyDerivation(chc, writeSecret);
                final SecretKey writeKey = writeKD.deriveKey("TlsKey", null);
                final SecretKey writeIvSecret = writeKD.deriveKey("TlsIv", null);
                final IvParameterSpec writeIv = new IvParameterSpec(writeIvSecret.getEncoded());
                final SSLCipher.SSLWriteCipher writeCipher = chc.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(chc.negotiatedProtocol), chc.negotiatedProtocol, writeKey, writeIv, chc.sslContext.getSecureRandom());
                if (writeCipher == null) {
                    throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + chc.negotiatedCipherSuite + ") and protocol version (" + chc.negotiatedProtocol + ")");
                }
                chc.baseWriteSecret = writeSecret;
                chc.conContext.outputRecord.changeWriteCiphers(writeCipher, false);
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", gse);
            }
            final SSLSecretDerivation sd = ((SSLSecretDerivation)kd).forContext(chc);
            final SecretKey resumptionMasterSecret = sd.deriveKey("TlsResumptionMasterSecret", null);
            chc.handshakeSession.setResumptionMasterSecret(resumptionMasterSecret);
            chc.conContext.conSession = chc.handshakeSession.finish();
            chc.conContext.protocolVersion = chc.negotiatedProtocol;
            chc.handshakeFinished = true;
            chc.conContext.finishHandshake();
            return null;
        }
        
        private byte[] onProduceFinished(final ServerHandshakeContext shc, final SSLHandshake.HandshakeMessage message) throws IOException {
            shc.handshakeHash.update();
            final FinishedMessage fm = new FinishedMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Finished handshake message", fm);
            }
            fm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            final SSLKeyDerivation kd = shc.handshakeKeyDerivation;
            if (kd == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
            if (kdg == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
            }
            try {
                final SecretKey saltSecret = kd.deriveKey("TlsSaltSecret", null);
                final CipherSuite.HashAlg hashAlg = shc.negotiatedCipherSuite.hashAlg;
                final HKDF hkdf = new HKDF(hashAlg.name);
                final byte[] zeros = new byte[hashAlg.hashLength];
                final SecretKeySpec sharedSecret = new SecretKeySpec(zeros, "TlsZeroSecret");
                final SecretKey masterSecret = hkdf.extract(saltSecret, sharedSecret, "TlsMasterSecret");
                final SSLKeyDerivation secretKD = new SSLSecretDerivation(shc, masterSecret);
                final SecretKey writeSecret = secretKD.deriveKey("TlsServerAppTrafficSecret", null);
                final SSLKeyDerivation writeKD = kdg.createKeyDerivation(shc, writeSecret);
                final SecretKey writeKey = writeKD.deriveKey("TlsKey", null);
                final SecretKey writeIvSecret = writeKD.deriveKey("TlsIv", null);
                final IvParameterSpec writeIv = new IvParameterSpec(writeIvSecret.getEncoded());
                final SSLCipher.SSLWriteCipher writeCipher = shc.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(shc.negotiatedProtocol), shc.negotiatedProtocol, writeKey, writeIv, shc.sslContext.getSecureRandom());
                if (writeCipher == null) {
                    throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + shc.negotiatedCipherSuite + ") and protocol version (" + shc.negotiatedProtocol + ")");
                }
                shc.baseWriteSecret = writeSecret;
                shc.conContext.outputRecord.changeWriteCiphers(writeCipher, false);
                shc.handshakeKeyDerivation = secretKD;
            }
            catch (final GeneralSecurityException gse) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", gse);
            }
            if (shc.conContext.secureRenegotiation) {
                shc.conContext.serverVerifyData = fm.verifyData;
            }
            shc.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            return null;
        }
    }
    
    private static final class T13FinishedConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            if (hc.sslConfig.isClientMode) {
                this.onConsumeFinished((ClientHandshakeContext)context, message);
            }
            else {
                this.onConsumeFinished((ServerHandshakeContext)context, message);
            }
        }
        
        private void onConsumeFinished(final ClientHandshakeContext chc, final ByteBuffer message) throws IOException {
            if (!chc.isResumption && (chc.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE.id) || chc.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE_VERIFY.id))) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected Finished handshake message");
            }
            final FinishedMessage fm = new FinishedMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming server Finished handshake message", fm);
            }
            if (chc.conContext.secureRenegotiation) {
                chc.conContext.serverVerifyData = fm.verifyData;
            }
            chc.conContext.consumers.remove(ContentType.CHANGE_CIPHER_SPEC.id);
            chc.handshakeHash.update();
            final SSLKeyDerivation kd = chc.handshakeKeyDerivation;
            if (kd == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
            if (kdg == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
            }
            if (!chc.isResumption && chc.handshakeSession.isRejoinable()) {
                final SSLSessionContextImpl sessionContext = (SSLSessionContextImpl)chc.sslContext.engineGetClientSessionContext();
                sessionContext.put(chc.handshakeSession);
            }
            try {
                final SecretKey saltSecret = kd.deriveKey("TlsSaltSecret", null);
                final CipherSuite.HashAlg hashAlg = chc.negotiatedCipherSuite.hashAlg;
                final HKDF hkdf = new HKDF(hashAlg.name);
                final byte[] zeros = new byte[hashAlg.hashLength];
                final SecretKeySpec sharedSecret = new SecretKeySpec(zeros, "TlsZeroSecret");
                final SecretKey masterSecret = hkdf.extract(saltSecret, sharedSecret, "TlsMasterSecret");
                final SSLKeyDerivation secretKD = new SSLSecretDerivation(chc, masterSecret);
                final SecretKey readSecret = secretKD.deriveKey("TlsServerAppTrafficSecret", null);
                final SSLKeyDerivation writeKD = kdg.createKeyDerivation(chc, readSecret);
                final SecretKey readKey = writeKD.deriveKey("TlsKey", null);
                final SecretKey readIvSecret = writeKD.deriveKey("TlsIv", null);
                final IvParameterSpec readIv = new IvParameterSpec(readIvSecret.getEncoded());
                final SSLCipher.SSLReadCipher readCipher = chc.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(chc.negotiatedProtocol), chc.negotiatedProtocol, readKey, readIv, chc.sslContext.getSecureRandom());
                if (readCipher == null) {
                    throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + chc.negotiatedCipherSuite + ") and protocol version (" + chc.negotiatedProtocol + ")");
                }
                chc.baseReadSecret = readSecret;
                chc.conContext.inputRecord.changeReadCiphers(readCipher);
                chc.handshakeKeyDerivation = secretKD;
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", gse);
            }
            chc.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_VERIFY, SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = chc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(chc, null);
                }
            }
        }
        
        private void onConsumeFinished(final ServerHandshakeContext shc, final ByteBuffer message) throws IOException {
            if (!shc.isResumption && (shc.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE.id) || shc.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE_VERIFY.id))) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected Finished handshake message");
            }
            final FinishedMessage fm = new FinishedMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming client Finished handshake message", fm);
            }
            if (shc.conContext.secureRenegotiation) {
                shc.conContext.clientVerifyData = fm.verifyData;
            }
            final SSLKeyDerivation kd = shc.handshakeKeyDerivation;
            if (kd == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
            if (kdg == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
            }
            if (!shc.isResumption && shc.handshakeSession.isRejoinable()) {
                final SSLSessionContextImpl sessionContext = (SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext();
                sessionContext.put(shc.handshakeSession);
            }
            try {
                final SecretKey readSecret = kd.deriveKey("TlsClientAppTrafficSecret", null);
                final SSLKeyDerivation readKD = kdg.createKeyDerivation(shc, readSecret);
                final SecretKey readKey = readKD.deriveKey("TlsKey", null);
                final SecretKey readIvSecret = readKD.deriveKey("TlsIv", null);
                final IvParameterSpec readIv = new IvParameterSpec(readIvSecret.getEncoded());
                final SSLCipher.SSLReadCipher readCipher = shc.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(shc.negotiatedProtocol), shc.negotiatedProtocol, readKey, readIv, shc.sslContext.getSecureRandom());
                if (readCipher == null) {
                    throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + shc.negotiatedCipherSuite + ") and protocol version (" + shc.negotiatedProtocol + ")");
                }
                shc.baseReadSecret = readSecret;
                shc.conContext.inputRecord.changeReadCiphers(readCipher);
                shc.handshakeHash.update();
                final SSLSecretDerivation sd = ((SSLSecretDerivation)kd).forContext(shc);
                final SecretKey resumptionMasterSecret = sd.deriveKey("TlsResumptionMasterSecret", null);
                shc.handshakeSession.setResumptionMasterSecret(resumptionMasterSecret);
            }
            catch (final GeneralSecurityException gse) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", gse);
            }
            shc.conContext.conSession = shc.handshakeSession.finish();
            shc.conContext.protocolVersion = shc.negotiatedProtocol;
            shc.handshakeFinished = true;
            if (!shc.sslContext.isDTLS()) {
                shc.conContext.finishHandshake();
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Sending new session ticket", new Object[0]);
            }
            NewSessionTicket.kickstartProducer.produce(shc);
        }
    }
    
    interface VerifyDataGenerator
    {
        byte[] createVerifyData(final HandshakeContext p0, final boolean p1) throws IOException;
    }
}
