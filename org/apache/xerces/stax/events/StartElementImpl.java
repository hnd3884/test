package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import java.io.Writer;
import org.apache.xerces.stax.DefaultNamespaceContext;
import java.util.Collections;
import javax.xml.stream.events.Attribute;
import java.util.TreeMap;
import javax.xml.stream.Location;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Map;
import java.util.Comparator;
import javax.xml.stream.events.StartElement;

public final class StartElementImpl extends ElementImpl implements StartElement
{
    private static final Comparator QNAME_COMPARATOR;
    private final Map fAttributes;
    private final NamespaceContext fNamespaceContext;
    
    public StartElementImpl(final QName qName, final Iterator iterator, final Iterator iterator2, final NamespaceContext namespaceContext, final Location location) {
        super(qName, true, iterator2, location);
        if (iterator != null && iterator.hasNext()) {
            this.fAttributes = new TreeMap(StartElementImpl.QNAME_COMPARATOR);
            do {
                final Attribute attribute = iterator.next();
                this.fAttributes.put(attribute.getName(), attribute);
            } while (iterator.hasNext());
        }
        else {
            this.fAttributes = Collections.EMPTY_MAP;
        }
        this.fNamespaceContext = ((namespaceContext != null) ? namespaceContext : DefaultNamespaceContext.getInstance());
    }
    
    public Iterator getAttributes() {
        return ElementImpl.createImmutableIterator(this.fAttributes.values().iterator());
    }
    
    public Attribute getAttributeByName(final QName qName) {
        return this.fAttributes.get(qName);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
    
    public String getNamespaceURI(final String s) {
        return this.fNamespaceContext.getNamespaceURI(s);
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write(60);
            final QName name = this.getName();
            final String prefix = name.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(name.getLocalPart());
            final Iterator namespaces = this.getNamespaces();
            while (namespaces.hasNext()) {
                final Namespace namespace = namespaces.next();
                writer.write(32);
                namespace.writeAsEncodedUnicode(writer);
            }
            final Iterator attributes = this.getAttributes();
            while (attributes.hasNext()) {
                final Attribute attribute = attributes.next();
                writer.write(32);
                attribute.writeAsEncodedUnicode(writer);
            }
            writer.write(62);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    static {
        QNAME_COMPARATOR = new Comparator() {
            public int compare(final Object o, final Object o2) {
                if (o.equals(o2)) {
                    return 0;
                }
                return ((QName)o).toString().compareTo(((QName)o2).toString());
            }
        };
    }
}
