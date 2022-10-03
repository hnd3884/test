package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class Woodstox3StreamReaderWrapper extends XMLStreamReaderWrapper implements DelegatingXMLStreamReader
{
    public Woodstox3StreamReaderWrapper(final XMLStreamReader reader) {
        super(reader);
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
    public boolean isCharacters() {
        return this.getEventType() == 4;
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }
    
    public XMLStreamReader getParent() {
        return super.getParent();
    }
}
