package sun.security.ssl;

import java.util.Map;

interface SSLHandshakeBinding
{
    default SSLHandshake[] getRelatedHandshakers(final HandshakeContext handshakeContext) {
        return new SSLHandshake[0];
    }
    
    default Map.Entry<Byte, HandshakeProducer>[] getHandshakeProducers(final HandshakeContext handshakeContext) {
        return new Map.Entry[0];
    }
    
    default Map.Entry<Byte, SSLConsumer>[] getHandshakeConsumers(final HandshakeContext handshakeContext) {
        return new Map.Entry[0];
    }
}
