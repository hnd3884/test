package org.glassfish.jersey.message.internal;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.transform.Source;
import java.io.Closeable;
import javax.ws.rs.ext.ReaderInterceptor;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.PropertiesDelegate;
import java.util.StringTokenizer;
import javax.ws.rs.core.Link;
import java.net.URI;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.NewCookie;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import javax.ws.rs.core.Cookie;
import java.util.Collections;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.Set;
import java.text.ParseException;
import java.util.Date;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.function.Function;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.lang.annotation.Annotation;
import java.io.InputStream;

public abstract class InboundMessageContext
{
    private static final InputStream EMPTY;
    private static final Annotation[] EMPTY_ANNOTATIONS;
    private static final List<AcceptableMediaType> WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST;
    private final MultivaluedMap<String, String> headers;
    private final EntityContent entityContent;
    private final boolean translateNce;
    private MessageBodyWorkers workers;
    
    public InboundMessageContext() {
        this(false);
    }
    
    public InboundMessageContext(final boolean translateNce) {
        this.headers = (MultivaluedMap<String, String>)HeaderUtils.createInbound();
        this.entityContent = new EntityContent();
        this.translateNce = translateNce;
    }
    
    public InboundMessageContext header(final String name, final Object value) {
        this.getHeaders().add((Object)name, (Object)HeaderUtils.asString(value, RuntimeDelegate.getInstance()));
        return this;
    }
    
    public InboundMessageContext headers(final String name, final Object... values) {
        this.getHeaders().addAll((Object)name, (List)HeaderUtils.asStringList(Arrays.asList(values), RuntimeDelegate.getInstance()));
        return this;
    }
    
    public InboundMessageContext headers(final String name, final Iterable<?> values) {
        this.getHeaders().addAll((Object)name, (List)iterableToList(values));
        return this;
    }
    
    public InboundMessageContext headers(final MultivaluedMap<String, String> newHeaders) {
        for (final Map.Entry<String, List<String>> header : newHeaders.entrySet()) {
            this.headers.addAll((Object)header.getKey(), (List)header.getValue());
        }
        return this;
    }
    
    public InboundMessageContext headers(final Map<String, List<String>> newHeaders) {
        for (final Map.Entry<String, List<String>> header : newHeaders.entrySet()) {
            this.headers.addAll((Object)header.getKey(), (List)header.getValue());
        }
        return this;
    }
    
    public InboundMessageContext remove(final String name) {
        this.getHeaders().remove((Object)name);
        return this;
    }
    
    private static List<String> iterableToList(final Iterable<?> values) {
        final LinkedList<String> linkedList = new LinkedList<String>();
        final RuntimeDelegate rd = RuntimeDelegate.getInstance();
        for (final Object element : values) {
            linkedList.add(HeaderUtils.asString(element, rd));
        }
        return linkedList;
    }
    
    public String getHeaderString(final String name) {
        final List<String> values = (List<String>)this.headers.get((Object)name);
        if (values == null) {
            return null;
        }
        if (values.isEmpty()) {
            return "";
        }
        final Iterator<String> valuesIterator = values.iterator();
        final StringBuilder buffer = new StringBuilder(valuesIterator.next());
        while (valuesIterator.hasNext()) {
            buffer.append(',').append(valuesIterator.next());
        }
        return buffer.toString();
    }
    
    private <T> T singleHeader(final String name, final Function<String, T> converter, final boolean convertNull) {
        final List<String> values = (List<String>)this.headers.get((Object)name);
        if (values == null || values.isEmpty()) {
            return convertNull ? converter.apply(null) : null;
        }
        if (values.size() > 1) {
            throw new HeaderValueException(LocalizationMessages.TOO_MANY_HEADER_VALUES(name, values.toString()), HeaderValueException.Context.INBOUND);
        }
        final Object value = values.get(0);
        if (value == null) {
            return convertNull ? converter.apply(null) : null;
        }
        try {
            return converter.apply(HeaderUtils.asString(value, null));
        }
        catch (final ProcessingException ex) {
            throw exception(name, value, (Exception)ex);
        }
    }
    
    private static HeaderValueException exception(final String headerName, final Object headerValue, final Exception e) {
        return new HeaderValueException(LocalizationMessages.UNABLE_TO_PARSE_HEADER_VALUE(headerName, headerValue), e, HeaderValueException.Context.INBOUND);
    }
    
    public MultivaluedMap<String, String> getHeaders() {
        return this.headers;
    }
    
    public Date getDate() {
        return this.singleHeader("Date", (Function<String, Date>)new Function<String, Date>() {
            @Override
            public Date apply(final String input) {
                try {
                    return HttpHeaderReader.readDate(input);
                }
                catch (final ParseException ex) {
                    throw new ProcessingException((Throwable)ex);
                }
            }
        }, false);
    }
    
    public Set<MatchingEntityTag> getIfMatch() {
        final String ifMatch = this.getHeaderString("If-Match");
        if (ifMatch == null || ifMatch.isEmpty()) {
            return null;
        }
        try {
            return HttpHeaderReader.readMatchingEntityTag(ifMatch);
        }
        catch (final ParseException e) {
            throw exception("If-Match", ifMatch, e);
        }
    }
    
    public Set<MatchingEntityTag> getIfNoneMatch() {
        final String ifNoneMatch = this.getHeaderString("If-None-Match");
        if (ifNoneMatch == null || ifNoneMatch.isEmpty()) {
            return null;
        }
        try {
            return HttpHeaderReader.readMatchingEntityTag(ifNoneMatch);
        }
        catch (final ParseException e) {
            throw exception("If-None-Match", ifNoneMatch, e);
        }
    }
    
    public Locale getLanguage() {
        return this.singleHeader("Content-Language", (Function<String, Locale>)new Function<String, Locale>() {
            @Override
            public Locale apply(final String input) {
                try {
                    return new LanguageTag(input).getAsLocale();
                }
                catch (final ParseException e) {
                    throw new ProcessingException((Throwable)e);
                }
            }
        }, false);
    }
    
    public int getLength() {
        return this.singleHeader("Content-Length", (Function<String, Integer>)new Function<String, Integer>() {
            @Override
            public Integer apply(final String input) {
                try {
                    return (input != null && !input.isEmpty()) ? Integer.parseInt(input) : -1;
                }
                catch (final NumberFormatException ex) {
                    throw new ProcessingException((Throwable)ex);
                }
            }
        }, true);
    }
    
    public MediaType getMediaType() {
        return this.singleHeader("Content-Type", (Function<String, MediaType>)new Function<String, MediaType>() {
            @Override
            public MediaType apply(final String input) {
                try {
                    return MediaType.valueOf(input);
                }
                catch (final IllegalArgumentException iae) {
                    throw new ProcessingException((Throwable)iae);
                }
            }
        }, false);
    }
    
    public List<AcceptableMediaType> getQualifiedAcceptableMediaTypes() {
        final String value = this.getHeaderString("Accept");
        if (value == null || value.isEmpty()) {
            return InboundMessageContext.WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST;
        }
        try {
            return Collections.unmodifiableList((List<? extends AcceptableMediaType>)HttpHeaderReader.readAcceptMediaType(value));
        }
        catch (final ParseException e) {
            throw exception("Accept", value, e);
        }
    }
    
    public List<AcceptableLanguageTag> getQualifiedAcceptableLanguages() {
        final String value = this.getHeaderString("Accept-Language");
        if (value == null || value.isEmpty()) {
            return Collections.singletonList(new AcceptableLanguageTag("*", null));
        }
        try {
            return Collections.unmodifiableList((List<? extends AcceptableLanguageTag>)HttpHeaderReader.readAcceptLanguage(value));
        }
        catch (final ParseException e) {
            throw exception("Accept-Language", value, e);
        }
    }
    
    public List<AcceptableToken> getQualifiedAcceptCharset() {
        final String acceptCharset = this.getHeaderString("Accept-Charset");
        try {
            if (acceptCharset == null || acceptCharset.isEmpty()) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptCharset);
        }
        catch (final ParseException e) {
            throw exception("Accept-Charset", acceptCharset, e);
        }
    }
    
    public List<AcceptableToken> getQualifiedAcceptEncoding() {
        final String acceptEncoding = this.getHeaderString("Accept-Encoding");
        try {
            if (acceptEncoding == null || acceptEncoding.isEmpty()) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptEncoding);
        }
        catch (final ParseException e) {
            throw exception("Accept-Encoding", acceptEncoding, e);
        }
    }
    
    public Map<String, Cookie> getRequestCookies() {
        final List<String> cookies = (List<String>)this.headers.get((Object)"Cookie");
        if (cookies == null || cookies.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Cookie> result = new HashMap<String, Cookie>();
        for (final String cookie : cookies) {
            if (cookie != null) {
                result.putAll(HttpHeaderReader.readCookies(cookie));
            }
        }
        return result;
    }
    
    public Set<String> getAllowedMethods() {
        final String allowed = this.getHeaderString("Allow");
        if (allowed == null || allowed.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            return new HashSet<String>(HttpHeaderReader.readStringList(allowed.toUpperCase()));
        }
        catch (final ParseException e) {
            throw exception("Allow", allowed, e);
        }
    }
    
    public Map<String, NewCookie> getResponseCookies() {
        final List<String> cookies = (List<String>)this.headers.get((Object)"Set-Cookie");
        if (cookies == null || cookies.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, NewCookie> result = new HashMap<String, NewCookie>();
        for (final String cookie : cookies) {
            if (cookie != null) {
                final NewCookie newCookie = HttpHeaderReader.readNewCookie(cookie);
                result.put(newCookie.getName(), newCookie);
            }
        }
        return result;
    }
    
    public EntityTag getEntityTag() {
        return this.singleHeader("ETag", (Function<String, EntityTag>)new Function<String, EntityTag>() {
            @Override
            public EntityTag apply(final String value) {
                return EntityTag.valueOf(value);
            }
        }, false);
    }
    
    public Date getLastModified() {
        return this.singleHeader("Last-Modified", (Function<String, Date>)new Function<String, Date>() {
            @Override
            public Date apply(final String input) {
                try {
                    return HttpHeaderReader.readDate(input);
                }
                catch (final ParseException e) {
                    throw new ProcessingException((Throwable)e);
                }
            }
        }, false);
    }
    
    public URI getLocation() {
        return this.singleHeader("Location", (Function<String, URI>)new Function<String, URI>() {
            @Override
            public URI apply(final String value) {
                try {
                    return URI.create(value);
                }
                catch (final IllegalArgumentException ex) {
                    throw new ProcessingException((Throwable)ex);
                }
            }
        }, false);
    }
    
    public Set<Link> getLinks() {
        final List<String> links = (List<String>)this.headers.get((Object)"Link");
        if (links == null || links.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            final Set<Link> result = new HashSet<Link>(links.size());
            for (final String link : links) {
                StringBuilder linkString = new StringBuilder();
                final StringTokenizer st = new StringTokenizer(link, "<>,", true);
                boolean linkOpen = false;
                while (st.hasMoreTokens()) {
                    final String n = st.nextToken();
                    if (n.equals("<")) {
                        linkOpen = true;
                    }
                    else if (n.equals(">")) {
                        linkOpen = false;
                    }
                    else if (!linkOpen && n.equals(",")) {
                        result.add(Link.valueOf(linkString.toString().trim()));
                        linkString = new StringBuilder();
                        continue;
                    }
                    linkString.append(n);
                }
                if (linkString.length() > 0) {
                    result.add(Link.valueOf(linkString.toString().trim()));
                }
            }
            return result;
        }
        catch (final IllegalArgumentException e) {
            throw exception("Link", links, e);
        }
    }
    
    public boolean hasLink(final String relation) {
        for (final Link link : this.getLinks()) {
            final List<String> relations = LinkProvider.getLinkRelations(link.getRel());
            if (relations != null && relations.contains(relation)) {
                return true;
            }
        }
        return false;
    }
    
    public Link getLink(final String relation) {
        for (final Link link : this.getLinks()) {
            final List<String> relations = LinkProvider.getLinkRelations(link.getRel());
            if (relations != null && relations.contains(relation)) {
                return link;
            }
        }
        return null;
    }
    
    public Link.Builder getLinkBuilder(final String relation) {
        final Link link = this.getLink(relation);
        if (link == null) {
            return null;
        }
        return Link.fromLink(link);
    }
    
    public MessageBodyWorkers getWorkers() {
        return this.workers;
    }
    
    public void setWorkers(final MessageBodyWorkers workers) {
        this.workers = workers;
    }
    
    public boolean hasEntity() {
        this.entityContent.ensureNotClosed();
        try {
            return !this.entityContent.isEmpty();
        }
        catch (final IllegalStateException ex) {
            return false;
        }
    }
    
    public InputStream getEntityStream() {
        this.entityContent.ensureNotClosed();
        return this.entityContent.getWrappedStream();
    }
    
    public void setEntityStream(final InputStream input) {
        this.entityContent.setContent(input, false);
    }
    
    public <T> T readEntity(final Class<T> rawType, final PropertiesDelegate propertiesDelegate) {
        return this.readEntity(rawType, rawType, InboundMessageContext.EMPTY_ANNOTATIONS, propertiesDelegate);
    }
    
    public <T> T readEntity(final Class<T> rawType, final Annotation[] annotations, final PropertiesDelegate propertiesDelegate) {
        return this.readEntity(rawType, rawType, annotations, propertiesDelegate);
    }
    
    public <T> T readEntity(final Class<T> rawType, final Type type, final PropertiesDelegate propertiesDelegate) {
        return this.readEntity(rawType, type, InboundMessageContext.EMPTY_ANNOTATIONS, propertiesDelegate);
    }
    
    public <T> T readEntity(final Class<T> rawType, final Type type, final Annotation[] annotations, final PropertiesDelegate propertiesDelegate) {
        final boolean buffered = this.entityContent.isBuffered();
        if (buffered) {
            this.entityContent.reset();
        }
        this.entityContent.ensureNotClosed();
        if (this.workers == null) {
            return null;
        }
        MediaType mediaType = this.getMediaType();
        mediaType = ((mediaType == null) ? MediaType.APPLICATION_OCTET_STREAM_TYPE : mediaType);
        boolean shouldClose = !buffered;
        try {
            final T t = (T)this.workers.readFrom(rawType, type, annotations, mediaType, this.headers, propertiesDelegate, this.entityContent.getWrappedStream(), (Iterable<ReaderInterceptor>)(this.entityContent.hasContent() ? this.getReaderInterceptors() : Collections.emptyList()), this.translateNce);
            shouldClose = (shouldClose && !(t instanceof Closeable) && !(t instanceof Source));
            return t;
        }
        catch (final IOException ex) {
            throw new ProcessingException(LocalizationMessages.ERROR_READING_ENTITY_FROM_INPUT_STREAM(), (Throwable)ex);
        }
        finally {
            if (shouldClose) {
                ReaderWriter.safelyClose(this.entityContent);
            }
        }
    }
    
    public boolean bufferEntity() throws ProcessingException {
        this.entityContent.ensureNotClosed();
        try {
            if (this.entityContent.isBuffered() || !this.entityContent.hasContent()) {
                return true;
            }
            final InputStream entityStream = this.entityContent.getWrappedStream();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ReaderWriter.writeTo(entityStream, baos);
            }
            finally {
                ReaderWriter.safelyClose(entityStream);
            }
            this.entityContent.setContent(new ByteArrayInputStream(baos.toByteArray()), true);
            return true;
        }
        catch (final IOException ex) {
            throw new ProcessingException(LocalizationMessages.MESSAGE_CONTENT_BUFFERING_FAILED(), (Throwable)ex);
        }
    }
    
    public void close() {
        this.entityContent.close(true);
    }
    
    protected abstract Iterable<ReaderInterceptor> getReaderInterceptors();
    
    static {
        EMPTY = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
            
            @Override
            public void mark(final int readlimit) {
            }
            
            @Override
            public void reset() throws IOException {
            }
            
            @Override
            public boolean markSupported() {
                return true;
            }
        };
        EMPTY_ANNOTATIONS = new Annotation[0];
        WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST = Collections.singletonList(MediaTypes.WILDCARD_ACCEPTABLE_TYPE);
    }
    
    private static class EntityContent extends EntityInputStream
    {
        private boolean buffered;
        
        EntityContent() {
            super(InboundMessageContext.EMPTY);
        }
        
        void setContent(final InputStream content, final boolean buffered) {
            this.buffered = buffered;
            this.setWrappedStream(content);
        }
        
        boolean hasContent() {
            return this.getWrappedStream() != InboundMessageContext.EMPTY;
        }
        
        boolean isBuffered() {
            return this.buffered;
        }
        
        @Override
        public void close() {
            this.close(false);
        }
        
        void close(final boolean force) {
            if (this.buffered && !force) {
                return;
            }
            try {
                super.close();
            }
            finally {
                this.buffered = false;
                this.setWrappedStream(null);
            }
        }
    }
}
