package com.sun.xml.internal.stream.events;

import javax.xml.stream.events.Namespace;
import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.util.Iterator;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import java.util.List;
import javax.xml.stream.events.EndElement;

public class EndElementEvent extends DummyEvent implements EndElement
{
    List fNamespaces;
    QName fQName;
    
    public EndElementEvent() {
        this.fNamespaces = null;
        this.init();
    }
    
    protected void init() {
        this.setEventType(2);
        this.fNamespaces = new ArrayList();
    }
    
    public EndElementEvent(final String prefix, final String uri, final String localpart) {
        this(new QName(uri, localpart, prefix));
    }
    
    public EndElementEvent(final QName qname) {
        this.fNamespaces = null;
        this.fQName = qname;
        this.init();
    }
    
    @Override
    public QName getName() {
        return this.fQName;
    }
    
    public void setName(final QName qname) {
        this.fQName = qname;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write("</");
        final String prefix = this.fQName.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(58);
        }
        writer.write(this.fQName.getLocalPart());
        writer.write(62);
    }
    
    @Override
    public Iterator getNamespaces() {
        if (this.fNamespaces != null) {
            this.fNamespaces.iterator();
        }
        return new ReadOnlyIterator();
    }
    
    void addNamespace(final Namespace attr) {
        if (attr != null) {
            this.fNamespaces.add(attr);
        }
    }
    
    @Override
    public String toString() {
        String s = "</" + this.nameAsString();
        s += ">";
        return s;
    }
    
    public String nameAsString() {
        if ("".equals(this.fQName.getNamespaceURI())) {
            return this.fQName.getLocalPart();
        }
        if (this.fQName.getPrefix() != null) {
            return "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getPrefix() + ":" + this.fQName.getLocalPart();
        }
        return "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getLocalPart();
    }
}
