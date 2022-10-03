package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;

public class NamedEvent extends DummyEvent
{
    private QName name;
    
    public NamedEvent() {
    }
    
    public NamedEvent(final QName qname) {
        this.name = qname;
    }
    
    public NamedEvent(final String prefix, final String uri, final String localpart) {
        this.name = new QName(uri, localpart, prefix);
    }
    
    public String getPrefix() {
        return this.name.getPrefix();
    }
    
    public QName getName() {
        return this.name;
    }
    
    public void setName(final QName qname) {
        this.name = qname;
    }
    
    public String nameAsString() {
        if ("".equals(this.name.getNamespaceURI())) {
            return this.name.getLocalPart();
        }
        if (this.name.getPrefix() != null) {
            return "['" + this.name.getNamespaceURI() + "']:" + this.getPrefix() + ":" + this.name.getLocalPart();
        }
        return "['" + this.name.getNamespaceURI() + "']:" + this.name.getLocalPart();
    }
    
    public String getNamespace() {
        return this.name.getNamespaceURI();
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write(this.nameAsString());
    }
}
