package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.ExceptionUtil;
import java.io.IOException;

public class NoHttpResponseException extends IOException
{
    public NoHttpResponseException() {
    }
    
    public NoHttpResponseException(final String message) {
        super(message);
    }
    
    public NoHttpResponseException(final String message, final Throwable cause) {
        super(message);
        ExceptionUtil.initCause(this, cause);
    }
}
