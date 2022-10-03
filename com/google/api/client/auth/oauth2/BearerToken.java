package com.google.api.client.auth.oauth2;

import com.google.api.client.util.Data;
import com.google.api.client.http.UrlEncodedContent;
import java.util.Map;
import com.google.api.client.util.Preconditions;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import java.util.regex.Pattern;

public class BearerToken
{
    static final String PARAM_NAME = "access_token";
    static final Pattern INVALID_TOKEN_ERROR;
    
    public static Credential.AccessMethod authorizationHeaderAccessMethod() {
        return new AuthorizationHeaderAccessMethod();
    }
    
    public static Credential.AccessMethod formEncodedBodyAccessMethod() {
        return new FormEncodedBodyAccessMethod();
    }
    
    public static Credential.AccessMethod queryParameterAccessMethod() {
        return new QueryParameterAccessMethod();
    }
    
    static {
        INVALID_TOKEN_ERROR = Pattern.compile("\\s*error\\s*=\\s*\"?invalid_token\"?");
    }
    
    static final class AuthorizationHeaderAccessMethod implements Credential.AccessMethod
    {
        static final String HEADER_PREFIX = "Bearer ";
        
        @Override
        public void intercept(final HttpRequest request, final String accessToken) throws IOException {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        }
        
        @Override
        public String getAccessTokenFromRequest(final HttpRequest request) {
            final List<String> authorizationAsList = request.getHeaders().getAuthorizationAsList();
            if (authorizationAsList != null) {
                for (final String header : authorizationAsList) {
                    if (header.startsWith("Bearer ")) {
                        return header.substring("Bearer ".length());
                    }
                }
            }
            return null;
        }
    }
    
    static final class FormEncodedBodyAccessMethod implements Credential.AccessMethod
    {
        @Override
        public void intercept(final HttpRequest request, final String accessToken) throws IOException {
            Preconditions.checkArgument(!"GET".equals(request.getRequestMethod()), (Object)"HTTP GET method is not supported");
            getData(request).put("access_token", accessToken);
        }
        
        @Override
        public String getAccessTokenFromRequest(final HttpRequest request) {
            final Object bodyParam = getData(request).get("access_token");
            return (bodyParam == null) ? null : bodyParam.toString();
        }
        
        private static Map<String, Object> getData(final HttpRequest request) {
            return Data.mapOf(UrlEncodedContent.getContent(request).getData());
        }
    }
    
    static final class QueryParameterAccessMethod implements Credential.AccessMethod
    {
        @Override
        public void intercept(final HttpRequest request, final String accessToken) throws IOException {
            request.getUrl().set("access_token", (Object)accessToken);
        }
        
        @Override
        public String getAccessTokenFromRequest(final HttpRequest request) {
            final Object param = request.getUrl().get((Object)"access_token");
            return (param == null) ? null : param.toString();
        }
    }
}
