package org.openjsse.sun.security.ssl;

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
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(chc.negotiatedCipherSuite.keyExchange, chc.negotiatedProtocol);
            if (ke != null) {
                for (final Map.Entry<Byte, HandshakeProducer> hp : ke.getHandshakeProducers(chc)) {
                    if (hp.getKey() == SSLHandshake.CLIENT_KEY_EXCHANGE.id) {
                        return hp.getValue().produce(context, message);
                    }
                }
            }
            throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ClientKeyExchange handshake message.");
        }
    }
    
    private static final class ClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.handshakeConsumers.remove(SSLHandshake.CLIENT_KEY_EXCHANGE.id);
            if (shc.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE.id)) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ClientKeyExchange handshake message.");
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(shc.negotiatedCipherSuite.keyExchange, shc.negotiatedProtocol);
            if (ke != null) {
                for (final Map.Entry<Byte, SSLConsumer> hc : ke.getHandshakeConsumers(shc)) {
                    if (hc.getKey() == SSLHandshake.CLIENT_KEY_EXCHANGE.id) {
                        hc.getValue().consume(context, message);
                        return;
                    }
                }
            }
            throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ClientKeyExchange handshake message.");
        }
    }
}
