package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.ExceptionUtil;
import java.io.InterruptedIOException;

public class ConnectTimeoutException extends InterruptedIOException
{
    public ConnectTimeoutException() {
    }
    
    public ConnectTimeoutException(final String message) {
        super(message);
    }
    
    public ConnectTimeoutException(final String message, final Throwable cause) {
        super(message);
        ExceptionUtil.initCause(this, cause);
    }
}
