package io.netty.channel.nio;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelConfig;
import java.util.ArrayList;
import io.netty.channel.AbstractChannel;
import java.util.List;
import io.netty.channel.ServerChannel;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.SelectionKey;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.RecvByteBufAllocator;
import java.nio.channels.SelectableChannel;
import io.netty.channel.Channel;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel
{
    boolean inputShutdown;
    
    protected AbstractNioMessageChannel(final Channel parent, final SelectableChannel ch, final int readInterestOp) {
        super(parent, ch, readInterestOp);
    }
    
    @Override
    protected AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe();
    }
    
    @Override
    protected void doBeginRead() throws Exception {
        if (this.inputShutdown) {
            return;
        }
        super.doBeginRead();
    }
    
    protected boolean continueReading(final RecvByteBufAllocator.Handle allocHandle) {
        return allocHandle.continueReading();
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        final SelectionKey key = this.selectionKey();
        final int interestOps = key.interestOps();
        int maxMessagesPerWrite = this.maxMessagesPerWrite();
        while (maxMessagesPerWrite > 0) {
            final Object msg = in.current();
            if (msg == null) {
                break;
            }
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                    if (this.doWriteMessage(msg, in)) {
                        done = true;
                        break;
                    }
                }
                if (!done) {
                    break;
                }
                --maxMessagesPerWrite;
                in.remove();
            }
            catch (final Exception e) {
                if (!this.continueOnWriteError()) {
                    throw e;
                }
                --maxMessagesPerWrite;
                in.remove(e);
            }
        }
        if (in.isEmpty()) {
            if ((interestOps & 0x4) != 0x0) {
                key.interestOps(interestOps & 0xFFFFFFFB);
            }
        }
        else if ((interestOps & 0x4) == 0x0) {
            key.interestOps(interestOps | 0x4);
        }
    }
    
    protected boolean continueOnWriteError() {
        return false;
    }
    
    protected boolean closeOnReadError(final Throwable cause) {
        return !this.isActive() || (!(cause instanceof PortUnreachableException) && (!(cause instanceof IOException) || !(this instanceof ServerChannel)));
    }
    
    protected abstract int doReadMessages(final List<Object> p0) throws Exception;
    
    protected abstract boolean doWriteMessage(final Object p0, final ChannelOutboundBuffer p1) throws Exception;
    
    private final class NioMessageUnsafe extends AbstractNioUnsafe
    {
        private final List<Object> readBuf;
        
        private NioMessageUnsafe() {
            this.readBuf = new ArrayList<Object>();
        }
        
        @Override
        public void read() {
            assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();
            final ChannelConfig config = AbstractNioMessageChannel.this.config();
            final ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            final RecvByteBufAllocator.Handle allocHandle = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
            allocHandle.reset(config);
            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    do {
                        final int localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
                        if (localRead == 0) {
                            break;
                        }
                        if (localRead < 0) {
                            closed = true;
                            break;
                        }
                        allocHandle.incMessagesRead(localRead);
                    } while (AbstractNioMessageChannel.this.continueReading(allocHandle));
                }
                catch (final Throwable t) {
                    exception = t;
                }
                for (int size = this.readBuf.size(), i = 0; i < size; ++i) {
                    AbstractNioMessageChannel.this.readPending = false;
                    pipeline.fireChannelRead(this.readBuf.get(i));
                }
                this.readBuf.clear();
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    closed = AbstractNioMessageChannel.this.closeOnReadError(exception);
                    pipeline.fireExceptionCaught(exception);
                }
                if (closed) {
                    AbstractNioMessageChannel.this.inputShutdown = true;
                    if (AbstractNioMessageChannel.this.isOpen()) {
                        this.close(this.voidPromise());
                    }
                }
            }
            finally {
                if (!AbstractNioMessageChannel.this.readPending && !config.isAutoRead()) {
                    this.removeReadOp();
                }
            }
        }
    }
}
