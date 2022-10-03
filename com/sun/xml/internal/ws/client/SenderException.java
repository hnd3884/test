package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class SenderException extends JAXWSExceptionBase
{
    public SenderException(final String key, final Object... args) {
        super(key, args);
    }
    
    public SenderException(final Throwable throwable) {
        super(throwable);
    }
    
    public SenderException(final Localizable arg) {
        super("sender.nestedError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.sender";
    }
}
