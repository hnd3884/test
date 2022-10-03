package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.glassfish.jersey.server.internal.process.MappableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.inject.Singleton;
import javax.annotation.Priority;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;

@Priority(10)
@Singleton
public class MappableExceptionWrapperInterceptor implements ReaderInterceptor, WriterInterceptor
{
    public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        try {
            return context.proceed();
        }
        catch (final WebApplicationException | MappableException | MessageBodyProviderNotFoundException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new MappableException(e2);
        }
    }
    
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        try {
            context.proceed();
        }
        catch (final WebApplicationException | MappableException e) {
            throw e;
        }
        catch (final MessageBodyProviderNotFoundException nfe) {
            throw new InternalServerErrorException((Throwable)nfe);
        }
        catch (final Exception e2) {
            throw new MappableException(e2);
        }
    }
    
    public static class Binder extends AbstractBinder
    {
        protected void configure() {
            ((ClassBinding)((ClassBinding)this.bind((Class)MappableExceptionWrapperInterceptor.class).to((Class)ReaderInterceptor.class)).to((Class)WriterInterceptor.class)).in((Class)Singleton.class);
        }
    }
}
