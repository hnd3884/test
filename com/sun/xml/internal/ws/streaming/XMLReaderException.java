package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLReaderException extends JAXWSExceptionBase
{
    public XMLReaderException(final String key, final Object... args) {
        super(key, args);
    }
    
    public XMLReaderException(final Throwable throwable) {
        super(throwable);
    }
    
    public XMLReaderException(final Localizable arg) {
        super("xmlreader.nestedError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.streaming";
    }
}
