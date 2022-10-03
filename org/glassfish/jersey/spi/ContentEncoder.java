package org.glassfish.jersey.spi;

import javax.ws.rs.ext.WriterInterceptorContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;

@Priority(4000)
@Contract
public abstract class ContentEncoder implements ReaderInterceptor, WriterInterceptor
{
    private final Set<String> supportedEncodings;
    
    protected ContentEncoder(final String... supportedEncodings) {
        if (supportedEncodings.length == 0) {
            throw new IllegalArgumentException();
        }
        this.supportedEncodings = Collections.unmodifiableSet((Set<? extends String>)Arrays.stream(supportedEncodings).collect((Collector<? super String, ?, Set<? extends T>>)Collectors.toSet()));
    }
    
    public final Set<String> getSupportedEncodings() {
        return this.supportedEncodings;
    }
    
    public abstract InputStream decode(final String p0, final InputStream p1) throws IOException;
    
    public abstract OutputStream encode(final String p0, final OutputStream p1) throws IOException;
    
    public final Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
        final String contentEncoding = (String)context.getHeaders().getFirst((Object)"Content-Encoding");
        if (contentEncoding != null && this.getSupportedEncodings().contains(contentEncoding)) {
            context.setInputStream(this.decode(contentEncoding, context.getInputStream()));
        }
        return context.proceed();
    }
    
    public final void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        final String contentEncoding = (String)context.getHeaders().getFirst((Object)"Content-Encoding");
        if (contentEncoding != null && this.getSupportedEncodings().contains(contentEncoding)) {
            context.setOutputStream(this.encode(contentEncoding, context.getOutputStream()));
        }
        context.proceed();
    }
}
