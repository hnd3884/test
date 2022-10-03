package com.sun.xml.internal.ws.encoding.soap;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class SerializationException extends JAXWSExceptionBase
{
    public SerializationException(final String key, final Object... args) {
        super(key, args);
    }
    
    public SerializationException(final Localizable arg) {
        super("nestedSerializationError", new Object[] { arg });
    }
    
    public SerializationException(final Throwable throwable) {
        super(throwable);
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.encoding";
    }
}
