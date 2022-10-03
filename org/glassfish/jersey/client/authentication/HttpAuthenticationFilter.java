package org.glassfish.jersey.client.authentication;

import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.Iterator;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.net.URI;
import javax.ws.rs.client.Client;
import java.io.InputStream;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.ClientResponseContext;
import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import org.glassfish.jersey.client.ClientProperties;
import java.util.Collections;
import java.util.LinkedHashMap;
import javax.ws.rs.core.Configuration;
import java.util.Map;
import java.nio.charset.Charset;
import javax.annotation.Priority;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.ClientRequestFilter;

@Priority(1000)
class HttpAuthenticationFilter implements ClientRequestFilter, ClientResponseFilter
{
    private static final String REQUEST_PROPERTY_FILTER_REUSED = "org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused";
    private static final String REQUEST_PROPERTY_OPERATION = "org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation";
    static final Charset CHARACTER_SET;
    private final HttpAuthenticationFeature.Mode mode;
    private final Map<String, Type> uriCache;
    private final DigestAuthenticator digestAuth;
    private final BasicAuthenticator basicAuth;
    private static final int MAXIMUM_DIGEST_CACHE_SIZE = 10000;
    
    HttpAuthenticationFilter(final HttpAuthenticationFeature.Mode mode, final Credentials basicCredentials, final Credentials digestCredentials, final Configuration configuration) {
        final int limit = this.getMaximumCacheLimit(configuration);
        final int uriCacheLimit = limit * 2;
        this.uriCache = Collections.synchronizedMap((Map<String, Type>)new LinkedHashMap<String, Type>(uriCacheLimit) {
            private static final long serialVersionUID = 1946245645415625L;
            
            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, Type> eldest) {
                return this.size() > uriCacheLimit;
            }
        });
        this.mode = mode;
        switch (mode) {
            case BASIC_PREEMPTIVE:
            case BASIC_NON_PREEMPTIVE: {
                this.basicAuth = new BasicAuthenticator(basicCredentials);
                this.digestAuth = null;
                break;
            }
            case DIGEST: {
                this.basicAuth = null;
                this.digestAuth = new DigestAuthenticator(digestCredentials, limit);
                break;
            }
            case UNIVERSAL: {
                this.basicAuth = new BasicAuthenticator(basicCredentials);
                this.digestAuth = new DigestAuthenticator(digestCredentials, limit);
                break;
            }
            default: {
                throw new IllegalStateException("Not implemented.");
            }
        }
    }
    
    private int getMaximumCacheLimit(final Configuration configuration) {
        int limit = ClientProperties.getValue(configuration.getProperties(), "jersey.config.client.digestAuthUriCacheSizeLimit", 10000);
        if (limit < 1) {
            limit = 10000;
        }
        return limit;
    }
    
    public void filter(final ClientRequestContext request) throws IOException {
        if ("true".equals(request.getProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused"))) {
            return;
        }
        if (request.getHeaders().containsKey((Object)"Authorization")) {
            return;
        }
        Type operation = null;
        if (this.mode == HttpAuthenticationFeature.Mode.BASIC_PREEMPTIVE) {
            this.basicAuth.filterRequest(request);
            operation = Type.BASIC;
        }
        else if (this.mode != HttpAuthenticationFeature.Mode.BASIC_NON_PREEMPTIVE) {
            if (this.mode == HttpAuthenticationFeature.Mode.DIGEST) {
                if (this.digestAuth.filterRequest(request)) {
                    operation = Type.DIGEST;
                }
            }
            else if (this.mode == HttpAuthenticationFeature.Mode.UNIVERSAL) {
                final Type lastSuccessfulMethod = this.uriCache.get(this.getCacheKey(request));
                if (lastSuccessfulMethod != null) {
                    request.setProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation", (Object)lastSuccessfulMethod);
                    if (lastSuccessfulMethod == Type.BASIC) {
                        this.basicAuth.filterRequest(request);
                        operation = Type.BASIC;
                    }
                    else if (lastSuccessfulMethod == Type.DIGEST && this.digestAuth.filterRequest(request)) {
                        operation = Type.DIGEST;
                    }
                }
            }
        }
        if (operation != null) {
            request.setProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation", (Object)operation);
        }
    }
    
    public void filter(final ClientRequestContext request, final ClientResponseContext response) throws IOException {
        if ("true".equals(request.getProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused"))) {
            return;
        }
        Type result = null;
        boolean authenticate;
        if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            final String authString = (String)response.getHeaders().getFirst((Object)"WWW-Authenticate");
            if (authString != null) {
                final String upperCaseAuth = authString.trim().toUpperCase();
                if (upperCaseAuth.startsWith("BASIC")) {
                    result = Type.BASIC;
                }
                else {
                    if (!upperCaseAuth.startsWith("DIGEST")) {
                        return;
                    }
                    result = Type.DIGEST;
                }
            }
            authenticate = true;
        }
        else {
            authenticate = false;
        }
        if (this.mode != HttpAuthenticationFeature.Mode.BASIC_PREEMPTIVE) {
            if (this.mode == HttpAuthenticationFeature.Mode.BASIC_NON_PREEMPTIVE) {
                if (authenticate && result == Type.BASIC) {
                    this.basicAuth.filterResponseAndAuthenticate(request, response);
                }
            }
            else if (this.mode == HttpAuthenticationFeature.Mode.DIGEST) {
                if (authenticate && result == Type.DIGEST) {
                    this.digestAuth.filterResponse(request, response);
                }
            }
            else if (this.mode == HttpAuthenticationFeature.Mode.UNIVERSAL) {
                final Type operation = (Type)request.getProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation");
                if (operation != null) {
                    this.updateCache(request, !authenticate, operation);
                }
                if (authenticate) {
                    boolean success = false;
                    if (result == Type.BASIC) {
                        success = this.basicAuth.filterResponseAndAuthenticate(request, response);
                    }
                    else if (result == Type.DIGEST) {
                        success = this.digestAuth.filterResponse(request, response);
                    }
                    this.updateCache(request, success, result);
                }
            }
        }
    }
    
    private String getCacheKey(final ClientRequestContext request) {
        return request.getUri().toString() + ":" + request.getMethod();
    }
    
    private void updateCache(final ClientRequestContext request, final boolean success, final Type operation) {
        final String cacheKey = this.getCacheKey(request);
        if (success) {
            this.uriCache.put(cacheKey, operation);
        }
        else {
            this.uriCache.remove(cacheKey);
        }
    }
    
    static boolean repeatRequest(final ClientRequestContext request, final ClientResponseContext response, final String newAuthorizationHeader) {
        final Client client = request.getClient();
        final String method = request.getMethod();
        final MediaType mediaType = request.getMediaType();
        final URI lUri = request.getUri();
        final WebTarget resourceTarget = client.target(lUri);
        final Invocation.Builder builder = resourceTarget.request(new MediaType[] { mediaType });
        final MultivaluedMap<String, Object> newHeaders = (MultivaluedMap<String, Object>)new MultivaluedHashMap();
        for (final Map.Entry<String, List<Object>> entry : request.getHeaders().entrySet()) {
            if ("Authorization".equals(entry.getKey())) {
                continue;
            }
            newHeaders.put((Object)entry.getKey(), (Object)entry.getValue());
        }
        newHeaders.add((Object)"Authorization", (Object)newAuthorizationHeader);
        builder.headers((MultivaluedMap)newHeaders);
        builder.property("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused", (Object)"true");
        Invocation invocation;
        if (request.getEntity() == null) {
            invocation = builder.build(method);
        }
        else {
            invocation = builder.build(method, Entity.entity(request.getEntity(), request.getMediaType()));
        }
        final Response nextResponse = invocation.invoke();
        if (nextResponse.hasEntity()) {
            response.setEntityStream((InputStream)nextResponse.readEntity((Class)InputStream.class));
        }
        final MultivaluedMap<String, String> headers = (MultivaluedMap<String, String>)response.getHeaders();
        headers.clear();
        headers.putAll((Map)nextResponse.getStringHeaders());
        response.setStatus(nextResponse.getStatus());
        return response.getStatus() != Response.Status.UNAUTHORIZED.getStatusCode();
    }
    
    private static Credentials extractCredentials(final ClientRequestContext request, final Type type) {
        String usernameKey = null;
        String passwordKey = null;
        if (type == null) {
            usernameKey = "jersey.config.client.http.auth.username";
            passwordKey = "jersey.config.client.http.auth.password";
        }
        else if (type == Type.BASIC) {
            usernameKey = "jersey.config.client.http.auth.basic.username";
            passwordKey = "jersey.config.client.http.auth.basic.password";
        }
        else if (type == Type.DIGEST) {
            usernameKey = "jersey.config.client.http.auth.digest.username";
            passwordKey = "jersey.config.client.http.auth.digest.password";
        }
        final String userName = (String)request.getProperty(usernameKey);
        if (userName != null && !userName.equals("")) {
            final Object password = request.getProperty(passwordKey);
            byte[] pwdBytes;
            if (password instanceof byte[]) {
                pwdBytes = (byte[])password;
            }
            else {
                if (!(password instanceof String)) {
                    throw new RequestAuthenticationException(LocalizationMessages.AUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED());
                }
                pwdBytes = ((String)password).getBytes(HttpAuthenticationFilter.CHARACTER_SET);
            }
            return new Credentials(userName, pwdBytes);
        }
        return null;
    }
    
    static Credentials getCredentials(final ClientRequestContext request, final Credentials defaultCredentials, final Type type) {
        final Credentials commonCredentials = extractCredentials(request, type);
        if (commonCredentials != null) {
            return commonCredentials;
        }
        final Credentials specificCredentials = extractCredentials(request, null);
        return (specificCredentials != null) ? specificCredentials : defaultCredentials;
    }
    
    static {
        CHARACTER_SET = Charset.forName("iso-8859-1");
    }
    
    enum Type
    {
        BASIC, 
        DIGEST;
    }
    
    static class Credentials
    {
        private final String username;
        private final byte[] password;
        
        Credentials(final String username, final byte[] password) {
            this.username = username;
            this.password = password;
        }
        
        Credentials(final String username, final String password) {
            this.username = username;
            this.password = (byte[])((password == null) ? null : password.getBytes(HttpAuthenticationFilter.CHARACTER_SET));
        }
        
        String getUsername() {
            return this.username;
        }
        
        byte[] getPassword() {
            return this.password;
        }
    }
}
