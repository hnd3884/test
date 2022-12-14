package com.google.api.client.auth.oauth;

import com.google.api.client.http.HttpContent;
import com.google.api.client.util.Data;
import com.google.api.client.http.UrlEncodedContent;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import com.google.common.collect.SortedMultiset;
import java.util.Collection;
import java.util.Map;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.escape.PercentEscaper;
import java.security.SecureRandom;
import com.google.api.client.util.Beta;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpExecuteInterceptor;

@Beta
public final class OAuthParameters implements HttpExecuteInterceptor, HttpRequestInitializer
{
    private static final SecureRandom RANDOM;
    public OAuthSigner signer;
    public String callback;
    public String consumerKey;
    public String nonce;
    public String realm;
    public String signature;
    public String signatureMethod;
    public String timestamp;
    public String token;
    public String verifier;
    public String version;
    private static final PercentEscaper ESCAPER;
    
    public void computeNonce() {
        this.nonce = Long.toHexString(Math.abs(OAuthParameters.RANDOM.nextLong()));
    }
    
    public void computeTimestamp() {
        this.timestamp = Long.toString(System.currentTimeMillis() / 1000L);
    }
    
    public void computeSignature(final String requestMethod, final GenericUrl requestUrl) throws GeneralSecurityException {
        final OAuthSigner signer = this.signer;
        final String signatureMethod2 = signer.getSignatureMethod();
        this.signatureMethod = signatureMethod2;
        final String signatureMethod = signatureMethod2;
        final SortedMultiset<Parameter> parameters = (SortedMultiset<Parameter>)TreeMultiset.create();
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_callback", this.callback);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_consumer_key", this.consumerKey);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_nonce", this.nonce);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_signature_method", signatureMethod);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_timestamp", this.timestamp);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_token", this.token);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_verifier", this.verifier);
        this.putParameterIfValueNotNull((Multiset<Parameter>)parameters, "oauth_version", this.version);
        for (final Map.Entry<String, Object> fieldEntry : requestUrl.entrySet()) {
            final Object value = fieldEntry.getValue();
            if (value != null) {
                final String name = fieldEntry.getKey();
                if (value instanceof Collection) {
                    for (final Object repeatedValue : (Collection)value) {
                        this.putParameter((Multiset<Parameter>)parameters, name, repeatedValue);
                    }
                }
                else {
                    this.putParameter((Multiset<Parameter>)parameters, name, value);
                }
            }
        }
        final StringBuilder parametersBuf = new StringBuilder();
        boolean first = true;
        for (final Parameter parameter : parameters.elementSet()) {
            if (first) {
                first = false;
            }
            else {
                parametersBuf.append('&');
            }
            parametersBuf.append(parameter.getKey());
            final String value2 = parameter.getValue();
            if (value2 != null) {
                parametersBuf.append('=').append(value2);
            }
        }
        final String normalizedParameters = parametersBuf.toString();
        final GenericUrl normalized = new GenericUrl();
        final String scheme = requestUrl.getScheme();
        normalized.setScheme(scheme);
        normalized.setHost(requestUrl.getHost());
        normalized.setPathParts(requestUrl.getPathParts());
        int port = requestUrl.getPort();
        if (("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) {
            port = -1;
        }
        normalized.setPort(port);
        final String normalizedPath = normalized.build();
        final StringBuilder buf = new StringBuilder();
        buf.append(escape(requestMethod)).append('&');
        buf.append(escape(normalizedPath)).append('&');
        buf.append(escape(normalizedParameters));
        final String signatureBaseString = buf.toString();
        this.signature = signer.computeSignature(signatureBaseString);
    }
    
    public String getAuthorizationHeader() {
        final StringBuilder buf = new StringBuilder("OAuth");
        this.appendParameter(buf, "realm", this.realm);
        this.appendParameter(buf, "oauth_callback", this.callback);
        this.appendParameter(buf, "oauth_consumer_key", this.consumerKey);
        this.appendParameter(buf, "oauth_nonce", this.nonce);
        this.appendParameter(buf, "oauth_signature", this.signature);
        this.appendParameter(buf, "oauth_signature_method", this.signatureMethod);
        this.appendParameter(buf, "oauth_timestamp", this.timestamp);
        this.appendParameter(buf, "oauth_token", this.token);
        this.appendParameter(buf, "oauth_verifier", this.verifier);
        this.appendParameter(buf, "oauth_version", this.version);
        return buf.substring(0, buf.length() - 1);
    }
    
    private void appendParameter(final StringBuilder buf, final String name, final String value) {
        if (value != null) {
            buf.append(' ').append(escape(name)).append("=\"").append(escape(value)).append("\",");
        }
    }
    
    private void putParameterIfValueNotNull(final Multiset<Parameter> parameters, final String key, final String value) {
        if (value != null) {
            this.putParameter(parameters, key, value);
        }
    }
    
    private void putParameter(final Multiset<Parameter> parameters, final String key, final Object value) {
        parameters.add((Object)new Parameter(escape(key), (value == null) ? null : escape(value.toString())));
    }
    
    public static String escape(final String value) {
        return OAuthParameters.ESCAPER.escape(value);
    }
    
    public void initialize(final HttpRequest request) throws IOException {
        request.setInterceptor((HttpExecuteInterceptor)this);
    }
    
    public void intercept(final HttpRequest request) throws IOException {
        this.computeNonce();
        this.computeTimestamp();
        try {
            final GenericUrl url = request.getUrl();
            final HttpContent content = request.getContent();
            Map<String, Object> urlEncodedParams = null;
            if (content instanceof UrlEncodedContent) {
                urlEncodedParams = Data.mapOf(((UrlEncodedContent)content).getData());
                url.putAll((Map)urlEncodedParams);
            }
            this.computeSignature(request.getRequestMethod(), url);
            if (urlEncodedParams != null) {
                for (final Map.Entry<String, Object> entry : urlEncodedParams.entrySet()) {
                    url.remove((Object)entry.getKey());
                }
            }
        }
        catch (final GeneralSecurityException e) {
            final IOException io = new IOException();
            io.initCause(e);
            throw io;
        }
        request.getHeaders().setAuthorization(this.getAuthorizationHeader());
    }
    
    static {
        RANDOM = new SecureRandom();
        ESCAPER = new PercentEscaper("-_.~");
    }
    
    private static class Parameter implements Comparable<Parameter>
    {
        private final String key;
        private final String value;
        
        public Parameter(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getValue() {
            return this.value;
        }
        
        @Override
        public int compareTo(final Parameter p) {
            final int result = this.key.compareTo(p.key);
            return (result == 0) ? this.value.compareTo(p.value) : result;
        }
    }
}
