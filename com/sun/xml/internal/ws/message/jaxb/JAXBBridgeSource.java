package com.sun.xml.internal.ws.message.jaxb;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXParseException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.XMLReader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.transform.sax.SAXSource;

final class JAXBBridgeSource extends SAXSource
{
    private final XMLBridge bridge;
    private final Object contentObject;
    private final XMLReader pseudoParser;
    
    public JAXBBridgeSource(final XMLBridge bridge, final Object contentObject) {
        this.pseudoParser = new XMLFilterImpl() {
            private LexicalHandler lexicalHandler;
            
            @Override
            public boolean getFeature(final String name) throws SAXNotRecognizedException {
                if (name.equals("http://xml.org/sax/features/namespaces")) {
                    return true;
                }
                if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
                    return false;
                }
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException {
                if (name.equals("http://xml.org/sax/features/namespaces") && value) {
                    return;
                }
                if (name.equals("http://xml.org/sax/features/namespace-prefixes") && !value) {
                    return;
                }
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public Object getProperty(final String name) throws SAXNotRecognizedException {
                if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                    return this.lexicalHandler;
                }
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public void setProperty(final String name, final Object value) throws SAXNotRecognizedException {
                if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                    this.lexicalHandler = (LexicalHandler)value;
                    return;
                }
                throw new SAXNotRecognizedException(name);
            }
            
            @Override
            public void parse(final InputSource input) throws SAXException {
                this.parse();
            }
            
            @Override
            public void parse(final String systemId) throws SAXException {
                this.parse();
            }
            
            public void parse() throws SAXException {
                try {
                    this.startDocument();
                    JAXBBridgeSource.this.bridge.marshal(JAXBBridgeSource.this.contentObject, this, null);
                    this.endDocument();
                }
                catch (final JAXBException e) {
                    final SAXParseException se = new SAXParseException(e.getMessage(), null, null, -1, -1, e);
                    this.fatalError(se);
                    throw se;
                }
            }
        };
        this.bridge = bridge;
        this.contentObject = contentObject;
        super.setXMLReader(this.pseudoParser);
        super.setInputSource(new InputSource());
    }
}
