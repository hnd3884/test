package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.Collection;
import java.io.IOException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.List;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import javax.ws.rs.ext.WriterInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;

public final class WriterInterceptorExecutor extends InterceptorExecutor<WriterInterceptor> implements WriterInterceptorContext, InjectionManagerSupplier
{
    private static final Logger LOGGER;
    private OutputStream outputStream;
    private final MultivaluedMap<String, Object> headers;
    private Object entity;
    private final Iterator<WriterInterceptor> iterator;
    private int processedCount;
    private final InjectionManager injectionManager;
    
    public WriterInterceptorExecutor(final Object entity, final Class<?> rawType, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> headers, final PropertiesDelegate propertiesDelegate, final OutputStream entityStream, final MessageBodyWorkers workers, final Iterable<WriterInterceptor> writerInterceptors, final InjectionManager injectionManager) {
        super(rawType, type, annotations, mediaType, propertiesDelegate);
        this.entity = entity;
        this.headers = headers;
        this.outputStream = entityStream;
        this.injectionManager = injectionManager;
        final List<WriterInterceptor> effectiveInterceptors = StreamSupport.stream(writerInterceptors.spliterator(), false).collect((Collector<? super WriterInterceptor, ?, List<WriterInterceptor>>)Collectors.toList());
        effectiveInterceptors.add((WriterInterceptor)new TerminalWriterInterceptor(workers));
        this.iterator = effectiveInterceptors.iterator();
        this.processedCount = 0;
    }
    
    private WriterInterceptor getNextInterceptor() {
        if (!this.iterator.hasNext()) {
            return null;
        }
        return this.iterator.next();
    }
    
    public void proceed() throws IOException {
        final WriterInterceptor nextInterceptor = this.getNextInterceptor();
        if (nextInterceptor == null) {
            throw new ProcessingException(LocalizationMessages.ERROR_INTERCEPTOR_WRITER_PROCEED());
        }
        this.traceBefore(nextInterceptor, MsgTraceEvent.WI_BEFORE);
        try {
            nextInterceptor.aroundWriteTo((WriterInterceptorContext)this);
        }
        finally {
            ++this.processedCount;
            this.traceAfter(nextInterceptor, MsgTraceEvent.WI_AFTER);
        }
    }
    
    public Object getEntity() {
        return this.entity;
    }
    
    public void setEntity(final Object entity) {
        this.entity = entity;
    }
    
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    public void setOutputStream(final OutputStream os) {
        this.outputStream = os;
    }
    
    public MultivaluedMap<String, Object> getHeaders() {
        return this.headers;
    }
    
    int getProcessedCount() {
        return this.processedCount;
    }
    
    public InjectionManager getInjectionManager() {
        return this.injectionManager;
    }
    
    static {
        LOGGER = Logger.getLogger(WriterInterceptorExecutor.class.getName());
    }
    
    private class TerminalWriterInterceptor implements WriterInterceptor
    {
        private final MessageBodyWorkers workers;
        
        TerminalWriterInterceptor(final MessageBodyWorkers workers) {
            this.workers = workers;
        }
        
        public void aroundWriteTo(final WriterInterceptorContext context) throws WebApplicationException, IOException {
            WriterInterceptorExecutor.this.processedCount--;
            WriterInterceptorExecutor.this.traceBefore(null, MsgTraceEvent.WI_BEFORE);
            try {
                final TracingLogger tracingLogger = WriterInterceptorExecutor.this.getTracingLogger();
                if (tracingLogger.isLogEnabled(MsgTraceEvent.MBW_FIND)) {
                    tracingLogger.log(MsgTraceEvent.MBW_FIND, context.getType().getName(), (context.getGenericType() instanceof Class) ? ((Class)context.getGenericType()).getName() : context.getGenericType(), context.getMediaType(), Arrays.toString(context.getAnnotations()));
                }
                final MessageBodyWriter writer = this.workers.getMessageBodyWriter((Class<Object>)context.getType(), context.getGenericType(), context.getAnnotations(), context.getMediaType(), WriterInterceptorExecutor.this);
                if (writer == null) {
                    WriterInterceptorExecutor.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_NOTFOUND_MESSAGEBODYWRITER(context.getMediaType(), context.getType(), context.getGenericType()));
                    throw new MessageBodyProviderNotFoundException(LocalizationMessages.ERROR_NOTFOUND_MESSAGEBODYWRITER(context.getMediaType(), context.getType(), context.getGenericType()));
                }
                this.invokeWriteTo(context, writer);
            }
            finally {
                WriterInterceptorExecutor.this.clearLastTracedInterceptor();
                WriterInterceptorExecutor.this.traceAfter(null, MsgTraceEvent.WI_AFTER);
            }
        }
        
        private void invokeWriteTo(final WriterInterceptorContext context, final MessageBodyWriter writer) throws WebApplicationException, IOException {
            final TracingLogger tracingLogger = WriterInterceptorExecutor.this.getTracingLogger();
            final long timestamp = tracingLogger.timestamp(MsgTraceEvent.MBW_WRITE_TO);
            final UnCloseableOutputStream entityStream = new UnCloseableOutputStream(context.getOutputStream(), writer);
            try {
                writer.writeTo(context.getEntity(), context.getType(), context.getGenericType(), context.getAnnotations(), context.getMediaType(), context.getHeaders(), (OutputStream)entityStream);
            }
            finally {
                tracingLogger.logDuration(MsgTraceEvent.MBW_WRITE_TO, timestamp, writer);
            }
        }
    }
    
    private static class UnCloseableOutputStream extends OutputStream
    {
        private final OutputStream original;
        private final MessageBodyWriter writer;
        
        private UnCloseableOutputStream(final OutputStream original, final MessageBodyWriter writer) {
            this.original = original;
            this.writer = writer;
        }
        
        @Override
        public void write(final int i) throws IOException {
            this.original.write(i);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.original.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.original.write(b, off, len);
        }
        
        @Override
        public void flush() throws IOException {
            this.original.flush();
        }
        
        @Override
        public void close() throws IOException {
            if (WriterInterceptorExecutor.LOGGER.isLoggable(Level.FINE)) {
                WriterInterceptorExecutor.LOGGER.log(Level.FINE, LocalizationMessages.MBW_TRYING_TO_CLOSE_STREAM(this.writer.getClass()));
            }
        }
    }
}
