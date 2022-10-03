package com.sun.xml.internal.bind.v2.runtime.output;

import org.xml.sax.Attributes;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.internal.bind.util.AttributesImpl;
import org.xml.sax.ContentHandler;

public class SAXOutput extends XmlOutputAbstractImpl
{
    protected final ContentHandler out;
    private String elementNsUri;
    private String elementLocalName;
    private String elementQName;
    private char[] buf;
    private final AttributesImpl atts;
    
    public SAXOutput(final ContentHandler out) {
        this.buf = new char[256];
        this.atts = new AttributesImpl();
        (this.out = out).setDocumentLocator(new LocatorImpl());
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws SAXException, IOException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        if (!fragment) {
            this.out.startDocument();
        }
    }
    
    @Override
    public void endDocument(final boolean fragment) throws SAXException, IOException, XMLStreamException {
        if (!fragment) {
            this.out.endDocument();
        }
        super.endDocument(fragment);
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) {
        this.elementNsUri = this.nsContext.getNamespaceURI(prefix);
        this.elementLocalName = localName;
        this.elementQName = this.getQName(prefix, localName);
        this.atts.clear();
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) {
        String nsUri;
        String qname;
        if (prefix == -1) {
            nsUri = "";
            qname = localName;
        }
        else {
            nsUri = this.nsContext.getNamespaceURI(prefix);
            final String p = this.nsContext.getPrefix(prefix);
            if (p.length() == 0) {
                qname = localName;
            }
            else {
                qname = p + ':' + localName;
            }
        }
        this.atts.addAttribute(nsUri, localName, qname, "CDATA", value);
    }
    
    @Override
    public void endStartTag() throws SAXException {
        final NamespaceContextImpl.Element ns = this.nsContext.getCurrent();
        if (ns != null) {
            for (int sz = ns.count(), i = 0; i < sz; ++i) {
                final String p = ns.getPrefix(i);
                final String uri = ns.getNsUri(i);
                if (uri.length() != 0 || ns.getBase() != 1) {
                    this.out.startPrefixMapping(p, uri);
                }
            }
        }
        this.out.startElement(this.elementNsUri, this.elementLocalName, this.elementQName, this.atts);
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws SAXException {
        this.out.endElement(this.nsContext.getNamespaceURI(prefix), localName, this.getQName(prefix, localName));
        final NamespaceContextImpl.Element ns = this.nsContext.getCurrent();
        if (ns != null) {
            final int sz = ns.count();
            for (int i = sz - 1; i >= 0; --i) {
                final String p = ns.getPrefix(i);
                final String uri = ns.getNsUri(i);
                if (uri.length() != 0 || ns.getBase() != 1) {
                    this.out.endPrefixMapping(p);
                }
            }
        }
    }
    
    private String getQName(final int prefix, final String localName) {
        final String p = this.nsContext.getPrefix(prefix);
        String qname;
        if (p.length() == 0) {
            qname = localName;
        }
        else {
            qname = p + ':' + localName;
        }
        return qname;
    }
    
    @Override
    public void text(final String value, final boolean needsSP) throws IOException, SAXException, XMLStreamException {
        final int vlen = value.length();
        if (this.buf.length <= vlen) {
            this.buf = new char[Math.max(this.buf.length * 2, vlen + 1)];
        }
        if (needsSP) {
            value.getChars(0, vlen, this.buf, 1);
            this.buf[0] = ' ';
        }
        else {
            value.getChars(0, vlen, this.buf, 0);
        }
        this.out.characters(this.buf, 0, vlen + (needsSP ? 1 : 0));
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSP) throws IOException, SAXException, XMLStreamException {
        final int vlen = value.length();
        if (this.buf.length <= vlen) {
            this.buf = new char[Math.max(this.buf.length * 2, vlen + 1)];
        }
        if (needsSP) {
            value.writeTo(this.buf, 1);
            this.buf[0] = ' ';
        }
        else {
            value.writeTo(this.buf, 0);
        }
        this.out.characters(this.buf, 0, vlen + (needsSP ? 1 : 0));
    }
}
