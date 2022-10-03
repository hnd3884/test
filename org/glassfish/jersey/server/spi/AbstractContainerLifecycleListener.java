package org.glassfish.jersey.server.spi;

public abstract class AbstractContainerLifecycleListener implements ContainerLifecycleListener
{
    @Override
    public void onStartup(final Container container) {
    }
    
    @Override
    public void onReload(final Container container) {
    }
    
    @Override
    public void onShutdown(final Container container) {
    }
}
