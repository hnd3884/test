package io.netty.handler.ssl;

import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandlerContext;
import java.util.Locale;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;

public abstract class AbstractSniHandler<T> extends SslClientHelloHandler<T>
{
    private String hostname;
    
    private static String extractSniHostname(final ByteBuf in) {
        int offset = in.readerIndex();
        final int endOffset = in.writerIndex();
        offset += 34;
        if (endOffset - offset >= 6) {
            final int sessionIdLength = in.getUnsignedByte(offset);
            offset += sessionIdLength + 1;
            final int cipherSuitesLength = in.getUnsignedShort(offset);
            offset += cipherSuitesLength + 2;
            final int compressionMethodLength = in.getUnsignedByte(offset);
            offset += compressionMethodLength + 1;
            final int extensionsLength = in.getUnsignedShort(offset);
            offset += 2;
            final int extensionsLimit = offset + extensionsLength;
            if (extensionsLimit <= endOffset) {
                while (extensionsLimit - offset >= 4) {
                    final int extensionType = in.getUnsignedShort(offset);
                    offset += 2;
                    final int extensionLength = in.getUnsignedShort(offset);
                    offset += 2;
                    if (extensionsLimit - offset < extensionLength) {
                        break;
                    }
                    if (extensionType == 0) {
                        offset += 2;
                        if (extensionsLimit - offset < 3) {
                            break;
                        }
                        final int serverNameType = in.getUnsignedByte(offset);
                        ++offset;
                        if (serverNameType != 0) {
                            break;
                        }
                        final int serverNameLength = in.getUnsignedShort(offset);
                        offset += 2;
                        if (extensionsLimit - offset < serverNameLength) {
                            break;
                        }
                        final String hostname = in.toString(offset, serverNameLength, CharsetUtil.US_ASCII);
                        return hostname.toLowerCase(Locale.US);
                    }
                    else {
                        offset += extensionLength;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    protected Future<T> lookup(final ChannelHandlerContext ctx, final ByteBuf clientHello) throws Exception {
        this.hostname = ((clientHello == null) ? null : extractSniHostname(clientHello));
        return this.lookup(ctx, this.hostname);
    }
    
    @Override
    protected void onLookupComplete(final ChannelHandlerContext ctx, final Future<T> future) throws Exception {
        try {
            this.onLookupComplete(ctx, this.hostname, future);
        }
        finally {
            fireSniCompletionEvent(ctx, this.hostname, future);
        }
    }
    
    protected abstract Future<T> lookup(final ChannelHandlerContext p0, final String p1) throws Exception;
    
    protected abstract void onLookupComplete(final ChannelHandlerContext p0, final String p1, final Future<T> p2) throws Exception;
    
    private static void fireSniCompletionEvent(final ChannelHandlerContext ctx, final String hostname, final Future<?> future) {
        final Throwable cause = future.cause();
        if (cause == null) {
            ctx.fireUserEventTriggered((Object)new SniCompletionEvent(hostname));
        }
        else {
            ctx.fireUserEventTriggered((Object)new SniCompletionEvent(hostname, cause));
        }
    }
}
