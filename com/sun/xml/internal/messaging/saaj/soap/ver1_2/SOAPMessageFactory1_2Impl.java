package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;

public class SOAPMessageFactory1_2Impl extends MessageFactoryImpl
{
    @Override
    public SOAPMessage createMessage() throws SOAPException {
        return new Message1_2Impl();
    }
    
    @Override
    public SOAPMessage createMessage(final boolean isFastInfoset, final boolean acceptFastInfoset) throws SOAPException {
        return new Message1_2Impl(isFastInfoset, acceptFastInfoset);
    }
    
    @Override
    public SOAPMessage createMessage(MimeHeaders headers, final InputStream in) throws IOException, SOAPExceptionImpl {
        if (headers == null) {
            headers = new MimeHeaders();
        }
        if (MessageFactoryImpl.getContentType(headers) == null) {
            headers.setHeader("Content-Type", "application/soap+xml");
        }
        final MessageImpl msg = new Message1_2Impl(headers, in);
        msg.setLazyAttachments(this.lazyAttachments);
        return msg;
    }
}
