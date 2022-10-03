package com.sun.xml.internal.ws.encoding;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamReader;
import com.sun.istack.internal.Nullable;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.istack.internal.NotNull;

public final class TagInfoset
{
    @NotNull
    public final String[] ns;
    @NotNull
    public final AttributesImpl atts;
    @Nullable
    public final String prefix;
    @Nullable
    public final String nsUri;
    @NotNull
    public final String localName;
    @Nullable
    private String qname;
    private static final String[] EMPTY_ARRAY;
    private static final AttributesImpl EMPTY_ATTRIBUTES;
    
    public TagInfoset(final String nsUri, final String localName, final String prefix, final AttributesImpl atts, final String... ns) {
        this.nsUri = nsUri;
        this.prefix = prefix;
        this.localName = localName;
        this.atts = atts;
        this.ns = ns;
    }
    
    public TagInfoset(final XMLStreamReader reader) {
        this.prefix = reader.getPrefix();
        this.nsUri = reader.getNamespaceURI();
        this.localName = reader.getLocalName();
        final int nsc = reader.getNamespaceCount();
        if (nsc > 0) {
            this.ns = new String[nsc * 2];
            for (int i = 0; i < nsc; ++i) {
                this.ns[i * 2] = fixNull(reader.getNamespacePrefix(i));
                this.ns[i * 2 + 1] = fixNull(reader.getNamespaceURI(i));
            }
        }
        else {
            this.ns = TagInfoset.EMPTY_ARRAY;
        }
        final int ac = reader.getAttributeCount();
        if (ac > 0) {
            this.atts = new AttributesImpl();
            final StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ac; ++j) {
                sb.setLength(0);
                final String prefix = reader.getAttributePrefix(j);
                final String localName = reader.getAttributeLocalName(j);
                String qname;
                if (prefix != null && prefix.length() != 0) {
                    sb.append(prefix);
                    sb.append(":");
                    sb.append(localName);
                    qname = sb.toString();
                }
                else {
                    qname = localName;
                }
                this.atts.addAttribute(fixNull(reader.getAttributeNamespace(j)), localName, qname, reader.getAttributeType(j), reader.getAttributeValue(j));
            }
        }
        else {
            this.atts = TagInfoset.EMPTY_ATTRIBUTES;
        }
    }
    
    public void writeStart(final ContentHandler contentHandler) throws SAXException {
        for (int i = 0; i < this.ns.length; i += 2) {
            contentHandler.startPrefixMapping(fixNull(this.ns[i]), fixNull(this.ns[i + 1]));
        }
        contentHandler.startElement(fixNull(this.nsUri), this.localName, this.getQName(), this.atts);
    }
    
    public void writeEnd(final ContentHandler contentHandler) throws SAXException {
        contentHandler.endElement(fixNull(this.nsUri), this.localName, this.getQName());
        for (int i = this.ns.length - 2; i >= 0; i -= 2) {
            contentHandler.endPrefixMapping(fixNull(this.ns[i]));
        }
    }
    
    public void writeStart(final XMLStreamWriter w) throws XMLStreamException {
        if (this.prefix == null) {
            if (this.nsUri == null) {
                w.writeStartElement(this.localName);
            }
            else {
                w.writeStartElement("", this.localName, this.nsUri);
            }
        }
        else {
            w.writeStartElement(this.prefix, this.localName, this.nsUri);
        }
        for (int i = 0; i < this.ns.length; i += 2) {
            w.writeNamespace(this.ns[i], this.ns[i + 1]);
        }
        for (int i = 0; i < this.atts.getLength(); ++i) {
            final String nsUri = this.atts.getURI(i);
            if (nsUri == null || nsUri.length() == 0) {
                w.writeAttribute(this.atts.getLocalName(i), this.atts.getValue(i));
            }
            else {
                final String rawName = this.atts.getQName(i);
                final String prefix = rawName.substring(0, rawName.indexOf(58));
                w.writeAttribute(prefix, nsUri, this.atts.getLocalName(i), this.atts.getValue(i));
            }
        }
    }
    
    private String getQName() {
        if (this.qname != null) {
            return this.qname;
        }
        final StringBuilder sb = new StringBuilder();
        if (this.prefix != null) {
            sb.append(this.prefix);
            sb.append(':');
            sb.append(this.localName);
            this.qname = sb.toString();
        }
        else {
            this.qname = this.localName;
        }
        return this.qname;
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public String getNamespaceURI(final String prefix) {
        for (int size = this.ns.length / 2, i = 0; i < size; ++i) {
            final String p = this.ns[i * 2];
            final String n = this.ns[i * 2 + 1];
            if (prefix.equals(p)) {
                return n;
            }
        }
        return null;
    }
    
    public String getPrefix(final String namespaceURI) {
        for (int size = this.ns.length / 2, i = 0; i < size; ++i) {
            final String p = this.ns[i * 2];
            final String n = this.ns[i * 2 + 1];
            if (namespaceURI.equals(n)) {
                return p;
            }
        }
        return null;
    }
    
    public List<String> allPrefixes(final String namespaceURI) {
        final int size = this.ns.length / 2;
        final List<String> l = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            final String p = this.ns[i * 2];
            final String n = this.ns[i * 2 + 1];
            if (namespaceURI.equals(n)) {
                l.add(p);
            }
        }
        return l;
    }
    
    static {
        EMPTY_ARRAY = new String[0];
        EMPTY_ATTRIBUTES = new AttributesImpl();
    }
}
