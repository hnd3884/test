package org.glassfish.jersey.logging;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.util.HashSet;
import javax.ws.rs.WebApplicationException;
import org.glassfish.jersey.message.MessageUtils;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.nio.charset.Charset;
import java.io.InputStream;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.WriterInterceptor;

abstract class LoggingInterceptor implements WriterInterceptor
{
    static final String REQUEST_PREFIX = "> ";
    static final String RESPONSE_PREFIX = "< ";
    static final String ENTITY_LOGGER_PROPERTY;
    static final String LOGGING_ID_PROPERTY;
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final MediaType TEXT_MEDIA_TYPE;
    private static final Set<MediaType> READABLE_APP_MEDIA_TYPES;
    private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR;
    final Logger logger;
    final Level level;
    final AtomicLong _id;
    final LoggingFeature.Verbosity verbosity;
    final int maxEntitySize;
    
    LoggingInterceptor(final Logger logger, final Level level, final LoggingFeature.Verbosity verbosity, final int maxEntitySize) {
        this._id = new AtomicLong(0L);
        this.logger = logger;
        this.level = level;
        this.verbosity = verbosity;
        this.maxEntitySize = Math.max(0, maxEntitySize);
    }
    
    void log(final StringBuilder b) {
        if (this.logger != null && this.logger.isLoggable(this.level)) {
            this.logger.log(this.level, b.toString());
        }
    }
    
    private StringBuilder prefixId(final StringBuilder b, final long id) {
        b.append(Long.toString(id)).append(" ");
        return b;
    }
    
    void printRequestLine(final StringBuilder b, final String note, final long id, final String method, final URI uri) {
        this.prefixId(b, id).append("* ").append(note).append(" on thread ").append(Thread.currentThread().getName()).append("\n");
        this.prefixId(b, id).append("> ").append(method).append(" ").append(uri.toASCIIString()).append("\n");
    }
    
    void printResponseLine(final StringBuilder b, final String note, final long id, final int status) {
        this.prefixId(b, id).append("* ").append(note).append(" on thread ").append(Thread.currentThread().getName()).append("\n");
        this.prefixId(b, id).append("< ").append(Integer.toString(status)).append("\n");
    }
    
    void printPrefixedHeaders(final StringBuilder b, final long id, final String prefix, final MultivaluedMap<String, String> headers) {
        for (final Map.Entry<String, List<String>> headerEntry : this.getSortedHeaders(headers.entrySet())) {
            final List<?> val = headerEntry.getValue();
            final String header = headerEntry.getKey();
            if (val.size() == 1) {
                this.prefixId(b, id).append(prefix).append(header).append(": ").append(val.get(0)).append("\n");
            }
            else {
                final StringBuilder sb = new StringBuilder();
                boolean add = false;
                for (final Object s : val) {
                    if (add) {
                        sb.append(',');
                    }
                    add = true;
                    sb.append(s);
                }
                this.prefixId(b, id).append(prefix).append(header).append(": ").append(sb.toString()).append("\n");
            }
        }
    }
    
    Set<Map.Entry<String, List<String>>> getSortedHeaders(final Set<Map.Entry<String, List<String>>> headers) {
        final TreeSet<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<Map.Entry<String, List<String>>>(LoggingInterceptor.COMPARATOR);
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }
    
    InputStream logInboundEntity(final StringBuilder b, InputStream stream, final Charset charset) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(this.maxEntitySize + 1);
        final byte[] entity = new byte[this.maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, this.maxEntitySize), charset));
        if (entitySize > this.maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }
    
    public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final LoggingStream stream = (LoggingStream)writerInterceptorContext.getProperty(LoggingInterceptor.ENTITY_LOGGER_PROPERTY);
        writerInterceptorContext.proceed();
        if (this.logger.isLoggable(this.level) && printEntity(this.verbosity, writerInterceptorContext.getMediaType()) && stream != null) {
            this.log(stream.getStringBuilder(MessageUtils.getCharset(writerInterceptorContext.getMediaType())));
        }
    }
    
    static boolean isReadable(final MediaType mediaType) {
        if (mediaType != null) {
            for (final MediaType readableMediaType : LoggingInterceptor.READABLE_APP_MEDIA_TYPES) {
                if (readableMediaType.isCompatible(mediaType)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static boolean printEntity(final LoggingFeature.Verbosity verbosity, final MediaType mediaType) {
        return verbosity == LoggingFeature.Verbosity.PAYLOAD_ANY || (verbosity == LoggingFeature.Verbosity.PAYLOAD_TEXT && isReadable(mediaType));
    }
    
    static {
        ENTITY_LOGGER_PROPERTY = LoggingFeature.class.getName() + ".entityLogger";
        LOGGING_ID_PROPERTY = LoggingFeature.class.getName() + ".id";
        TEXT_MEDIA_TYPE = new MediaType("text", "*");
        READABLE_APP_MEDIA_TYPES = new HashSet<MediaType>() {
            {
                this.add(LoggingInterceptor.TEXT_MEDIA_TYPE);
                this.add(MediaType.APPLICATION_ATOM_XML_TYPE);
                this.add(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
                this.add(MediaType.APPLICATION_JSON_TYPE);
                this.add(MediaType.APPLICATION_SVG_XML_TYPE);
                this.add(MediaType.APPLICATION_XHTML_XML_TYPE);
                this.add(MediaType.APPLICATION_XML_TYPE);
            }
        };
        COMPARATOR = new Comparator<Map.Entry<String, List<String>>>() {
            @Override
            public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
                return o1.getKey().compareToIgnoreCase(o2.getKey());
            }
        };
    }
    
    class LoggingStream extends FilterOutputStream
    {
        private final StringBuilder b;
        private final ByteArrayOutputStream baos;
        
        LoggingStream(final StringBuilder b, final OutputStream inner) {
            super(inner);
            this.baos = new ByteArrayOutputStream();
            this.b = b;
        }
        
        StringBuilder getStringBuilder(final Charset charset) {
            final byte[] entity = this.baos.toByteArray();
            this.b.append(new String(entity, 0, Math.min(entity.length, LoggingInterceptor.this.maxEntitySize), charset));
            if (entity.length > LoggingInterceptor.this.maxEntitySize) {
                this.b.append("...more...");
            }
            this.b.append('\n');
            return this.b;
        }
        
        @Override
        public void write(final int i) throws IOException {
            if (this.baos.size() <= LoggingInterceptor.this.maxEntitySize) {
                this.baos.write(i);
            }
            this.out.write(i);
        }
    }
}
