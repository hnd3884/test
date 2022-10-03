package org.glassfish.jersey.server.internal.monitoring;

import java.util.List;
import java.util.ArrayList;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import java.util.Iterator;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;

public class CompositeApplicationEventListener implements ApplicationEventListener
{
    private final Iterable<ApplicationEventListener> applicationEventListeners;
    
    public CompositeApplicationEventListener(final Iterable<ApplicationEventListener> applicationEventListeners) {
        this.applicationEventListeners = applicationEventListeners;
    }
    
    @Override
    public void onEvent(final ApplicationEvent event) {
        for (final ApplicationEventListener applicationEventListener : this.applicationEventListeners) {
            applicationEventListener.onEvent(event);
        }
    }
    
    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        final List<RequestEventListener> requestEventListeners = new ArrayList<RequestEventListener>();
        for (final ApplicationEventListener applicationEventListener : this.applicationEventListeners) {
            final RequestEventListener requestEventListener = applicationEventListener.onRequest(requestEvent);
            if (requestEventListener != null) {
                requestEventListeners.add(requestEventListener);
            }
        }
        return requestEventListeners.isEmpty() ? null : new CompositeRequestEventListener(requestEventListeners);
    }
}
