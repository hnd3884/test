package org.glassfish.jersey.servlet.internal;

import java.util.concurrent.ExecutionException;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import org.glassfish.jersey.server.ContainerException;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.server.internal.JerseyRequestTimeoutHandler;
import org.glassfish.jersey.servlet.spi.AsyncContextDelegate;
import org.glassfish.jersey.server.ContainerResponse;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;

public class ResponseWriter implements ContainerResponseWriter
{
    private static final Logger LOGGER;
    private final HttpServletResponse response;
    private final boolean useSetStatusOn404;
    private final boolean configSetStatusOverSendError;
    private final CompletableFuture<ContainerResponse> responseContext;
    private final AsyncContextDelegate asyncExt;
    private final JerseyRequestTimeoutHandler requestTimeoutHandler;
    
    public ResponseWriter(final boolean useSetStatusOn404, final boolean configSetStatusOverSendError, final HttpServletResponse response, final AsyncContextDelegate asyncExt, final ScheduledExecutorService timeoutTaskExecutor) {
        this.useSetStatusOn404 = useSetStatusOn404;
        this.configSetStatusOverSendError = configSetStatusOverSendError;
        this.response = response;
        this.asyncExt = asyncExt;
        this.responseContext = new CompletableFuture<ContainerResponse>();
        this.requestTimeoutHandler = new JerseyRequestTimeoutHandler((ContainerResponseWriter)this, timeoutTaskExecutor);
    }
    
    public boolean suspend(final long timeOut, final TimeUnit timeUnit, final ContainerResponseWriter.TimeoutHandler timeoutHandler) {
        try {
            this.asyncExt.suspend();
        }
        catch (final IllegalStateException ex) {
            ResponseWriter.LOGGER.log(Level.WARNING, LocalizationMessages.SERVLET_REQUEST_SUSPEND_FAILED(), ex);
            return false;
        }
        return this.requestTimeoutHandler.suspend(timeOut, timeUnit, timeoutHandler);
    }
    
    public void setSuspendTimeout(final long timeOut, final TimeUnit timeUnit) throws IllegalStateException {
        this.requestTimeoutHandler.setSuspendTimeout(timeOut, timeUnit);
    }
    
    public OutputStream writeResponseStatusAndHeaders(final long contentLength, final ContainerResponse responseContext) throws ContainerException {
        this.responseContext.complete(responseContext);
        if (responseContext.hasEntity() && contentLength != -1L && contentLength < 2147483647L) {
            this.response.setContentLength((int)contentLength);
        }
        final MultivaluedMap<String, String> headers = (MultivaluedMap<String, String>)this.getResponseContext().getStringHeaders();
        for (final Map.Entry<String, List<String>> e : headers.entrySet()) {
            final Iterator<String> it = e.getValue().iterator();
            if (!it.hasNext()) {
                continue;
            }
            final String header = e.getKey();
            if (this.response.containsHeader(header)) {
                this.response.setHeader(header, (String)it.next());
            }
            while (it.hasNext()) {
                this.response.addHeader(header, (String)it.next());
            }
        }
        final String reasonPhrase = responseContext.getStatusInfo().getReasonPhrase();
        if (reasonPhrase != null) {
            this.response.setStatus(responseContext.getStatus(), reasonPhrase);
        }
        else {
            this.response.setStatus(responseContext.getStatus());
        }
        if (!responseContext.hasEntity()) {
            return null;
        }
        try {
            final OutputStream outputStream = (OutputStream)this.response.getOutputStream();
            return new NonCloseableOutputStreamWrapper(outputStream);
        }
        catch (final IOException e2) {
            throw new ContainerException((Throwable)e2);
        }
    }
    
    public void commit() {
        try {
            this.callSendError();
        }
        finally {
            this.requestTimeoutHandler.close();
            this.asyncExt.complete();
        }
    }
    
    private void callSendError() {
        if (!this.configSetStatusOverSendError && !this.response.isCommitted()) {
            final ContainerResponse responseContext = this.getResponseContext();
            final boolean hasEntity = responseContext.hasEntity();
            final Response.StatusType status = responseContext.getStatusInfo();
            if (!hasEntity && status != null && status.getStatusCode() >= 400 && (!this.useSetStatusOn404 || status != Response.Status.NOT_FOUND)) {
                final String reason = status.getReasonPhrase();
                try {
                    if (reason == null || reason.isEmpty()) {
                        this.response.sendError(status.getStatusCode());
                    }
                    else {
                        this.response.sendError(status.getStatusCode(), reason);
                    }
                }
                catch (final IOException ex) {
                    throw new ContainerException(LocalizationMessages.EXCEPTION_SENDING_ERROR_RESPONSE(status, (reason != null) ? reason : "--"), (Throwable)ex);
                }
            }
        }
    }
    
    public void failure(final Throwable error) {
        try {
            if (!this.response.isCommitted()) {
                try {
                    if (this.configSetStatusOverSendError) {
                        this.response.reset();
                        this.response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Request failed.");
                    }
                    else {
                        this.response.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Request failed.");
                    }
                }
                catch (final IllegalStateException ex) {
                    ResponseWriter.LOGGER.log(Level.FINER, "Unable to reset failed response.", ex);
                }
                catch (final IOException ex2) {
                    throw new ContainerException(LocalizationMessages.EXCEPTION_SENDING_ERROR_RESPONSE(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Request failed."), (Throwable)ex2);
                }
                finally {
                    this.asyncExt.complete();
                }
            }
        }
        finally {
            this.requestTimeoutHandler.close();
            this.rethrow(error);
        }
    }
    
    public boolean enableResponseBuffering() {
        return true;
    }
    
    private void rethrow(final Throwable error) {
        if (error instanceof RuntimeException) {
            throw (RuntimeException)error;
        }
        throw new ContainerException(error);
    }
    
    public int getResponseStatus() {
        return this.getResponseContext().getStatus();
    }
    
    public boolean responseContextResolved() {
        return this.responseContext.isDone();
    }
    
    public ContainerResponse getResponseContext() {
        try {
            return this.responseContext.get();
        }
        catch (final InterruptedException | ExecutionException ex) {
            throw new ContainerException((Throwable)ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ResponseWriter.class.getName());
    }
    
    private static class NonCloseableOutputStreamWrapper extends OutputStream
    {
        private final OutputStream delegate;
        
        public NonCloseableOutputStreamWrapper(final OutputStream delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.delegate.write(b);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.delegate.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.delegate.write(b, off, len);
        }
        
        @Override
        public void flush() throws IOException {
            this.delegate.flush();
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
