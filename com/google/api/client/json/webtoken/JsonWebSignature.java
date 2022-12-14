package com.google.api.client.json.webtoken;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.google.api.client.util.GenericData;
import com.google.api.client.json.GenericJson;
import java.util.Collection;
import java.util.ArrayList;
import com.google.api.client.util.Key;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.Base64;
import java.security.PrivateKey;
import java.io.IOException;
import com.google.api.client.json.JsonFactory;
import java.util.Arrays;
import javax.net.ssl.TrustManager;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import com.google.api.client.util.Beta;
import java.util.List;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import com.google.api.client.util.SecurityUtils;
import java.security.PublicKey;
import com.google.api.client.util.Preconditions;

public class JsonWebSignature extends JsonWebToken
{
    private final byte[] signatureBytes;
    private final byte[] signedContentBytes;
    
    public JsonWebSignature(final Header header, final Payload payload, final byte[] signatureBytes, final byte[] signedContentBytes) {
        super(header, payload);
        this.signatureBytes = Preconditions.checkNotNull(signatureBytes);
        this.signedContentBytes = Preconditions.checkNotNull(signedContentBytes);
    }
    
    @Override
    public Header getHeader() {
        return (Header)super.getHeader();
    }
    
    public final boolean verifySignature(final PublicKey publicKey) throws GeneralSecurityException {
        final String algorithm = this.getHeader().getAlgorithm();
        if ("RS256".equals(algorithm)) {
            return SecurityUtils.verify(SecurityUtils.getSha256WithRsaSignatureAlgorithm(), publicKey, this.signatureBytes, this.signedContentBytes);
        }
        return "ES256".equals(algorithm) && SecurityUtils.verify(SecurityUtils.getEs256SignatureAlgorithm(), publicKey, DerEncoder.encode(this.signatureBytes), this.signedContentBytes);
    }
    
    @Beta
    public final X509Certificate verifySignature(final X509TrustManager trustManager) throws GeneralSecurityException {
        final List<String> x509Certificates = this.getHeader().getX509Certificates();
        if (x509Certificates == null || x509Certificates.isEmpty()) {
            return null;
        }
        final String algorithm = this.getHeader().getAlgorithm();
        if ("RS256".equals(algorithm)) {
            return SecurityUtils.verify(SecurityUtils.getSha256WithRsaSignatureAlgorithm(), trustManager, x509Certificates, this.signatureBytes, this.signedContentBytes);
        }
        if ("ES256".equals(algorithm)) {
            return SecurityUtils.verify(SecurityUtils.getEs256SignatureAlgorithm(), trustManager, x509Certificates, DerEncoder.encode(this.signatureBytes), this.signedContentBytes);
        }
        return null;
    }
    
    @Beta
    public final X509Certificate verifySignature() throws GeneralSecurityException {
        final X509TrustManager trustManager = getDefaultX509TrustManager();
        if (trustManager == null) {
            return null;
        }
        return this.verifySignature(trustManager);
    }
    
    private static X509TrustManager getDefaultX509TrustManager() {
        try {
            final TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore)null);
            for (final TrustManager manager : factory.getTrustManagers()) {
                if (manager instanceof X509TrustManager) {
                    return (X509TrustManager)manager;
                }
            }
            return null;
        }
        catch (final NoSuchAlgorithmException e) {
            return null;
        }
        catch (final KeyStoreException e2) {
            return null;
        }
    }
    
    public final byte[] getSignatureBytes() {
        return Arrays.copyOf(this.signatureBytes, this.signatureBytes.length);
    }
    
    public final byte[] getSignedContentBytes() {
        return Arrays.copyOf(this.signedContentBytes, this.signedContentBytes.length);
    }
    
    public static JsonWebSignature parse(final JsonFactory jsonFactory, final String tokenString) throws IOException {
        return parser(jsonFactory).parse(tokenString);
    }
    
    public static Parser parser(final JsonFactory jsonFactory) {
        return new Parser(jsonFactory);
    }
    
    public static String signUsingRsaSha256(final PrivateKey privateKey, final JsonFactory jsonFactory, final Header header, final Payload payload) throws GeneralSecurityException, IOException {
        final String content = Base64.encodeBase64URLSafeString(jsonFactory.toByteArray(header)) + "." + Base64.encodeBase64URLSafeString(jsonFactory.toByteArray(payload));
        final byte[] contentBytes = StringUtils.getBytesUtf8(content);
        final byte[] signature = SecurityUtils.sign(SecurityUtils.getSha256WithRsaSignatureAlgorithm(), privateKey, contentBytes);
        return content + "." + Base64.encodeBase64URLSafeString(signature);
    }
    
    public static class Header extends JsonWebToken.Header
    {
        @Key("alg")
        private String algorithm;
        @Key("jku")
        private String jwkUrl;
        @Key("jwk")
        private String jwk;
        @Key("kid")
        private String keyId;
        @Key("x5u")
        private String x509Url;
        @Key("x5t")
        private String x509Thumbprint;
        @Key("x5c")
        private ArrayList<String> x509Certificates;
        @Key("crit")
        private List<String> critical;
        
        @Override
        public Header setType(final String type) {
            super.setType(type);
            return this;
        }
        
        public final String getAlgorithm() {
            return this.algorithm;
        }
        
        public Header setAlgorithm(final String algorithm) {
            this.algorithm = algorithm;
            return this;
        }
        
        public final String getJwkUrl() {
            return this.jwkUrl;
        }
        
        public Header setJwkUrl(final String jwkUrl) {
            this.jwkUrl = jwkUrl;
            return this;
        }
        
        public final String getJwk() {
            return this.jwk;
        }
        
        public Header setJwk(final String jwk) {
            this.jwk = jwk;
            return this;
        }
        
        public final String getKeyId() {
            return this.keyId;
        }
        
        public Header setKeyId(final String keyId) {
            this.keyId = keyId;
            return this;
        }
        
        public final String getX509Url() {
            return this.x509Url;
        }
        
        public Header setX509Url(final String x509Url) {
            this.x509Url = x509Url;
            return this;
        }
        
        public final String getX509Thumbprint() {
            return this.x509Thumbprint;
        }
        
        public Header setX509Thumbprint(final String x509Thumbprint) {
            this.x509Thumbprint = x509Thumbprint;
            return this;
        }
        
        public final List<String> getX509Certificates() {
            return new ArrayList<String>(this.x509Certificates);
        }
        
        public Header setX509Certificates(final List<String> x509Certificates) {
            this.x509Certificates = new ArrayList<String>(x509Certificates);
            return this;
        }
        
        public final List<String> getCritical() {
            if (this.critical == null || this.critical.isEmpty()) {
                return null;
            }
            return new ArrayList<String>(this.critical);
        }
        
        public Header setCritical(final List<String> critical) {
            this.critical = new ArrayList<String>(critical);
            return this;
        }
        
        @Override
        public Header set(final String fieldName, final Object value) {
            return (Header)super.set(fieldName, value);
        }
        
        @Override
        public Header clone() {
            return (Header)super.clone();
        }
    }
    
    public static final class Parser
    {
        private final JsonFactory jsonFactory;
        private Class<? extends Header> headerClass;
        private Class<? extends Payload> payloadClass;
        
        public Parser(final JsonFactory jsonFactory) {
            this.headerClass = Header.class;
            this.payloadClass = Payload.class;
            this.jsonFactory = Preconditions.checkNotNull(jsonFactory);
        }
        
        public Class<? extends Header> getHeaderClass() {
            return this.headerClass;
        }
        
        public Parser setHeaderClass(final Class<? extends Header> headerClass) {
            this.headerClass = headerClass;
            return this;
        }
        
        public Class<? extends Payload> getPayloadClass() {
            return this.payloadClass;
        }
        
        public Parser setPayloadClass(final Class<? extends Payload> payloadClass) {
            this.payloadClass = payloadClass;
            return this;
        }
        
        public JsonFactory getJsonFactory() {
            return this.jsonFactory;
        }
        
        public JsonWebSignature parse(final String tokenString) throws IOException {
            final int firstDot = tokenString.indexOf(46);
            Preconditions.checkArgument(firstDot != -1);
            final byte[] headerBytes = Base64.decodeBase64(tokenString.substring(0, firstDot));
            final int secondDot = tokenString.indexOf(46, firstDot + 1);
            Preconditions.checkArgument(secondDot != -1);
            Preconditions.checkArgument(tokenString.indexOf(46, secondDot + 1) == -1);
            final byte[] payloadBytes = Base64.decodeBase64(tokenString.substring(firstDot + 1, secondDot));
            final byte[] signatureBytes = Base64.decodeBase64(tokenString.substring(secondDot + 1));
            final byte[] signedContentBytes = StringUtils.getBytesUtf8(tokenString.substring(0, secondDot));
            final Header header = this.jsonFactory.fromInputStream(new ByteArrayInputStream(headerBytes), this.headerClass);
            Preconditions.checkArgument(header.getAlgorithm() != null);
            final Payload payload = this.jsonFactory.fromInputStream(new ByteArrayInputStream(payloadBytes), this.payloadClass);
            return new JsonWebSignature(header, payload, signatureBytes, signedContentBytes);
        }
    }
}
