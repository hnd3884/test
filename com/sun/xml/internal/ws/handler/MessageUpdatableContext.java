package com.sun.xml.internal.ws.handler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.handler.MessageContext;

public abstract class MessageUpdatableContext implements MessageContext
{
    final Packet packet;
    private MessageContextImpl ctxt;
    
    public MessageUpdatableContext(final Packet packet) {
        this.ctxt = new MessageContextImpl(packet);
        this.packet = packet;
    }
    
    abstract void updateMessage();
    
    Message getPacketMessage() {
        this.updateMessage();
        return this.packet.getMessage();
    }
    
    abstract void setPacketMessage(final Message p0);
    
    public final void updatePacket() {
        this.updateMessage();
    }
    
    MessageContextImpl getMessageContext() {
        return this.ctxt;
    }
    
    @Override
    public void setScope(final String name, final Scope scope) {
        this.ctxt.setScope(name, scope);
    }
    
    @Override
    public Scope getScope(final String name) {
        return this.ctxt.getScope(name);
    }
    
    @Override
    public void clear() {
        this.ctxt.clear();
    }
    
    @Override
    public boolean containsKey(final Object obj) {
        return this.ctxt.containsKey(obj);
    }
    
    @Override
    public boolean containsValue(final Object obj) {
        return this.ctxt.containsValue(obj);
    }
    
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.ctxt.entrySet();
    }
    
    @Override
    public Object get(final Object obj) {
        return this.ctxt.get(obj);
    }
    
    @Override
    public boolean isEmpty() {
        return this.ctxt.isEmpty();
    }
    
    @Override
    public Set<String> keySet() {
        return this.ctxt.keySet();
    }
    
    @Override
    public Object put(final String str, final Object obj) {
        return this.ctxt.put(str, obj);
    }
    
    @Override
    public void putAll(final Map<? extends String, ?> map) {
        this.ctxt.putAll(map);
    }
    
    @Override
    public Object remove(final Object obj) {
        return this.ctxt.remove(obj);
    }
    
    @Override
    public int size() {
        return this.ctxt.size();
    }
    
    @Override
    public Collection<Object> values() {
        return this.ctxt.values();
    }
}
