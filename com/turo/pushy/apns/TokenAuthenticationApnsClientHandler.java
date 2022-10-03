package com.turo.pushy.apns;

import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameListener;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import io.netty.handler.codec.http2.Http2Headers;
import java.util.Objects;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import org.slf4j.Logger;
import io.netty.util.AsciiString;
import com.turo.pushy.apns.auth.AuthenticationToken;
import com.turo.pushy.apns.auth.ApnsSigningKey;

class TokenAuthenticationApnsClientHandler extends ApnsClientHandler
{
    private final ApnsSigningKey signingKey;
    private AuthenticationToken authenticationToken;
    private int mostRecentStreamWithNewToken;
    private static final AsciiString APNS_AUTHORIZATION_HEADER;
    private static final String EXPIRED_AUTH_TOKEN_REASON = "ExpiredProviderToken";
    private static final Logger log;
    
    protected TokenAuthenticationApnsClientHandler(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings, final String authority, final ApnsSigningKey signingKey, final long idlePingIntervalMillis) {
        super(decoder, encoder, initialSettings, authority, idlePingIntervalMillis);
        this.mostRecentStreamWithNewToken = 0;
        Objects.requireNonNull(signingKey, "Signing key must not be null for token-based client handlers.");
        this.signingKey = signingKey;
    }
    
    @Override
    protected Http2Headers getHeadersForPushNotification(final ApnsPushNotification pushNotification, final int streamId) {
        final Http2Headers headers = super.getHeadersForPushNotification(pushNotification, streamId);
        if (this.authenticationToken == null) {
            try {
                this.authenticationToken = new AuthenticationToken(this.signingKey, new Date());
                this.mostRecentStreamWithNewToken = streamId;
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                TokenAuthenticationApnsClientHandler.log.error("Failed to generate authentication token.", (Throwable)e);
                throw new RuntimeException(e);
            }
        }
        headers.add((Object)TokenAuthenticationApnsClientHandler.APNS_AUTHORIZATION_HEADER, (Object)this.authenticationToken.getAuthorizationHeader());
        return headers;
    }
    
    @Override
    protected void handleErrorResponse(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final ApnsPushNotification pushNotification, final ErrorResponse errorResponse) {
        if ("ExpiredProviderToken".equals(errorResponse.getReason())) {
            if (streamId >= this.mostRecentStreamWithNewToken) {
                this.authenticationToken = null;
            }
            this.retryPushNotificationFromStream(context, streamId);
        }
        else {
            super.handleErrorResponse(context, streamId, headers, pushNotification, errorResponse);
        }
    }
    
    static {
        APNS_AUTHORIZATION_HEADER = new AsciiString((CharSequence)"authorization");
        log = LoggerFactory.getLogger((Class)TokenAuthenticationApnsClientHandler.class);
    }
    
    public static class TokenAuthenticationApnsClientHandlerBuilder extends ApnsClientHandlerBuilder
    {
        private ApnsSigningKey signingKey;
        
        public TokenAuthenticationApnsClientHandlerBuilder signingKey(final ApnsSigningKey signingKey) {
            this.signingKey = signingKey;
            return this;
        }
        
        public ApnsSigningKey signingKey() {
            return this.signingKey;
        }
        
        @Override
        public ApnsClientHandler build(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
            Objects.requireNonNull(this.authority(), "Authority must be set before building a TokenAuthenticationApnsClientHandler.");
            Objects.requireNonNull(this.signingKey(), "Signing key must be set before building a TokenAuthenticationApnsClientHandler.");
            final ApnsClientHandler handler = new TokenAuthenticationApnsClientHandler(decoder, encoder, initialSettings, this.authority(), this.signingKey(), this.idlePingIntervalMillis());
            this.frameListener((Http2FrameListener)handler);
            return handler;
        }
    }
}
