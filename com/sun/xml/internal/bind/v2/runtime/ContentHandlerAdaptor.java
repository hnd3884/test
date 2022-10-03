package com.sun.xml.internal.bind.v2.runtime;

import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.istack.internal.SAXException2;
import org.xml.sax.Attributes;
import com.sun.istack.internal.FinalArrayList;
import org.xml.sax.helpers.DefaultHandler;

final class ContentHandlerAdaptor extends DefaultHandler
{
    private final FinalArrayList<String> prefixMap;
    private final XMLSerializer serializer;
    private final StringBuffer text;
    
    ContentHandlerAdaptor(final XMLSerializer _serializer) {
        this.prefixMap = new FinalArrayList<String>();
        this.text = new StringBuffer();
        this.serializer = _serializer;
    }
    
    @Override
    public void startDocument() {
        this.prefixMap.clear();
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        this.prefixMap.add(prefix);
        this.prefixMap.add(uri);
    }
    
    private boolean containsPrefixMapping(final String prefix, final String uri) {
        for (int i = 0; i < this.prefixMap.size(); i += 2) {
            if (this.prefixMap.get(i).equals(prefix) && this.prefixMap.get(i + 1).equals(uri)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            this.flushText();
            final int len = atts.getLength();
            final String p = this.getPrefix(qName);
            if (this.containsPrefixMapping(p, namespaceURI)) {
                this.serializer.startElementForce(namespaceURI, localName, p, null);
            }
            else {
                this.serializer.startElement(namespaceURI, localName, p, null);
            }
            for (int i = 0; i < this.prefixMap.size(); i += 2) {
                this.serializer.getNamespaceContext().force(this.prefixMap.get(i + 1), this.prefixMap.get(i));
            }
            for (int i = 0; i < len; ++i) {
                final String qname = atts.getQName(i);
                if (!qname.startsWith("xmlns")) {
                    if (atts.getURI(i).length() != 0) {
                        final String prefix = this.getPrefix(qname);
                        this.serializer.getNamespaceContext().declareNamespace(atts.getURI(i), prefix, true);
                    }
                }
            }
            this.serializer.endNamespaceDecls(null);
            for (int i = 0; i < len; ++i) {
                if (!atts.getQName(i).startsWith("xmlns")) {
                    this.serializer.attribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
                }
            }
            this.prefixMap.clear();
            this.serializer.endAttributes();
        }
        catch (final IOException e) {
            throw new SAXException2(e);
        }
        catch (final XMLStreamException e2) {
            throw new SAXException2(e2);
        }
    }
    
    private String getPrefix(final String qname) {
        final int idx = qname.indexOf(58);
        final String prefix = (idx == -1) ? "" : qname.substring(0, idx);
        return prefix;
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            this.flushText();
            this.serializer.endElement();
        }
        catch (final IOException e) {
            throw new SAXException2(e);
        }
        catch (final XMLStreamException e2) {
            throw new SAXException2(e2);
        }
    }
    
    private void flushText() throws SAXException, IOException, XMLStreamException {
        if (this.text.length() != 0) {
            this.serializer.text(this.text.toString(), null);
            this.text.setLength(0);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.text.append(ch, start, length);
    }
}
