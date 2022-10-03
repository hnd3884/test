package com.btr.proxy.selector.pac;

import com.btr.proxy.util.ProxyException;

public class ProxyEvaluationException extends ProxyException
{
    private static final long serialVersionUID = 1L;
    
    public ProxyEvaluationException() {
    }
    
    public ProxyEvaluationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ProxyEvaluationException(final String message) {
        super(message);
    }
    
    public ProxyEvaluationException(final Throwable cause) {
        super(cause);
    }
}
