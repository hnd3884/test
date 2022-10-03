package com.sun.xml.internal.ws.client;

import java.util.Iterator;
import java.util.Collections;
import com.sun.xml.internal.ws.handler.HandlerException;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.handler.MessageHandler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.Handler;
import java.util.List;
import java.util.Set;

public class HandlerConfiguration
{
    private final Set<String> roles;
    private final List<Handler> handlerChain;
    private final List<LogicalHandler> logicalHandlers;
    private final List<SOAPHandler> soapHandlers;
    private final List<MessageHandler> messageHandlers;
    private final Set<QName> handlerKnownHeaders;
    
    public HandlerConfiguration(final Set<String> roles, final List<Handler> handlerChain) {
        this.roles = roles;
        this.handlerChain = handlerChain;
        this.logicalHandlers = new ArrayList<LogicalHandler>();
        this.soapHandlers = new ArrayList<SOAPHandler>();
        this.messageHandlers = new ArrayList<MessageHandler>();
        final Set<QName> modHandlerKnownHeaders = new HashSet<QName>();
        for (final Handler handler : handlerChain) {
            if (handler instanceof LogicalHandler) {
                this.logicalHandlers.add((LogicalHandler)handler);
            }
            else if (handler instanceof SOAPHandler) {
                this.soapHandlers.add((SOAPHandler)handler);
                final Set<QName> headers = ((SOAPHandler)handler).getHeaders();
                if (headers == null) {
                    continue;
                }
                modHandlerKnownHeaders.addAll(headers);
            }
            else {
                if (!(handler instanceof MessageHandler)) {
                    throw new HandlerException("handler.not.valid.type", new Object[] { handler.getClass() });
                }
                this.messageHandlers.add((MessageHandler)handler);
                final Set<QName> headers = ((MessageHandler)handler).getHeaders();
                if (headers == null) {
                    continue;
                }
                modHandlerKnownHeaders.addAll(headers);
            }
        }
        this.handlerKnownHeaders = Collections.unmodifiableSet((Set<? extends QName>)modHandlerKnownHeaders);
    }
    
    public HandlerConfiguration(final Set<String> roles, final HandlerConfiguration oldConfig) {
        this.roles = roles;
        this.handlerChain = oldConfig.handlerChain;
        this.logicalHandlers = oldConfig.logicalHandlers;
        this.soapHandlers = oldConfig.soapHandlers;
        this.messageHandlers = oldConfig.messageHandlers;
        this.handlerKnownHeaders = oldConfig.handlerKnownHeaders;
    }
    
    public Set<String> getRoles() {
        return this.roles;
    }
    
    public List<Handler> getHandlerChain() {
        if (this.handlerChain == null) {
            return (List<Handler>)Collections.emptyList();
        }
        return new ArrayList<Handler>(this.handlerChain);
    }
    
    public List<LogicalHandler> getLogicalHandlers() {
        return this.logicalHandlers;
    }
    
    public List<SOAPHandler> getSoapHandlers() {
        return this.soapHandlers;
    }
    
    public List<MessageHandler> getMessageHandlers() {
        return this.messageHandlers;
    }
    
    public Set<QName> getHandlerKnownHeaders() {
        return this.handlerKnownHeaders;
    }
}
