package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLStreamReaderException extends JAXWSExceptionBase
{
    public XMLStreamReaderException(final String key, final Object... args) {
        super(key, args);
    }
    
    public XMLStreamReaderException(final Throwable throwable) {
        super(throwable);
    }
    
    public XMLStreamReaderException(final Localizable arg) {
        super("xmlreader.nestedError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.streaming";
    }
}
