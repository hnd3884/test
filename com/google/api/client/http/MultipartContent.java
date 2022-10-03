package com.google.api.client.http;

import com.google.api.client.util.Preconditions;
import java.util.Collections;
import java.util.Collection;
import java.io.IOException;
import java.util.Iterator;
import java.io.Writer;
import java.util.logging.Logger;
import com.google.api.client.util.StreamingContent;
import java.util.Arrays;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.UUID;
import java.util.ArrayList;

public class MultipartContent extends AbstractHttpContent
{
    static final String NEWLINE = "\r\n";
    private static final String TWO_DASHES = "--";
    private ArrayList<Part> parts;
    
    public MultipartContent() {
        this("__END_OF_PART__" + UUID.randomUUID().toString() + "__");
    }
    
    public MultipartContent(final String boundary) {
        super(new HttpMediaType("multipart/related").setParameter("boundary", boundary));
        this.parts = new ArrayList<Part>();
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        final Writer writer = new OutputStreamWriter(out, this.getCharset());
        final String boundary = this.getBoundary();
        for (final Part part : this.parts) {
            final HttpHeaders headers = new HttpHeaders().setAcceptEncoding(null);
            if (part.headers != null) {
                headers.fromHttpHeaders(part.headers);
            }
            headers.setContentEncoding(null).setUserAgent(null).setContentType(null).setContentLength(null).set("Content-Transfer-Encoding", null);
            final HttpContent content = part.content;
            StreamingContent streamingContent = null;
            if (content != null) {
                headers.set("Content-Transfer-Encoding", Arrays.asList("binary"));
                headers.setContentType(content.getType());
                final HttpEncoding encoding = part.encoding;
                long contentLength;
                if (encoding == null) {
                    contentLength = content.getLength();
                    streamingContent = content;
                }
                else {
                    headers.setContentEncoding(encoding.getName());
                    streamingContent = new HttpEncodingStreamingContent(content, encoding);
                    contentLength = AbstractHttpContent.computeLength(content);
                }
                if (contentLength != -1L) {
                    headers.setContentLength(contentLength);
                }
            }
            writer.write("--");
            writer.write(boundary);
            writer.write("\r\n");
            HttpHeaders.serializeHeadersForMultipartRequests(headers, null, null, writer);
            if (streamingContent != null) {
                writer.write("\r\n");
                writer.flush();
                streamingContent.writeTo(out);
            }
            writer.write("\r\n");
        }
        writer.write("--");
        writer.write(boundary);
        writer.write("--");
        writer.write("\r\n");
        writer.flush();
    }
    
    @Override
    public boolean retrySupported() {
        for (final Part part : this.parts) {
            if (!part.content.retrySupported()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public MultipartContent setMediaType(final HttpMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }
    
    public final Collection<Part> getParts() {
        return Collections.unmodifiableCollection((Collection<? extends Part>)this.parts);
    }
    
    public MultipartContent addPart(final Part part) {
        this.parts.add(Preconditions.checkNotNull(part));
        return this;
    }
    
    public MultipartContent setParts(final Collection<Part> parts) {
        this.parts = new ArrayList<Part>(parts);
        return this;
    }
    
    public MultipartContent setContentParts(final Collection<? extends HttpContent> contentParts) {
        this.parts = new ArrayList<Part>(contentParts.size());
        for (final HttpContent contentPart : contentParts) {
            this.addPart(new Part(contentPart));
        }
        return this;
    }
    
    public final String getBoundary() {
        return this.getMediaType().getParameter("boundary");
    }
    
    public MultipartContent setBoundary(final String boundary) {
        this.getMediaType().setParameter("boundary", Preconditions.checkNotNull(boundary));
        return this;
    }
    
    public static final class Part
    {
        HttpContent content;
        HttpHeaders headers;
        HttpEncoding encoding;
        
        public Part() {
            this(null);
        }
        
        public Part(final HttpContent content) {
            this(null, content);
        }
        
        public Part(final HttpHeaders headers, final HttpContent content) {
            this.setHeaders(headers);
            this.setContent(content);
        }
        
        public Part setContent(final HttpContent content) {
            this.content = content;
            return this;
        }
        
        public HttpContent getContent() {
            return this.content;
        }
        
        public Part setHeaders(final HttpHeaders headers) {
            this.headers = headers;
            return this;
        }
        
        public HttpHeaders getHeaders() {
            return this.headers;
        }
        
        public Part setEncoding(final HttpEncoding encoding) {
            this.encoding = encoding;
            return this;
        }
        
        public HttpEncoding getEncoding() {
            return this.encoding;
        }
    }
}
