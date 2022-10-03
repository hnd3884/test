package org.openjsse.sun.security.ssl;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.security.spec.AlgorithmParameterSpec;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.KeyFactory;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import java.security.Key;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.AlgorithmConstraints;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
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
        
        ECDHClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ECPublicKey publicKey) {
            super(handshakeContext);
            final ECPoint point = publicKey.getW();
            final ECParameterSpec params = publicKey.getParams();
            this.encodedPoint = JsseJce.encodePoint(point, params.getCurve());
        }
        
        ECDHClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.remaining() != 0) {
                this.encodedPoint = Record.getBytes8(m);
            }
            else {
                this.encodedPoint = new byte[0];
            }
        }
        
        static void checkConstraints(final AlgorithmConstraints constraints, final ECPublicKey publicKey, final byte[] encodedPoint) throws SSLHandshakeException {
            try {
                final ECParameterSpec params = publicKey.getParams();
                final ECPoint point = JsseJce.decodePoint(encodedPoint, params.getCurve());
                final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
                final KeyFactory kf = JsseJce.getKeyFactory("EC");
                final ECPublicKey peerPublicKey = (ECPublicKey)kf.generatePublic(spec);
                if (!constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), peerPublicKey)) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
            }
            catch (final GeneralSecurityException | IOException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause(e);
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
        
        public void send(final HandshakeOutStream hos) throws IOException {
            if (this.encodedPoint != null && this.encodedPoint.length != 0) {
                hos.putBytes8(this.encodedPoint);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"ECDH ClientKeyExchange\": '{'\n  \"ecdh public\": '{'\n{0}\n  '}',\n'}'", Locale.ENGLISH);
            if (this.encodedPoint == null || this.encodedPoint.length == 0) {
                final Object[] messageFields = { "    <implicit>" };
                return messageFormat.format(messageFields);
            }
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields2 = { Utilities.indent(hexEncoder.encodeBuffer(this.encodedPoint), "    ") };
            return messageFormat.format(messageFields2);
        }
    }
    
    private static final class ECDHClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials credential : chc.handshakeCredentials) {
                if (credential instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)credential;
                    break;
                }
            }
            if (x509Credentials == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "No server certificate for ECDH client key exchange");
            }
            final PublicKey publicKey = x509Credentials.popPublicKey;
            if (!publicKey.getAlgorithm().equals("EC")) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not EC server certificate for ECDH client key exchange");
            }
            final ECParameterSpec params = ((ECPublicKey)publicKey).getParams();
            final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
            if (namedGroup == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH client key exchange");
            }
            final ECDHKeyExchange.ECDHEPossession ecdhePossession = new ECDHKeyExchange.ECDHEPossession(namedGroup, chc.sslContext.getSecureRandom());
            chc.handshakePossessions.add(ecdhePossession);
            final ECDHClientKeyExchangeMessage cke = new ECDHClientKeyExchangeMessage(chc, ecdhePossession.publicKey);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ECDH ClientKeyExchange handshake message", cke);
            }
            cke.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(chc.negotiatedCipherSuite.keyExchange, chc.negotiatedProtocol);
            if (ke == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SSLKeyDerivation masterKD = ke.createKeyDerivation(chc);
            final SecretKey masterSecret = masterKD.deriveKey("MasterSecret", null);
            chc.handshakeSession.setMasterSecret(masterSecret);
            final SSLTrafficKeyDerivation kd = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
            if (kd == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
            }
            chc.handshakeKeyDerivation = kd.createKeyDerivation(chc, masterSecret);
            return null;
        }
    }
    
    private static final class ECDHClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "No expected EC server cert for ECDH client key exchange");
            }
            final ECParameterSpec params = x509Possession.getECParameterSpec();
            if (params == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not EC server cert for ECDH client key exchange");
            }
            final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
            if (namedGroup == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH client key exchange");
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(shc.negotiatedCipherSuite.keyExchange, shc.negotiatedProtocol);
            if (ke == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final ECDHClientKeyExchangeMessage cke = new ECDHClientKeyExchangeMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ECDH ClientKeyExchange handshake message", cke);
            }
            try {
                final ECPoint point = JsseJce.decodePoint(cke.encodedPoint, params.getCurve());
                final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
                final KeyFactory kf = JsseJce.getKeyFactory("EC");
                final ECPublicKey peerPublicKey = (ECPublicKey)kf.generatePublic(spec);
                if (!shc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), peerPublicKey)) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
                shc.handshakeCredentials.add(new ECDHKeyExchange.ECDHECredentials(peerPublicKey, namedGroup));
            }
            catch (final GeneralSecurityException | IOException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause(e);
            }
            final SSLKeyDerivation masterKD = ke.createKeyDerivation(shc);
            final SecretKey masterSecret = masterKD.deriveKey("MasterSecret", null);
            shc.handshakeSession.setMasterSecret(masterSecret);
            final SSLTrafficKeyDerivation kd = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
            if (kd == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
            }
            shc.handshakeKeyDerivation = kd.createKeyDerivation(shc, masterSecret);
        }
    }
    
    private static final class ECDHEClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            ECDHKeyExchange.ECDHECredentials ecdheCredentials = null;
            for (final SSLCredentials cd : chc.handshakeCredentials) {
                if (cd instanceof ECDHKeyExchange.ECDHECredentials) {
                    ecdheCredentials = (ECDHKeyExchange.ECDHECredentials)cd;
                    break;
                }
            }
            if (ecdheCredentials == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "No ECDHE credentials negotiated for client key exchange");
            }
            final ECDHKeyExchange.ECDHEPossession ecdhePossession = new ECDHKeyExchange.ECDHEPossession(ecdheCredentials, chc.sslContext.getSecureRandom());
            chc.handshakePossessions.add(ecdhePossession);
            final ECDHClientKeyExchangeMessage cke = new ECDHClientKeyExchangeMessage(chc, ecdhePossession.publicKey);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ECDHE ClientKeyExchange handshake message", cke);
            }
            cke.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(chc.negotiatedCipherSuite.keyExchange, chc.negotiatedProtocol);
            if (ke == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SSLKeyDerivation masterKD = ke.createKeyDerivation(chc);
            final SecretKey masterSecret = masterKD.deriveKey("MasterSecret", null);
            chc.handshakeSession.setMasterSecret(masterSecret);
            final SSLTrafficKeyDerivation kd = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
            if (kd == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
            }
            chc.handshakeKeyDerivation = kd.createKeyDerivation(chc, masterSecret);
            return null;
        }
    }
    
    private static final class ECDHEClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            ECDHKeyExchange.ECDHEPossession ecdhePossession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof ECDHKeyExchange.ECDHEPossession) {
                    ecdhePossession = (ECDHKeyExchange.ECDHEPossession)possession;
                    break;
                }
            }
            if (ecdhePossession == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "No expected ECDHE possessions for client key exchange");
            }
            final ECParameterSpec params = ecdhePossession.publicKey.getParams();
            final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
            if (namedGroup == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDHE client key exchange");
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(shc.negotiatedCipherSuite.keyExchange, shc.negotiatedProtocol);
            if (ke == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final ECDHClientKeyExchangeMessage cke = new ECDHClientKeyExchangeMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ECDHE ClientKeyExchange handshake message", cke);
            }
            try {
                final ECPoint point = JsseJce.decodePoint(cke.encodedPoint, params.getCurve());
                final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
                final KeyFactory kf = JsseJce.getKeyFactory("EC");
                final ECPublicKey peerPublicKey = (ECPublicKey)kf.generatePublic(spec);
                if (!shc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), peerPublicKey)) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
                shc.handshakeCredentials.add(new ECDHKeyExchange.ECDHECredentials(peerPublicKey, namedGroup));
            }
            catch (final GeneralSecurityException | IOException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause(e);
            }
            final SSLKeyDerivation masterKD = ke.createKeyDerivation(shc);
            final SecretKey masterSecret = masterKD.deriveKey("MasterSecret", null);
            shc.handshakeSession.setMasterSecret(masterSecret);
            final SSLTrafficKeyDerivation kd = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
            if (kd == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
            }
            shc.handshakeKeyDerivation = kd.createKeyDerivation(shc, masterSecret);
        }
    }
}
