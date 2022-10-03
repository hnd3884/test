package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class NamespaceContextCorrectingXMLStreamReaderWrapper extends XMLStreamReaderWrapper implements DelegatingXMLStreamReader
{
    private final ScopedNamespaceContext namespaceContext;
    
    public NamespaceContextCorrectingXMLStreamReaderWrapper(final XMLStreamReader parent) {
        super(parent);
        this.namespaceContext = new ScopedNamespaceContext();
    }
    
    private void startElement() {
        this.namespaceContext.startScope();
        for (int i = 0, c = this.getNamespaceCount(); i < c; ++i) {
            final String prefix = this.getNamespacePrefix(i);
            this.namespaceContext.setPrefix((prefix == null) ? "" : prefix, this.getNamespaceURI(i));
        }
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (this.isEndElement()) {
            this.namespaceContext.endScope();
        }
        final int event = super.next();
        if (event == 1) {
            this.startElement();
        }
        return event;
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        if (this.isEndElement()) {
            this.namespaceContext.endScope();
        }
        final int event = super.nextTag();
        if (event == 1) {
            this.startElement();
        }
        return event;
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        final String uri = this.namespaceContext.getNamespaceURI(prefix);
        return (uri.length() == 0) ? null : uri;
    }
    
    public XMLStreamReader getParent() {
        return super.getParent();
    }
}
