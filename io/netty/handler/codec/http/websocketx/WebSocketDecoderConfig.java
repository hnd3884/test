package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketDecoderConfig
{
    static final WebSocketDecoderConfig DEFAULT;
    private final int maxFramePayloadLength;
    private final boolean expectMaskedFrames;
    private final boolean allowMaskMismatch;
    private final boolean allowExtensions;
    private final boolean closeOnProtocolViolation;
    private final boolean withUTF8Validator;
    
    private WebSocketDecoderConfig(final int maxFramePayloadLength, final boolean expectMaskedFrames, final boolean allowMaskMismatch, final boolean allowExtensions, final boolean closeOnProtocolViolation, final boolean withUTF8Validator) {
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.expectMaskedFrames = expectMaskedFrames;
        this.allowMaskMismatch = allowMaskMismatch;
        this.allowExtensions = allowExtensions;
        this.closeOnProtocolViolation = closeOnProtocolViolation;
        this.withUTF8Validator = withUTF8Validator;
    }
    
    public int maxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }
    
    public boolean expectMaskedFrames() {
        return this.expectMaskedFrames;
    }
    
    public boolean allowMaskMismatch() {
        return this.allowMaskMismatch;
    }
    
    public boolean allowExtensions() {
        return this.allowExtensions;
    }
    
    public boolean closeOnProtocolViolation() {
        return this.closeOnProtocolViolation;
    }
    
    public boolean withUTF8Validator() {
        return this.withUTF8Validator;
    }
    
    @Override
    public String toString() {
        return "WebSocketDecoderConfig [maxFramePayloadLength=" + this.maxFramePayloadLength + ", expectMaskedFrames=" + this.expectMaskedFrames + ", allowMaskMismatch=" + this.allowMaskMismatch + ", allowExtensions=" + this.allowExtensions + ", closeOnProtocolViolation=" + this.closeOnProtocolViolation + ", withUTF8Validator=" + this.withUTF8Validator + "]";
    }
    
    public Builder toBuilder() {
        return new Builder(this);
    }
    
    public static Builder newBuilder() {
        return new Builder(WebSocketDecoderConfig.DEFAULT);
    }
    
    static {
        DEFAULT = new WebSocketDecoderConfig(65536, true, false, false, true, true);
    }
    
    public static final class Builder
    {
        private int maxFramePayloadLength;
        private boolean expectMaskedFrames;
        private boolean allowMaskMismatch;
        private boolean allowExtensions;
        private boolean closeOnProtocolViolation;
        private boolean withUTF8Validator;
        
        private Builder(final WebSocketDecoderConfig decoderConfig) {
            ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
            this.maxFramePayloadLength = decoderConfig.maxFramePayloadLength();
            this.expectMaskedFrames = decoderConfig.expectMaskedFrames();
            this.allowMaskMismatch = decoderConfig.allowMaskMismatch();
            this.allowExtensions = decoderConfig.allowExtensions();
            this.closeOnProtocolViolation = decoderConfig.closeOnProtocolViolation();
            this.withUTF8Validator = decoderConfig.withUTF8Validator();
        }
        
        public Builder maxFramePayloadLength(final int maxFramePayloadLength) {
            this.maxFramePayloadLength = maxFramePayloadLength;
            return this;
        }
        
        public Builder expectMaskedFrames(final boolean expectMaskedFrames) {
            this.expectMaskedFrames = expectMaskedFrames;
            return this;
        }
        
        public Builder allowMaskMismatch(final boolean allowMaskMismatch) {
            this.allowMaskMismatch = allowMaskMismatch;
            return this;
        }
        
        public Builder allowExtensions(final boolean allowExtensions) {
            this.allowExtensions = allowExtensions;
            return this;
        }
        
        public Builder closeOnProtocolViolation(final boolean closeOnProtocolViolation) {
            this.closeOnProtocolViolation = closeOnProtocolViolation;
            return this;
        }
        
        public Builder withUTF8Validator(final boolean withUTF8Validator) {
            this.withUTF8Validator = withUTF8Validator;
            return this;
        }
        
        public WebSocketDecoderConfig build() {
            return new WebSocketDecoderConfig(this.maxFramePayloadLength, this.expectMaskedFrames, this.allowMaskMismatch, this.allowExtensions, this.closeOnProtocolViolation, this.withUTF8Validator, null);
        }
    }
}
