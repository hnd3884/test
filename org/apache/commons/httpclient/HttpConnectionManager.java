package org.apache.commons.httpclient;

import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public interface HttpConnectionManager
{
    HttpConnection getConnection(final HostConfiguration p0);
    
    HttpConnection getConnection(final HostConfiguration p0, final long p1) throws HttpException;
    
    HttpConnection getConnectionWithTimeout(final HostConfiguration p0, final long p1) throws ConnectionPoolTimeoutException;
    
    void releaseConnection(final HttpConnection p0);
    
    void closeIdleConnections(final long p0);
    
    HttpConnectionManagerParams getParams();
    
    void setParams(final HttpConnectionManagerParams p0);
}
