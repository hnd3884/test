package org.openjsse.sun.security.ssl;

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
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(shc.negotiatedCipherSuite.keyExchange, shc.negotiatedProtocol);
            if (ke != null) {
                for (final Map.Entry<Byte, HandshakeProducer> hc : ke.getHandshakeProducers(shc)) {
                    if (hc.getKey() == SSLHandshake.SERVER_KEY_EXCHANGE.id) {
                        return hc.getValue().produce(context, message);
                    }
                }
            }
            throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No ServerKeyExchange handshake message can be produced.");
        }
    }
    
    private static final class ServerKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.SERVER_KEY_EXCHANGE.id);
            final SSLConsumer certStatCons = chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id);
            if (certStatCons != null) {
                CertificateStatus.handshakeAbsence.absent(context, null);
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(chc.negotiatedCipherSuite.keyExchange, chc.negotiatedProtocol);
            if (ke != null) {
                for (final Map.Entry<Byte, SSLConsumer> hc : ke.getHandshakeConsumers(chc)) {
                    if (hc.getKey() == SSLHandshake.SERVER_KEY_EXCHANGE.id) {
                        hc.getValue().consume(context, message);
                        return;
                    }
                }
            }
            throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ServerKeyExchange handshake message.");
        }
    }
}
