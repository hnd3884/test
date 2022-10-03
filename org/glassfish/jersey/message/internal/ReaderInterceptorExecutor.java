package org.glassfish.jersey.message.internal;

import javax.ws.rs.core.NoContentException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.MessageBodyReader;
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
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.io.InputStream;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.message.MessageBodyWorkers;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.ReaderInterceptor;

public final class ReaderInterceptorExecutor extends InterceptorExecutor<ReaderInterceptor> implements ReaderInterceptorContext, InjectionManagerSupplier
{
    private static final Logger LOGGER;
    private final MultivaluedMap<String, String> headers;
    private final Iterator<ReaderInterceptor> interceptors;
    private final MessageBodyWorkers workers;
    private final boolean translateNce;
    private final InjectionManager injectionManager;
    private InputStream inputStream;
    private int processedCount;
    
    ReaderInterceptorExecutor(final Class<?> rawType, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> headers, final PropertiesDelegate propertiesDelegate, final InputStream inputStream, final MessageBodyWorkers workers, final Iterable<ReaderInterceptor> readerInterceptors, final boolean translateNce, final InjectionManager injectionManager) {
        super(rawType, type, annotations, mediaType, propertiesDelegate);
        this.headers = headers;
        this.inputStream = inputStream;
        this.workers = workers;
        this.translateNce = translateNce;
        this.injectionManager = injectionManager;
        final List<ReaderInterceptor> effectiveInterceptors = StreamSupport.stream(readerInterceptors.spliterator(), false).collect((Collector<? super ReaderInterceptor, ?, List<ReaderInterceptor>>)Collectors.toList());
        effectiveInterceptors.add((ReaderInterceptor)new TerminalReaderInterceptor());
        this.interceptors = effectiveInterceptors.iterator();
        this.processedCount = 0;
    }
    
    public Object proceed() throws IOException {
        if (!this.interceptors.hasNext()) {
            throw new ProcessingException(LocalizationMessages.ERROR_INTERCEPTOR_READER_PROCEED());
        }
        final ReaderInterceptor interceptor = this.interceptors.next();
        this.traceBefore(interceptor, MsgTraceEvent.RI_BEFORE);
        try {
            return interceptor.aroundReadFrom((ReaderInterceptorContext)this);
        }
        finally {
            ++this.processedCount;
            this.traceAfter(interceptor, MsgTraceEvent.RI_AFTER);
        }
    }
    
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    public void setInputStream(final InputStream is) {
        this.inputStream = is;
    }
    
    public MultivaluedMap<String, String> getHeaders() {
        return this.headers;
    }
    
    int getProcessedCount() {
        return this.processedCount;
    }
    
    public InjectionManager getInjectionManager() {
        return this.injectionManager;
    }
    
    public static InputStream closeableInputStream(final InputStream inputStream) {
        if (inputStream instanceof UnCloseableInputStream) {
            return ((UnCloseableInputStream)inputStream).unwrap();
        }
        return inputStream;
    }
    
    static {
        LOGGER = Logger.getLogger(ReaderInterceptorExecutor.class.getName());
    }
    
    private class TerminalReaderInterceptor implements ReaderInterceptor
    {
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            ReaderInterceptorExecutor.this.processedCount--;
            ReaderInterceptorExecutor.this.traceBefore(null, MsgTraceEvent.RI_BEFORE);
            try {
                final TracingLogger tracingLogger = ReaderInterceptorExecutor.this.getTracingLogger();
                if (tracingLogger.isLogEnabled(MsgTraceEvent.MBR_FIND)) {
                    tracingLogger.log(MsgTraceEvent.MBR_FIND, context.getType().getName(), (context.getGenericType() instanceof Class) ? ((Class)context.getGenericType()).getName() : context.getGenericType(), String.valueOf(context.getMediaType()), Arrays.toString(context.getAnnotations()));
                }
                final MessageBodyReader bodyReader = ReaderInterceptorExecutor.this.workers.getMessageBodyReader((Class<Object>)context.getType(), context.getGenericType(), context.getAnnotations(), context.getMediaType(), ReaderInterceptorExecutor.this);
                final EntityInputStream input = new EntityInputStream(context.getInputStream());
                if (bodyReader != null) {
                    Object entity = this.invokeReadFrom(context, bodyReader, input);
                    if (bodyReader instanceof CompletableReader) {
                        entity = ((CompletableReader)bodyReader).complete(entity);
                    }
                    return entity;
                }
                if (input.isEmpty() && !context.getHeaders().containsKey((Object)"Content-Type")) {
                    return null;
                }
                ReaderInterceptorExecutor.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_NOTFOUND_MESSAGEBODYREADER(context.getMediaType(), context.getType(), context.getGenericType()));
                throw new MessageBodyProviderNotFoundException(LocalizationMessages.ERROR_NOTFOUND_MESSAGEBODYREADER(context.getMediaType(), context.getType(), context.getGenericType()));
            }
            finally {
                ReaderInterceptorExecutor.this.clearLastTracedInterceptor();
                ReaderInterceptorExecutor.this.traceAfter(null, MsgTraceEvent.RI_AFTER);
            }
        }
        
        private Object invokeReadFrom(final ReaderInterceptorContext context, final MessageBodyReader reader, final EntityInputStream input) throws WebApplicationException, IOException {
            final TracingLogger tracingLogger = ReaderInterceptorExecutor.this.getTracingLogger();
            final long timestamp = tracingLogger.timestamp(MsgTraceEvent.MBR_READ_FROM);
            final InputStream stream = new UnCloseableInputStream((InputStream)input, reader);
            try {
                return reader.readFrom(context.getType(), context.getGenericType(), context.getAnnotations(), context.getMediaType(), context.getHeaders(), stream);
            }
            catch (final NoContentException ex) {
                if (ReaderInterceptorExecutor.this.translateNce) {
                    throw new BadRequestException((Throwable)ex);
                }
                throw ex;
            }
            finally {
                tracingLogger.logDuration(MsgTraceEvent.MBR_READ_FROM, timestamp, reader);
            }
        }
    }
    
    private static class UnCloseableInputStream extends InputStream
    {
        private final InputStream original;
        private final MessageBodyReader reader;
        
        private UnCloseableInputStream(final InputStream original, final MessageBodyReader reader) {
            this.original = original;
            this.reader = reader;
        }
        
        @Override
        public int read() throws IOException {
            return this.original.read();
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.original.read(b);
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return this.original.read(b, off, len);
        }
        
        @Override
        public long skip(final long l) throws IOException {
            return this.original.skip(l);
        }
        
        @Override
        public int available() throws IOException {
            return this.original.available();
        }
        
        @Override
        public synchronized void mark(final int i) {
            this.original.mark(i);
        }
        
        @Override
        public synchronized void reset() throws IOException {
            this.original.reset();
        }
        
        @Override
        public boolean markSupported() {
            return this.original.markSupported();
        }
        
        @Override
        public void close() throws IOException {
            if (ReaderInterceptorExecutor.LOGGER.isLoggable(Level.FINE)) {
                ReaderInterceptorExecutor.LOGGER.log(Level.FINE, LocalizationMessages.MBR_TRYING_TO_CLOSE_STREAM(this.reader.getClass()));
            }
        }
        
        private InputStream unwrap() {
            return this.original;
        }
    }
}
