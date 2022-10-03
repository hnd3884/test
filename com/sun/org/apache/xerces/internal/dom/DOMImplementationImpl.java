package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;

public class DOMImplementationImpl extends CoreDOMImplementationImpl implements DOMImplementation
{
    static DOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.singleton;
    }
    
    @Override
    public boolean hasFeature(String feature, final String version) {
        final boolean result = super.hasFeature(feature, version);
        if (!result) {
            final boolean anyVersion = version == null || version.length() == 0;
            if (feature.startsWith("+")) {
                feature = feature.substring(1);
            }
            return (feature.equalsIgnoreCase("Events") && (anyVersion || version.equals("2.0"))) || (feature.equalsIgnoreCase("MutationEvents") && (anyVersion || version.equals("2.0"))) || (feature.equalsIgnoreCase("Traversal") && (anyVersion || version.equals("2.0"))) || (feature.equalsIgnoreCase("Range") && (anyVersion || version.equals("2.0"))) || (feature.equalsIgnoreCase("MutationEvents") && (anyVersion || version.equals("2.0")));
        }
        return result;
    }
    
    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
        if (namespaceURI == null && qualifiedName == null && doctype == null) {
            return new DocumentImpl();
        }
        if (doctype != null && doctype.getOwnerDocument() != null) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException((short)4, msg);
        }
        final DocumentImpl doc = new DocumentImpl(doctype);
        final Element e = doc.createElementNS(namespaceURI, qualifiedName);
        doc.appendChild(e);
        return doc;
    }
    
    static {
        DOMImplementationImpl.singleton = new DOMImplementationImpl();
    }
}
