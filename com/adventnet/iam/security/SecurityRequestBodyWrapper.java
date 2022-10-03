package com.adventnet.iam.security;

import java.io.StringReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.servlet.ServletInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SecurityRequestBodyWrapper extends SecurityRequestWrapper
{
    private static final Logger LOGGER;
    private byte[] cachedRequestBody;
    private boolean requestBodyRead;
    private Map<String, List<String>> formURLEncodedPostParams;
    
    SecurityRequestBodyWrapper(final HttpServletRequest request) {
        super(request);
        this.requestBodyRead = false;
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            this.cacheRequestBody(request);
            this.requestBodyRead = true;
            this.parseRequestBody();
            request.setAttribute("ZSEC_REQUEST_BODY_READ_FOR_URL_VERIFICATION", (Object)true);
        }
        this.init();
    }
    
    private void cacheRequestBody(final HttpServletRequest request) {
        InputStream in = null;
        try {
            in = (InputStream)request.getInputStream();
        }
        catch (final IOException io) {
            SecurityRequestBodyWrapper.LOGGER.log(Level.WARNING, "Exception occured while reading input stream, request uri : {0}, reason {1}", new Object[] { request.getRequestURI(), io.getMessage() });
            throw new IAMSecurityException("UNABLE_TO_READ_INPUTSTREAM");
        }
        this.cachedRequestBody = SecurityUtil.convertInputStreamAsByteArray(in, -1L);
    }
    
    private void parseRequestBody() {
        if (this.cachedRequestBody != null) {
            String requestBodyAsString = null;
            try {
                requestBodyAsString = new String(this.cachedRequestBody, this.characterEncoding).trim();
            }
            catch (final UnsupportedEncodingException enEx) {
                SecurityRequestBodyWrapper.LOGGER.log(Level.SEVERE, "Unable to convert the request body to string due to unsupported character encoding. Request URI :{0}, ExMsg : {1}", new Object[] { this.getRequestURI(), enEx.getMessage() });
                throw new IAMSecurityException("UNABLE_TO_READ_INPUTSTREAM");
            }
            if (SecurityUtil.isValid(requestBodyAsString)) {
                this.formURLEncodedPostParams = new HashMap<String, List<String>>();
                final String[] params = requestBodyAsString.split("&");
                for (int i = 0; i < params.length; ++i) {
                    final String[] paramNameValuePair = params[i].split("=", 2);
                    final String paramName = SecurityUtil.decode(paramNameValuePair[0], this.characterEncoding);
                    final String paramValue = (paramNameValuePair.length == 2) ? SecurityUtil.decode(paramNameValuePair[1], this.characterEncoding) : "";
                    if (paramName != null && paramValue != null) {
                        if (this.formURLEncodedPostParams.containsKey(paramName)) {
                            this.formURLEncodedPostParams.get(paramName).add(paramValue);
                        }
                        else {
                            final List<String> values = new ArrayList<String>();
                            values.add(paramValue);
                            this.formURLEncodedPostParams.put(paramName, values);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected final String getParameterForValidation(final String paramName) {
        final String paramValue = super.getParameterForValidation(paramName);
        if (paramValue != null) {
            return paramValue;
        }
        if (this.formURLEncodedPostParams != null && this.formURLEncodedPostParams.containsKey(paramName)) {
            return this.formURLEncodedPostParams.get(paramName).get(0);
        }
        return null;
    }
    
    @Override
    protected final String[] getParameterValuesForValidation(final String paramName) {
        final String[] paramValues = super.getParameterValuesForValidation(paramName);
        if (paramValues != null) {
            return paramValues;
        }
        if (this.formURLEncodedPostParams != null && this.formURLEncodedPostParams.containsKey(paramName)) {
            return (String[])this.formURLEncodedPostParams.get(paramName).toArray(new String[0]);
        }
        return null;
    }
    
    @Override
    protected String[] getParameterValuesForLogging(final String paramName) {
        final String[] paramValues = super.getParameterValuesForLogging(paramName);
        if (paramValues != null) {
            return paramValues;
        }
        if (this.formURLEncodedPostParams != null && this.formURLEncodedPostParams.containsKey(paramName)) {
            return (String[])this.formURLEncodedPostParams.get(paramName).toArray(new String[0]);
        }
        return null;
    }
    
    @Override
    public Enumeration<String> getParameterNames() {
        final Enumeration<String> requestParams = super.getParameterNames();
        final ArrayList<String> list = Collections.list(requestParams);
        if (this.formURLEncodedPostParams != null) {
            list.addAll(this.formURLEncodedPostParams.keySet());
        }
        return Collections.enumeration(list);
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.requestBodyRead && this.locked) {
            return new CachedServletInputStream(this.cachedRequestBody);
        }
        return new CachedServletInputStream("", this.characterEncoding);
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        if (this.requestBodyRead && this.locked) {
            return new BufferedReader(new InputStreamReader((InputStream)new CachedServletInputStream(this.cachedRequestBody)));
        }
        return new BufferedReader(new StringReader(""));
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityRequestBodyWrapper.class.getName());
    }
}
