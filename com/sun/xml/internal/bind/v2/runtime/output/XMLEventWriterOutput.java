package com.sun.xml.internal.bind.v2.runtime.output;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.stream.events.XMLEvent;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.stream.events.Characters;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;

public class XMLEventWriterOutput extends XmlOutputAbstractImpl
{
    private final XMLEventWriter out;
    private final XMLEventFactory ef;
    private final Characters sp;
    
    public XMLEventWriterOutput(final XMLEventWriter out) {
        this.out = out;
        this.ef = XMLEventFactory.newInstance();
        this.sp = this.ef.createCharacters(" ");
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        if (!fragment) {
            this.out.add(this.ef.createStartDocument());
        }
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        if (!fragment) {
            this.out.add(this.ef.createEndDocument());
            this.out.flush();
        }
        super.endDocument(fragment);
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException, XMLStreamException {
        this.out.add(this.ef.createStartElement(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName));
        final NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
        if (nse.count() > 0) {
            for (int i = nse.count() - 1; i >= 0; --i) {
                final String uri = nse.getNsUri(i);
                if (uri.length() != 0 || nse.getBase() != 1) {
                    this.out.add(this.ef.createNamespace(nse.getPrefix(i), uri));
                }
            }
        }
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException, XMLStreamException {
        Attribute att;
        if (prefix == -1) {
            att = this.ef.createAttribute(localName, value);
        }
        else {
            att = this.ef.createAttribute(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName, value);
        }
        this.out.add(att);
    }
    
    @Override
    public void endStartTag() throws IOException, SAXException {
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException, SAXException, XMLStreamException {
        this.out.add(this.ef.createEndElement(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName));
    }
    
    @Override
    public void text(final String value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.add(this.sp);
        }
        this.out.add(this.ef.createCharacters(value));
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        this.text(value.toString(), needsSeparatingWhitespace);
    }
}
