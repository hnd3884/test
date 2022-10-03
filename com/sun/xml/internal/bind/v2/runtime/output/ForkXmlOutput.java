package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

public final class ForkXmlOutput extends XmlOutputAbstractImpl
{
    private final XmlOutput lhs;
    private final XmlOutput rhs;
    
    public ForkXmlOutput(final XmlOutput lhs, final XmlOutput rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        this.lhs.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        this.rhs.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        this.lhs.endDocument(fragment);
        this.rhs.endDocument(fragment);
    }
    
    @Override
    public void beginStartTag(final Name name) throws IOException, XMLStreamException {
        this.lhs.beginStartTag(name);
        this.rhs.beginStartTag(name);
    }
    
    @Override
    public void attribute(final Name name, final String value) throws IOException, XMLStreamException {
        this.lhs.attribute(name, value);
        this.rhs.attribute(name, value);
    }
    
    @Override
    public void endTag(final Name name) throws IOException, SAXException, XMLStreamException {
        this.lhs.endTag(name);
        this.rhs.endTag(name);
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException, XMLStreamException {
        this.lhs.beginStartTag(prefix, localName);
        this.rhs.beginStartTag(prefix, localName);
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException, XMLStreamException {
        this.lhs.attribute(prefix, localName, value);
        this.rhs.attribute(prefix, localName, value);
    }
    
    @Override
    public void endStartTag() throws IOException, SAXException {
        this.lhs.endStartTag();
        this.rhs.endStartTag();
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException, SAXException, XMLStreamException {
        this.lhs.endTag(prefix, localName);
        this.rhs.endTag(prefix, localName);
    }
    
    @Override
    public void text(final String value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        this.lhs.text(value, needsSeparatingWhitespace);
        this.rhs.text(value, needsSeparatingWhitespace);
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        this.lhs.text(value, needsSeparatingWhitespace);
        this.rhs.text(value, needsSeparatingWhitespace);
    }
}
