package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.Clock;
import java.util.Collection;
import java.util.Arrays;
import com.google.api.client.util.Preconditions;
import java.util.Iterator;
import com.google.api.client.auth.openidconnect.IdToken;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Beta;
import com.google.api.client.auth.openidconnect.IdTokenVerifier;

@Beta
public class GoogleIdTokenVerifier extends IdTokenVerifier
{
    private final GooglePublicKeysManager publicKeys;
    
    public GoogleIdTokenVerifier(final HttpTransport transport, final JsonFactory jsonFactory) {
        this(new Builder(transport, jsonFactory));
    }
    
    public GoogleIdTokenVerifier(final GooglePublicKeysManager publicKeys) {
        this(new Builder(publicKeys));
    }
    
    protected GoogleIdTokenVerifier(final Builder builder) {
        super((IdTokenVerifier.Builder)builder);
        this.publicKeys = builder.publicKeys;
    }
    
    public final GooglePublicKeysManager getPublicKeysManager() {
        return this.publicKeys;
    }
    
    public final HttpTransport getTransport() {
        return this.publicKeys.getTransport();
    }
    
    public final JsonFactory getJsonFactory() {
        return this.publicKeys.getJsonFactory();
    }
    
    @Deprecated
    public final String getPublicCertsEncodedUrl() {
        return this.publicKeys.getPublicCertsEncodedUrl();
    }
    
    @Deprecated
    public final List<PublicKey> getPublicKeys() throws GeneralSecurityException, IOException {
        return this.publicKeys.getPublicKeys();
    }
    
    @Deprecated
    public final long getExpirationTimeMilliseconds() {
        return this.publicKeys.getExpirationTimeMilliseconds();
    }
    
    public boolean verify(final GoogleIdToken googleIdToken) throws GeneralSecurityException, IOException {
        if (!super.verify((IdToken)googleIdToken)) {
            return false;
        }
        for (final PublicKey publicKey : this.publicKeys.getPublicKeys()) {
            if (googleIdToken.verifySignature(publicKey)) {
                return true;
            }
        }
        return false;
    }
    
    public GoogleIdToken verify(final String idTokenString) throws GeneralSecurityException, IOException {
        final GoogleIdToken idToken = GoogleIdToken.parse(this.getJsonFactory(), idTokenString);
        return this.verify(idToken) ? idToken : null;
    }
    
    @Deprecated
    public GoogleIdTokenVerifier loadPublicCerts() throws GeneralSecurityException, IOException {
        this.publicKeys.refresh();
        return this;
    }
    
    @Beta
    public static class Builder extends IdTokenVerifier.Builder
    {
        GooglePublicKeysManager publicKeys;
        
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory) {
            this(new GooglePublicKeysManager(transport, jsonFactory));
        }
        
        public Builder(final GooglePublicKeysManager publicKeys) {
            this.publicKeys = (GooglePublicKeysManager)Preconditions.checkNotNull((Object)publicKeys);
            this.setIssuers(Arrays.asList("accounts.google.com", "https://accounts.google.com"));
        }
        
        public GoogleIdTokenVerifier build() {
            return new GoogleIdTokenVerifier(this);
        }
        
        public final GooglePublicKeysManager getPublicCerts() {
            return this.publicKeys;
        }
        
        public final HttpTransport getTransport() {
            return this.publicKeys.getTransport();
        }
        
        public final JsonFactory getJsonFactory() {
            return this.publicKeys.getJsonFactory();
        }
        
        @Deprecated
        public final String getPublicCertsEncodedUrl() {
            return this.publicKeys.getPublicCertsEncodedUrl();
        }
        
        @Deprecated
        public Builder setPublicCertsEncodedUrl(final String publicKeysEncodedUrl) {
            this.publicKeys = new GooglePublicKeysManager.Builder(this.getTransport(), this.getJsonFactory()).setPublicCertsEncodedUrl(publicKeysEncodedUrl).setClock(this.publicKeys.getClock()).build();
            return this;
        }
        
        public Builder setIssuer(final String issuer) {
            return (Builder)super.setIssuer(issuer);
        }
        
        public Builder setIssuers(final Collection<String> issuers) {
            return (Builder)super.setIssuers((Collection)issuers);
        }
        
        public Builder setAudience(final Collection<String> audience) {
            return (Builder)super.setAudience((Collection)audience);
        }
        
        public Builder setAcceptableTimeSkewSeconds(final long acceptableTimeSkewSeconds) {
            return (Builder)super.setAcceptableTimeSkewSeconds(acceptableTimeSkewSeconds);
        }
        
        public Builder setClock(final Clock clock) {
            return (Builder)super.setClock(clock);
        }
    }
}
