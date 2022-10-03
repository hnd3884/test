package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.GenericJson;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.webtoken.JsonWebToken;
import java.security.GeneralSecurityException;
import java.io.IOException;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Beta;
import com.google.api.client.auth.openidconnect.IdToken;

@Beta
public class GoogleIdToken extends IdToken
{
    public static GoogleIdToken parse(final JsonFactory jsonFactory, final String idTokenString) throws IOException {
        final JsonWebSignature jws = JsonWebSignature.parser(jsonFactory).setPayloadClass((Class)Payload.class).parse(idTokenString);
        return new GoogleIdToken(jws.getHeader(), (Payload)jws.getPayload(), jws.getSignatureBytes(), jws.getSignedContentBytes());
    }
    
    public GoogleIdToken(final JsonWebSignature.Header header, final Payload payload, final byte[] signatureBytes, final byte[] signedContentBytes) {
        super(header, (IdToken.Payload)payload, signatureBytes, signedContentBytes);
    }
    
    public boolean verify(final GoogleIdTokenVerifier verifier) throws GeneralSecurityException, IOException {
        return verifier.verify(this);
    }
    
    public Payload getPayload() {
        return (Payload)super.getPayload();
    }
    
    @Beta
    public static class Payload extends IdToken.Payload
    {
        @Key("hd")
        private String hostedDomain;
        @Key("email")
        private String email;
        @Key("email_verified")
        private Object emailVerified;
        
        @Deprecated
        public String getUserId() {
            return this.getSubject();
        }
        
        @Deprecated
        public Payload setUserId(final String userId) {
            return this.setSubject(userId);
        }
        
        @Deprecated
        public String getIssuee() {
            return this.getAuthorizedParty();
        }
        
        @Deprecated
        public Payload setIssuee(final String issuee) {
            return this.setAuthorizedParty(issuee);
        }
        
        public String getHostedDomain() {
            return this.hostedDomain;
        }
        
        public Payload setHostedDomain(final String hostedDomain) {
            this.hostedDomain = hostedDomain;
            return this;
        }
        
        public String getEmail() {
            return this.email;
        }
        
        public Payload setEmail(final String email) {
            this.email = email;
            return this;
        }
        
        public Boolean getEmailVerified() {
            if (this.emailVerified == null) {
                return null;
            }
            if (this.emailVerified instanceof Boolean) {
                return (Boolean)this.emailVerified;
            }
            return Boolean.valueOf((String)this.emailVerified);
        }
        
        public Payload setEmailVerified(final Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }
        
        public Payload setAuthorizationTimeSeconds(final Long authorizationTimeSeconds) {
            return (Payload)super.setAuthorizationTimeSeconds(authorizationTimeSeconds);
        }
        
        public Payload setAuthorizedParty(final String authorizedParty) {
            return (Payload)super.setAuthorizedParty(authorizedParty);
        }
        
        public Payload setNonce(final String nonce) {
            return (Payload)super.setNonce(nonce);
        }
        
        public Payload setAccessTokenHash(final String accessTokenHash) {
            return (Payload)super.setAccessTokenHash(accessTokenHash);
        }
        
        public Payload setClassReference(final String classReference) {
            return (Payload)super.setClassReference(classReference);
        }
        
        public Payload setMethodsReferences(final List<String> methodsReferences) {
            return (Payload)super.setMethodsReferences((List)methodsReferences);
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
