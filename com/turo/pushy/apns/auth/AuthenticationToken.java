package com.turo.pushy.apns.auth;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Type;
import com.turo.pushy.apns.util.DateAsTimeSinceEpochTypeAdapter;
import java.util.concurrent.TimeUnit;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.buffer.Unpooled;
import java.security.PublicKey;
import java.util.Objects;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.netty.util.AsciiString;
import com.google.gson.Gson;

public class AuthenticationToken
{
    private static final Gson GSON;
    private final AuthenticationTokenHeader header;
    private final AuthenticationTokenClaims claims;
    private final byte[] signatureBytes;
    private final transient String base64EncodedToken;
    private final transient AsciiString authorizationHeader;
    
    public AuthenticationToken(final ApnsSigningKey signingKey, final Date issuedAt) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.header = new AuthenticationTokenHeader(signingKey.getKeyId());
        this.claims = new AuthenticationTokenClaims(signingKey.getTeamId(), issuedAt);
        final String headerJson = AuthenticationToken.GSON.toJson((Object)this.header);
        final String claimsJson = AuthenticationToken.GSON.toJson((Object)this.claims);
        final StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append(encodeUnpaddedBase64UrlString(headerJson.getBytes(StandardCharsets.US_ASCII)));
        payloadBuilder.append('.');
        payloadBuilder.append(encodeUnpaddedBase64UrlString(claimsJson.getBytes(StandardCharsets.US_ASCII)));
        final Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(signingKey);
        signature.update(payloadBuilder.toString().getBytes(StandardCharsets.US_ASCII));
        this.signatureBytes = signature.sign();
        payloadBuilder.append('.');
        payloadBuilder.append(encodeUnpaddedBase64UrlString(this.signatureBytes));
        this.base64EncodedToken = payloadBuilder.toString();
        this.authorizationHeader = new AsciiString((CharSequence)("bearer " + payloadBuilder.toString()));
    }
    
    public AuthenticationToken(final String base64EncodedToken) {
        Objects.requireNonNull(base64EncodedToken, "Encoded token must not be null.");
        this.base64EncodedToken = base64EncodedToken;
        this.authorizationHeader = new AsciiString((CharSequence)("bearer " + base64EncodedToken));
        final String[] jwtSegments = base64EncodedToken.split("\\.");
        if (jwtSegments.length != 3) {
            throw new IllegalArgumentException();
        }
        this.header = (AuthenticationTokenHeader)AuthenticationToken.GSON.fromJson(new String(decodeBase64UrlEncodedString(jwtSegments[0]), StandardCharsets.US_ASCII), (Class)AuthenticationTokenHeader.class);
        this.claims = (AuthenticationTokenClaims)AuthenticationToken.GSON.fromJson(new String(decodeBase64UrlEncodedString(jwtSegments[1]), StandardCharsets.US_ASCII), (Class)AuthenticationTokenClaims.class);
        this.signatureBytes = decodeBase64UrlEncodedString(jwtSegments[2]);
    }
    
    public Date getIssuedAt() {
        return this.claims.getIssuedAt();
    }
    
    public String getKeyId() {
        return this.header.getKeyId();
    }
    
    public String getTeamId() {
        return this.claims.getIssuer();
    }
    
    public boolean verifySignature(final ApnsVerificationKey verificationKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (!this.header.getKeyId().equals(verificationKey.getKeyId())) {
            return false;
        }
        if (!this.claims.getIssuer().equals(verificationKey.getTeamId())) {
            return false;
        }
        final String headerJson = AuthenticationToken.GSON.toJson((Object)this.header);
        final String claimsJson = AuthenticationToken.GSON.toJson((Object)this.claims);
        final String encodedHeaderAndClaims = encodeUnpaddedBase64UrlString(headerJson.getBytes(StandardCharsets.US_ASCII)) + '.' + encodeUnpaddedBase64UrlString(claimsJson.getBytes(StandardCharsets.US_ASCII));
        final byte[] headerAndClaimsBytes = encodedHeaderAndClaims.getBytes(StandardCharsets.US_ASCII);
        final Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(verificationKey);
        signature.update(headerAndClaimsBytes);
        return signature.verify(this.signatureBytes);
    }
    
    public AsciiString getAuthorizationHeader() {
        return this.authorizationHeader;
    }
    
    @Override
    public String toString() {
        return this.base64EncodedToken;
    }
    
    @Override
    public int hashCode() {
        return this.base64EncodedToken.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AuthenticationToken)) {
            return false;
        }
        final AuthenticationToken other = (AuthenticationToken)obj;
        if (this.base64EncodedToken == null) {
            return other.base64EncodedToken == null;
        }
        return this.base64EncodedToken.equals(other.base64EncodedToken);
    }
    
    static String encodeUnpaddedBase64UrlString(final byte[] data) {
        final ByteBuf wrappedString = Unpooled.wrappedBuffer(data);
        final ByteBuf encodedString = Base64.encode(wrappedString, Base64Dialect.URL_SAFE);
        final String encodedUnpaddedString = encodedString.toString(StandardCharsets.US_ASCII).replace("=", "");
        wrappedString.release();
        encodedString.release();
        return encodedUnpaddedString;
    }
    
    static byte[] decodeBase64UrlEncodedString(final String base64UrlEncodedString) {
        String paddedBase64UrlEncodedString = null;
        switch (base64UrlEncodedString.length() % 4) {
            case 2: {
                paddedBase64UrlEncodedString = base64UrlEncodedString + "==";
                break;
            }
            case 3: {
                paddedBase64UrlEncodedString = base64UrlEncodedString + "=";
                break;
            }
            default: {
                paddedBase64UrlEncodedString = base64UrlEncodedString;
                break;
            }
        }
        final ByteBuf base64EncodedByteBuf = Unpooled.wrappedBuffer(paddedBase64UrlEncodedString.getBytes(StandardCharsets.US_ASCII));
        final ByteBuf decodedByteBuf = Base64.decode(base64EncodedByteBuf, Base64Dialect.URL_SAFE);
        final byte[] decodedBytes = new byte[decodedByteBuf.readableBytes()];
        decodedByteBuf.readBytes(decodedBytes);
        base64EncodedByteBuf.release();
        decodedByteBuf.release();
        return decodedBytes;
    }
    
    static {
        GSON = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter((Type)Date.class, (Object)new DateAsTimeSinceEpochTypeAdapter(TimeUnit.SECONDS)).create();
    }
    
    private static class AuthenticationTokenHeader
    {
        @SerializedName("alg")
        private final String algorithm = "ES256";
        @SerializedName("typ")
        private final String tokenType = "JWT";
        @SerializedName("kid")
        private final String keyId;
        
        AuthenticationTokenHeader(final String keyId) {
            this.keyId = keyId;
        }
        
        String getKeyId() {
            return this.keyId;
        }
    }
    
    private static class AuthenticationTokenClaims
    {
        @SerializedName("iss")
        private final String issuer;
        @SerializedName("iat")
        private final Date issuedAt;
        
        AuthenticationTokenClaims(final String teamId, final Date issuedAt) {
            this.issuer = teamId;
            this.issuedAt = issuedAt;
        }
        
        String getIssuer() {
            return this.issuer;
        }
        
        Date getIssuedAt() {
            return this.issuedAt;
        }
    }
}
