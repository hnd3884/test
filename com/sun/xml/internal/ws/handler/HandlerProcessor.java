package com.sun.xml.internal.ws.handler;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.ProtocolException;
import java.util.logging.Level;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.ws.handler.Handler;
import java.util.List;
import java.util.logging.Logger;

abstract class HandlerProcessor<C extends MessageUpdatableContext>
{
    boolean isClient;
    static final Logger logger;
    private List<? extends Handler> handlers;
    WSBinding binding;
    private int index;
    private HandlerTube owner;
    
    protected HandlerProcessor(final HandlerTube owner, final WSBinding binding, List<? extends Handler> chain) {
        this.index = -1;
        this.owner = owner;
        if (chain == null) {
            chain = new ArrayList<Handler>();
        }
        this.handlers = chain;
        this.binding = binding;
    }
    
    int getIndex() {
        return this.index;
    }
    
    void setIndex(final int i) {
        this.index = i;
    }
    
    public boolean callHandlersRequest(final Direction direction, final C context, final boolean responseExpected) {
        this.setDirection(direction, context);
        boolean result;
        try {
            if (direction == Direction.OUTBOUND) {
                result = this.callHandleMessage(context, 0, this.handlers.size() - 1);
            }
            else {
                result = this.callHandleMessage(context, this.handlers.size() - 1, 0);
            }
        }
        catch (final ProtocolException pe) {
            HandlerProcessor.logger.log(Level.FINER, "exception in handler chain", pe);
            if (responseExpected) {
                this.insertFaultMessage(context, pe);
                this.reverseDirection(direction, context);
                this.setHandleFaultProperty();
                if (direction == Direction.OUTBOUND) {
                    this.callHandleFault(context, this.getIndex() - 1, 0);
                }
                else {
                    this.callHandleFault(context, this.getIndex() + 1, this.handlers.size() - 1);
                }
                return false;
            }
            throw pe;
        }
        catch (final RuntimeException re) {
            HandlerProcessor.logger.log(Level.FINER, "exception in handler chain", re);
            throw re;
        }
        if (!result) {
            if (responseExpected) {
                this.reverseDirection(direction, context);
                if (direction == Direction.OUTBOUND) {
                    this.callHandleMessageReverse(context, this.getIndex() - 1, 0);
                }
                else {
                    this.callHandleMessageReverse(context, this.getIndex() + 1, this.handlers.size() - 1);
                }
            }
            else {
                this.setHandleFalseProperty();
            }
            return false;
        }
        return result;
    }
    
    public void callHandlersResponse(final Direction direction, final C context, final boolean isFault) {
        this.setDirection(direction, context);
        try {
            if (isFault) {
                if (direction == Direction.OUTBOUND) {
                    this.callHandleFault(context, 0, this.handlers.size() - 1);
                }
                else {
                    this.callHandleFault(context, this.handlers.size() - 1, 0);
                }
            }
            else if (direction == Direction.OUTBOUND) {
                this.callHandleMessageReverse(context, 0, this.handlers.size() - 1);
            }
            else {
                this.callHandleMessageReverse(context, this.handlers.size() - 1, 0);
            }
        }
        catch (final RuntimeException re) {
            HandlerProcessor.logger.log(Level.FINER, "exception in handler chain", re);
            throw re;
        }
    }
    
    private void reverseDirection(final Direction origDirection, final C context) {
        if (origDirection == Direction.OUTBOUND) {
            context.put("javax.xml.ws.handler.message.outbound", (Object)false);
        }
        else {
            context.put("javax.xml.ws.handler.message.outbound", (Object)true);
        }
    }
    
    private void setDirection(final Direction direction, final C context) {
        if (direction == Direction.OUTBOUND) {
            context.put("javax.xml.ws.handler.message.outbound", (Object)true);
        }
        else {
            context.put("javax.xml.ws.handler.message.outbound", (Object)false);
        }
    }
    
    private void setHandleFaultProperty() {
        this.owner.setHandleFault();
    }
    
    private void setHandleFalseProperty() {
        this.owner.setHandleFalse();
    }
    
    abstract void insertFaultMessage(final C p0, final ProtocolException p1);
    
    private boolean callHandleMessage(final C context, final int start, final int end) {
        int i = start;
        try {
            if (start > end) {
                while (i >= end) {
                    if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                        this.setIndex(i);
                        return false;
                    }
                    --i;
                }
            }
            else {
                while (i <= end) {
                    if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                        this.setIndex(i);
                        return false;
                    }
                    ++i;
                }
            }
        }
        catch (final RuntimeException e) {
            this.setIndex(i);
            throw e;
        }
        return true;
    }
    
    private boolean callHandleMessageReverse(final C context, final int start, final int end) {
        if (this.handlers.isEmpty() || start == -1 || start == this.handlers.size()) {
            return false;
        }
        int i;
        if ((i = start) > end) {
            while (i >= end) {
                if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                    this.setHandleFalseProperty();
                    return false;
                }
                --i;
            }
        }
        else {
            while (i <= end) {
                if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                    this.setHandleFalseProperty();
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
    
    private boolean callHandleFault(final C context, final int start, final int end) {
        if (this.handlers.isEmpty() || start == -1 || start == this.handlers.size()) {
            return false;
        }
        int i;
        if ((i = start) > end) {
            try {
                while (i >= end) {
                    if (!((Handler)this.handlers.get(i)).handleFault(context)) {
                        return false;
                    }
                    --i;
                }
                return true;
            }
            catch (final RuntimeException re) {
                HandlerProcessor.logger.log(Level.FINER, "exception in handler chain", re);
                throw re;
            }
        }
        try {
            while (i <= end) {
                if (!((Handler)this.handlers.get(i)).handleFault(context)) {
                    return false;
                }
                ++i;
            }
        }
        catch (final RuntimeException re) {
            HandlerProcessor.logger.log(Level.FINER, "exception in handler chain", re);
            throw re;
        }
        return true;
    }
    
    void closeHandlers(final MessageContext context, final int start, final int end) {
        if (this.handlers.isEmpty() || start == -1) {
            return;
        }
        if (start > end) {
            for (int i = start; i >= end; --i) {
                try {
                    ((Handler)this.handlers.get(i)).close(context);
                }
                catch (final RuntimeException re) {
                    HandlerProcessor.logger.log(Level.INFO, "Exception ignored during close", re);
                }
            }
        }
        else {
            for (int i = start; i <= end; ++i) {
                try {
                    ((Handler)this.handlers.get(i)).close(context);
                }
                catch (final RuntimeException re) {
                    HandlerProcessor.logger.log(Level.INFO, "Exception ignored during close", re);
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
    }
    
    public enum RequestOrResponse
    {
        REQUEST, 
        RESPONSE;
    }
    
    public enum Direction
    {
        OUTBOUND, 
        INBOUND;
    }
}
