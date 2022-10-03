package com.google.api.client.auth.openidconnect;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.GenericJson;
import java.util.List;
import com.google.api.client.util.Key;
import java.io.IOException;
import com.google.api.client.json.JsonFactory;
import java.util.Collection;
import java.util.Collections;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.util.Beta;
import com.google.api.client.json.webtoken.JsonWebSignature;

@Beta
public class IdToken extends JsonWebSignature
{
    public IdToken(final JsonWebSignature.Header header, final Payload payload, final byte[] signatureBytes, final byte[] signedContentBytes) {
        super(header, (JsonWebToken.Payload)payload, signatureBytes, signedContentBytes);
    }
    
    public Payload getPayload() {
        return (Payload)super.getPayload();
    }
    
    public final boolean verifyIssuer(final String expectedIssuer) {
        return this.verifyIssuer(Collections.singleton(expectedIssuer));
    }
    
    public final boolean verifyIssuer(final Collection<String> expectedIssuer) {
        return expectedIssuer.contains(this.getPayload().getIssuer());
    }
    
    public final boolean verifyAudience(final Collection<String> trustedClientIds) {
        final Collection<String> audience = this.getPayload().getAudienceAsList();
        return !audience.isEmpty() && trustedClientIds.containsAll(audience);
    }
    
    public final boolean verifyTime(final long currentTimeMillis, final long acceptableTimeSkewSeconds) {
        return this.verifyExpirationTime(currentTimeMillis, acceptableTimeSkewSeconds) && this.verifyIssuedAtTime(currentTimeMillis, acceptableTimeSkewSeconds);
    }
    
    public final boolean verifyExpirationTime(final long currentTimeMillis, final long acceptableTimeSkewSeconds) {
        return currentTimeMillis <= (this.getPayload().getExpirationTimeSeconds() + acceptableTimeSkewSeconds) * 1000L;
    }
    
    public final boolean verifyIssuedAtTime(final long currentTimeMillis, final long acceptableTimeSkewSeconds) {
        return currentTimeMillis >= (this.getPayload().getIssuedAtTimeSeconds() - acceptableTimeSkewSeconds) * 1000L;
    }
    
    public static IdToken parse(final JsonFactory jsonFactory, final String idTokenString) throws IOException {
        final JsonWebSignature jws = JsonWebSignature.parser(jsonFactory).setPayloadClass((Class)Payload.class).parse(idTokenString);
        return new IdToken(jws.getHeader(), (Payload)jws.getPayload(), jws.getSignatureBytes(), jws.getSignedContentBytes());
    }
    
    @Beta
    public static class Payload extends JsonWebToken.Payload
    {
        @Key("auth_time")
        private Long authorizationTimeSeconds;
        @Key("azp")
        private String authorizedParty;
        @Key
        private String nonce;
        @Key("at_hash")
        private String accessTokenHash;
        @Key("acr")
        private String classReference;
        @Key("amr")
        private List<String> methodsReferences;
        
        public final Long getAuthorizationTimeSeconds() {
            return this.authorizationTimeSeconds;
        }
        
        public Payload setAuthorizationTimeSeconds(final Long authorizationTimeSeconds) {
            this.authorizationTimeSeconds = authorizationTimeSeconds;
            return this;
        }
        
        public final String getAuthorizedParty() {
            return this.authorizedParty;
        }
        
        public Payload setAuthorizedParty(final String authorizedParty) {
            this.authorizedParty = authorizedParty;
            return this;
        }
        
        public final String getNonce() {
            return this.nonce;
        }
        
        public Payload setNonce(final String nonce) {
            this.nonce = nonce;
            return this;
        }
        
        public final String getAccessTokenHash() {
            return this.accessTokenHash;
        }
        
        public Payload setAccessTokenHash(final String accessTokenHash) {
            this.accessTokenHash = accessTokenHash;
            return this;
        }
        
        public final String getClassReference() {
            return this.classReference;
        }
        
        public Payload setClassReference(final String classReference) {
            this.classReference = classReference;
            return this;
        }
        
        public final List<String> getMethodsReferences() {
            return this.methodsReferences;
        }
        
        public Payload setMethodsReferences(final List<String> methodsReferences) {
            this.methodsReferences = methodsReferences;
            return this;
        }
        
        public Payload setExpirationTimeSeconds(final Long expirationTimeSeconds) {
            return (Payload)super.setExpirationTimeSeconds(expirationTimeSeconds);
        }
        
        public Payload setNotBeforeTimeSeconds(final Long notBeforeTimeSeconds) {
            return (Payload)super.setNotBeforeTimeSeconds(notBeforeTimeSeconds);
        }
        
        public Payload setIssuedAtTimeSeconds(final Long issuedAtTimeSeconds) {
            return (Payload)super.setIssuedAtTimeSeconds(issuedAtTimeSeconds);
        }
        
        public Payload setIssuer(final String issuer) {
            return (Payload)super.setIssuer(issuer);
        }
        
        public Payload setAudience(final Object audience) {
            return (Payload)super.setAudience(audience);
        }
        
        public Payload setJwtId(final String jwtId) {
            return (Payload)super.setJwtId(jwtId);
        }
        
        public Payload setType(final String type) {
            return (Payload)super.setType(type);
        }
        
        public Payload setSubject(final String subject) {
            return (Payload)super.setSubject(subject);
        }
        
        public Payload set(final String fieldName, final Object value) {
            return (Payload)super.set(fieldName, value);
        }
        
        public Payload clone() {
            return (Payload)super.clone();
        }
    }
}
