package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.List;
import java.util.HashSet;
import java.util.Collection;
import com.sun.xml.internal.ws.binding.BindingImpl;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import javax.xml.ws.handler.MessageContext;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import javax.activation.DataHandler;
import java.util.Map;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.Nullable;
import java.util.Set;
import com.sun.xml.internal.ws.api.model.SEIModel;

public class ClientMessageHandlerTube extends HandlerTube
{
    private SEIModel seiModel;
    private Set<String> roles;
    
    public ClientMessageHandlerTube(@Nullable final SEIModel seiModel, final WSBinding binding, final WSDLPort port, final Tube next) {
        super(next, port, binding);
        this.seiModel = seiModel;
    }
    
    private ClientMessageHandlerTube(final ClientMessageHandlerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
    }
    
    @Override
    public AbstractFilterTubeImpl copy(final TubeCloner cloner) {
        return new ClientMessageHandlerTube(this, cloner);
    }
    
    @Override
    void callHandlersOnResponse(final MessageUpdatableContext context, final boolean handleFault) {
        try {
            this.processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, context, handleFault);
        }
        catch (final WebServiceException wse) {
            throw wse;
        }
        catch (final RuntimeException re) {
            throw new WebServiceException(re);
        }
    }
    
    @Override
    boolean callHandlersOnRequest(final MessageUpdatableContext context, final boolean isOneWay) {
        final Map<String, DataHandler> atts = (Map<String, DataHandler>)context.get("javax.xml.ws.binding.attachments.outbound");
        final AttachmentSet attSet = context.packet.getMessage().getAttachments();
        for (final Map.Entry<String, DataHandler> entry : atts.entrySet()) {
            final String cid = entry.getKey();
            if (attSet.get(cid) == null) {
                final Attachment att = new DataHandlerAttachment(cid, atts.get(cid));
                attSet.add(att);
            }
        }
        boolean handlerResult;
        try {
            handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, context, !isOneWay);
        }
        catch (final WebServiceException wse) {
            this.remedyActionTaken = true;
            throw wse;
        }
        catch (final RuntimeException re) {
            this.remedyActionTaken = true;
            throw new WebServiceException(re);
        }
        if (!handlerResult) {
            this.remedyActionTaken = true;
        }
        return handlerResult;
    }
    
    @Override
    void closeHandlers(final MessageContext mc) {
        this.closeClientsideHandlers(mc);
    }
    
    @Override
    void setUpProcessor() {
        if (this.handlers == null) {
            this.handlers = new ArrayList<Handler>();
            final HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
            final List<MessageHandler> msgHandlersSnapShot = handlerConfig.getMessageHandlers();
            if (!msgHandlersSnapShot.isEmpty()) {
                this.handlers.addAll(msgHandlersSnapShot);
                (this.roles = new HashSet<String>()).addAll(handlerConfig.getRoles());
                this.processor = new SOAPHandlerProcessor(true, this, this.getBinding(), this.handlers);
            }
        }
    }
    
    @Override
    MessageUpdatableContext getContext(final Packet p) {
        final MessageHandlerContextImpl context = new MessageHandlerContextImpl(this.seiModel, this.getBinding(), this.port, p, this.roles);
        return context;
    }
}
