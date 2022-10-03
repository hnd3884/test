package io.netty.handler.codec.http.websocketx;

import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CloseWebSocketFrame extends WebSocketFrame
{
    public CloseWebSocketFrame() {
        super(Unpooled.buffer(0));
    }
    
    public CloseWebSocketFrame(final WebSocketCloseStatus status) {
        this(requireValidStatusCode(status.code()), status.reasonText());
    }
    
    public CloseWebSocketFrame(final WebSocketCloseStatus status, final String reasonText) {
        this(requireValidStatusCode(status.code()), reasonText);
    }
    
    public CloseWebSocketFrame(final int statusCode, final String reasonText) {
        this(true, 0, requireValidStatusCode(statusCode), reasonText);
    }
    
    public CloseWebSocketFrame(final boolean finalFragment, final int rsv) {
        this(finalFragment, rsv, Unpooled.buffer(0));
    }
    
    public CloseWebSocketFrame(final boolean finalFragment, final int rsv, final int statusCode, final String reasonText) {
        super(finalFragment, rsv, newBinaryData(requireValidStatusCode(statusCode), reasonText));
    }
    
    private static ByteBuf newBinaryData(final int statusCode, String reasonText) {
        if (reasonText == null) {
            reasonText = "";
        }
        final ByteBuf binaryData = Unpooled.buffer(2 + reasonText.length());
        binaryData.writeShort(statusCode);
        if (!reasonText.isEmpty()) {
            binaryData.writeCharSequence(reasonText, CharsetUtil.UTF_8);
        }
        binaryData.readerIndex(0);
        return binaryData;
    }
    
    public CloseWebSocketFrame(final boolean finalFragment, final int rsv, final ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }
    
    public int statusCode() {
        final ByteBuf binaryData = this.content();
        if (binaryData == null || binaryData.capacity() == 0) {
            return -1;
        }
        binaryData.readerIndex(0);
        return binaryData.getShort(0);
    }
    
    public String reasonText() {
        final ByteBuf binaryData = this.content();
        if (binaryData == null || binaryData.capacity() <= 2) {
            return "";
        }
        binaryData.readerIndex(2);
        final String reasonText = binaryData.toString(CharsetUtil.UTF_8);
        binaryData.readerIndex(0);
        return reasonText;
    }
    
    @Override
    public CloseWebSocketFrame copy() {
        return (CloseWebSocketFrame)super.copy();
    }
    
    @Override
    public CloseWebSocketFrame duplicate() {
        return (CloseWebSocketFrame)super.duplicate();
    }
    
    @Override
    public CloseWebSocketFrame retainedDuplicate() {
        return (CloseWebSocketFrame)super.retainedDuplicate();
    }
    
    @Override
    public CloseWebSocketFrame replace(final ByteBuf content) {
        return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
    }
    
    @Override
    public CloseWebSocketFrame retain() {
        super.retain();
        return this;
    }
    
    @Override
    public CloseWebSocketFrame retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public CloseWebSocketFrame touch() {
        super.touch();
        return this;
    }
    
    @Override
    public CloseWebSocketFrame touch(final Object hint) {
        super.touch(hint);
        return this;
    }
    
    static int requireValidStatusCode(final int statusCode) {
        if (WebSocketCloseStatus.isValidStatusCode(statusCode)) {
            return statusCode;
        }
        throw new IllegalArgumentException("WebSocket close status code does NOT comply with RFC-6455: " + statusCode);
    }
}
