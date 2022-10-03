package org.openjsse.sun.security.ssl;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;

final class PostHandshakeContext extends HandshakeContext
{
    PostHandshakeContext(final TransportContext context) throws IOException {
        super(context);
        if (!this.negotiatedProtocol.useTLS13PlusSpec()) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Post-handshake not supported in " + this.negotiatedProtocol.name);
        }
        this.localSupportedSignAlgs = new ArrayList<SignatureScheme>(context.conSession.getLocalSupportedSignatureSchemes());
        this.handshakeConsumers = new LinkedHashMap<Byte, SSLConsumer>();
        if (context.sslConfig.isClientMode) {
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
    void dispatch(final byte handshakeType, final ByteBuffer fragment) throws IOException {
        final SSLConsumer consumer = this.handshakeConsumers.get(handshakeType);
        if (consumer == null) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected post-handshake message: " + SSLHandshake.nameOf(handshakeType));
        }
        try {
            consumer.consume(this, fragment);
        }
        catch (final UnsupportedOperationException unsoe) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported post-handshake message: " + SSLHandshake.nameOf(handshakeType), unsoe);
        }
        catch (final BufferUnderflowException | BufferOverflowException be) {
            throw this.conContext.fatal(Alert.DECODE_ERROR, "Illegal handshake message: " + SSLHandshake.nameOf(handshakeType), be);
        }
    }
    
    static boolean isConsumable(final TransportContext context, final byte handshakeType) {
        if (handshakeType == SSLHandshake.KEY_UPDATE.id) {
            return context.protocolVersion.useTLS13PlusSpec();
        }
        return handshakeType == SSLHandshake.NEW_SESSION_TICKET.id && context.sslConfig.isClientMode;
    }
}
