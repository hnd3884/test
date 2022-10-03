package sun.security.ssl;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;

final class PostHandshakeContext extends HandshakeContext
{
    PostHandshakeContext(final TransportContext transportContext) throws IOException {
        super(transportContext);
        if (!this.negotiatedProtocol.useTLS13PlusSpec()) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Post-handshake not supported in " + this.negotiatedProtocol.name);
        }
        this.localSupportedSignAlgs = new ArrayList<SignatureScheme>(transportContext.conSession.getLocalSupportedSignatureSchemes());
        if (transportContext.sslConfig.isClientMode) {
            this.handshakeConsumers.putIfAbsent(SSLHandshake.KEY_UPDATE.id, SSLHandshake.KEY_UPDATE);
            this.handshakeConsumers.putIfAbsent(SSLHandshake.NEW_SESSION_TICKET.id, SSLHandshake.NEW_SESSION_TICKET);
        }
        else {
            this.handshakeConsumers.putIfAbsent(SSLHandshake.KEY_UPDATE.id, SSLHandshake.KEY_UPDATE);
        }
        this.handshakeFinished = true;
    }
    
    @Override
    void kickstart() throws IOException {
        SSLHandshake.kickstart(this);
    }
    
    @Override
    void dispatch(final byte b, final ByteBuffer byteBuffer) throws IOException {
        final SSLConsumer sslConsumer = this.handshakeConsumers.get(b);
        if (sslConsumer == null) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected post-handshake message: " + SSLHandshake.nameOf(b));
        }
        try {
            sslConsumer.consume(this, byteBuffer);
        }
        catch (final UnsupportedOperationException ex) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported post-handshake message: " + SSLHandshake.nameOf(b), ex);
        }
        catch (final BufferUnderflowException | BufferOverflowException ex2) {
            throw this.conContext.fatal(Alert.DECODE_ERROR, "Illegal handshake message: " + SSLHandshake.nameOf(b), (Throwable)ex2);
        }
    }
    
    static boolean isConsumable(final TransportContext transportContext, final byte b) {
        if (b == SSLHandshake.KEY_UPDATE.id) {
            return transportContext.protocolVersion.useTLS13PlusSpec();
        }
        return b == SSLHandshake.NEW_SESSION_TICKET.id && transportContext.sslConfig.isClientMode;
    }
}
