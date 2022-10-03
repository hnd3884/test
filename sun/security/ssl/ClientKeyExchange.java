package sun.security.ssl;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Map;

final class ClientKeyExchange
{
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    
    static {
        handshakeConsumer = new ClientKeyExchangeConsumer();
        handshakeProducer = new ClientKeyExchangeProducer();
    }
    
    private static final class ClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final SSLKeyExchange value = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
            if (value != null) {
                for (final Map.Entry<Byte, HandshakeProducer> entry : value.getHandshakeProducers(clientHandshakeContext)) {
                    if (entry.getKey() == SSLHandshake.CLIENT_KEY_EXCHANGE.id) {
                        return entry.getValue().produce(connectionContext, handshakeMessage);
                    }
                }
            }
            throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ClientKeyExchange handshake message.");
        }
    }
    
    private static final class ClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CLIENT_KEY_EXCHANGE.id);
            if (serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE.id)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ClientKeyExchange handshake message.");
            }
            final SSLKeyExchange value = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value != null) {
                for (final Map.Entry<Byte, SSLConsumer> entry : value.getHandshakeConsumers(serverHandshakeContext)) {
                    if (entry.getKey() == SSLHandshake.CLIENT_KEY_EXCHANGE.id) {
                        entry.getValue().consume(connectionContext, byteBuffer);
                        return;
                    }
                }
            }
            throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ClientKeyExchange handshake message.");
        }
    }
}
