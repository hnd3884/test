package org.apache.xerces.dom;

import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;

public class PSVIDOMImplementationImpl extends DOMImplementationImpl
{
    static final PSVIDOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return PSVIDOMImplementationImpl.singleton;
    }
    
    public boolean hasFeature(final String s, final String s2) {
        return super.hasFeature(s, s2) || s.equalsIgnoreCase("psvi");
    }
    
    protected CoreDocumentImpl createDocument(final DocumentType documentType) {
        return new PSVIDocumentImpl(documentType);
    }
    
    static {
        singleton = new PSVIDOMImplementationImpl();
    }
}
