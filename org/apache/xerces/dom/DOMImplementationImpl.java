package org.apache.xerces.dom;

import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;

public class DOMImplementationImpl extends CoreDOMImplementationImpl implements DOMImplementation
{
    static final DOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.singleton;
    }
    
    public boolean hasFeature(String substring, final String s) {
        final boolean hasFeature = super.hasFeature(substring, s);
        if (!hasFeature) {
            final boolean b = s == null || s.length() == 0;
            if (substring.startsWith("+")) {
                substring = substring.substring(1);
            }
            return (substring.equalsIgnoreCase("Events") && (b || s.equals("2.0"))) || (substring.equalsIgnoreCase("MutationEvents") && (b || s.equals("2.0"))) || (substring.equalsIgnoreCase("Traversal") && (b || s.equals("2.0"))) || (substring.equalsIgnoreCase("Range") && (b || s.equals("2.0"))) || (substring.equalsIgnoreCase("MutationEvents") && (b || s.equals("2.0")));
        }
        return hasFeature;
    }
    
    protected CoreDocumentImpl createDocument(final DocumentType documentType) {
        return new DocumentImpl(documentType);
    }
    
    static {
        singleton = new DOMImplementationImpl();
    }
}
