package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Messages;
import javax.xml.ws.http.HTTPException;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import java.util.List;
import com.sun.xml.internal.ws.api.WSBinding;

final class XMLHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C>
{
    public XMLHandlerProcessor(final HandlerTube owner, final WSBinding binding, final List<? extends Handler> chain) {
        super(owner, binding, chain);
    }
    
    @Override
    final void insertFaultMessage(final C context, final ProtocolException exception) {
        if (exception instanceof HTTPException) {
            context.put("javax.xml.ws.http.response.code", (Object)((HTTPException)exception).getStatusCode());
        }
        if (context != null) {
            context.setPacketMessage(Messages.createEmpty(this.binding.getSOAPVersion()));
        }
    }
}
