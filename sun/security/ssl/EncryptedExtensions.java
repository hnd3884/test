package sun.security.ssl;

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
        
        EncryptedExtensionsMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid EncryptedExtensions handshake message: no sufficient data");
            }
            this.extensions = new SSLExtensions(this, byteBuffer, handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.ENCRYPTED_EXTENSIONS));
        }
        
        @Override
        SSLHandshake handshakeType() {
            return SSLHandshake.ENCRYPTED_EXTENSIONS;
        }
        
        @Override
        int messageLength() {
            int length = this.extensions.length();
            if (length == 0) {
                length = 2;
            }
            return length;
        }
        
        @Override
        void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            if (this.extensions.length() == 0) {
                handshakeOutStream.putInt16(0);
            }
            else {
                this.extensions.send(handshakeOutStream);
            }
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"EncryptedExtensions\": [\n{0}\n]", Locale.ENGLISH).format(new Object[] { Utilities.indent(this.extensions.toString()) });
        }
    }
    
    private static final class EncryptedExtensionsProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final EncryptedExtensionsMessage encryptedExtensionsMessage = new EncryptedExtensionsMessage(serverHandshakeContext);
            encryptedExtensionsMessage.extensions.produce(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.ENCRYPTED_EXTENSIONS, serverHandshakeContext.negotiatedProtocol));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced EncryptedExtensions message", encryptedExtensionsMessage);
            }
            encryptedExtensionsMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class EncryptedExtensionsConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.ENCRYPTED_EXTENSIONS.id);
            final EncryptedExtensionsMessage encryptedExtensionsMessage = new EncryptedExtensionsMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming EncryptedExtensions handshake message", encryptedExtensionsMessage);
            }
            final SSLExtension[] enabledExtensions = clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.ENCRYPTED_EXTENSIONS);
            encryptedExtensionsMessage.extensions.consumeOnLoad(clientHandshakeContext, enabledExtensions);
            encryptedExtensionsMessage.extensions.consumeOnTrade(clientHandshakeContext, enabledExtensions);
        }
    }
}
