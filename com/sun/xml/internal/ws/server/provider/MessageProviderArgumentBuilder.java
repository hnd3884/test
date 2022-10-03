package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;

final class MessageProviderArgumentBuilder extends ProviderArgumentsBuilder<Message>
{
    private final SOAPVersion soapVersion;
    
    public MessageProviderArgumentBuilder(final SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }
    
    @Override
    public Message getParameter(final Packet packet) {
        return packet.getMessage();
    }
    
    @Override
    protected Message getResponseMessage(final Message returnValue) {
        return returnValue;
    }
    
    @Override
    protected Message getResponseMessage(final Exception e) {
        return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
    }
}
