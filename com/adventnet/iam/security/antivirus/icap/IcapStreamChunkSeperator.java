package com.adventnet.iam.security.antivirus.icap;

import org.jboss.netty.channel.DownstreamMessageEvent;
import ch.mimo.netty.handler.codec.icap.IcapChunk;
import org.jboss.netty.handler.stream.ChunkedInput;
import ch.mimo.netty.handler.codec.icap.DefaultIcapChunk;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.stream.ChunkedStream;
import java.io.InputStream;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelDownstreamHandler;

public class IcapStreamChunkSeperator implements ChannelDownstreamHandler
{
    private static final int DEFAULT_CHUNK_SIZE = 8192;
    private final int chunkSize;
    
    public IcapStreamChunkSeperator() {
        this.chunkSize = 8192;
    }
    
    public IcapStreamChunkSeperator(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            final MessageEvent msgEvent = (MessageEvent)e;
            final Object msg = msgEvent.getMessage();
            if (msg instanceof InputStream) {
                final ChunkedInput chunkedFile = (ChunkedInput)new ChunkedStream((InputStream)msg, this.chunkSize);
                while (chunkedFile.hasNextChunk()) {
                    final ChannelBuffer chunkBuffer = (ChannelBuffer)chunkedFile.nextChunk();
                    final IcapChunk chunk = (IcapChunk)new DefaultIcapChunk(chunkBuffer);
                    this.fireDownstreamEvent(ctx, chunk, msgEvent);
                }
            }
            else {
                ctx.sendDownstream(e);
            }
        }
        else {
            ctx.sendDownstream(e);
        }
    }
    
    private void fireDownstreamEvent(final ChannelHandlerContext ctx, final Object message, final MessageEvent messageEvent) {
        final DownstreamMessageEvent downstreamMessageEvent = new DownstreamMessageEvent(ctx.getChannel(), messageEvent.getFuture(), message, messageEvent.getRemoteAddress());
        ctx.sendDownstream((ChannelEvent)downstreamMessageEvent);
    }
}
