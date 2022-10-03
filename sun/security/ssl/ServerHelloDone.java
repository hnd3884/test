package sun.security.ssl;

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
        
        ServerHelloDoneMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.hasRemaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing ServerHelloDone message: not empty");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.SERVER_HELLO_DONE;
        }
        
        public int messageLength() {
            return 0;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
        }
        
        @Override
        public String toString() {
            return "<empty>";
        }
    }
    
    private static final class ServerHelloDoneProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ServerHelloDoneMessage serverHelloDoneMessage = new ServerHelloDoneMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ServerHelloDone handshake message", serverHelloDoneMessage);
            }
            serverHelloDoneMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CLIENT_KEY_EXCHANGE.id, SSLHandshake.CLIENT_KEY_EXCHANGE);
            serverHandshakeContext.conContext.consumers.put(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            return null;
        }
    }
    
    private static final class ServerHelloDoneConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id) != null) {
                CertificateStatus.handshakeAbsence.absent(connectionContext, null);
            }
            clientHandshakeContext.handshakeConsumers.clear();
            final ServerHelloDoneMessage serverHelloDoneMessage = new ServerHelloDoneMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ServerHelloDone handshake message", serverHelloDoneMessage);
            }
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CLIENT_KEY_EXCHANGE.id, SSLHandshake.CLIENT_KEY_EXCHANGE);
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            final SSLHandshake[] array = { SSLHandshake.CERTIFICATE, SSLHandshake.CLIENT_KEY_EXCHANGE, SSLHandshake.CERTIFICATE_VERIFY, SSLHandshake.FINISHED };
            for (int length = array.length, i = 0; i < length; ++i) {
                final HandshakeProducer handshakeProducer = clientHandshakeContext.handshakeProducers.remove(array[i].id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(connectionContext, null);
                }
            }
        }
    }
}
