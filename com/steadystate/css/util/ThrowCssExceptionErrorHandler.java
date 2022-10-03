package com.steadystate.css.util;

import org.w3c.css.sac.CSSParseException;
import java.io.Serializable;
import org.w3c.css.sac.ErrorHandler;

public class ThrowCssExceptionErrorHandler implements ErrorHandler, Serializable
{
    private static final long serialVersionUID = -3933638774901855095L;
    public static final ThrowCssExceptionErrorHandler INSTANCE;
    
    public void error(final CSSParseException exception) {
        throw exception;
    }
    
    public void fatalError(final CSSParseException exception) {
        throw exception;
    }
    
    public void warning(final CSSParseException exception) {
    }
    
    static {
        INSTANCE = new ThrowCssExceptionErrorHandler();
    }
}
