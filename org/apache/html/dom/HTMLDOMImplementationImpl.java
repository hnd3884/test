package org.apache.html.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLDOMImplementation;
import org.apache.xerces.dom.DOMImplementationImpl;

public class HTMLDOMImplementationImpl extends DOMImplementationImpl implements HTMLDOMImplementation
{
    private static final HTMLDOMImplementation _instance;
    
    private HTMLDOMImplementationImpl() {
    }
    
    public final HTMLDocument createHTMLDocument(final String title) throws DOMException {
        if (title == null) {
            throw new NullPointerException("HTM014 Argument 'title' is null.");
        }
        final HTMLDocumentImpl htmlDocumentImpl = new HTMLDocumentImpl();
        htmlDocumentImpl.setTitle(title);
        return htmlDocumentImpl;
    }
    
    public static HTMLDOMImplementation getHTMLDOMImplementation() {
        return HTMLDOMImplementationImpl._instance;
    }
    
    static {
        _instance = new HTMLDOMImplementationImpl();
    }
}
