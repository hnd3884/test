package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import org.apache.axiom.ext.stax.DTDReader;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class SJSXPStreamReaderWrapper extends XMLStreamReaderWrapper implements DelegatingXMLStreamReader
{
    public SJSXPStreamReaderWrapper(final XMLStreamReader parent) {
        super(parent);
    }
    
    @Override
    public Object getProperty(final String name) {
        if (DTDReader.PROPERTY.equals(name)) {
            return new AbstractDTDReader(this.getParent()) {
                @Override
                protected String getDocumentTypeDeclaration(final XMLStreamReader reader) {
                    return reader.getText();
                }
            };
        }
        return super.getProperty(name);
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        if (this.getEventType() == 7) {
            return super.getCharacterEncodingScheme();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public String getEncoding() {
        if (this.getEventType() == 7) {
            return super.getEncoding();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public String getVersion() {
        if (this.getEventType() == 7) {
            return super.getVersion();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public boolean isStandalone() {
        if (this.getEventType() == 7) {
            return super.isStandalone();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public boolean standaloneSet() {
        if (this.getEventType() == 7) {
            return super.standaloneSet();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public String getLocalName() {
        final int event = super.getEventType();
        if (event == 1 || event == 2 || event == 9) {
            return super.getLocalName();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public String getPrefix() {
        final int event = super.getEventType();
        if (event == 1 || event == 2) {
            final String result = super.getPrefix();
            return (result == null || result.length() == 0) ? null : result;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public String getNamespaceURI() {
        final int event = this.getEventType();
        if (event == 1 || event == 2) {
            return super.getNamespaceURI();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public QName getName() {
        try {
            return super.getName();
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean hasName() {
        final int event = super.getEventType();
        return event == 1 || event == 2;
    }
    
    @Override
    public boolean hasText() {
        return super.hasText() || super.getEventType() == 6;
    }
    
    @Override
    public boolean isWhiteSpace() {
        return super.isWhiteSpace() || super.getEventType() == 6;
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (this.hasNext()) {
            return super.next();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return new SJSXPNamespaceContextWrapper(super.getNamespaceContext());
    }
    
    public XMLStreamReader getParent() {
        return super.getParent();
    }
}
