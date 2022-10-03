package org.openjsse.sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;

final class ServerHelloDone
{
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    
    static {
        handshakeConsumer = new ServerHelloDoneConsumer();
        handshakeProducer = new ServerHelloDoneProducer();
    }
    
    static final class ServerHelloDoneMessage extends SSLHandshake.HandshakeMessage
    {
        ServerHelloDoneMessage(final HandshakeContext handshakeContext) {
            super(handshakeContext);
        }
        
        ServerHelloDoneMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.hasRemaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing ServerHelloDone message: not empty");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.SERVER_HELLO_DONE;
        }
        
        public int messageLength() {
            return 0;
        }
        
        public void send(final HandshakeOutStream s) throws IOException {
        }
        
        @Override
        public String toString() {
            return "<empty>";
        }
    }
    
    private static final class ServerHelloDoneProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ServerHelloDoneMessage shdm = new ServerHelloDoneMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ServerHelloDone handshake message", shdm);
            }
            shdm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeConsumers.put(SSLHandshake.CLIENT_KEY_EXCHANGE.id, SSLHandshake.CLIENT_KEY_EXCHANGE);
            shc.conContext.consumers.put(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
            shc.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            return null;
        }
    }
    
    private static final class ServerHelloDoneConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final SSLConsumer certStatCons = chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id);
            if (certStatCons != null) {
                CertificateStatus.handshakeAbsence.absent(context, null);
            }
            chc.handshakeConsumers.clear();
            final ServerHelloDoneMessage shdm = new ServerHelloDoneMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ServerHelloDone handshake message", shdm);
            }
            chc.handshakeProducers.put(SSLHandshake.CLIENT_KEY_EXCHANGE.id, SSLHandshake.CLIENT_KEY_EXCHANGE);
            chc.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            final SSLHandshake[] array;
            final SSLHandshake[] probableHandshakeMessages = array = new SSLHandshake[] { SSLHandshake.CERTIFICATE, SSLHandshake.CLIENT_KEY_EXCHANGE, SSLHandshake.CERTIFICATE_VERIFY, SSLHandshake.FINISHED };
            for (final SSLHandshake hs : array) {
                final HandshakeProducer handshakeProducer = chc.handshakeProducers.remove(hs.id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(context, null);
                }
            }
        }
    }
}
