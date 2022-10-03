package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLStreamWriterException extends JAXWSExceptionBase
{
    public XMLStreamWriterException(final String key, final Object... args) {
        super(key, args);
    }
    
    public XMLStreamWriterException(final Throwable throwable) {
        super(throwable);
    }
    
    public XMLStreamWriterException(final Localizable arg) {
        super("xmlwriter.nestedError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.streaming";
    }
}
