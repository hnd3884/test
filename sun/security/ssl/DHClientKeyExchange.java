package sun.security.ssl;

import javax.crypto.spec.DHParameterSpec;
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
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.io.IOException;
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
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            DHKeyExchange.DHEPossession dhePossession = null;
            for (final SSLPossession sslPossession : clientHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof DHKeyExchange.DHEPossession) {
                    dhePossession = (DHKeyExchange.DHEPossession)sslPossession;
                    break;
                }
            }
            if (dhePossession == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No DHE credentials negotiated for client key exchange");
            }
            final DHPublicKey publicKey = dhePossession.publicKey;
            publicKey.getParams();
            this.y = Utilities.toByteArray(publicKey.getY());
        }
        
        DHClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            if (byteBuffer.remaining() < 3) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ClientKeyExchange message: insufficient data");
            }
            this.y = Record.getBytes16(byteBuffer);
            if (byteBuffer.hasRemaining()) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid DH ClientKeyExchange message: unknown extra data");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_KEY_EXCHANGE;
        }
        
        public int messageLength() {
            return this.y.length + 2;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes16(this.y);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"DH ClientKeyExchange\": '{'\n  \"parameters\": '{'\n    \"dh_Yc\": '{'\n{0}\n    '}',\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { Utilities.indent(new HexDumpEncoder().encodeBuffer(this.y), "      ") });
        }
    }
    
    private static final class DHClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            DHKeyExchange.DHECredentials dheCredentials = null;
            for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof DHKeyExchange.DHECredentials) {
                    dheCredentials = (DHKeyExchange.DHECredentials)sslCredentials;
                    break;
                }
            }
            if (dheCredentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No DHE credentials negotiated for client key exchange");
            }
            clientHandshakeContext.handshakePossessions.add(new DHKeyExchange.DHEPossession(dheCredentials, clientHandshakeContext.sslContext.getSecureRandom()));
            final DHClientKeyExchangeMessage dhClientKeyExchangeMessage = new DHClientKeyExchangeMessage(clientHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced DH ClientKeyExchange handshake message", dhClientKeyExchangeMessage);
            }
            dhClientKeyExchangeMessage.write(clientHandshakeContext.handshakeOutput);
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
    
    private static final class DHClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            DHKeyExchange.DHEPossession dhePossession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof DHKeyExchange.DHEPossession) {
                    dhePossession = (DHKeyExchange.DHEPossession)sslPossession;
                    break;
                }
            }
            if (dhePossession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No expected DHE possessions for client key exchange");
            }
            final SSLKeyExchange value = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final DHClientKeyExchangeMessage dhClientKeyExchangeMessage = new DHClientKeyExchangeMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming DH ClientKeyExchange handshake message", dhClientKeyExchangeMessage);
            }
            try {
                final DHParameterSpec params = dhePossession.publicKey.getParams();
                final DHPublicKey dhPublicKey = (DHPublicKey)JsseJce.getKeyFactory("DiffieHellman").generatePublic(new DHPublicKeySpec(new BigInteger(1, dhClientKeyExchangeMessage.y), params.getP(), params.getG()));
                if (!serverHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), dhPublicKey)) {
                    throw new SSLHandshakeException("DHPublicKey does not comply to algorithm constraints");
                }
                serverHandshakeContext.handshakeCredentials.add(new DHKeyExchange.DHECredentials(dhPublicKey, SupportedGroupsExtension.NamedGroup.valueOf(params)));
            }
            catch (final GeneralSecurityException | IOException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate DHPublicKey").initCause((Throwable)ex);
            }
            final SecretKey deriveKey = value.createKeyDerivation(serverHandshakeContext).deriveKey("MasterSecret", null);
            serverHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value2 = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeKeyDerivation = value2.createKeyDerivation(serverHandshakeContext, deriveKey);
        }
    }
}
