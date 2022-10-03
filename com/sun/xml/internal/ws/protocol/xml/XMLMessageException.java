package com.sun.xml.internal.ws.protocol.xml;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLMessageException extends JAXWSExceptionBase
{
    public XMLMessageException(final String key, final Object... args) {
        super(key, args);
    }
    
    public XMLMessageException(final Throwable throwable) {
        super(throwable);
    }
    
    public XMLMessageException(final Localizable arg) {
        super("server.rt.err", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.xmlmessage";
    }
}
