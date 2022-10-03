package sun.security.ssl;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Map;

final class ServerKeyExchange
{
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    
    static {
        handshakeConsumer = new ServerKeyExchangeConsumer();
        handshakeProducer = new ServerKeyExchangeProducer();
    }
    
    private static final class ServerKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final SSLKeyExchange value = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value != null) {
                for (final Map.Entry<Byte, HandshakeProducer> entry : value.getHandshakeProducers(serverHandshakeContext)) {
                    if (entry.getKey() == SSLHandshake.SERVER_KEY_EXCHANGE.id) {
                        return entry.getValue().produce(connectionContext, handshakeMessage);
                    }
                }
            }
            throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No ServerKeyExchange handshake message can be produced.");
        }
    }
    
    private static final class ServerKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.SERVER_KEY_EXCHANGE.id);
            if (clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id) != null) {
                CertificateStatus.handshakeAbsence.absent(connectionContext, null);
            }
            final SSLKeyExchange value = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
            if (value != null) {
                for (final Map.Entry<Byte, SSLConsumer> entry : value.getHandshakeConsumers(clientHandshakeContext)) {
                    if (entry.getKey() == SSLHandshake.SERVER_KEY_EXCHANGE.id) {
                        entry.getValue().consume(connectionContext, byteBuffer);
                        return;
                    }
                }
            }
            throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ServerKeyExchange handshake message.");
        }
    }
}
