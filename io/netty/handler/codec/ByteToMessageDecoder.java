package io.netty.handler.codec;

import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.StringUtil;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter
{
    public static final Cumulator MERGE_CUMULATOR;
    public static final Cumulator COMPOSITE_CUMULATOR;
    private static final byte STATE_INIT = 0;
    private static final byte STATE_CALLING_CHILD_DECODE = 1;
    private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
    ByteBuf cumulation;
    private Cumulator cumulator;
    private boolean singleDecode;
    private boolean first;
    private boolean firedChannelRead;
    private byte decodeState;
    private int discardAfterReads;
    private int numReads;
    
    protected ByteToMessageDecoder() {
        this.cumulator = ByteToMessageDecoder.MERGE_CUMULATOR;
        this.decodeState = 0;
        this.discardAfterReads = 16;
        this.ensureNotSharable();
    }
    
    public void setSingleDecode(final boolean singleDecode) {
        this.singleDecode = singleDecode;
    }
    
    public boolean isSingleDecode() {
        return this.singleDecode;
    }
    
    public void setCumulator(final Cumulator cumulator) {
        this.cumulator = ObjectUtil.checkNotNull(cumulator, "cumulator");
    }
    
    public void setDiscardAfterReads(final int discardAfterReads) {
        ObjectUtil.checkPositive(discardAfterReads, "discardAfterReads");
        this.discardAfterReads = discardAfterReads;
    }
    
    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }
    
    protected ByteBuf internalBuffer() {
        if (this.cumulation != null) {
            return this.cumulation;
        }
        return Unpooled.EMPTY_BUFFER;
    }
    
    @Override
    public final void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        if (this.decodeState == 1) {
            this.decodeState = 2;
            return;
        }
        final ByteBuf buf = this.cumulation;
        if (buf != null) {
            this.cumulation = null;
            this.numReads = 0;
            final int readable = buf.readableBytes();
            if (readable > 0) {
                ctx.fireChannelRead((Object)buf);
                ctx.fireChannelReadComplete();
            }
            else {
                buf.release();
            }
        }
        this.handlerRemoved0(ctx);
    }
    
    protected void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            final CodecOutputList out = CodecOutputList.newInstance();
            try {
                this.first = (this.cumulation == null);
                this.callDecode(ctx, this.cumulation = this.cumulator.cumulate(ctx.alloc(), this.first ? Unpooled.EMPTY_BUFFER : this.cumulation, (ByteBuf)msg), out);
            }
            catch (final DecoderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new DecoderException(e2);
            }
            finally {
                try {
                    if (this.cumulation != null && !this.cumulation.isReadable()) {
                        this.numReads = 0;
                        this.cumulation.release();
                        this.cumulation = null;
                    }
                    else if (++this.numReads >= this.discardAfterReads) {
                        this.numReads = 0;
                        this.discardSomeReadBytes();
                    }
                    final int size = out.size();
                    this.firedChannelRead |= out.insertSinceRecycled();
                    fireChannelRead(ctx, out, size);
                }
                finally {
                    out.recycle();
                }
            }
        }
        else {
            ctx.fireChannelRead(msg);
        }
    }
    
    static void fireChannelRead(final ChannelHandlerContext ctx, final List<Object> msgs, final int numElements) {
        if (msgs instanceof CodecOutputList) {
            fireChannelRead(ctx, (CodecOutputList)msgs, numElements);
        }
        else {
            for (int i = 0; i < numElements; ++i) {
                ctx.fireChannelRead(msgs.get(i));
            }
        }
    }
    
    static void fireChannelRead(final ChannelHandlerContext ctx, final CodecOutputList msgs, final int numElements) {
        for (int i = 0; i < numElements; ++i) {
            ctx.fireChannelRead(msgs.getUnsafe(i));
        }
    }
    
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        this.numReads = 0;
        this.discardSomeReadBytes();
        if (!this.firedChannelRead && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        this.firedChannelRead = false;
        ctx.fireChannelReadComplete();
    }
    
    protected final void discardSomeReadBytes() {
        if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
            this.cumulation.discardSomeReadBytes();
        }
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.channelInputClosed(ctx, true);
    }
    
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof ChannelInputShutdownEvent) {
            this.channelInputClosed(ctx, false);
        }
        super.userEventTriggered(ctx, evt);
    }
    
    private void channelInputClosed(final ChannelHandlerContext ctx, final boolean callChannelInactive) {
        final CodecOutputList out = CodecOutputList.newInstance();
        try {
            this.channelInputClosed(ctx, out);
        }
        catch (final DecoderException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new DecoderException(e2);
        }
        finally {
            try {
                if (this.cumulation != null) {
                    this.cumulation.release();
                    this.cumulation = null;
                }
                final int size = out.size();
                fireChannelRead(ctx, out, size);
                if (size > 0) {
                    ctx.fireChannelReadComplete();
                }
                if (callChannelInactive) {
                    ctx.fireChannelInactive();
                }
            }
            finally {
                out.recycle();
            }
        }
    }
    
    void channelInputClosed(final ChannelHandlerContext ctx, final List<Object> out) throws Exception {
        if (this.cumulation != null) {
            this.callDecode(ctx, this.cumulation, out);
            if (!ctx.isRemoved()) {
                final ByteBuf buffer = (this.cumulation == null) ? Unpooled.EMPTY_BUFFER : this.cumulation;
                this.decodeLast(ctx, buffer, out);
            }
        }
        else {
            this.decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
        }
    }
    
    protected void callDecode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        try {
            while (in.isReadable()) {
                final int outSize = out.size();
                if (outSize > 0) {
                    fireChannelRead(ctx, out, outSize);
                    out.clear();
                    if (ctx.isRemoved()) {
                        break;
                    }
                }
                final int oldInputLength = in.readableBytes();
                this.decodeRemovalReentryProtection(ctx, in, out);
                if (ctx.isRemoved()) {
                    break;
                }
                if (out.isEmpty()) {
                    if (oldInputLength == in.readableBytes()) {
                        break;
                    }
                    continue;
                }
                else {
                    if (oldInputLength == in.readableBytes()) {
                        throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                    }
                    if (this.isSingleDecode()) {
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final DecoderException e) {
            throw e;
        }
        catch (final Exception cause) {
            throw new DecoderException(cause);
        }
    }
    
    protected abstract void decode(final ChannelHandlerContext p0, final ByteBuf p1, final List<Object> p2) throws Exception;
    
    final void decodeRemovalReentryProtection(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        this.decodeState = 1;
        try {
            this.decode(ctx, in, out);
        }
        finally {
            final boolean removePending = this.decodeState == 2;
            this.decodeState = 0;
            if (removePending) {
                fireChannelRead(ctx, out, out.size());
                out.clear();
                this.handlerRemoved(ctx);
            }
        }
    }
    
    protected void decodeLast(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (in.isReadable()) {
            this.decodeRemovalReentryProtection(ctx, in, out);
        }
    }
    
    static ByteBuf expandCumulation(final ByteBufAllocator alloc, final ByteBuf oldCumulation, final ByteBuf in) {
        final int oldBytes = oldCumulation.readableBytes();
        final int newBytes = in.readableBytes();
        final int totalBytes = oldBytes + newBytes;
        ByteBuf toRelease;
        final ByteBuf newCumulation = toRelease = alloc.buffer(alloc.calculateNewCapacity(totalBytes, Integer.MAX_VALUE));
        try {
            newCumulation.setBytes(0, oldCumulation, oldCumulation.readerIndex(), oldBytes).setBytes(oldBytes, in, in.readerIndex(), newBytes).writerIndex(totalBytes);
            in.readerIndex(in.writerIndex());
            toRelease = oldCumulation;
            return newCumulation;
        }
        finally {
            toRelease.release();
        }
    }
    
    static {
        MERGE_CUMULATOR = new Cumulator() {
            @Override
            public ByteBuf cumulate(final ByteBufAllocator alloc, final ByteBuf cumulation, final ByteBuf in) {
                if (!cumulation.isReadable() && in.isContiguous()) {
                    cumulation.release();
                    return in;
                }
                try {
                    final int required = in.readableBytes();
                    if (required > cumulation.maxWritableBytes() || (required > cumulation.maxFastWritableBytes() && cumulation.refCnt() > 1) || cumulation.isReadOnly()) {
                        return ByteToMessageDecoder.expandCumulation(alloc, cumulation, in);
                    }
                    cumulation.writeBytes(in, in.readerIndex(), required);
                    in.readerIndex(in.writerIndex());
                    return cumulation;
                }
                finally {
                    in.release();
                }
            }
        };
        COMPOSITE_CUMULATOR = new Cumulator() {
            @Override
            public ByteBuf cumulate(final ByteBufAllocator alloc, final ByteBuf cumulation, ByteBuf in) {
                if (!cumulation.isReadable()) {
                    cumulation.release();
                    return in;
                }
                CompositeByteBuf composite = null;
                try {
                    if (cumulation instanceof CompositeByteBuf && cumulation.refCnt() == 1) {
                        composite = (CompositeByteBuf)cumulation;
                        if (composite.writerIndex() != composite.capacity()) {
                            composite.capacity(composite.writerIndex());
                        }
                    }
                    else {
                        composite = alloc.compositeBuffer(Integer.MAX_VALUE).addFlattenedComponents(true, cumulation);
                    }
                    composite.addFlattenedComponents(true, in);
                    in = null;
                    return composite;
                }
                finally {
                    if (in != null) {
                        in.release();
                        if (composite != null && composite != cumulation) {
                            composite.release();
                        }
                    }
                }
            }
        };
    }
    
    public interface Cumulator
    {
        ByteBuf cumulate(final ByteBufAllocator p0, final ByteBuf p1, final ByteBuf p2);
    }
}
