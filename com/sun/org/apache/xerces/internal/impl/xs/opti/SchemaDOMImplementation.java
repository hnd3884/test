package com.sun.org.apache.xerces.internal.impl.xs.opti;

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
    
    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        if (SchemaDOMImplementation.singleton.hasFeature(feature, version)) {
            return SchemaDOMImplementation.singleton;
        }
        return null;
    }
    
    @Override
    public boolean hasFeature(final String feature, final String version) {
        final boolean anyVersion = version == null || version.length() == 0;
        return (feature.equalsIgnoreCase("Core") || feature.equalsIgnoreCase("XML")) && (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0"));
    }
    
    static {
        singleton = new SchemaDOMImplementation();
    }
}
