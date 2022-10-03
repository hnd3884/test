package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.transform.Source;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import com.sun.xml.internal.messaging.saaj.soap.EnvelopeFactory;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl;

public class SOAPPart1_2Impl extends SOAPPartImpl implements SOAPConstants
{
    protected static final Logger log;
    
    public SOAPPart1_2Impl() {
    }
    
    public SOAPPart1_2Impl(final MessageImpl message) {
        super(message);
    }
    
    @Override
    protected String getContentType() {
        return "application/soap+xml";
    }
    
    @Override
    protected Envelope createEmptyEnvelope(final String prefix) throws SOAPException {
        return new Envelope1_2Impl(this.getDocument(), prefix, true, true);
    }
    
    @Override
    protected Envelope createEnvelopeFromSource() throws SOAPException {
        final XMLDeclarationParser parser = this.lookForXmlDecl();
        final Source tmp = this.source;
        this.source = null;
        final EnvelopeImpl envelope = (EnvelopeImpl)EnvelopeFactory.createEnvelope(tmp, this);
        if (!envelope.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            SOAPPart1_2Impl.log.severe("SAAJ0415.ver1_2.msg.invalid.soap1.2");
            throw new SOAPException("InputStream does not represent a valid SOAP 1.2 Message");
        }
        if (parser != null && !this.omitXmlDecl) {
            envelope.setOmitXmlDecl("no");
            envelope.setXmlDecl(parser.getXmlDeclaration());
            envelope.setCharsetEncoding(parser.getEncoding());
        }
        return envelope;
    }
    
    @Override
    protected SOAPPartImpl duplicateType() {
        return new SOAPPart1_2Impl();
    }
    
    static {
        log = Logger.getLogger(SOAPPart1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
    }
}
