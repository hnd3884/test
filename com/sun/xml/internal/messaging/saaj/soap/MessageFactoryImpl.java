package com.sun.xml.internal.messaging.saaj.soap;

import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Message1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Message1_1Impl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;

public class MessageFactoryImpl extends MessageFactory
{
    protected static final Logger log;
    protected OutputStream listener;
    protected boolean lazyAttachments;
    
    public MessageFactoryImpl() {
        this.lazyAttachments = false;
    }
    
    public OutputStream listen(final OutputStream newListener) {
        final OutputStream oldListener = this.listener;
        this.listener = newListener;
        return oldListener;
    }
    
    @Override
    public SOAPMessage createMessage() throws SOAPException {
        throw new UnsupportedOperationException();
    }
    
    public SOAPMessage createMessage(final boolean isFastInfoset, final boolean acceptFastInfoset) throws SOAPException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SOAPMessage createMessage(final MimeHeaders headers, InputStream in) throws SOAPException, IOException {
        final String contentTypeString = MessageImpl.getContentType(headers);
        if (this.listener != null) {
            in = new TeeInputStream(in, this.listener);
        }
        try {
            final ContentType contentType = new ContentType(contentTypeString);
            final int stat = MessageImpl.identifyContentType(contentType);
            if (MessageImpl.isSoap1_1Content(stat)) {
                return new Message1_1Impl(headers, contentType, stat, in);
            }
            if (MessageImpl.isSoap1_2Content(stat)) {
                return new Message1_2Impl(headers, contentType, stat, in);
            }
            MessageFactoryImpl.log.severe("SAAJ0530.soap.unknown.Content-Type");
            throw new SOAPExceptionImpl("Unrecognized Content-Type");
        }
        catch (final ParseException e) {
            MessageFactoryImpl.log.severe("SAAJ0531.soap.cannot.parse.Content-Type");
            throw new SOAPExceptionImpl("Unable to parse content type: " + e.getMessage());
        }
    }
    
    protected static final String getContentType(final MimeHeaders headers) {
        final String[] values = headers.getHeader("Content-Type");
        if (values == null) {
            return null;
        }
        return values[0];
    }
    
    public void setLazyAttachmentOptimization(final boolean flag) {
        this.lazyAttachments = flag;
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
    }
}
