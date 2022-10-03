package sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;

final class HelloRequest
{
    static final SSLProducer kickstartProducer;
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    
    static {
        kickstartProducer = new HelloRequestKickstartProducer();
        handshakeConsumer = new HelloRequestConsumer();
        handshakeProducer = new HelloRequestProducer();
    }
    
    static final class HelloRequestMessage extends SSLHandshake.HandshakeMessage
    {
        HelloRequestMessage(final HandshakeContext handshakeContext) {
            super(handshakeContext);
        }
        
        HelloRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.hasRemaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing HelloRequest message: not empty");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.HELLO_REQUEST;
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
    
    private static final class HelloRequestKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final HelloRequestMessage helloRequestMessage = new HelloRequestMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloRequest handshake message", helloRequestMessage);
            }
            helloRequestMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class HelloRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final HelloRequestMessage helloRequestMessage = new HelloRequestMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloRequest handshake message", helloRequestMessage);
            }
            helloRequestMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class HelloRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final HelloRequestMessage helloRequestMessage = new HelloRequestMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming HelloRequest handshake message", helloRequestMessage);
            }
            if (!clientHandshakeContext.kickstartMessageDelivered) {
                if (!clientHandshakeContext.conContext.secureRenegotiation && !HandshakeContext.allowUnsafeRenegotiation) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (!clientHandshakeContext.conContext.secureRenegotiation && SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Continue with insecure renegotiation", new Object[0]);
                }
                clientHandshakeContext.handshakeProducers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
                SSLHandshake.CLIENT_HELLO.produce(connectionContext, helloRequestMessage);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Ingore HelloRequest, handshaking is in progress", new Object[0]);
            }
        }
    }
}
