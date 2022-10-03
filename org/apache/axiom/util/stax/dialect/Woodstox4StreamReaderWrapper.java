package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.codehaus.stax2.XMLStreamReader2;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;

class Woodstox4StreamReaderWrapper extends StAX2StreamReaderWrapper implements DelegatingXMLStreamReader, CharacterDataReader
{
    public Woodstox4StreamReaderWrapper(final XMLStreamReader reader) {
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
    public String getPrefix() {
        final String prefix = super.getPrefix();
        return (prefix == null || prefix.length() == 0) ? null : prefix;
    }
    
    @Override
    public String getNamespaceURI() {
        final String uri = super.getNamespaceURI();
        return (uri == null || uri.length() == 0) ? null : uri;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        final String uri = super.getNamespaceURI(prefix);
        return (uri == null || uri.length() == 0) ? null : uri;
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        final String prefix = super.getNamespacePrefix(index);
        return (prefix == null || prefix.length() == 0) ? null : prefix;
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        final String uri = super.getAttributeNamespace(index);
        return (uri == null || uri.length() == 0) ? null : uri;
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }
    
    public XMLStreamReader getParent() {
        return super.getParent();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (CharacterDataReader.PROPERTY.equals(name)) {
            return this;
        }
        return super.getProperty(name);
    }
    
    public void writeTextTo(final Writer writer) throws XMLStreamException, IOException {
        ((XMLStreamReader2)XMLStreamReaderUtils.getOriginalXMLStreamReader(this)).getText(writer, false);
    }
}
