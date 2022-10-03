package com.sun.xml.internal.ws.handler;

import java.util.Collection;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Attachment;
import javax.activation.DataHandler;
import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

class MessageContextImpl implements MessageContext
{
    private final Set<String> handlerScopeProps;
    private final Packet packet;
    private final Map<String, Object> asMapIncludingInvocationProperties;
    
    public MessageContextImpl(final Packet packet) {
        this.packet = packet;
        this.asMapIncludingInvocationProperties = packet.asMapIncludingInvocationProperties();
        this.handlerScopeProps = packet.getHandlerScopePropertyNames(false);
    }
    
    protected void updatePacket() {
        throw new UnsupportedOperationException("wrong call");
    }
    
    @Override
    public void setScope(final String name, final Scope scope) {
        if (!this.containsKey(name)) {
            throw new IllegalArgumentException("Property " + name + " does not exist.");
        }
        if (scope == Scope.APPLICATION) {
            this.handlerScopeProps.remove(name);
        }
        else {
            this.handlerScopeProps.add(name);
        }
    }
    
    @Override
    public Scope getScope(final String name) {
        if (!this.containsKey(name)) {
            throw new IllegalArgumentException("Property " + name + " does not exist.");
        }
        if (this.handlerScopeProps.contains(name)) {
            return Scope.HANDLER;
        }
        return Scope.APPLICATION;
    }
    
    @Override
    public int size() {
        return this.asMapIncludingInvocationProperties.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.asMapIncludingInvocationProperties.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.asMapIncludingInvocationProperties.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.asMapIncludingInvocationProperties.containsValue(value);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        if (!this.asMapIncludingInvocationProperties.containsKey(key)) {
            this.handlerScopeProps.add(key);
        }
        return this.asMapIncludingInvocationProperties.put(key, value);
    }
    
    @Override
    public Object get(final Object key) {
        if (key == null) {
            return null;
        }
        final Object value = this.asMapIncludingInvocationProperties.get(key);
        if (key.equals("javax.xml.ws.binding.attachments.outbound") || key.equals("javax.xml.ws.binding.attachments.inbound")) {
            Map<String, DataHandler> atts = (Map<String, DataHandler>)value;
            if (atts == null) {
                atts = new HashMap<String, DataHandler>();
            }
            final AttachmentSet attSet = this.packet.getMessage().getAttachments();
            for (final Attachment att : attSet) {
                final String cid = att.getContentId();
                if (cid.indexOf("@jaxws.sun.com") == -1) {
                    Object a = atts.get(cid);
                    if (a != null) {
                        continue;
                    }
                    a = atts.get("<" + cid + ">");
                    if (a != null) {
                        continue;
                    }
                    atts.put(att.getContentId(), att.asDataHandler());
                }
                else {
                    atts.put(att.getContentId(), att.asDataHandler());
                }
            }
            return atts;
        }
        return value;
    }
    
    @Override
    public void putAll(final Map<? extends String, ?> t) {
        for (final String key : t.keySet()) {
            if (!this.asMapIncludingInvocationProperties.containsKey(key)) {
                this.handlerScopeProps.add(key);
            }
        }
        this.asMapIncludingInvocationProperties.putAll(t);
    }
    
    @Override
    public void clear() {
        this.asMapIncludingInvocationProperties.clear();
    }
    
    @Override
    public Object remove(final Object key) {
        this.handlerScopeProps.remove(key);
        return this.asMapIncludingInvocationProperties.remove(key);
    }
    
    @Override
    public Set<String> keySet() {
        return this.asMapIncludingInvocationProperties.keySet();
    }
    
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.asMapIncludingInvocationProperties.entrySet();
    }
    
    @Override
    public Collection<Object> values() {
        return this.asMapIncludingInvocationProperties.values();
    }
}
