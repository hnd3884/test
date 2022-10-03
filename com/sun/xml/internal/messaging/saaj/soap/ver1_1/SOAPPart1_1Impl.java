package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import javax.xml.transform.Source;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.soap.EnvelopeFactory;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl;

public class SOAPPart1_1Impl extends SOAPPartImpl implements SOAPConstants
{
    protected static final Logger log;
    
    public SOAPPart1_1Impl() {
    }
    
    public SOAPPart1_1Impl(final MessageImpl message) {
        super(message);
    }
    
    @Override
    protected String getContentType() {
        return this.isFastInfoset() ? "application/fastinfoset" : "text/xml";
    }
    
    @Override
    protected Envelope createEnvelopeFromSource() throws SOAPException {
        final XMLDeclarationParser parser = this.lookForXmlDecl();
        final Source tmp = this.source;
        this.source = null;
        final EnvelopeImpl envelope = (EnvelopeImpl)EnvelopeFactory.createEnvelope(tmp, this);
        if (!envelope.getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/envelope/")) {
            SOAPPart1_1Impl.log.severe("SAAJ0304.ver1_1.msg.invalid.SOAP1.1");
            throw new SOAPException("InputStream does not represent a valid SOAP 1.1 Message");
        }
        if (parser != null && !this.omitXmlDecl) {
            envelope.setOmitXmlDecl("no");
            envelope.setXmlDecl(parser.getXmlDeclaration());
            envelope.setCharsetEncoding(parser.getEncoding());
        }
        return envelope;
    }
    
    @Override
    protected Envelope createEmptyEnvelope(final String prefix) throws SOAPException {
        return new Envelope1_1Impl(this.getDocument(), prefix, true, true);
    }
    
    @Override
    protected SOAPPartImpl duplicateType() {
        return new SOAPPart1_1Impl();
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
    }
}
