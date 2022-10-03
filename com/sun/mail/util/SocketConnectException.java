package com.sun.mail.util;

import java.io.IOException;

public class SocketConnectException extends IOException
{
    private String host;
    private int port;
    private int cto;
    private static final long serialVersionUID = 3997871560538755463L;
    
    public SocketConnectException(final String msg, final Exception cause, final String host, final int port, final int cto) {
        super(msg);
        this.initCause(cause);
        this.host = host;
        this.port = port;
        this.cto = cto;
    }
    
    public Exception getException() {
        final Throwable t = this.getCause();
        assert !(!(t instanceof Exception));
        return (Exception)t;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public int getConnectionTimeout() {
        return this.cto;
    }
}
