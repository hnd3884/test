package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.Unpooled;
import java.nio.ByteOrder;
import io.netty.buffer.ByteBufUtil;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.handler.codec.ByteToMessageDecoder;

public class WebSocket08FrameDecoder extends ByteToMessageDecoder implements WebSocketFrameDecoder
{
    private static final InternalLogger logger;
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private final WebSocketDecoderConfig config;
    private int fragmentedFramesCount;
    private boolean frameFinalFlag;
    private boolean frameMasked;
    private int frameRsv;
    private int frameOpcode;
    private long framePayloadLength;
    private byte[] maskingKey;
    private int framePayloadLen1;
    private boolean receivedClosingHandshake;
    private State state;
    
    public WebSocket08FrameDecoder(final boolean expectMaskedFrames, final boolean allowExtensions, final int maxFramePayloadLength) {
        this(expectMaskedFrames, allowExtensions, maxFramePayloadLength, false);
    }
    
    public WebSocket08FrameDecoder(final boolean expectMaskedFrames, final boolean allowExtensions, final int maxFramePayloadLength, final boolean allowMaskMismatch) {
        this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(expectMaskedFrames).allowExtensions(allowExtensions).maxFramePayloadLength(maxFramePayloadLength).allowMaskMismatch(allowMaskMismatch).build());
    }
    
    public WebSocket08FrameDecoder(final WebSocketDecoderConfig decoderConfig) {
        this.state = State.READING_FIRST;
        this.config = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (this.receivedClosingHandshake) {
            in.skipBytes(this.actualReadableBytes());
            return;
        }
        switch (this.state) {
            case READING_FIRST: {
                if (!in.isReadable()) {
                    return;
                }
                this.framePayloadLength = 0L;
                final byte b = in.readByte();
                this.frameFinalFlag = ((b & 0x80) != 0x0);
                this.frameRsv = (b & 0x70) >> 4;
                this.frameOpcode = (b & 0xF);
                if (WebSocket08FrameDecoder.logger.isTraceEnabled()) {
                    WebSocket08FrameDecoder.logger.trace("Decoding WebSocket Frame opCode={}", (Object)this.frameOpcode);
                }
                this.state = State.READING_SECOND;
            }
            case READING_SECOND: {
                if (!in.isReadable()) {
                    return;
                }
                final byte b = in.readByte();
                this.frameMasked = ((b & 0x80) != 0x0);
                this.framePayloadLen1 = (b & 0x7F);
                if (this.frameRsv != 0 && !this.config.allowExtensions()) {
                    this.protocolViolation(ctx, in, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                    return;
                }
                if (!this.config.allowMaskMismatch() && this.config.expectMaskedFrames() != this.frameMasked) {
                    this.protocolViolation(ctx, in, "received a frame that is not masked as expected");
                    return;
                }
                if (this.frameOpcode > 7) {
                    if (!this.frameFinalFlag) {
                        this.protocolViolation(ctx, in, "fragmented control frame");
                        return;
                    }
                    if (this.framePayloadLen1 > 125) {
                        this.protocolViolation(ctx, in, "control frame with payload length > 125 octets");
                        return;
                    }
                    if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                        this.protocolViolation(ctx, in, "control frame using reserved opcode " + this.frameOpcode);
                        return;
                    }
                    if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
                        this.protocolViolation(ctx, in, "received close control frame with payload len 1");
                        return;
                    }
                }
                else {
                    if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                        this.protocolViolation(ctx, in, "data frame using reserved opcode " + this.frameOpcode);
                        return;
                    }
                    if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                        this.protocolViolation(ctx, in, "received continuation data frame outside fragmented message");
                        return;
                    }
                    if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0) {
                        this.protocolViolation(ctx, in, "received non-continuation data frame while inside fragmented message");
                        return;
                    }
                }
                this.state = State.READING_SIZE;
            }
            case READING_SIZE: {
                if (this.framePayloadLen1 == 126) {
                    if (in.readableBytes() < 2) {
                        return;
                    }
                    this.framePayloadLength = in.readUnsignedShort();
                    if (this.framePayloadLength < 126L) {
                        this.protocolViolation(ctx, in, "invalid data frame length (not using minimal length encoding)");
                        return;
                    }
                }
                else if (this.framePayloadLen1 == 127) {
                    if (in.readableBytes() < 8) {
                        return;
                    }
                    this.framePayloadLength = in.readLong();
                    if (this.framePayloadLength < 65536L) {
                        this.protocolViolation(ctx, in, "invalid data frame length (not using minimal length encoding)");
                        return;
                    }
                }
                else {
                    this.framePayloadLength = this.framePayloadLen1;
                }
                if (this.framePayloadLength > this.config.maxFramePayloadLength()) {
                    this.protocolViolation(ctx, in, WebSocketCloseStatus.MESSAGE_TOO_BIG, "Max frame length of " + this.config.maxFramePayloadLength() + " has been exceeded.");
                    return;
                }
                if (WebSocket08FrameDecoder.logger.isTraceEnabled()) {
                    WebSocket08FrameDecoder.logger.trace("Decoding WebSocket Frame length={}", (Object)this.framePayloadLength);
                }
                this.state = State.MASKING_KEY;
            }
            case MASKING_KEY: {
                if (this.frameMasked) {
                    if (in.readableBytes() < 4) {
                        return;
                    }
                    if (this.maskingKey == null) {
                        this.maskingKey = new byte[4];
                    }
                    in.readBytes(this.maskingKey);
                }
                this.state = State.PAYLOAD;
            }
            case PAYLOAD: {
                if (in.readableBytes() < this.framePayloadLength) {
                    return;
                }
                ByteBuf payloadBuffer = null;
                try {
                    payloadBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toFrameLength(this.framePayloadLength));
                    this.state = State.READING_FIRST;
                    if (this.frameMasked) {
                        this.unmask(payloadBuffer);
                    }
                    if (this.frameOpcode == 9) {
                        out.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 10) {
                        out.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 8) {
                        this.receivedClosingHandshake = true;
                        this.checkCloseFrameBody(ctx, payloadBuffer);
                        out.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameFinalFlag) {
                        this.fragmentedFramesCount = 0;
                    }
                    else {
                        ++this.fragmentedFramesCount;
                    }
                    if (this.frameOpcode == 1) {
                        out.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 2) {
                        out.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 0) {
                        out.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
                }
                finally {
                    if (payloadBuffer != null) {
                        payloadBuffer.release();
                    }
                }
            }
            case CORRUPT: {
                if (in.isReadable()) {
                    in.readByte();
                }
                return;
            }
            default: {
                throw new Error("Shouldn't reach here.");
            }
        }
    }
    
    private void unmask(final ByteBuf frame) {
        int i = frame.readerIndex();
        final int end = frame.writerIndex();
        final ByteOrder order = frame.order();
        int intMask = (this.maskingKey[0] & 0xFF) << 24 | (this.maskingKey[1] & 0xFF) << 16 | (this.maskingKey[2] & 0xFF) << 8 | (this.maskingKey[3] & 0xFF);
        if (order == ByteOrder.LITTLE_ENDIAN) {
            intMask = Integer.reverseBytes(intMask);
        }
        while (i + 3 < end) {
            final int unmasked = frame.getInt(i) ^ intMask;
            frame.setInt(i, unmasked);
            i += 4;
        }
        while (i < end) {
            frame.setByte(i, frame.getByte(i) ^ this.maskingKey[i % 4]);
            ++i;
        }
    }
    
    private void protocolViolation(final ChannelHandlerContext ctx, final ByteBuf in, final String reason) {
        this.protocolViolation(ctx, in, WebSocketCloseStatus.PROTOCOL_ERROR, reason);
    }
    
    private void protocolViolation(final ChannelHandlerContext ctx, final ByteBuf in, final WebSocketCloseStatus status, final String reason) {
        this.protocolViolation(ctx, in, new CorruptedWebSocketFrameException(status, reason));
    }
    
    private void protocolViolation(final ChannelHandlerContext ctx, final ByteBuf in, final CorruptedWebSocketFrameException ex) {
        this.state = State.CORRUPT;
        final int readableBytes = in.readableBytes();
        if (readableBytes > 0) {
            in.skipBytes(readableBytes);
        }
        if (ctx.channel().isActive() && this.config.closeOnProtocolViolation()) {
            Object closeMessage;
            if (this.receivedClosingHandshake) {
                closeMessage = Unpooled.EMPTY_BUFFER;
            }
            else {
                final WebSocketCloseStatus closeStatus = ex.closeStatus();
                String reasonText = ex.getMessage();
                if (reasonText == null) {
                    reasonText = closeStatus.reasonText();
                }
                closeMessage = new CloseWebSocketFrame(closeStatus, reasonText);
            }
            ctx.writeAndFlush(closeMessage).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
        throw ex;
    }
    
    private static int toFrameLength(final long l) {
        if (l > 2147483647L) {
            throw new TooLongFrameException("Length:" + l);
        }
        return (int)l;
    }
    
    protected void checkCloseFrameBody(final ChannelHandlerContext ctx, final ByteBuf buffer) {
        if (buffer == null || !buffer.isReadable()) {
            return;
        }
        if (buffer.readableBytes() == 1) {
            this.protocolViolation(ctx, buffer, WebSocketCloseStatus.INVALID_PAYLOAD_DATA, "Invalid close frame body");
        }
        final int idx = buffer.readerIndex();
        buffer.readerIndex(0);
        final int statusCode = buffer.readShort();
        if (!WebSocketCloseStatus.isValidStatusCode(statusCode)) {
            this.protocolViolation(ctx, buffer, "Invalid close frame getStatus code: " + statusCode);
        }
        if (buffer.isReadable()) {
            try {
                new Utf8Validator().check(buffer);
            }
            catch (final CorruptedWebSocketFrameException ex) {
                this.protocolViolation(ctx, buffer, ex);
            }
        }
        buffer.readerIndex(idx);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
    }
    
    enum State
    {
        READING_FIRST, 
        READING_SECOND, 
        READING_SIZE, 
        MASKING_KEY, 
        PAYLOAD, 
        CORRUPT;
    }
}
