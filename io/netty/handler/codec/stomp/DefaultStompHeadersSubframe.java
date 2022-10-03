package io.netty.handler.codec.stomp;

import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.DecoderResult;

public class DefaultStompHeadersSubframe implements StompHeadersSubframe
{
    protected final StompCommand command;
    protected DecoderResult decoderResult;
    protected final DefaultStompHeaders headers;
    
    public DefaultStompHeadersSubframe(final StompCommand command) {
        this(command, null);
    }
    
    DefaultStompHeadersSubframe(final StompCommand command, final DefaultStompHeaders headers) {
        this.decoderResult = DecoderResult.SUCCESS;
        this.command = ObjectUtil.checkNotNull(command, "command");
        this.headers = ((headers == null) ? new DefaultStompHeaders() : headers);
    }
    
    @Override
    public StompCommand command() {
        return this.command;
    }
    
    @Override
    public StompHeaders headers() {
        return this.headers;
    }
    
    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }
    
    @Override
    public void setDecoderResult(final DecoderResult decoderResult) {
        this.decoderResult = decoderResult;
    }
    
    @Override
    public String toString() {
        return "StompFrame{command=" + this.command + ", headers=" + this.headers + '}';
    }
}
