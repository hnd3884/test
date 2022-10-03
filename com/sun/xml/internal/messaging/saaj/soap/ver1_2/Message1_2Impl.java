package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.soap.SOAPPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPConstants;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;

public class Message1_2Impl extends MessageImpl implements SOAPConstants
{
    public Message1_2Impl() {
    }
    
    public Message1_2Impl(final SOAPMessage msg) {
        super(msg);
    }
    
    public Message1_2Impl(final boolean isFastInfoset, final boolean acceptFastInfoset) {
        super(isFastInfoset, acceptFastInfoset);
    }
    
    public Message1_2Impl(final MimeHeaders headers, final InputStream in) throws IOException, SOAPExceptionImpl {
        super(headers, in);
    }
    
    public Message1_2Impl(final MimeHeaders headers, final ContentType ct, final int stat, final InputStream in) throws SOAPExceptionImpl {
        super(headers, ct, stat, in);
    }
    
    @Override
    public SOAPPart getSOAPPart() {
        if (this.soapPartImpl == null) {
            this.soapPartImpl = new SOAPPart1_2Impl(this);
        }
        return this.soapPartImpl;
    }
    
    @Override
    protected boolean isCorrectSoapVersion(final int contentTypeId) {
        return (contentTypeId & 0x8) != 0x0;
    }
    
    @Override
    protected String getExpectedContentType() {
        return this.isFastInfoset ? "application/soap+fastinfoset" : "application/soap+xml";
    }
    
    @Override
    protected String getExpectedAcceptHeader() {
        final String accept = "application/soap+xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        return this.acceptFastInfoset ? ("application/soap+fastinfoset, " + accept) : accept;
    }
}
