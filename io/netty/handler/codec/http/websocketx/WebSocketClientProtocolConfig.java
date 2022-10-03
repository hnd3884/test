package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;

public final class WebSocketClientProtocolConfig
{
    static final boolean DEFAULT_PERFORM_MASKING = true;
    static final boolean DEFAULT_ALLOW_MASK_MISMATCH = false;
    static final boolean DEFAULT_HANDLE_CLOSE_FRAMES = true;
    static final boolean DEFAULT_DROP_PONG_FRAMES = true;
    private final URI webSocketUri;
    private final String subprotocol;
    private final WebSocketVersion version;
    private final boolean allowExtensions;
    private final HttpHeaders customHeaders;
    private final int maxFramePayloadLength;
    private final boolean performMasking;
    private final boolean allowMaskMismatch;
    private final boolean handleCloseFrames;
    private final WebSocketCloseStatus sendCloseFrame;
    private final boolean dropPongFrames;
    private final long handshakeTimeoutMillis;
    private final long forceCloseTimeoutMillis;
    private final boolean absoluteUpgradeUrl;
    
    private WebSocketClientProtocolConfig(final URI webSocketUri, final String subprotocol, final WebSocketVersion version, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean performMasking, final boolean allowMaskMismatch, final boolean handleCloseFrames, final WebSocketCloseStatus sendCloseFrame, final boolean dropPongFrames, final long handshakeTimeoutMillis, final long forceCloseTimeoutMillis, final boolean absoluteUpgradeUrl) {
        this.webSocketUri = webSocketUri;
        this.subprotocol = subprotocol;
        this.version = version;
        this.allowExtensions = allowExtensions;
        this.customHeaders = customHeaders;
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.performMasking = performMasking;
        this.allowMaskMismatch = allowMaskMismatch;
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
        this.handleCloseFrames = handleCloseFrames;
        this.sendCloseFrame = sendCloseFrame;
        this.dropPongFrames = dropPongFrames;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
        this.absoluteUpgradeUrl = absoluteUpgradeUrl;
    }
    
    public URI webSocketUri() {
        return this.webSocketUri;
    }
    
    public String subprotocol() {
        return this.subprotocol;
    }
    
    public WebSocketVersion version() {
        return this.version;
    }
    
    public boolean allowExtensions() {
        return this.allowExtensions;
    }
    
    public HttpHeaders customHeaders() {
        return this.customHeaders;
    }
    
    public int maxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }
    
    public boolean performMasking() {
        return this.performMasking;
    }
    
    public boolean allowMaskMismatch() {
        return this.allowMaskMismatch;
    }
    
    public boolean handleCloseFrames() {
        return this.handleCloseFrames;
    }
    
    public WebSocketCloseStatus sendCloseFrame() {
        return this.sendCloseFrame;
    }
    
    public boolean dropPongFrames() {
        return this.dropPongFrames;
    }
    
    public long handshakeTimeoutMillis() {
        return this.handshakeTimeoutMillis;
    }
    
    public long forceCloseTimeoutMillis() {
        return this.forceCloseTimeoutMillis;
    }
    
    public boolean absoluteUpgradeUrl() {
        return this.absoluteUpgradeUrl;
    }
    
    @Override
    public String toString() {
        return "WebSocketClientProtocolConfig {webSocketUri=" + this.webSocketUri + ", subprotocol=" + this.subprotocol + ", version=" + this.version + ", allowExtensions=" + this.allowExtensions + ", customHeaders=" + this.customHeaders + ", maxFramePayloadLength=" + this.maxFramePayloadLength + ", performMasking=" + this.performMasking + ", allowMaskMismatch=" + this.allowMaskMismatch + ", handleCloseFrames=" + this.handleCloseFrames + ", sendCloseFrame=" + this.sendCloseFrame + ", dropPongFrames=" + this.dropPongFrames + ", handshakeTimeoutMillis=" + this.handshakeTimeoutMillis + ", forceCloseTimeoutMillis=" + this.forceCloseTimeoutMillis + ", absoluteUpgradeUrl=" + this.absoluteUpgradeUrl + "}";
    }
    
    public Builder toBuilder() {
        return new Builder(this);
    }
    
    public static Builder newBuilder() {
        return new Builder(URI.create("https://localhost/"), (String)null, WebSocketVersion.V13, false, (HttpHeaders)EmptyHttpHeaders.INSTANCE, 65536, true, false, true, WebSocketCloseStatus.NORMAL_CLOSURE, true, 10000L, -1L, false);
    }
    
    public static final class Builder
    {
        private URI webSocketUri;
        private String subprotocol;
        private WebSocketVersion version;
        private boolean allowExtensions;
        private HttpHeaders customHeaders;
        private int maxFramePayloadLength;
        private boolean performMasking;
        private boolean allowMaskMismatch;
        private boolean handleCloseFrames;
        private WebSocketCloseStatus sendCloseFrame;
        private boolean dropPongFrames;
        private long handshakeTimeoutMillis;
        private long forceCloseTimeoutMillis;
        private boolean absoluteUpgradeUrl;
        
        private Builder(final WebSocketClientProtocolConfig clientConfig) {
            this(ObjectUtil.checkNotNull(clientConfig, "clientConfig").webSocketUri(), clientConfig.subprotocol(), clientConfig.version(), clientConfig.allowExtensions(), clientConfig.customHeaders(), clientConfig.maxFramePayloadLength(), clientConfig.performMasking(), clientConfig.allowMaskMismatch(), clientConfig.handleCloseFrames(), clientConfig.sendCloseFrame(), clientConfig.dropPongFrames(), clientConfig.handshakeTimeoutMillis(), clientConfig.forceCloseTimeoutMillis(), clientConfig.absoluteUpgradeUrl());
        }
        
        private Builder(final URI webSocketUri, final String subprotocol, final WebSocketVersion version, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean performMasking, final boolean allowMaskMismatch, final boolean handleCloseFrames, final WebSocketCloseStatus sendCloseFrame, final boolean dropPongFrames, final long handshakeTimeoutMillis, final long forceCloseTimeoutMillis, final boolean absoluteUpgradeUrl) {
            this.webSocketUri = webSocketUri;
            this.subprotocol = subprotocol;
            this.version = version;
            this.allowExtensions = allowExtensions;
            this.customHeaders = customHeaders;
            this.maxFramePayloadLength = maxFramePayloadLength;
            this.performMasking = performMasking;
            this.allowMaskMismatch = allowMaskMismatch;
            this.handleCloseFrames = handleCloseFrames;
            this.sendCloseFrame = sendCloseFrame;
            this.dropPongFrames = dropPongFrames;
            this.handshakeTimeoutMillis = handshakeTimeoutMillis;
            this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
            this.absoluteUpgradeUrl = absoluteUpgradeUrl;
        }
        
        public Builder webSocketUri(final String webSocketUri) {
            return this.webSocketUri(URI.create(webSocketUri));
        }
        
        public Builder webSocketUri(final URI webSocketUri) {
            this.webSocketUri = webSocketUri;
            return this;
        }
        
        public Builder subprotocol(final String subprotocol) {
            this.subprotocol = subprotocol;
            return this;
        }
        
        public Builder version(final WebSocketVersion version) {
            this.version = version;
            return this;
        }
        
        public Builder allowExtensions(final boolean allowExtensions) {
            this.allowExtensions = allowExtensions;
            return this;
        }
        
        public Builder customHeaders(final HttpHeaders customHeaders) {
            this.customHeaders = customHeaders;
            return this;
        }
        
        public Builder maxFramePayloadLength(final int maxFramePayloadLength) {
            this.maxFramePayloadLength = maxFramePayloadLength;
            return this;
        }
        
        public Builder performMasking(final boolean performMasking) {
            this.performMasking = performMasking;
            return this;
        }
        
        public Builder allowMaskMismatch(final boolean allowMaskMismatch) {
            this.allowMaskMismatch = allowMaskMismatch;
            return this;
        }
        
        public Builder handleCloseFrames(final boolean handleCloseFrames) {
            this.handleCloseFrames = handleCloseFrames;
            return this;
        }
        
        public Builder sendCloseFrame(final WebSocketCloseStatus sendCloseFrame) {
            this.sendCloseFrame = sendCloseFrame;
            return this;
        }
        
        public Builder dropPongFrames(final boolean dropPongFrames) {
            this.dropPongFrames = dropPongFrames;
            return this;
        }
        
        public Builder handshakeTimeoutMillis(final long handshakeTimeoutMillis) {
            this.handshakeTimeoutMillis = handshakeTimeoutMillis;
            return this;
        }
        
        public Builder forceCloseTimeoutMillis(final long forceCloseTimeoutMillis) {
            this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
            return this;
        }
        
        public Builder absoluteUpgradeUrl(final boolean absoluteUpgradeUrl) {
            this.absoluteUpgradeUrl = absoluteUpgradeUrl;
            return this;
        }
        
        public WebSocketClientProtocolConfig build() {
            return new WebSocketClientProtocolConfig(this.webSocketUri, this.subprotocol, this.version, this.allowExtensions, this.customHeaders, this.maxFramePayloadLength, this.performMasking, this.allowMaskMismatch, this.handleCloseFrames, this.sendCloseFrame, this.dropPongFrames, this.handshakeTimeoutMillis, this.forceCloseTimeoutMillis, this.absoluteUpgradeUrl, null);
        }
    }
}
