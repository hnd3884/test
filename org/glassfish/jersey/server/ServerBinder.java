package org.glassfish.jersey.server;

import javax.ws.rs.ext.WriterInterceptor;
import org.glassfish.jersey.server.internal.JsonWithPaddingInterceptor;
import javax.inject.Singleton;
import javax.ws.rs.ext.MessageBodyWriter;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.server.internal.monitoring.MonitoringContainerListener;
import org.glassfish.jersey.server.internal.MappableExceptionWrapperInterceptor;
import org.glassfish.jersey.internal.inject.AbstractBinder;

class ServerBinder extends AbstractBinder
{
    protected void configure() {
        this.install(new AbstractBinder[] { new MappableExceptionWrapperInterceptor.Binder(), new MonitoringContainerListener.Binder() });
        ((ClassBinding)this.bind((Class)ChunkedResponseWriter.class).to((Class)MessageBodyWriter.class)).in((Class)Singleton.class);
        ((ClassBinding)this.bind((Class)JsonWithPaddingInterceptor.class).to((Class)WriterInterceptor.class)).in((Class)Singleton.class);
    }
}
