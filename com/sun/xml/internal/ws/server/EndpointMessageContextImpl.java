package com.sun.xml.internal.ws.server;

import java.util.AbstractSet;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Attachment;
import javax.activation.DataHandler;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;
import java.util.AbstractMap;

public final class EndpointMessageContextImpl extends AbstractMap<String, Object> implements MessageContext
{
    private Set<Map.Entry<String, Object>> entrySet;
    private final Packet packet;
    
    public EndpointMessageContextImpl(final Packet packet) {
        this.packet = packet;
    }
    
    @Override
    public Object get(final Object key) {
        if (this.packet.supports(key)) {
            return this.packet.get(key);
        }
        if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
            return null;
        }
        final Object value = this.packet.invocationProperties.get(key);
        if (key.equals("javax.xml.ws.binding.attachments.outbound") || key.equals("javax.xml.ws.binding.attachments.inbound")) {
            Map<String, DataHandler> atts = (Map<String, DataHandler>)value;
            if (atts == null) {
                atts = new HashMap<String, DataHandler>();
            }
            final AttachmentSet attSet = this.packet.getMessage().getAttachments();
            for (final Attachment att : attSet) {
                atts.put(att.getContentId(), att.asDataHandler());
            }
            return atts;
        }
        return value;
    }
    
    @Override
    public Object put(final String key, final Object value) {
        if (this.packet.supports(key)) {
            return this.packet.put(key, value);
        }
        final Object old = this.packet.invocationProperties.get(key);
        if (old == null) {
            this.packet.invocationProperties.put(key, value);
            return null;
        }
        if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
            throw new IllegalArgumentException("Cannot overwrite property in HANDLER scope");
        }
        this.packet.invocationProperties.put(key, value);
        return old;
    }
    
    @Override
    public Object remove(final Object key) {
        if (this.packet.supports(key)) {
            return this.packet.remove(key);
        }
        final Object old = this.packet.invocationProperties.get(key);
        if (old == null) {
            return null;
        }
        if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
            throw new IllegalArgumentException("Cannot remove property in HANDLER scope");
        }
        this.packet.invocationProperties.remove(key);
        return old;
    }
    
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }
    
    @Override
    public void setScope(final String name, final Scope scope) {
        throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do setScope().");
    }
    
    @Override
    public Scope getScope(final String name) {
        throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do getScope().");
    }
    
    private Map<String, Object> createBackupMap() {
        final Map<String, Object> backupMap = new HashMap<String, Object>();
        backupMap.putAll(this.packet.createMapView());
        final Set<String> handlerProps = this.packet.getHandlerScopePropertyNames(true);
        for (final Map.Entry<String, Object> e : this.packet.invocationProperties.entrySet()) {
            if (!handlerProps.contains(e.getKey())) {
                backupMap.put(e.getKey(), e.getValue());
            }
        }
        return backupMap;
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<String, Object>>
    {
        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            final Iterator<Map.Entry<String, Object>> it = EndpointMessageContextImpl.this.createBackupMap().entrySet().iterator();
            return new Iterator<Map.Entry<String, Object>>() {
                Map.Entry<String, Object> cur;
                
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }
                
                @Override
                public Map.Entry<String, Object> next() {
                    return this.cur = it.next();
                }
                
                @Override
                public void remove() {
                    it.remove();
                    EndpointMessageContextImpl.this.remove(this.cur.getKey());
                }
            };
        }
        
        @Override
        public int size() {
            return EndpointMessageContextImpl.this.createBackupMap().size();
        }
    }
}
