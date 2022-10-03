package org.openjsse.sun.security.ssl;

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
        
        HelloRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.hasRemaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing HelloRequest message: not empty");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.HELLO_REQUEST;
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
    
    private static final class HelloRequestKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final HelloRequestMessage hrm = new HelloRequestMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloRequest handshake message", hrm);
            }
            hrm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class HelloRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final HelloRequestMessage hrm = new HelloRequestMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloRequest handshake message", hrm);
            }
            hrm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class HelloRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final HelloRequestMessage hrm = new HelloRequestMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming HelloRequest handshake message", hrm);
            }
            if (!chc.kickstartMessageDelivered) {
                if (!chc.conContext.secureRenegotiation && !HandshakeContext.allowUnsafeRenegotiation) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsafe renegotiation is not allowed");
                }
                if (!chc.conContext.secureRenegotiation && SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Continue with insecure renegotiation", new Object[0]);
                }
                chc.handshakeProducers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
                SSLHandshake.CLIENT_HELLO.produce(context, hrm);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Ingore HelloRequest, handshaking is in progress", new Object[0]);
            }
        }
    }
}
