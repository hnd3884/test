package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import javax.activation.DataHandler;
import java.util.Map;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.handler.LogicalHandler;
import java.util.List;
import java.util.Collection;
import com.sun.xml.internal.ws.binding.BindingImpl;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import javax.xml.ws.handler.MessageContext;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;

public class ServerLogicalHandlerTube extends HandlerTube
{
    private SEIModel seiModel;
    
    public ServerLogicalHandlerTube(final WSBinding binding, final SEIModel seiModel, final WSDLPort port, final Tube next) {
        super(next, port, binding);
        this.seiModel = seiModel;
        this.setUpHandlersOnce();
    }
    
    public ServerLogicalHandlerTube(final WSBinding binding, final SEIModel seiModel, final Tube next, final HandlerTube cousinTube) {
        super(next, cousinTube, binding);
        this.seiModel = seiModel;
        this.setUpHandlersOnce();
    }
    
    private ServerLogicalHandlerTube(final ServerLogicalHandlerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
        this.handlers = that.handlers;
    }
    
    @Override
    protected void initiateClosing(final MessageContext mc) {
        if (this.getBinding().getSOAPVersion() != null) {
            super.initiateClosing(mc);
        }
        else {
            this.close(mc);
            super.initiateClosing(mc);
        }
    }
    
    @Override
    public AbstractFilterTubeImpl copy(final TubeCloner cloner) {
        return new ServerLogicalHandlerTube(this, cloner);
    }
    
    private void setUpHandlersOnce() {
        this.handlers = new ArrayList<Handler>();
        final List<LogicalHandler> logicalSnapShot = ((BindingImpl)this.getBinding()).getHandlerConfig().getLogicalHandlers();
        if (!logicalSnapShot.isEmpty()) {
            this.handlers.addAll(logicalSnapShot);
        }
    }
    
    @Override
    protected void resetProcessor() {
        this.processor = null;
    }
    
    @Override
    void setUpProcessor() {
        if (!this.handlers.isEmpty() && this.processor == null) {
            if (this.getBinding().getSOAPVersion() == null) {
                this.processor = new XMLHandlerProcessor(this, this.getBinding(), this.handlers);
            }
            else {
                this.processor = new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
            }
        }
    }
    
    @Override
    MessageUpdatableContext getContext(final Packet packet) {
        return new LogicalMessageContextImpl(this.getBinding(), this.getBindingContext(), packet);
    }
    
    private BindingContext getBindingContext() {
        return (this.seiModel != null && this.seiModel instanceof AbstractSEIModelImpl) ? ((AbstractSEIModelImpl)this.seiModel).getBindingContext() : null;
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
            final Attachment att = new DataHandlerAttachment(cid, atts.get(cid));
            attSet.add(att);
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
