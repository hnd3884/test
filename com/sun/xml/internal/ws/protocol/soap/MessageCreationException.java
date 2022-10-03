package com.sun.xml.internal.ws.protocol.soap;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

public class MessageCreationException extends ExceptionHasMessage
{
    private final SOAPVersion soapVersion;
    
    public MessageCreationException(final SOAPVersion soapVersion, final Object... args) {
        super("soap.msg.create.err", args);
        this.soapVersion = soapVersion;
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.soap";
    }
    
    @Override
    public Message getFaultMessage() {
        final QName faultCode = this.soapVersion.faultCodeClient;
        return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.getLocalizedMessage(), faultCode);
    }
}
