package org.glassfish.jersey.server.internal.monitoring;

import java.util.Date;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import javax.inject.Inject;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.inject.Provider;
import javax.annotation.Priority;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;

@Priority(1000)
public final class ApplicationInfoListener implements ApplicationEventListener
{
    public static final int PRIORITY = 1000;
    @Inject
    private Provider<Ref<ApplicationInfo>> applicationInfoRefProvider;
    
    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        return null;
    }
    
    @Override
    public void onEvent(final ApplicationEvent event) {
        final ApplicationEvent.Type type = event.getType();
        switch (type) {
            case RELOAD_FINISHED:
            case INITIALIZATION_FINISHED: {
                this.processApplicationStatistics(event);
                break;
            }
        }
    }
    
    private void processApplicationStatistics(final ApplicationEvent event) {
        final long now = System.currentTimeMillis();
        final ApplicationInfo applicationInfo = new ApplicationInfoImpl(event.getResourceConfig(), new Date(now), event.getRegisteredClasses(), event.getRegisteredInstances(), event.getProviders());
        ((Ref)this.applicationInfoRefProvider.get()).set((Object)applicationInfo);
    }
}
