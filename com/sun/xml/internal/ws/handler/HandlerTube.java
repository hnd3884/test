package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import javax.xml.ws.handler.MessageContext;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.ws.handler.Handler;
import java.util.List;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

public abstract class HandlerTube extends AbstractFilterTubeImpl
{
    HandlerTube cousinTube;
    protected List<Handler> handlers;
    HandlerProcessor processor;
    boolean remedyActionTaken;
    @Nullable
    protected final WSDLPort port;
    boolean requestProcessingSucessful;
    private WSBinding binding;
    private HandlerConfiguration hc;
    private HandlerTubeExchange exchange;
    
    public HandlerTube(final Tube next, final WSDLPort port, final WSBinding binding) {
        super(next);
        this.remedyActionTaken = false;
        this.requestProcessingSucessful = false;
        this.port = port;
        this.binding = binding;
    }
    
    public HandlerTube(final Tube next, final HandlerTube cousinTube, final WSBinding binding) {
        super(next);
        this.remedyActionTaken = false;
        this.requestProcessingSucessful = false;
        this.cousinTube = cousinTube;
        this.binding = binding;
        if (cousinTube != null) {
            this.port = cousinTube.port;
        }
        else {
            this.port = null;
        }
    }
    
    protected HandlerTube(final HandlerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.remedyActionTaken = false;
        this.requestProcessingSucessful = false;
        if (that.cousinTube != null) {
            this.cousinTube = cloner.copy(that.cousinTube);
        }
        this.port = that.port;
        this.binding = that.binding;
    }
    
    protected WSBinding getBinding() {
        return this.binding;
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        this.setupExchange();
        if (this.isHandleFalse()) {
            this.remedyActionTaken = true;
            return this.doInvoke(super.next, request);
        }
        this.setUpProcessorInternal();
        final MessageUpdatableContext context = this.getContext(request);
        final boolean isOneWay = this.checkOneWay(request);
        try {
            if (!this.isHandlerChainEmpty()) {
                final boolean handlerResult = this.callHandlersOnRequest(context, isOneWay);
                context.updatePacket();
                if (!isOneWay && !handlerResult) {
                    return this.doReturnWith(request);
                }
            }
            this.requestProcessingSucessful = true;
            return this.doInvoke(super.next, request);
        }
        catch (final RuntimeException re) {
            if (isOneWay) {
                if (request.transportBackChannel != null) {
                    request.transportBackChannel.close();
                }
                request.setMessage(null);
                return this.doReturnWith(request);
            }
            throw re;
        }
        finally {
            if (!this.requestProcessingSucessful) {
                this.initiateClosing(context.getMessageContext());
            }
        }
    }
    
    @Override
    public NextAction processResponse(final Packet response) {
        this.setupExchange();
        final MessageUpdatableContext context = this.getContext(response);
        try {
            if (this.isHandleFalse() || response.getMessage() == null) {
                return this.doReturnWith(response);
            }
            this.setUpProcessorInternal();
            final boolean isFault = this.isHandleFault(response);
            if (!this.isHandlerChainEmpty()) {
                this.callHandlersOnResponse(context, isFault);
            }
        }
        finally {
            this.initiateClosing(context.getMessageContext());
        }
        context.updatePacket();
        return this.doReturnWith(response);
    }
    
    @Override
    public NextAction processException(final Throwable t) {
        try {
            return this.doThrow(t);
        }
        finally {
            final Packet packet = Fiber.current().getPacket();
            final MessageUpdatableContext context = this.getContext(packet);
            this.initiateClosing(context.getMessageContext());
        }
    }
    
    protected void initiateClosing(final MessageContext mc) {
    }
    
    public final void close(final MessageContext msgContext) {
        if (this.requestProcessingSucessful && this.cousinTube != null) {
            this.cousinTube.close(msgContext);
        }
        if (this.processor != null) {
            this.closeHandlers(msgContext);
        }
        this.exchange = null;
        this.requestProcessingSucessful = false;
    }
    
    abstract void closeHandlers(final MessageContext p0);
    
    protected void closeClientsideHandlers(final MessageContext msgContext) {
        if (this.processor == null) {
            return;
        }
        if (this.remedyActionTaken) {
            this.processor.closeHandlers(msgContext, this.processor.getIndex(), 0);
            this.processor.setIndex(-1);
            this.remedyActionTaken = false;
        }
        else {
            this.processor.closeHandlers(msgContext, this.handlers.size() - 1, 0);
        }
    }
    
    protected void closeServersideHandlers(final MessageContext msgContext) {
        if (this.processor == null) {
            return;
        }
        if (this.remedyActionTaken) {
            this.processor.closeHandlers(msgContext, this.processor.getIndex(), this.handlers.size() - 1);
            this.processor.setIndex(-1);
            this.remedyActionTaken = false;
        }
        else {
            this.processor.closeHandlers(msgContext, 0, this.handlers.size() - 1);
        }
    }
    
    abstract void callHandlersOnResponse(final MessageUpdatableContext p0, final boolean p1);
    
    abstract boolean callHandlersOnRequest(final MessageUpdatableContext p0, final boolean p1);
    
    private boolean checkOneWay(final Packet packet) {
        if (this.port != null) {
            return packet.getMessage().isOneWay(this.port);
        }
        return packet.expectReply == null || !packet.expectReply;
    }
    
    private void setUpProcessorInternal() {
        final HandlerConfiguration hc = ((BindingImpl)this.binding).getHandlerConfig();
        if (hc != this.hc) {
            this.resetProcessor();
        }
        this.hc = hc;
        this.setUpProcessor();
    }
    
    abstract void setUpProcessor();
    
    protected void resetProcessor() {
        this.handlers = null;
    }
    
    public final boolean isHandlerChainEmpty() {
        return this.handlers.isEmpty();
    }
    
    abstract MessageUpdatableContext getContext(final Packet p0);
    
    private boolean isHandleFault(final Packet packet) {
        if (this.cousinTube != null) {
            return this.exchange.isHandleFault();
        }
        final boolean isFault = packet.getMessage().isFault();
        this.exchange.setHandleFault(isFault);
        return isFault;
    }
    
    final void setHandleFault() {
        this.exchange.setHandleFault(true);
    }
    
    private boolean isHandleFalse() {
        return this.exchange.isHandleFalse();
    }
    
    final void setHandleFalse() {
        this.exchange.setHandleFalse();
    }
    
    private void setupExchange() {
        if (this.exchange == null) {
            this.exchange = new HandlerTubeExchange();
            if (this.cousinTube != null) {
                this.cousinTube.exchange = this.exchange;
            }
        }
        else if (this.cousinTube != null) {
            this.cousinTube.exchange = this.exchange;
        }
    }
    
    static final class HandlerTubeExchange
    {
        private boolean handleFalse;
        private boolean handleFault;
        
        boolean isHandleFault() {
            return this.handleFault;
        }
        
        void setHandleFault(final boolean isFault) {
            this.handleFault = isFault;
        }
        
        public boolean isHandleFalse() {
            return this.handleFalse;
        }
        
        void setHandleFalse() {
            this.handleFalse = true;
        }
    }
}
