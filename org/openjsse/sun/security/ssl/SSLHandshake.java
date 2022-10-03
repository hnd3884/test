package org.openjsse.sun.security.ssl;

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
    HELLO_VERIFY_REQUEST((byte)3, "hello_verify_request", (Map.Entry<SSLConsumer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(HelloVerifyRequest.handshakeConsumer, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<HandshakeProducer, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(HelloVerifyRequest.handshakeProducer, ProtocolVersion.PROTOCOLS_TO_12) }), 
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
    
    private SSLHandshake(final byte id, final String name) {
        this(id, name, new Map.Entry[0], new Map.Entry[0], new Map.Entry[0]);
    }
    
    private SSLHandshake(final byte id, final String name, final Map.Entry<SSLConsumer, ProtocolVersion[]>[] handshakeConsumers, final Map.Entry<HandshakeProducer, ProtocolVersion[]>[] handshakeProducers) {
        this(id, name, handshakeConsumers, handshakeProducers, new Map.Entry[0]);
    }
    
    private SSLHandshake(final byte id, final String name, final Map.Entry<SSLConsumer, ProtocolVersion[]>[] handshakeConsumers, final Map.Entry<HandshakeProducer, ProtocolVersion[]>[] handshakeProducers, final Map.Entry<HandshakeAbsence, ProtocolVersion[]>[] handshakeAbsence) {
        this.id = id;
        this.name = name;
        this.handshakeConsumers = handshakeConsumers;
        this.handshakeProducers = handshakeProducers;
        this.handshakeAbsences = handshakeAbsence;
    }
    
    @Override
    public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
        final SSLConsumer hc = this.getHandshakeConsumer(context);
        if (hc != null) {
            hc.consume(context, message);
            return;
        }
        throw new UnsupportedOperationException("Unsupported handshake consumer: " + this.name);
    }
    
    private SSLConsumer getHandshakeConsumer(final ConnectionContext context) {
        if (this.handshakeConsumers.length == 0) {
            return null;
        }
        final HandshakeContext hc = (HandshakeContext)context;
        ProtocolVersion protocolVersion;
        if (hc.negotiatedProtocol == null || hc.negotiatedProtocol == ProtocolVersion.NONE) {
            if (hc.conContext.isNegotiated && hc.conContext.protocolVersion != ProtocolVersion.NONE) {
                protocolVersion = hc.conContext.protocolVersion;
            }
            else {
                protocolVersion = hc.maximumActiveProtocol;
            }
        }
        else {
            protocolVersion = hc.negotiatedProtocol;
        }
        for (final Map.Entry<SSLConsumer, ProtocolVersion[]> phe : this.handshakeConsumers) {
            for (final ProtocolVersion pv : phe.getValue()) {
                if (protocolVersion == pv) {
                    return phe.getKey();
                }
            }
        }
        return null;
    }
    
    @Override
    public byte[] produce(final ConnectionContext context, final HandshakeMessage message) throws IOException {
        final HandshakeProducer hp = this.getHandshakeProducer(context);
        if (hp != null) {
            return hp.produce(context, message);
        }
        throw new UnsupportedOperationException("Unsupported handshake producer: " + this.name);
    }
    
    private HandshakeProducer getHandshakeProducer(final ConnectionContext context) {
        if (this.handshakeConsumers.length == 0) {
            return null;
        }
        final HandshakeContext hc = (HandshakeContext)context;
        ProtocolVersion protocolVersion;
        if (hc.negotiatedProtocol == null || hc.negotiatedProtocol == ProtocolVersion.NONE) {
            if (hc.conContext.isNegotiated && hc.conContext.protocolVersion != ProtocolVersion.NONE) {
                protocolVersion = hc.conContext.protocolVersion;
            }
            else {
                protocolVersion = hc.maximumActiveProtocol;
            }
        }
        else {
            protocolVersion = hc.negotiatedProtocol;
        }
        for (final Map.Entry<HandshakeProducer, ProtocolVersion[]> phe : this.handshakeProducers) {
            for (final ProtocolVersion pv : phe.getValue()) {
                if (protocolVersion == pv) {
                    return phe.getKey();
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static String nameOf(final byte id) {
        for (final SSLHandshake hs : values()) {
            if (hs.id == id) {
                return hs.name;
            }
        }
        return "UNKNOWN-HANDSHAKE-MESSAGE(" + id + ")";
    }
    
    static boolean isKnown(final byte id) {
        for (final SSLHandshake hs : values()) {
            if (hs.id == id && id != SSLHandshake.NOT_APPLICABLE.id) {
                return true;
            }
        }
        return false;
    }
    
    static final void kickstart(final HandshakeContext context) throws IOException {
        if (context instanceof ClientHandshakeContext) {
            if (context.conContext.isNegotiated && context.conContext.protocolVersion.useTLS13PlusSpec()) {
                KeyUpdate.kickstartProducer.produce(context);
            }
            else {
                ClientHello.kickstartProducer.produce(context);
            }
        }
        else if (context.conContext.protocolVersion.useTLS13PlusSpec()) {
            KeyUpdate.kickstartProducer.produce(context);
        }
        else {
            HelloRequest.kickstartProducer.produce(context);
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
        
        void write(final HandshakeOutStream hos) throws IOException {
            final int len = this.messageLength();
            if (len >= 16777216) {
                throw new SSLException("Handshake message is overflow, type = " + this.handshakeType() + ", len = " + len);
            }
            hos.write(this.handshakeType().id);
            hos.putInt24(len);
            this.send(hos);
            hos.complete();
        }
    }
}
