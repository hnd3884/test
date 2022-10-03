package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

abstract class StAXConnector
{
    protected final XmlVisitor visitor;
    protected final UnmarshallingContext context;
    protected final XmlVisitor.TextPredictor predictor;
    protected final TagName tagName;
    
    public abstract void bridge() throws XMLStreamException;
    
    protected StAXConnector(final XmlVisitor visitor) {
        this.tagName = new TagNameImpl();
        this.visitor = visitor;
        this.context = visitor.getContext();
        this.predictor = visitor.getPredictor();
    }
    
    protected abstract Location getCurrentLocation();
    
    protected abstract String getCurrentQName();
    
    protected final void handleStartDocument(final NamespaceContext nsc) throws SAXException {
        this.visitor.startDocument(new LocatorEx() {
            @Override
            public ValidationEventLocator getLocation() {
                return new ValidationEventLocatorImpl(this);
            }
            
            @Override
            public int getColumnNumber() {
                return StAXConnector.this.getCurrentLocation().getColumnNumber();
            }
            
            @Override
            public int getLineNumber() {
                return StAXConnector.this.getCurrentLocation().getLineNumber();
            }
            
            @Override
            public String getPublicId() {
                return StAXConnector.this.getCurrentLocation().getPublicId();
            }
            
            @Override
            public String getSystemId() {
                return StAXConnector.this.getCurrentLocation().getSystemId();
            }
        }, nsc);
    }
    
    protected final void handleEndDocument() throws SAXException {
        this.visitor.endDocument();
    }
    
    protected static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    protected final String getQName(final String prefix, final String localName) {
        if (prefix == null || prefix.length() == 0) {
            return localName;
        }
        return prefix + ':' + localName;
    }
    
    private final class TagNameImpl extends TagName
    {
        @Override
        public String getQname() {
            return StAXConnector.this.getCurrentQName();
        }
    }
}
