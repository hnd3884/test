package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;

final class SchemaDOMImplementation implements DOMImplementation
{
    private static final SchemaDOMImplementation singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return SchemaDOMImplementation.singleton;
    }
    
    private SchemaDOMImplementation() {
    }
    
    public Document createDocument(final String s, final String s2, final DocumentType documentType) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public DocumentType createDocumentType(final String s, final String s2, final String s3) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Object getFeature(final String s, final String s2) {
        if (SchemaDOMImplementation.singleton.hasFeature(s, s2)) {
            return SchemaDOMImplementation.singleton;
        }
        return null;
    }
    
    public boolean hasFeature(final String s, final String s2) {
        final boolean b = s2 == null || s2.length() == 0;
        return (s.equalsIgnoreCase("Core") || s.equalsIgnoreCase("XML")) && (b || s2.equals("1.0") || s2.equals("2.0") || s2.equals("3.0"));
    }
    
    static {
        singleton = new SchemaDOMImplementation();
    }
}
