package com.sun.xml.internal.ws.handler;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.message.Messages;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import java.util.List;
import com.sun.xml.internal.ws.api.WSBinding;

final class SOAPHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C>
{
    public SOAPHandlerProcessor(final boolean isClient, final HandlerTube owner, final WSBinding binding, final List<? extends Handler> chain) {
        super(owner, binding, chain);
        this.isClient = isClient;
    }
    
    @Override
    final void insertFaultMessage(final C context, final ProtocolException exception) {
        try {
            if (!context.getPacketMessage().isFault()) {
                final Message faultMessage = Messages.create(this.binding.getSOAPVersion(), exception, this.determineFaultCode(this.binding.getSOAPVersion()));
                context.setPacketMessage(faultMessage);
            }
        }
        catch (final Exception e) {
            SOAPHandlerProcessor.logger.log(Level.SEVERE, "exception while creating fault message in handler chain", e);
            throw new RuntimeException(e);
        }
    }
    
    private QName determineFaultCode(final SOAPVersion soapVersion) {
        return this.isClient ? soapVersion.faultCodeClient : soapVersion.faultCodeServer;
    }
}
