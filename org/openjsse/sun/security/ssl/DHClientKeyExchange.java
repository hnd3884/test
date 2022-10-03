package org.openjsse.sun.security.ssl;

import java.security.KeyFactory;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import java.security.Key;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.io.IOException;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;
import java.util.Iterator;

final class DHClientKeyExchange
{
    static final DHClientKeyExchangeConsumer dhHandshakeConsumer;
    static final DHClientKeyExchangeProducer dhHandshakeProducer;
    
    static {
        dhHandshakeConsumer = new DHClientKeyExchangeConsumer();
        dhHandshakeProducer = new DHClientKeyExchangeProducer();
    }
    
    private static final class DHClientKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        private byte[] y;
        
        DHClientKeyExchangeMessage(final HandshakeContext handshakeContext) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext chc = (ClientHandshakeContext)handshakeContext;
            DHKeyExchange.DHEPossession dhePossession = null;
            for (final SSLPossession possession : chc.handshakePossessions) {
                if (possession instanceof DHKeyExchange.DHEPossession) {
                    dhePossession = (DHKeyExchange.DHEPossession)possession;
                    break;
                }
            }
            if (dhePossession == null) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No DHE credentials negotiated for client key exchange");
            }
            final DHPublicKey publicKey = dhePossession.publicKey;
            final DHParameterSpec params = publicKey.getParams();
            this.y = Utilities.toByteArray(publicKey.getY());
        }
        
        DHClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext shc = (ServerHandshakeContext)handshakeContext;
            if (m.remaining() < 3) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ClientKeyExchange message: insufficient data");
            }
            this.y = Record.getBytes16(m);
            if (m.hasRemaining()) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ClientKeyExchange message: unknown extra data");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_KEY_EXCHANGE;
        }
        
        public int messageLength() {
            return this.y.length + 2;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes16(this.y);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"DH ClientKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_Yc\": '{'\n{0}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.y), "      ") };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class DHClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            DHKeyExchange.DHECredentials dheCredentials = null;
            for (final SSLCredentials cd : chc.handshakeCredentials) {
                if (cd instanceof DHKeyExchange.DHECredentials) {
                    dheCredentials = (DHKeyExchange.DHECredentials)cd;
                    break;
                }
            }
            if (dheCredentials == null) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No DHE credentials negotiated for client key exchange");
            }
            final DHKeyExchange.DHEPossession dhePossession = new DHKeyExchange.DHEPossession(dheCredentials, chc.sslContext.getSecureRandom());
            chc.handshakePossessions.add(dhePossession);
            final DHClientKeyExchangeMessage ckem = new DHClientKeyExchangeMessage(chc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced DH ClientKeyExchange handshake message", ckem);
            }
            ckem.write(chc.handshakeOutput);
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
    
    private static final class DHClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            DHKeyExchange.DHEPossession dhePossession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof DHKeyExchange.DHEPossession) {
                    dhePossession = (DHKeyExchange.DHEPossession)possession;
                    break;
                }
            }
            if (dhePossession == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No expected DHE possessions for client key exchange");
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(shc.negotiatedCipherSuite.keyExchange, shc.negotiatedProtocol);
            if (ke == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final DHClientKeyExchangeMessage ckem = new DHClientKeyExchangeMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming DH ClientKeyExchange handshake message", ckem);
            }
            try {
                final DHParameterSpec params = dhePossession.publicKey.getParams();
                final DHPublicKeySpec spec = new DHPublicKeySpec(new BigInteger(1, ckem.y), params.getP(), params.getG());
                final KeyFactory kf = JsseJce.getKeyFactory("DiffieHellman");
                final DHPublicKey peerPublicKey = (DHPublicKey)kf.generatePublic(spec);
                if (!shc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), peerPublicKey)) {
                    throw new SSLHandshakeException("DHPublicKey does not comply to algorithm constraints");
                }
                final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
                shc.handshakeCredentials.add(new DHKeyExchange.DHECredentials(peerPublicKey, namedGroup));
            }
            catch (final GeneralSecurityException | IOException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate DHPublicKey").initCause(e);
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
