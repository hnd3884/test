package org.glassfish.jersey.server.internal.monitoring;

import java.util.Iterator;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import java.util.List;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class CompositeRequestEventListener implements RequestEventListener
{
    private final List<RequestEventListener> requestEventListeners;
    
    public CompositeRequestEventListener(final List<RequestEventListener> requestEventListeners) {
        this.requestEventListeners = requestEventListeners;
    }
    
    @Override
    public void onEvent(final RequestEvent event) {
        for (final RequestEventListener requestEventListener : this.requestEventListeners) {
            requestEventListener.onEvent(event);
        }
    }
}
