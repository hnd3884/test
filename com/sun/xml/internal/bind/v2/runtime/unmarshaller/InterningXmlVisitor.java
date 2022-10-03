package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;

public final class InterningXmlVisitor implements XmlVisitor
{
    private final XmlVisitor next;
    private final AttributesImpl attributes;
    
    public InterningXmlVisitor(final XmlVisitor next) {
        this.attributes = new AttributesImpl();
        this.next = next;
    }
    
    @Override
    public void startDocument(final LocatorEx locator, final NamespaceContext nsContext) throws SAXException {
        this.next.startDocument(locator, nsContext);
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.next.endDocument();
    }
    
    @Override
    public void startElement(final TagName tagName) throws SAXException {
        this.attributes.setAttributes(tagName.atts);
        tagName.atts = this.attributes;
        tagName.uri = intern(tagName.uri);
        tagName.local = intern(tagName.local);
        this.next.startElement(tagName);
    }
    
    @Override
    public void endElement(final TagName tagName) throws SAXException {
        tagName.uri = intern(tagName.uri);
        tagName.local = intern(tagName.local);
        this.next.endElement(tagName);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String nsUri) throws SAXException {
        this.next.startPrefixMapping(intern(prefix), intern(nsUri));
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.next.endPrefixMapping(intern(prefix));
    }
    
    @Override
    public void text(final CharSequence pcdata) throws SAXException {
        this.next.text(pcdata);
    }
    
    @Override
    public UnmarshallingContext getContext() {
        return this.next.getContext();
    }
    
    @Override
    public TextPredictor getPredictor() {
        return this.next.getPredictor();
    }
    
    private static String intern(final String s) {
        if (s == null) {
            return null;
        }
        return s.intern();
    }
    
    private static class AttributesImpl implements Attributes
    {
        private Attributes core;
        
        void setAttributes(final Attributes att) {
            this.core = att;
        }
        
        @Override
        public int getIndex(final String qName) {
            return this.core.getIndex(qName);
        }
        
        @Override
        public int getIndex(final String uri, final String localName) {
            return this.core.getIndex(uri, localName);
        }
        
        @Override
        public int getLength() {
            return this.core.getLength();
        }
        
        @Override
        public String getLocalName(final int index) {
            return intern(this.core.getLocalName(index));
        }
        
        @Override
        public String getQName(final int index) {
            return intern(this.core.getQName(index));
        }
        
        @Override
        public String getType(final int index) {
            return intern(this.core.getType(index));
        }
        
        @Override
        public String getType(final String qName) {
            return intern(this.core.getType(qName));
        }
        
        @Override
        public String getType(final String uri, final String localName) {
            return intern(this.core.getType(uri, localName));
        }
        
        @Override
        public String getURI(final int index) {
            return intern(this.core.getURI(index));
        }
        
        @Override
        public String getValue(final int index) {
            return this.core.getValue(index);
        }
        
        @Override
        public String getValue(final String qName) {
            return this.core.getValue(qName);
        }
        
        @Override
        public String getValue(final String uri, final String localName) {
            return this.core.getValue(uri, localName);
        }
    }
}
