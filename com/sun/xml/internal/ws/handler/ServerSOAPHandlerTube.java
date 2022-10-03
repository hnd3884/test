package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import javax.xml.ws.handler.MessageContext;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import javax.activation.DataHandler;
import java.util.Map;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.handler.soap.SOAPHandler;
import java.util.List;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.HashSet;
import java.util.Collection;
import com.sun.xml.internal.ws.binding.BindingImpl;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Set;

public class ServerSOAPHandlerTube extends HandlerTube
{
    private Set<String> roles;
    
    public ServerSOAPHandlerTube(final WSBinding binding, final WSDLPort port, final Tube next) {
        super(next, port, binding);
        if (binding.getSOAPVersion() != null) {}
        this.setUpHandlersOnce();
    }
    
    public ServerSOAPHandlerTube(final WSBinding binding, final Tube next, final HandlerTube cousinTube) {
        super(next, cousinTube, binding);
        this.setUpHandlersOnce();
    }
    
    private ServerSOAPHandlerTube(final ServerSOAPHandlerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.handlers = that.handlers;
        this.roles = that.roles;
    }
    
    @Override
    public AbstractFilterTubeImpl copy(final TubeCloner cloner) {
        return new ServerSOAPHandlerTube(this, cloner);
    }
    
    private void setUpHandlersOnce() {
        this.handlers = new ArrayList<Handler>();
        final HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
        final List<SOAPHandler> soapSnapShot = handlerConfig.getSoapHandlers();
        if (!soapSnapShot.isEmpty()) {
            this.handlers.addAll(soapSnapShot);
            (this.roles = new HashSet<String>()).addAll(handlerConfig.getRoles());
        }
    }
    
    @Override
    protected void resetProcessor() {
        this.processor = null;
    }
    
    @Override
    void setUpProcessor() {
        if (!this.handlers.isEmpty() && this.processor == null) {
            this.processor = new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
        }
    }
    
    @Override
    MessageUpdatableContext getContext(final Packet packet) {
        final SOAPMessageContextImpl context = new SOAPMessageContextImpl(this.getBinding(), packet, this.roles);
        return context;
    }
    
    @Override
    boolean callHandlersOnRequest(final MessageUpdatableContext context, final boolean isOneWay) {
        boolean handlerResult;
        try {
            handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.INBOUND, context, !isOneWay);
        }
        catch (final RuntimeException re) {
            this.remedyActionTaken = true;
            throw re;
        }
        if (!handlerResult) {
            this.remedyActionTaken = true;
        }
        return handlerResult;
    }
    
    @Override
    void callHandlersOnResponse(final MessageUpdatableContext context, final boolean handleFault) {
        final Map<String, DataHandler> atts = (Map<String, DataHandler>)context.get("javax.xml.ws.binding.attachments.outbound");
        final AttachmentSet attSet = context.packet.getMessage().getAttachments();
        for (final Map.Entry<String, DataHandler> entry : atts.entrySet()) {
            final String cid = entry.getKey();
            if (attSet.get(cid) == null) {
                final Attachment att = new DataHandlerAttachment(cid, atts.get(cid));
                attSet.add(att);
            }
        }
        try {
            this.processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, context, handleFault);
        }
        catch (final WebServiceException wse) {
            throw wse;
        }
        catch (final RuntimeException re) {
            throw re;
        }
    }
    
    @Override
    void closeHandlers(final MessageContext mc) {
        this.closeServersideHandlers(mc);
    }
}
