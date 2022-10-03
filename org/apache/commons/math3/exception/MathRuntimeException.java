package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;

public class MathRuntimeException extends RuntimeException implements ExceptionContextProvider
{
    private static final long serialVersionUID = 20120926L;
    private final ExceptionContext context;
    
    public MathRuntimeException(final Localizable pattern, final Object... args) {
        (this.context = new ExceptionContext(this)).addMessage(pattern, args);
    }
    
    public ExceptionContext getContext() {
        return this.context;
    }
    
    @Override
    public String getMessage() {
        return this.context.getMessage();
    }
    
    @Override
    public String getLocalizedMessage() {
        return this.context.getLocalizedMessage();
    }
}
