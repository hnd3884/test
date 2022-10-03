package org.glassfish.jersey.server.internal.monitoring;

import javax.inject.Singleton;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

public final class MonitoringContainerListener implements ContainerLifecycleListener
{
    private volatile ApplicationEvent initFinishedEvent;
    private volatile ApplicationEventListener listener;
    
    public void init(final ApplicationEventListener listener, final ApplicationEvent initFinishedEvent) {
        this.listener = listener;
        this.initFinishedEvent = initFinishedEvent;
    }
    
    @Override
    public void onStartup(final Container container) {
        if (this.listener != null) {
            this.listener.onEvent(this.getApplicationEvent(ApplicationEvent.Type.INITIALIZATION_FINISHED));
        }
    }
    
    @Override
    public void onReload(final Container container) {
        if (this.listener != null) {
            this.listener.onEvent(this.getApplicationEvent(ApplicationEvent.Type.RELOAD_FINISHED));
        }
    }
    
    private ApplicationEvent getApplicationEvent(final ApplicationEvent.Type type) {
        return new ApplicationEventImpl(type, this.initFinishedEvent.getResourceConfig(), this.initFinishedEvent.getProviders(), this.initFinishedEvent.getRegisteredClasses(), this.initFinishedEvent.getRegisteredInstances(), this.initFinishedEvent.getResourceModel());
    }
    
    @Override
    public void onShutdown(final Container container) {
        if (this.listener != null) {
            this.listener.onEvent(this.getApplicationEvent(ApplicationEvent.Type.DESTROY_FINISHED));
        }
    }
    
    public static class Binder extends AbstractBinder
    {
        protected void configure() {
            ((ClassBinding)this.bindAsContract((Class)MonitoringContainerListener.class).to((Class)ContainerLifecycleListener.class)).in((Class)Singleton.class);
        }
    }
}
