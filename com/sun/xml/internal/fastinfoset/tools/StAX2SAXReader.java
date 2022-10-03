package com.sun.xml.internal.fastinfoset.tools;

import org.xml.sax.SAXException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public class StAX2SAXReader
{
    ContentHandler _handler;
    LexicalHandler _lexicalHandler;
    XMLStreamReader _reader;
    
    public StAX2SAXReader(final XMLStreamReader reader, final ContentHandler handler) {
        this._handler = handler;
        this._reader = reader;
    }
    
    public StAX2SAXReader(final XMLStreamReader reader) {
        this._reader = reader;
    }
    
    public void setContentHandler(final ContentHandler handler) {
        this._handler = handler;
    }
    
    public void setLexicalHandler(final LexicalHandler lexicalHandler) {
        this._lexicalHandler = lexicalHandler;
    }
    
    public void adapt() throws XMLStreamException, SAXException {
        final AttributesImpl attrs = new AttributesImpl();
        this._handler.startDocument();
        try {
            while (this._reader.hasNext()) {
                final int event = this._reader.next();
                switch (event) {
                    case 1: {
                        for (int nsc = this._reader.getNamespaceCount(), i = 0; i < nsc; ++i) {
                            this._handler.startPrefixMapping(this._reader.getNamespacePrefix(i), this._reader.getNamespaceURI(i));
                        }
                        attrs.clear();
                        for (int nat = this._reader.getAttributeCount(), i = 0; i < nat; ++i) {
                            final QName q = this._reader.getAttributeName(i);
                            String qName = this._reader.getAttributePrefix(i);
                            if (qName == null || qName == "") {
                                qName = q.getLocalPart();
                            }
                            else {
                                qName = qName + ":" + q.getLocalPart();
                            }
                            attrs.addAttribute(this._reader.getAttributeNamespace(i), q.getLocalPart(), qName, this._reader.getAttributeType(i), this._reader.getAttributeValue(i));
                        }
                        final QName qname = this._reader.getName();
                        final String prefix = qname.getPrefix();
                        final String localPart = qname.getLocalPart();
                        this._handler.startElement(this._reader.getNamespaceURI(), localPart, (prefix.length() > 0) ? (prefix + ":" + localPart) : localPart, attrs);
                        continue;
                    }
                    case 2: {
                        final QName qname = this._reader.getName();
                        final String prefix = qname.getPrefix();
                        final String localPart = qname.getLocalPart();
                        this._handler.endElement(this._reader.getNamespaceURI(), localPart, (prefix.length() > 0) ? (prefix + ":" + localPart) : localPart);
                        for (int nsc = this._reader.getNamespaceCount(), i = 0; i < nsc; ++i) {
                            this._handler.endPrefixMapping(this._reader.getNamespacePrefix(i));
                        }
                        continue;
                    }
                    case 4: {
                        this._handler.characters(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
                        continue;
                    }
                    case 5: {
                        this._lexicalHandler.comment(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
                        continue;
                    }
                    case 3: {
                        this._handler.processingInstruction(this._reader.getPITarget(), this._reader.getPIData());
                        continue;
                    }
                    case 8: {
                        continue;
                    }
                    default: {
                        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[] { event }));
                    }
                }
            }
        }
        catch (final XMLStreamException e) {
            this._handler.endDocument();
            throw e;
        }
        this._handler.endDocument();
    }
}
