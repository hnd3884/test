package com.sun.xml.internal.ws.encoding.soap;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class DeserializationException extends JAXWSExceptionBase
{
    public DeserializationException(final String key, final Object... args) {
        super(key, args);
    }
    
    public DeserializationException(final Throwable throwable) {
        super(throwable);
    }
    
    public DeserializationException(final Localizable arg) {
        super("nestedDeserializationError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.encoding";
    }
}
