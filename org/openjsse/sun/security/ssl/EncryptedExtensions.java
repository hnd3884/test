package org.openjsse.sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.io.IOException;

final class EncryptedExtensions
{
    static final HandshakeProducer handshakeProducer;
    static final SSLConsumer handshakeConsumer;
    
    static {
        handshakeProducer = new EncryptedExtensionsProducer();
        handshakeConsumer = new EncryptedExtensionsConsumer();
    }
    
    static final class EncryptedExtensionsMessage extends SSLHandshake.HandshakeMessage
    {
        private final SSLExtensions extensions;
        
        EncryptedExtensionsMessage(final HandshakeContext handshakeContext) throws IOException {
            super(handshakeContext);
            this.extensions = new SSLExtensions(this);
        }
        
        EncryptedExtensionsMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid EncryptedExtensions handshake message: no sufficient data");
            }
            final SSLExtension[] encryptedExtensions = handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.ENCRYPTED_EXTENSIONS);
            this.extensions = new SSLExtensions(this, m, encryptedExtensions);
        }
        
        @Override
        SSLHandshake handshakeType() {
            return SSLHandshake.ENCRYPTED_EXTENSIONS;
        }
        
        @Override
        int messageLength() {
            int extLen = this.extensions.length();
            if (extLen == 0) {
                extLen = 2;
            }
            return extLen;
        }
        
        @Override
        void send(final HandshakeOutStream hos) throws IOException {
            if (this.extensions.length() == 0) {
                hos.putInt16(0);
            }
            else {
                this.extensions.send(hos);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"EncryptedExtensions\": [\n{0}\n]", Locale.ENGLISH);
            final Object[] messageFields = { Utilities.indent(this.extensions.toString()) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class EncryptedExtensionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final EncryptedExtensionsMessage eem = new EncryptedExtensionsMessage(shc);
            final SSLExtension[] extTypes = shc.sslConfig.getEnabledExtensions(SSLHandshake.ENCRYPTED_EXTENSIONS, shc.negotiatedProtocol);
            eem.extensions.produce(shc, extTypes);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced EncryptedExtensions message", eem);
            }
            eem.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class EncryptedExtensionsConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.ENCRYPTED_EXTENSIONS.id);
            final EncryptedExtensionsMessage eem = new EncryptedExtensionsMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming EncryptedExtensions handshake message", eem);
            }
            final SSLExtension[] extTypes = chc.sslConfig.getEnabledExtensions(SSLHandshake.ENCRYPTED_EXTENSIONS);
            eem.extensions.consumeOnLoad(chc, extTypes);
            eem.extensions.consumeOnTrade(chc, extTypes);
        }
    }
}
