package com.sun.org.apache.xalan.internal.xsltc;

import org.xml.sax.SAXException;

public final class TransletException extends SAXException
{
    static final long serialVersionUID = -878916829521217293L;
    
    public TransletException() {
        super("Translet error");
    }
    
    public TransletException(final Exception e) {
        super(e.toString());
        this.initCause(e);
    }
    
    public TransletException(final String message) {
        super(message);
    }
}
