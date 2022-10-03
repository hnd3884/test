package sun.security.ssl;

import javax.crypto.SecretKey;
import java.util.Iterator;
import java.security.spec.AlgorithmParameterSpec;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.spec.ECParameterSpec;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import java.security.Key;
import java.util.Set;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.PublicKey;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.AlgorithmConstraints;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.interfaces.ECPublicKey;

final class ECDHClientKeyExchange
{
    static final SSLConsumer ecdhHandshakeConsumer;
    static final HandshakeProducer ecdhHandshakeProducer;
    static final SSLConsumer ecdheHandshakeConsumer;
    static final HandshakeProducer ecdheHandshakeProducer;
    
    static {
        ecdhHandshakeConsumer = new ECDHClientKeyExchangeConsumer();
        ecdhHandshakeProducer = new ECDHClientKeyExchangeProducer();
        ecdheHandshakeConsumer = new ECDHEClientKeyExchangeConsumer();
        ecdheHandshakeProducer = new ECDHEClientKeyExchangeProducer();
    }
    
    private static final class ECDHClientKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] encodedPoint;
        
        ECDHClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ECPublicKey ecPublicKey) {
            super(handshakeContext);
            this.encodedPoint = JsseJce.encodePoint(ecPublicKey.getW(), ecPublicKey.getParams().getCurve());
        }
        
        ECDHClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() != 0) {
                this.encodedPoint = Record.getBytes8(byteBuffer);
            }
            else {
                this.encodedPoint = new byte[0];
            }
        }
        
        static void checkConstraints(final AlgorithmConstraints algorithmConstraints, final ECPublicKey ecPublicKey, final byte[] array) throws SSLHandshakeException {
            try {
                final ECParameterSpec params = ecPublicKey.getParams();
                if (!algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(array, params.getCurve()), params)))) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
            }
            catch (final GeneralSecurityException | IOException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause((Throwable)ex);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_KEY_EXCHANGE;
        }
        
        public int messageLength() {
            if (this.encodedPoint == null || this.encodedPoint.length == 0) {
                return 0;
            }
            return 1 + this.encodedPoint.length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            if (this.encodedPoint != null && this.encodedPoint.length != 0) {
                handshakeOutStream.putBytes8(this.encodedPoint);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"ECDH ClientKeyExchange\": '{'\n  \"ecdh public\": '{'\n{0}\n  '}',\n'}'", Locale.ENGLISH);
            if (this.encodedPoint == null || this.encodedPoint.length == 0) {
                return messageFormat.format(new Object[] { "    <implicit>" });
            }
            return messageFormat.format(new Object[] { Utilities.indent(new HexDumpEncoder().encodeBuffer(this.encodedPoint), "    ") });
        }
    }
    
    private static final class ECDHClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No server certificate for ECDH client key exchange");
            }
            final PublicKey popPublicKey = x509Credentials.popPublicKey;
            if (!popPublicKey.getAlgorithm().equals("EC")) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not EC server certificate for ECDH client key exchange");
            }
            final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(((ECPublicKey)popPublicKey).getParams());
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH client key exchange");
            }
            final ECDHKeyExchange.ECDHEPossession ecdhePossession = new ECDHKeyExchange.ECDHEPossession(value, clientHandshakeContext.sslContext.getSecureRandom());
            clientHandshakeContext.handshakePossessions.add(ecdhePossession);
            final ECDHClientKeyExchangeMessage ecdhClientKeyExchangeMessage = new ECDHClientKeyExchangeMessage(clientHandshakeContext, ecdhePossession.publicKey);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ECDH ClientKeyExchange handshake message", ecdhClientKeyExchangeMessage);
            }
            ecdhClientKeyExchangeMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            final SSLKeyExchange value2 = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SecretKey deriveKey = value2.createKeyDerivation(clientHandshakeContext).deriveKey("MasterSecret", null);
            clientHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value3 = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value3 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            clientHandshakeContext.handshakeKeyDerivation = value3.createKeyDerivation(clientHandshakeContext, deriveKey);
            return null;
        }
    }
    
    private static final class ECDHClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No expected EC server cert for ECDH client key exchange");
            }
            final ECParameterSpec ecParameterSpec = x509Possession.getECParameterSpec();
            if (ecParameterSpec == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not EC server cert for ECDH client key exchange");
            }
            final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(ecParameterSpec);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH client key exchange");
            }
            final SSLKeyExchange value2 = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final ECDHClientKeyExchangeMessage ecdhClientKeyExchangeMessage = new ECDHClientKeyExchangeMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ECDH ClientKeyExchange handshake message", ecdhClientKeyExchangeMessage);
            }
            try {
                final ECPublicKey ecPublicKey = (ECPublicKey)JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(ecdhClientKeyExchangeMessage.encodedPoint, ecParameterSpec.getCurve()), ecParameterSpec));
                if (!serverHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ecPublicKey)) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
                serverHandshakeContext.handshakeCredentials.add(new ECDHKeyExchange.ECDHECredentials(ecPublicKey, value));
            }
            catch (final GeneralSecurityException | IOException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause((Throwable)ex);
            }
            final SecretKey deriveKey = value2.createKeyDerivation(serverHandshakeContext).deriveKey("MasterSecret", null);
            serverHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value3 = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value3 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeKeyDerivation = value3.createKeyDerivation(serverHandshakeContext, deriveKey);
        }
    }
    
    private static final class ECDHEClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            ECDHKeyExchange.ECDHECredentials ecdheCredentials = null;
            for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof ECDHKeyExchange.ECDHECredentials) {
                    ecdheCredentials = (ECDHKeyExchange.ECDHECredentials)sslCredentials;
                    break;
                }
            }
            if (ecdheCredentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No ECDHE credentials negotiated for client key exchange");
            }
            final ECDHKeyExchange.ECDHEPossession ecdhePossession = new ECDHKeyExchange.ECDHEPossession(ecdheCredentials, clientHandshakeContext.sslContext.getSecureRandom());
            clientHandshakeContext.handshakePossessions.add(ecdhePossession);
            final ECDHClientKeyExchangeMessage ecdhClientKeyExchangeMessage = new ECDHClientKeyExchangeMessage(clientHandshakeContext, ecdhePossession.publicKey);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ECDHE ClientKeyExchange handshake message", ecdhClientKeyExchangeMessage);
            }
            ecdhClientKeyExchangeMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            final SSLKeyExchange value = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SecretKey deriveKey = value.createKeyDerivation(clientHandshakeContext).deriveKey("MasterSecret", null);
            clientHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value2 = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            clientHandshakeContext.handshakeKeyDerivation = value2.createKeyDerivation(clientHandshakeContext, deriveKey);
            return null;
        }
    }
    
    private static final class ECDHEClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            ECDHKeyExchange.ECDHEPossession ecdhePossession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof ECDHKeyExchange.ECDHEPossession) {
                    ecdhePossession = (ECDHKeyExchange.ECDHEPossession)sslPossession;
                    break;
                }
            }
            if (ecdhePossession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No expected ECDHE possessions for client key exchange");
            }
            final ECParameterSpec params = ecdhePossession.publicKey.getParams();
            final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(params);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDHE client key exchange");
            }
            final SSLKeyExchange value2 = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final ECDHClientKeyExchangeMessage ecdhClientKeyExchangeMessage = new ECDHClientKeyExchangeMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ECDHE ClientKeyExchange handshake message", ecdhClientKeyExchangeMessage);
            }
            try {
                final ECPublicKey ecPublicKey = (ECPublicKey)JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(ecdhClientKeyExchangeMessage.encodedPoint, params.getCurve()), params));
                if (!serverHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), ecPublicKey)) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
                serverHandshakeContext.handshakeCredentials.add(new ECDHKeyExchange.ECDHECredentials(ecPublicKey, value));
            }
            catch (final GeneralSecurityException | IOException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause((Throwable)ex);
            }
            final SecretKey deriveKey = value2.createKeyDerivation(serverHandshakeContext).deriveKey("MasterSecret", null);
            serverHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value3 = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value3 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeKeyDerivation = value3.createKeyDerivation(serverHandshakeContext, deriveKey);
        }
    }
}
