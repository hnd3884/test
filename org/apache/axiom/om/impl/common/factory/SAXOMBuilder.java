package org.apache.axiom.om.impl.common.factory;

import org.apache.axiom.om.impl.builder.BuilderUtil;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.OMElement;
import org.xml.sax.XMLReader;
import java.io.IOException;
import org.apache.axiom.om.OMException;
import org.xml.sax.SAXException;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.OMDocument;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.OMContentHandler;

public class SAXOMBuilder extends OMContentHandler implements OMXMLParserWrapper
{
    private final SAXSource source;
    private OMDocument document;
    private final OMFactoryEx factory;
    
    public SAXOMBuilder(final OMFactory factory, final SAXSource source, final boolean expandEntityReferences) {
        super(expandEntityReferences);
        this.factory = (OMFactoryEx)factory;
        this.source = source;
    }
    
    @Override
    protected OMContainer doStartDocument() {
        return (OMContainer)(this.document = this.factory.createOMDocument((OMXMLParserWrapper)this));
    }
    
    @Override
    protected void doEndDocument() {
        ((OMContainerEx)this.document).setComplete(true);
    }
    
    public OMDocument getDocument() {
        if (this.document == null && this.source != null) {
            final XMLReader reader = this.source.getXMLReader();
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            try {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            }
            catch (final SAXException ex3) {}
            try {
                reader.setProperty("http://xml.org/sax/properties/declaration-handler", this);
            }
            catch (final SAXException ex4) {}
            try {
                reader.parse(this.source.getInputSource());
            }
            catch (final IOException ex) {
                throw new OMException((Throwable)ex);
            }
            catch (final SAXException ex2) {
                throw new OMException((Throwable)ex2);
            }
        }
        if (this.document != null && this.document.isComplete()) {
            return this.document;
        }
        throw new OMException("Tree not complete");
    }
    
    public int next() throws OMException {
        throw new UnsupportedOperationException();
    }
    
    public void discard(final OMElement el) throws OMException {
        throw new UnsupportedOperationException();
    }
    
    public void setCache(final boolean b) throws OMException {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCache() {
        throw new UnsupportedOperationException();
    }
    
    public Object getParser() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isCompleted() {
        return this.document != null && this.document.isComplete();
    }
    
    public OMElement getDocumentElement() {
        return this.getDocument().getOMDocumentElement();
    }
    
    public OMElement getDocumentElement(final boolean discardDocument) {
        final OMElement documentElement = this.getDocument().getOMDocumentElement();
        if (discardDocument) {
            documentElement.detach();
        }
        return documentElement;
    }
    
    public short getBuilderType() {
        throw new UnsupportedOperationException();
    }
    
    public void registerExternalContentHandler(final Object obj) {
        throw new UnsupportedOperationException();
    }
    
    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }
    
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }
    
    public void close() {
    }
    
    @Override
    protected void createOMDocType(final OMContainer parent, final String rootName, final String publicId, final String systemId, final String internalSubset) {
        this.factory.createOMDocType(parent, rootName, publicId, systemId, internalSubset, true);
    }
    
    @Override
    protected OMElement createOMElement(final OMContainer parent, final String localName, final String namespaceURI, final String prefix, final String[] namespaces, final int namespaceCount) {
        final OMElement element = this.factory.createOMElement(localName, parent, (OMXMLParserWrapper)this);
        for (int i = 0; i < namespaceCount; ++i) {
            ((OMElementEx)element).addNamespaceDeclaration(namespaces[2 * i + 1], namespaces[2 * i]);
        }
        BuilderUtil.setNamespace(element, namespaceURI, prefix, false);
        return element;
    }
    
    @Override
    protected void completed(final OMElement element) {
        ((OMElementEx)element).setComplete(true);
    }
    
    @Override
    protected void createOMText(final OMContainer parent, final String text, final int type) {
        this.factory.createOMText(parent, text, type, true);
    }
    
    @Override
    protected void createOMProcessingInstruction(final OMContainer parent, final String piTarget, final String piData) {
        this.factory.createOMProcessingInstruction(parent, piTarget, piData, true);
    }
    
    @Override
    protected void createOMComment(final OMContainer parent, final String content) {
        this.factory.createOMComment(parent, content, true);
    }
    
    @Override
    protected void createOMEntityReference(final OMContainer parent, final String name, final String replacementText) {
        this.factory.createOMEntityReference(parent, name, replacementText, true);
    }
    
    public void detach() {
        this.getDocument();
    }
}
