package sun.security.ssl;

import javax.net.ssl.SSLException;
import java.util.AbstractMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

enum SSLHandshake implements SSLConsumer, HandshakeProducer
{
    HELLO_REQUEST((byte)0, "hello_request", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(HelloRequest.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(HelloRequest.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_12) }), 
    CLIENT_HELLO((byte)1, "client_hello", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ClientHello.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ClientHello.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_13) }), 
    SERVER_HELLO((byte)2, "server_hello", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerHello.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerHello.t12HandshakeProducer, ProtocolVersion.PROTOCOLS_TO_12), new AbstractMap.SimpleImmutableEntry(ServerHello.t13HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    HELLO_RETRY_REQUEST((byte)2, "hello_retry_request", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerHello.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerHello.hrrHandshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    NEW_SESSION_TICKET((byte)4, "new_session_ticket", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(NewSessionTicket.handshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(NewSessionTicket.handshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    END_OF_EARLY_DATA((byte)5, "end_of_early_data"), 
    ENCRYPTED_EXTENSIONS((byte)8, "encrypted_extensions", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(EncryptedExtensions.handshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(EncryptedExtensions.handshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    CERTIFICATE((byte)11, "certificate", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateMessage.t12HandshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12), new AbstractMap.SimpleImmutableEntry(CertificateMessage.t13HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateMessage.t12HandshakeProducer, ProtocolVersion.PROTOCOLS_TO_12), new AbstractMap.SimpleImmutableEntry(CertificateMessage.t13HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    SERVER_KEY_EXCHANGE((byte)12, "server_key_exchange", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerKeyExchange.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerKeyExchange.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_12) }), 
    CERTIFICATE_REQUEST((byte)13, "certificate_request", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateRequest.t10HandshakeConsumer, ProtocolVersion.PROTOCOLS_TO_11), new AbstractMap.SimpleImmutableEntry(CertificateRequest.t12HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_12), new AbstractMap.SimpleImmutableEntry(CertificateRequest.t13HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateRequest.t10HandshakeProducer, ProtocolVersion.PROTOCOLS_TO_11), new AbstractMap.SimpleImmutableEntry(CertificateRequest.t12HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_12), new AbstractMap.SimpleImmutableEntry(CertificateRequest.t13HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    SERVER_HELLO_DONE((byte)14, "server_hello_done", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerHelloDone.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ServerHelloDone.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_12) }), 
    CERTIFICATE_VERIFY((byte)15, "certificate_verify", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateVerify.s30HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_30), new AbstractMap.SimpleImmutableEntry(CertificateVerify.t10HandshakeConsumer, ProtocolVersion.PROTOCOLS_10_11), new AbstractMap.SimpleImmutableEntry(CertificateVerify.t12HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_12), new AbstractMap.SimpleImmutableEntry(CertificateVerify.t13HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateVerify.s30HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_30), new AbstractMap.SimpleImmutableEntry(CertificateVerify.t10HandshakeProducer, ProtocolVersion.PROTOCOLS_10_11), new AbstractMap.SimpleImmutableEntry(CertificateVerify.t12HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_12), new AbstractMap.SimpleImmutableEntry(CertificateVerify.t13HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    CLIENT_KEY_EXCHANGE((byte)16, "client_key_exchange", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ClientKeyExchange.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(ClientKeyExchange.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_12) }), 
    FINISHED((byte)20, "finished", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(Finished.t12HandshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12), new AbstractMap.SimpleImmutableEntry(Finished.t13HandshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(Finished.t12HandshakeProducer, ProtocolVersion.PROTOCOLS_TO_12), new AbstractMap.SimpleImmutableEntry(Finished.t13HandshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    CERTIFICATE_URL((byte)21, "certificate_url"), 
    CERTIFICATE_STATUS((byte)22, "certificate_status", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateStatus.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateStatus.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeAbsence, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(CertificateStatus.handshakeAbsence, ProtocolVersion.PROTOCOLS_TO_12) }), 
    SUPPLEMENTAL_DATA((byte)23, "supplemental_data"), 
    KEY_UPDATE((byte)24, "key_update", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(KeyUpdate.handshakeConsumer, ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(KeyUpdate.handshakeProducer, ProtocolVersion.PROTOCOLS_OF_13) }), 
    MESSAGE_HASH((byte)(-2), "message_hash"), 
    NOT_APPLICABLE((byte)(-1), "not_applicable");
    
    final byte id;
    final String name;
    final Map.Entry<SSLConsumer, ProtocolVersion[]>[] handshakeConsumers;
    final Map.Entry<HandshakeProducer, ProtocolVersion[]>[] handshakeProducers;
    final Map.Entry<HandshakeAbsence, ProtocolVersion[]>[] handshakeAbsences;
    
    private SSLHandshake(final byte b, final String s2) {
        this(b, s2, new Map.Entry[0], new Map.Entry[0], new Map.Entry[0]);
    }
    
    private SSLHandshake(final byte b, final String s2, final Map.Entry<SSLConsumer, ProtocolVersion[]>[] array, final Map.Entry<HandshakeProducer, ProtocolVersion[]>[] array2) {
        this(b, s2, array, array2, new Map.Entry[0]);
    }
    
    private SSLHandshake(final byte id, final String name, final Map.Entry<SSLConsumer, ProtocolVersion[]>[] handshakeConsumers, final Map.Entry<HandshakeProducer, ProtocolVersion[]>[] handshakeProducers, final Map.Entry<HandshakeAbsence, ProtocolVersion[]>[] handshakeAbsences) {
        this.id = id;
        this.name = name;
        this.handshakeConsumers = handshakeConsumers;
        this.handshakeProducers = handshakeProducers;
        this.handshakeAbsences = handshakeAbsences;
    }
    
    @Override
    public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
        final SSLConsumer handshakeConsumer = this.getHandshakeConsumer(connectionContext);
        if (handshakeConsumer != null) {
            handshakeConsumer.consume(connectionContext, byteBuffer);
            return;
        }
        throw new UnsupportedOperationException("Unsupported handshake consumer: " + this.name);
    }
    
    private SSLConsumer getHandshakeConsumer(final ConnectionContext connectionContext) {
        if (this.handshakeConsumers.length == 0) {
            return null;
        }
        final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
        ProtocolVersion protocolVersion;
        if (handshakeContext.negotiatedProtocol == null || handshakeContext.negotiatedProtocol == ProtocolVersion.NONE) {
            if (handshakeContext.conContext.isNegotiated && handshakeContext.conContext.protocolVersion != ProtocolVersion.NONE) {
                protocolVersion = handshakeContext.conContext.protocolVersion;
            }
            else {
                protocolVersion = handshakeContext.maximumActiveProtocol;
            }
        }
        else {
            protocolVersion = handshakeContext.negotiatedProtocol;
        }
        for (final Map.Entry<SSLConsumer, ProtocolVersion[]> entry : this.handshakeConsumers) {
            final ProtocolVersion[] array = entry.getValue();
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                if (protocolVersion == array[j]) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    @Override
    public byte[] produce(final ConnectionContext connectionContext, final HandshakeMessage handshakeMessage) throws IOException {
        final HandshakeProducer handshakeProducer = this.getHandshakeProducer(connectionContext);
        if (handshakeProducer != null) {
            return handshakeProducer.produce(connectionContext, handshakeMessage);
        }
        throw new UnsupportedOperationException("Unsupported handshake producer: " + this.name);
    }
    
    private HandshakeProducer getHandshakeProducer(final ConnectionContext connectionContext) {
        if (this.handshakeConsumers.length == 0) {
            return null;
        }
        final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
        ProtocolVersion protocolVersion;
        if (handshakeContext.negotiatedProtocol == null || handshakeContext.negotiatedProtocol == ProtocolVersion.NONE) {
            if (handshakeContext.conContext.isNegotiated && handshakeContext.conContext.protocolVersion != ProtocolVersion.NONE) {
                protocolVersion = handshakeContext.conContext.protocolVersion;
            }
            else {
                protocolVersion = handshakeContext.maximumActiveProtocol;
            }
        }
        else {
            protocolVersion = handshakeContext.negotiatedProtocol;
        }
        for (final Map.Entry<HandshakeProducer, ProtocolVersion[]> entry : this.handshakeProducers) {
            final ProtocolVersion[] array = entry.getValue();
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                if (protocolVersion == array[j]) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static String nameOf(final byte b) {
        for (final SSLHandshake sslHandshake : values()) {
            if (sslHandshake.id == b) {
                return sslHandshake.name;
            }
        }
        return "UNKNOWN-HANDSHAKE-MESSAGE(" + b + ")";
    }
    
    static boolean isKnown(final byte b) {
        final SSLHandshake[] values = values();
        for (int length = values.length, i = 0; i < length; ++i) {
            if (values[i].id == b && b != SSLHandshake.NOT_APPLICABLE.id) {
                return true;
            }
        }
        return false;
    }
    
    static final void kickstart(final HandshakeContext handshakeContext) throws IOException {
        if (handshakeContext instanceof ClientHandshakeContext) {
            if (handshakeContext.conContext.isNegotiated && handshakeContext.conContext.protocolVersion.useTLS13PlusSpec()) {
                KeyUpdate.kickstartProducer.produce(handshakeContext);
            }
            else {
                ClientHello.kickstartProducer.produce(handshakeContext);
            }
        }
        else if (handshakeContext.conContext.protocolVersion.useTLS13PlusSpec()) {
            KeyUpdate.kickstartProducer.produce(handshakeContext);
        }
        else {
            HelloRequest.kickstartProducer.produce(handshakeContext);
        }
    }
    
    abstract static class HandshakeMessage
    {
        final HandshakeContext handshakeContext;
        
        HandshakeMessage(final HandshakeContext handshakeContext) {
            this.handshakeContext = handshakeContext;
        }
        
        abstract SSLHandshake handshakeType();
        
        abstract int messageLength();
        
        abstract void send(final HandshakeOutStream p0) throws IOException;
        
        void write(final HandshakeOutStream handshakeOutStream) throws IOException {
            final int messageLength = this.messageLength();
            if (messageLength >= 16777216) {
                throw new SSLException("Handshake message is overflow, type = " + this.handshakeType() + ", len = " + messageLength);
            }
            handshakeOutStream.write(this.handshakeType().id);
            handshakeOutStream.putInt24(messageLength);
            this.send(handshakeOutStream);
            handshakeOutStream.complete();
        }
    }
}
