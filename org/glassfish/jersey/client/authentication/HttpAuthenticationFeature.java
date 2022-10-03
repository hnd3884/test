package org.glassfish.jersey.client.authentication;

import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public class HttpAuthenticationFeature implements Feature
{
    public static final String HTTP_AUTHENTICATION_USERNAME = "jersey.config.client.http.auth.username";
    public static final String HTTP_AUTHENTICATION_PASSWORD = "jersey.config.client.http.auth.password";
    public static final String HTTP_AUTHENTICATION_BASIC_USERNAME = "jersey.config.client.http.auth.basic.username";
    public static final String HTTP_AUTHENTICATION_BASIC_PASSWORD = "jersey.config.client.http.auth.basic.password";
    public static final String HTTP_AUTHENTICATION_DIGEST_USERNAME = "jersey.config.client.http.auth.digest.username";
    public static final String HTTP_AUTHENTICATION_DIGEST_PASSWORD = "jersey.config.client.http.auth.digest.password";
    private final Mode mode;
    private final HttpAuthenticationFilter.Credentials basicCredentials;
    private final HttpAuthenticationFilter.Credentials digestCredentials;
    
    public static BasicBuilder basicBuilder() {
        return new BuilderImpl(Mode.BASIC_PREEMPTIVE);
    }
    
    public static HttpAuthenticationFeature basic(final String username, final byte[] password) {
        return build(Mode.BASIC_PREEMPTIVE, username, password);
    }
    
    public static HttpAuthenticationFeature basic(final String username, final String password) {
        return build(Mode.BASIC_PREEMPTIVE, username, password);
    }
    
    public static HttpAuthenticationFeature digest() {
        return build(Mode.DIGEST);
    }
    
    public static HttpAuthenticationFeature digest(final String username, final byte[] password) {
        return build(Mode.DIGEST, username, password);
    }
    
    public static HttpAuthenticationFeature digest(final String username, final String password) {
        return build(Mode.DIGEST, username, password);
    }
    
    public static UniversalBuilder universalBuilder() {
        return new BuilderImpl(Mode.UNIVERSAL);
    }
    
    public static HttpAuthenticationFeature universal(final String username, final byte[] password) {
        return build(Mode.UNIVERSAL, username, password);
    }
    
    public static HttpAuthenticationFeature universal(final String username, final String password) {
        return build(Mode.UNIVERSAL, username, password);
    }
    
    private static HttpAuthenticationFeature build(final Mode mode) {
        return new BuilderImpl(mode).build();
    }
    
    private static HttpAuthenticationFeature build(final Mode mode, final String username, final byte[] password) {
        return new BuilderImpl(mode).credentials(username, password).build();
    }
    
    private static HttpAuthenticationFeature build(final Mode mode, final String username, final String password) {
        return new BuilderImpl(mode).credentials(username, password).build();
    }
    
    private HttpAuthenticationFeature(final Mode mode, final HttpAuthenticationFilter.Credentials basicCredentials, final HttpAuthenticationFilter.Credentials digestCredentials) {
        this.mode = mode;
        this.basicCredentials = basicCredentials;
        this.digestCredentials = digestCredentials;
    }
    
    public boolean configure(final FeatureContext context) {
        context.register((Object)new HttpAuthenticationFilter(this.mode, this.basicCredentials, this.digestCredentials, context.getConfiguration()));
        return true;
    }
    
    enum Mode
    {
        BASIC_PREEMPTIVE, 
        BASIC_NON_PREEMPTIVE, 
        DIGEST, 
        UNIVERSAL;
    }
    
    static class BuilderImpl implements UniversalBuilder, BasicBuilder
    {
        private String usernameBasic;
        private byte[] passwordBasic;
        private String usernameDigest;
        private byte[] passwordDigest;
        private Mode mode;
        
        public BuilderImpl(final Mode mode) {
            this.mode = mode;
        }
        
        @Override
        public Builder credentials(final String username, final String password) {
            return this.credentials(username, (byte[])((password == null) ? null : password.getBytes(HttpAuthenticationFilter.CHARACTER_SET)));
        }
        
        @Override
        public Builder credentials(final String username, final byte[] password) {
            this.credentialsForBasic(username, password);
            this.credentialsForDigest(username, password);
            return this;
        }
        
        @Override
        public UniversalBuilder credentialsForBasic(final String username, final String password) {
            return this.credentialsForBasic(username, (byte[])((password == null) ? null : password.getBytes(HttpAuthenticationFilter.CHARACTER_SET)));
        }
        
        @Override
        public UniversalBuilder credentialsForBasic(final String username, final byte[] password) {
            this.usernameBasic = username;
            this.passwordBasic = password;
            return this;
        }
        
        @Override
        public UniversalBuilder credentialsForDigest(final String username, final String password) {
            return this.credentialsForDigest(username, (byte[])((password == null) ? null : password.getBytes(HttpAuthenticationFilter.CHARACTER_SET)));
        }
        
        @Override
        public UniversalBuilder credentialsForDigest(final String username, final byte[] password) {
            this.usernameDigest = username;
            this.passwordDigest = password;
            return this;
        }
        
        @Override
        public HttpAuthenticationFeature build() {
            return new HttpAuthenticationFeature(this.mode, (this.usernameBasic == null) ? null : new HttpAuthenticationFilter.Credentials(this.usernameBasic, this.passwordBasic), (this.usernameDigest == null) ? null : new HttpAuthenticationFilter.Credentials(this.usernameDigest, this.passwordDigest), null);
        }
        
        @Override
        public BasicBuilder nonPreemptive() {
            if (this.mode == Mode.BASIC_PREEMPTIVE) {
                this.mode = Mode.BASIC_NON_PREEMPTIVE;
            }
            return this;
        }
    }
    
    public interface UniversalBuilder extends Builder
    {
        UniversalBuilder credentialsForBasic(final String p0, final String p1);
        
        UniversalBuilder credentialsForBasic(final String p0, final byte[] p1);
        
        UniversalBuilder credentialsForDigest(final String p0, final String p1);
        
        UniversalBuilder credentialsForDigest(final String p0, final byte[] p1);
    }
    
    public interface Builder
    {
        Builder credentials(final String p0, final byte[] p1);
        
        Builder credentials(final String p0, final String p1);
        
        HttpAuthenticationFeature build();
    }
    
    public interface BasicBuilder extends Builder
    {
        BasicBuilder nonPreemptive();
    }
}
