package com.turo.pushy.apns.auth;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.base64.Base64;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.security.spec.ECParameterSpec;
import java.util.Objects;
import java.security.interfaces.ECKey;

abstract class ApnsKey implements ECKey
{
    private final String teamId;
    private final String keyId;
    private final ECKey key;
    public static final String APNS_SIGNATURE_ALGORITHM = "SHA256withECDSA";
    
    public ApnsKey(final String keyId, final String teamId, final ECKey key) {
        Objects.requireNonNull(keyId, "Key identifier must not be null.");
        Objects.requireNonNull(teamId, "Team identifier must not be null.");
        Objects.requireNonNull(key, "Key must not be null.");
        this.keyId = keyId;
        this.teamId = teamId;
        this.key = key;
    }
    
    public String getKeyId() {
        return this.keyId;
    }
    
    public String getTeamId() {
        return this.teamId;
    }
    
    protected ECKey getKey() {
        return this.key;
    }
    
    @Override
    public ECParameterSpec getParams() {
        return this.key.getParams();
    }
    
    protected static byte[] decodeBase64EncodedString(final String base64EncodedString) {
        final ByteBuf base64EncodedByteBuf = Unpooled.wrappedBuffer(base64EncodedString.getBytes(StandardCharsets.US_ASCII));
        final ByteBuf decodedByteBuf = Base64.decode(base64EncodedByteBuf);
        final byte[] decodedBytes = new byte[decodedByteBuf.readableBytes()];
        decodedByteBuf.readBytes(decodedBytes);
        base64EncodedByteBuf.release();
        decodedByteBuf.release();
        return decodedBytes;
    }
}
