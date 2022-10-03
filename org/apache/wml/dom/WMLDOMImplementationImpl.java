package org.apache.wml.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;
import org.apache.wml.WMLDOMImplementation;
import org.apache.xerces.dom.DOMImplementationImpl;

public class WMLDOMImplementationImpl extends DOMImplementationImpl implements WMLDOMImplementation
{
    static final DOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return WMLDOMImplementationImpl.singleton;
    }
    
    protected CoreDocumentImpl createDocument(final DocumentType documentType) {
        return new WMLDocumentImpl(documentType);
    }
    
    static {
        singleton = new WMLDOMImplementationImpl();
    }
}
