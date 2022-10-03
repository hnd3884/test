package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMContainer;

public class SAXResultContentHandler extends OMContentHandler
{
    private final OMContainer root;
    private final OMFactory factory;
    
    public SAXResultContentHandler(final OMContainer root) {
        super(true);
        this.root = root;
        this.factory = root.getOMFactory();
    }
    
    @Override
    protected OMContainer doStartDocument() {
        return this.root;
    }
    
    @Override
    protected void doEndDocument() {
    }
    
    @Override
    protected void createOMDocType(final OMContainer parent, final String rootName, final String publicId, final String systemId, final String internalSubset) {
        if (parent instanceof OMDocument) {
            this.factory.createOMDocType(parent, rootName, publicId, systemId, internalSubset);
        }
    }
    
    @Override
    protected OMElement createOMElement(final OMContainer parent, final String localName, final String namespaceURI, final String prefix, final String[] namespaces, final int namespaceCount) {
        final OMElement element = this.factory.createOMElement(localName, this.factory.createOMNamespace(namespaceURI, prefix), parent);
        for (int i = 0; i < namespaceCount; ++i) {
            final String nsPrefix = namespaces[2 * i];
            final String nsURI = namespaces[2 * i + 1];
            if (nsPrefix.length() == 0) {
                element.declareDefaultNamespace(nsURI);
            }
            else {
                element.declareNamespace(nsURI, nsPrefix);
            }
        }
        return element;
    }
    
    @Override
    protected void completed(final OMElement element) {
    }
    
    @Override
    protected void createOMText(final OMContainer parent, final String text, final int type) {
        this.factory.createOMText(parent, text, type);
    }
    
    @Override
    protected void createOMProcessingInstruction(final OMContainer parent, final String piTarget, final String piData) {
        this.factory.createOMProcessingInstruction(parent, piTarget, piData);
    }
    
    @Override
    protected void createOMComment(final OMContainer parent, final String content) {
        this.factory.createOMComment(parent, content);
    }
    
    @Override
    protected void createOMEntityReference(final OMContainer parent, final String name, final String replacementText) {
        if (replacementText == null) {
            this.factory.createOMEntityReference(parent, name);
            return;
        }
        throw new UnsupportedOperationException();
    }
}
