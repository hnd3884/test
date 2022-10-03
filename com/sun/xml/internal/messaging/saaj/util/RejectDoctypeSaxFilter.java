package com.sun.xml.internal.messaging.saaj.util;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.parsers.SAXParser;
import java.util.logging.Logger;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class RejectDoctypeSaxFilter extends XMLFilterImpl implements XMLReader, LexicalHandler
{
    protected static final Logger log;
    static final String LEXICAL_HANDLER_PROP = "http://xml.org/sax/properties/lexical-handler";
    static final String WSU_NS;
    static final String SIGNATURE_LNAME;
    static final String ENCRYPTED_DATA_LNAME;
    static final String DSIG_NS;
    static final String XENC_NS;
    static final String ID_NAME;
    private LexicalHandler lexicalHandler;
    
    public RejectDoctypeSaxFilter(final SAXParser saxParser) throws SOAPException {
        XMLReader xmlReader;
        try {
            xmlReader = saxParser.getXMLReader();
        }
        catch (final Exception e) {
            RejectDoctypeSaxFilter.log.severe("SAAJ0602.util.getXMLReader.exception");
            throw new SOAPExceptionImpl("Couldn't get an XMLReader while constructing a RejectDoctypeSaxFilter", e);
        }
        try {
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
        }
        catch (final Exception e) {
            RejectDoctypeSaxFilter.log.severe("SAAJ0603.util.setProperty.exception");
            throw new SOAPExceptionImpl("Couldn't set the lexical handler property while constructing a RejectDoctypeSaxFilter", e);
        }
        this.setParent(xmlReader);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            this.lexicalHandler = (LexicalHandler)value;
        }
        else {
            super.setProperty(name, value);
        }
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        throw new SAXException("Document Type Declaration is not allowed");
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startEntity(name);
        }
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endEntity(name);
        }
    }
    
    @Override
    public void startCDATA() throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startCDATA();
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endCDATA();
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.comment(ch, start, length);
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (atts != null) {
            boolean eos = false;
            if (namespaceURI == RejectDoctypeSaxFilter.DSIG_NS || RejectDoctypeSaxFilter.XENC_NS == namespaceURI) {
                eos = true;
            }
            final int length = atts.getLength();
            final AttributesImpl attrImpl = new AttributesImpl();
            for (int i = 0; i < length; ++i) {
                final String name = atts.getLocalName(i);
                if (name != null && name.equals("Id")) {
                    if (eos || atts.getURI(i) == RejectDoctypeSaxFilter.WSU_NS) {
                        attrImpl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), RejectDoctypeSaxFilter.ID_NAME, atts.getValue(i));
                    }
                    else {
                        attrImpl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
                    }
                }
                else {
                    attrImpl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
                }
            }
            super.startElement(namespaceURI, localName, qName, attrImpl);
        }
        else {
            super.startElement(namespaceURI, localName, qName, null);
        }
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.util", "com.sun.xml.internal.messaging.saaj.util.LocalStrings");
        WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
        SIGNATURE_LNAME = "Signature".intern();
        ENCRYPTED_DATA_LNAME = "EncryptedData".intern();
        DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
        XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
        ID_NAME = "ID".intern();
    }
}
