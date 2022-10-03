package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import java.util.logging.Level;
import javax.xml.soap.SOAPPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;

public class Message1_1Impl extends MessageImpl implements SOAPConstants
{
    protected static final Logger log;
    
    public Message1_1Impl() {
    }
    
    public Message1_1Impl(final boolean isFastInfoset, final boolean acceptFastInfoset) {
        super(isFastInfoset, acceptFastInfoset);
    }
    
    public Message1_1Impl(final SOAPMessage msg) {
        super(msg);
    }
    
    public Message1_1Impl(final MimeHeaders headers, final InputStream in) throws IOException, SOAPExceptionImpl {
        super(headers, in);
    }
    
    public Message1_1Impl(final MimeHeaders headers, final ContentType ct, final int stat, final InputStream in) throws SOAPExceptionImpl {
        super(headers, ct, stat, in);
    }
    
    @Override
    public SOAPPart getSOAPPart() {
        if (this.soapPartImpl == null) {
            this.soapPartImpl = new SOAPPart1_1Impl(this);
        }
        return this.soapPartImpl;
    }
    
    @Override
    protected boolean isCorrectSoapVersion(final int contentTypeId) {
        return (contentTypeId & 0x4) != 0x0;
    }
    
    @Override
    public String getAction() {
        Message1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Action" });
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }
    
    @Override
    public void setAction(final String type) {
        Message1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Action" });
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }
    
    @Override
    public String getCharset() {
        Message1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Charset" });
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }
    
    @Override
    public void setCharset(final String charset) {
        Message1_1Impl.log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Charset" });
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }
    
    @Override
    protected String getExpectedContentType() {
        return this.isFastInfoset ? "application/fastinfoset" : "text/xml";
    }
    
    @Override
    protected String getExpectedAcceptHeader() {
        final String accept = "text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        return this.acceptFastInfoset ? ("application/fastinfoset, " + accept) : accept;
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
    }
}
