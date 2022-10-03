package com.sun.xml.internal.ws.client;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Attachment;
import javax.activation.DataHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;

public class ResponseContext extends AbstractMap<String, Object>
{
    private final Packet packet;
    private Set<Map.Entry<String, Object>> entrySet;
    
    public ResponseContext(final Packet packet) {
        this.packet = packet;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (this.packet.supports(key)) {
            return this.packet.containsKey(key);
        }
        return this.packet.invocationProperties.containsKey(key) && !this.packet.getHandlerScopePropertyNames(true).contains(key);
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
        if (key.equals("javax.xml.ws.binding.attachments.inbound")) {
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
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends String, ?> t) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.entrySet == null) {
            final Map<String, Object> r = new HashMap<String, Object>();
            r.putAll(this.packet.invocationProperties);
            r.keySet().removeAll(this.packet.getHandlerScopePropertyNames(true));
            r.putAll(this.packet.createMapView());
            this.entrySet = Collections.unmodifiableSet((Set<? extends Map.Entry<String, Object>>)r.entrySet());
        }
        return this.entrySet;
    }
}
