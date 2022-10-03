package org.apache.axiom.om.impl.common.serializer.push.sax;

import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;
import java.util.Stack;

final class SAXHelper
{
    private Stack elementNameStack;
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private final AttributesImpl attributes;
    
    SAXHelper() {
        this.elementNameStack = new Stack();
        this.attributes = new AttributesImpl();
    }
    
    private static String getQName(final String prefix, final String localName) {
        if (prefix.length() == 0) {
            return localName;
        }
        return String.valueOf(prefix) + ":" + localName;
    }
    
    void beginStartElement(final String prefix, final String namespaceURI, final String localName) {
        this.elementURI = namespaceURI;
        this.elementLocalName = localName;
        this.elementQName = getQName(prefix, localName);
    }
    
    void addAttribute(final String prefix, final String namespaceURI, final String localName, final String type, final String value) {
        this.attributes.addAttribute(namespaceURI, localName, getQName(prefix, localName), type, value);
    }
    
    void finishStartElement(final ContentHandler contentHandler) throws SAXException {
        contentHandler.startElement(this.elementURI, this.elementLocalName, this.elementQName, this.attributes);
        this.elementNameStack.push(this.elementURI);
        this.elementNameStack.push(this.elementLocalName);
        this.elementNameStack.push(this.elementQName);
        this.elementURI = null;
        this.elementLocalName = null;
        this.elementQName = null;
        this.attributes.clear();
    }
    
    boolean isInStartElement() {
        return this.elementLocalName != null;
    }
    
    void writeEndElement(final ContentHandler contentHandler, final ScopedNamespaceContext nsContext) throws SAXException {
        final String elementQName = this.elementNameStack.pop();
        final String elementLocalName = this.elementNameStack.pop();
        final String elementURI = this.elementNameStack.pop();
        contentHandler.endElement(elementURI, elementLocalName, elementQName);
        if (nsContext != null) {
            for (int i = nsContext.getBindingsCount() - 1; i >= nsContext.getFirstBindingInCurrentScope(); --i) {
                contentHandler.endPrefixMapping(nsContext.getPrefix(i));
            }
            nsContext.endScope();
        }
    }
}
