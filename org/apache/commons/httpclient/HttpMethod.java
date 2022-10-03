package org.apache.commons.httpclient;

import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.params.HttpMethodParams;
import java.io.InputStream;
import java.io.IOException;

public interface HttpMethod
{
    String getName();
    
    HostConfiguration getHostConfiguration();
    
    void setPath(final String p0);
    
    String getPath();
    
    URI getURI() throws URIException;
    
    void setURI(final URI p0) throws URIException;
    
    void setStrictMode(final boolean p0);
    
    boolean isStrictMode();
    
    void setRequestHeader(final String p0, final String p1);
    
    void setRequestHeader(final Header p0);
    
    void addRequestHeader(final String p0, final String p1);
    
    void addRequestHeader(final Header p0);
    
    Header getRequestHeader(final String p0);
    
    void removeRequestHeader(final String p0);
    
    void removeRequestHeader(final Header p0);
    
    boolean getFollowRedirects();
    
    void setFollowRedirects(final boolean p0);
    
    void setQueryString(final String p0);
    
    void setQueryString(final NameValuePair[] p0);
    
    String getQueryString();
    
    Header[] getRequestHeaders();
    
    Header[] getRequestHeaders(final String p0);
    
    boolean validate();
    
    int getStatusCode();
    
    String getStatusText();
    
    Header[] getResponseHeaders();
    
    Header getResponseHeader(final String p0);
    
    Header[] getResponseHeaders(final String p0);
    
    Header[] getResponseFooters();
    
    Header getResponseFooter(final String p0);
    
    byte[] getResponseBody() throws IOException;
    
    String getResponseBodyAsString() throws IOException;
    
    InputStream getResponseBodyAsStream() throws IOException;
    
    boolean hasBeenUsed();
    
    int execute(final HttpState p0, final HttpConnection p1) throws HttpException, IOException;
    
    void abort();
    
    void recycle();
    
    void releaseConnection();
    
    void addResponseFooter(final Header p0);
    
    StatusLine getStatusLine();
    
    boolean getDoAuthentication();
    
    void setDoAuthentication(final boolean p0);
    
    HttpMethodParams getParams();
    
    void setParams(final HttpMethodParams p0);
    
    AuthState getHostAuthState();
    
    AuthState getProxyAuthState();
    
    boolean isRequestSent();
}
