package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class HandlerException extends JAXWSExceptionBase
{
    public HandlerException(final String key, final Object... args) {
        super(key, args);
    }
    
    public HandlerException(final Throwable throwable) {
        super(throwable);
    }
    
    public HandlerException(final Localizable arg) {
        super("handler.nestedError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.handler";
    }
}
