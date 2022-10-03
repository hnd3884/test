package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import javax.xml.ws.WebServiceException;
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

public class ClientLogicalHandlerTube extends HandlerTube
{
    private SEIModel seiModel;
    
    public ClientLogicalHandlerTube(final WSBinding binding, final SEIModel seiModel, final WSDLPort port, final Tube next) {
        super(next, port, binding);
        this.seiModel = seiModel;
    }
    
    public ClientLogicalHandlerTube(final WSBinding binding, final SEIModel seiModel, final Tube next, final HandlerTube cousinTube) {
        super(next, cousinTube, binding);
        this.seiModel = seiModel;
    }
    
    private ClientLogicalHandlerTube(final ClientLogicalHandlerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
    }
    
    @Override
    protected void initiateClosing(final MessageContext mc) {
        this.close(mc);
        super.initiateClosing(mc);
    }
    
    @Override
    public AbstractFilterTubeImpl copy(final TubeCloner cloner) {
        return new ClientLogicalHandlerTube(this, cloner);
    }
    
    @Override
    void setUpProcessor() {
        if (this.handlers == null) {
            this.handlers = new ArrayList<Handler>();
            final WSBinding binding = this.getBinding();
            final List<LogicalHandler> logicalSnapShot = ((BindingImpl)binding).getHandlerConfig().getLogicalHandlers();
            if (!logicalSnapShot.isEmpty()) {
                this.handlers.addAll(logicalSnapShot);
                if (binding.getSOAPVersion() == null) {
                    this.processor = new XMLHandlerProcessor(this, binding, this.handlers);
                }
                else {
                    this.processor = new SOAPHandlerProcessor(true, this, binding, this.handlers);
                }
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
    void closeHandlers(final MessageContext mc) {
        this.closeClientsideHandlers(mc);
    }
}
