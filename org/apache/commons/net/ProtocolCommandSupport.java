package org.apache.commons.net;

import java.util.Iterator;
import java.util.EventListener;
import org.apache.commons.net.util.ListenerList;
import java.io.Serializable;

public class ProtocolCommandSupport implements Serializable
{
    private static final long serialVersionUID = -8017692739988399978L;
    private final Object __source;
    private final ListenerList __listeners;
    
    public ProtocolCommandSupport(final Object source) {
        this.__listeners = new ListenerList();
        this.__source = source;
    }
    
    public void fireCommandSent(final String command, final String message) {
        final ProtocolCommandEvent event = new ProtocolCommandEvent(this.__source, command, message);
        for (final EventListener listener : this.__listeners) {
            ((ProtocolCommandListener)listener).protocolCommandSent(event);
        }
    }
    
    public void fireReplyReceived(final int replyCode, final String message) {
        final ProtocolCommandEvent event = new ProtocolCommandEvent(this.__source, replyCode, message);
        for (final EventListener listener : this.__listeners) {
            ((ProtocolCommandListener)listener).protocolReplyReceived(event);
        }
    }
    
    public void addProtocolCommandListener(final ProtocolCommandListener listener) {
        this.__listeners.addListener(listener);
    }
    
    public void removeProtocolCommandListener(final ProtocolCommandListener listener) {
        this.__listeners.removeListener(listener);
    }
    
    public int getListenerCount() {
        return this.__listeners.getListenerCount();
    }
}
