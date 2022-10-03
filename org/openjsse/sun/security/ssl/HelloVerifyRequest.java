package org.openjsse.sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.io.IOException;

final class HelloVerifyRequest
{
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    
    static {
        handshakeConsumer = new HelloVerifyRequestConsumer();
        handshakeProducer = new HelloVerifyRequestProducer();
    }
    
    static final class HelloVerifyRequestMessage extends SSLHandshake.HandshakeMessage
    {
        final int serverVersion;
        final byte[] cookie;
        
        HelloVerifyRequestMessage(final HandshakeContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            super(context);
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final HelloCookieManager hcMgr = shc.sslContext.getHelloCookieManager(ProtocolVersion.DTLS10);
            this.serverVersion = shc.clientHelloVersion;
            this.cookie = hcMgr.createCookie(shc, clientHello);
        }
        
        HelloVerifyRequestMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (m.remaining() < 3) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid HelloVerifyRequest: no sufficient data");
            }
            final byte major = m.get();
            final byte minor = m.get();
            this.serverVersion = ((major & 0xFF) << 8 | (minor & 0xFF));
            this.cookie = Record.getBytes8(m);
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.HELLO_VERIFY_REQUEST;
        }
        
        public int messageLength() {
            return 3 + this.cookie.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putInt8((byte)(this.serverVersion >>> 8 & 0xFF));
            hos.putInt8((byte)(this.serverVersion & 0xFF));
            hos.putBytes8(this.cookie);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"HelloVerifyRequest\": '{'\n  \"server version\"      : \"{0}\",\n  \"cookie\"              : \"{1}\",\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { ProtocolVersion.nameOf(this.serverVersion), Utilities.toHexString(this.cookie) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class HelloVerifyRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            shc.handshakeProducers.remove(SSLHandshake.HELLO_VERIFY_REQUEST.id);
            final HelloVerifyRequestMessage hvrm = new HelloVerifyRequestMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloVerifyRequest handshake message", hvrm);
            }
            hvrm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeHash.finish();
            shc.handshakeExtensions.clear();
            shc.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class HelloVerifyRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.HELLO_VERIFY_REQUEST.id);
            if (!chc.handshakeConsumers.isEmpty()) {
                chc.handshakeConsumers.remove(SSLHandshake.SERVER_HELLO.id);
            }
            if (!chc.handshakeConsumers.isEmpty()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No more message expected before HelloVerifyRequest is processed");
            }
            chc.handshakeHash.finish();
            final HelloVerifyRequestMessage hvrm = new HelloVerifyRequestMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming HelloVerifyRequest handshake message", hvrm);
            }
            chc.initialClientHelloMsg.setHelloCookie(hvrm.cookie);
            SSLHandshake.CLIENT_HELLO.produce(context, hvrm);
        }
    }
}
