package org.glassfish.jersey.message.internal;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import org.glassfish.jersey.CommonProperties;
import javax.ws.rs.core.Configuration;
import java.lang.reflect.Type;
import javax.ws.rs.core.GenericEntity;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.core.Link;
import java.net.URI;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.NewCookie;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import javax.ws.rs.core.Cookie;
import java.util.Iterator;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Locale;
import java.text.ParseException;
import java.util.Date;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.function.Function;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Map;
import java.io.OutputStream;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.lang.annotation.Annotation;

public class OutboundMessageContext
{
    private static final Annotation[] EMPTY_ANNOTATIONS;
    private static final List<MediaType> WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST;
    private final MultivaluedMap<String, Object> headers;
    private final CommittingOutputStream committingOutputStream;
    private Object entity;
    private GenericType<?> entityType;
    private Annotation[] entityAnnotations;
    private OutputStream entityStream;
    
    public OutboundMessageContext() {
        this.entityAnnotations = OutboundMessageContext.EMPTY_ANNOTATIONS;
        this.headers = (MultivaluedMap<String, Object>)HeaderUtils.createOutbound();
        this.committingOutputStream = new CommittingOutputStream();
        this.entityStream = this.committingOutputStream;
    }
    
    public OutboundMessageContext(final OutboundMessageContext original) {
        this.entityAnnotations = OutboundMessageContext.EMPTY_ANNOTATIONS;
        (this.headers = (MultivaluedMap<String, Object>)HeaderUtils.createOutbound()).putAll((Map)original.headers);
        this.committingOutputStream = new CommittingOutputStream();
        this.entityStream = this.committingOutputStream;
        this.entity = original.entity;
        this.entityType = original.entityType;
        this.entityAnnotations = original.entityAnnotations;
    }
    
    public void replaceHeaders(final MultivaluedMap<String, Object> headers) {
        this.getHeaders().clear();
        if (headers != null) {
            this.getHeaders().putAll((Map)headers);
        }
    }
    
    public MultivaluedMap<String, String> getStringHeaders() {
        return HeaderUtils.asStringHeaders(this.headers);
    }
    
    public String getHeaderString(final String name) {
        return HeaderUtils.asHeaderString((List<Object>)this.headers.get((Object)name), RuntimeDelegate.getInstance());
    }
    
    private <T> T singleHeader(final String name, final Class<T> valueType, final Function<String, T> converter, final boolean convertNull) {
        final List<Object> values = (List<Object>)this.headers.get((Object)name);
        if (values == null || values.isEmpty()) {
            return convertNull ? converter.apply(null) : null;
        }
        if (values.size() > 1) {
            throw new HeaderValueException(LocalizationMessages.TOO_MANY_HEADER_VALUES(name, values.toString()), HeaderValueException.Context.OUTBOUND);
        }
        final Object value = values.get(0);
        if (value == null) {
            return convertNull ? converter.apply(null) : null;
        }
        if (valueType.isInstance(value)) {
            return valueType.cast(value);
        }
        try {
            return converter.apply(HeaderUtils.asString(value, null));
        }
        catch (final ProcessingException ex) {
            throw exception(name, value, (Exception)ex);
        }
    }
    
    private static HeaderValueException exception(final String headerName, final Object headerValue, final Exception e) {
        return new HeaderValueException(LocalizationMessages.UNABLE_TO_PARSE_HEADER_VALUE(headerName, headerValue), e, HeaderValueException.Context.OUTBOUND);
    }
    
    public MultivaluedMap<String, Object> getHeaders() {
        return this.headers;
    }
    
    public Date getDate() {
        return this.singleHeader("Date", Date.class, input -> {
            try {
                return HttpHeaderReader.readDate(input);
            }
            catch (final ParseException e) {
                throw new ProcessingException((Throwable)e);
            }
        }, false);
    }
    
    public Locale getLanguage() {
        return this.singleHeader("Content-Language", Locale.class, input -> {
            try {
                return new LanguageTag(input).getAsLocale();
            }
            catch (final ParseException e) {
                throw new ProcessingException((Throwable)e);
            }
        }, false);
    }
    
    public MediaType getMediaType() {
        return this.singleHeader("Content-Type", MediaType.class, MediaType::valueOf, false);
    }
    
    public List<MediaType> getAcceptableMediaTypes() {
        final List<Object> values = (List<Object>)this.headers.get((Object)"Accept");
        if (values == null || values.isEmpty()) {
            return OutboundMessageContext.WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST;
        }
        final List<MediaType> result = new ArrayList<MediaType>(values.size());
        final RuntimeDelegate rd = RuntimeDelegate.getInstance();
        boolean conversionApplied = false;
        for (final Object value : values) {
            try {
                if (value instanceof MediaType) {
                    final AcceptableMediaType _value = AcceptableMediaType.valueOf((MediaType)value);
                    conversionApplied = (_value != value);
                    result.add(_value);
                }
                else {
                    conversionApplied = true;
                    result.addAll(HttpHeaderReader.readAcceptMediaType(HeaderUtils.asString(value, rd)));
                }
            }
            catch (final ParseException e) {
                throw exception("Accept", value, e);
            }
        }
        if (conversionApplied) {
            this.headers.put((Object)"Accept", (Object)result.stream().map(mediaType -> mediaType).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        return Collections.unmodifiableList((List<? extends MediaType>)result);
    }
    
    public List<Locale> getAcceptableLanguages() {
        final List<Object> values = (List<Object>)this.headers.get((Object)"Accept-Language");
        if (values == null || values.isEmpty()) {
            return Collections.singletonList(new AcceptableLanguageTag("*", null).getAsLocale());
        }
        final List<Locale> result = new ArrayList<Locale>(values.size());
        final RuntimeDelegate rd = RuntimeDelegate.getInstance();
        boolean conversionApplied = false;
        for (final Object value : values) {
            if (value instanceof Locale) {
                result.add((Locale)value);
            }
            else {
                conversionApplied = true;
                try {
                    result.addAll(HttpHeaderReader.readAcceptLanguage(HeaderUtils.asString(value, rd)).stream().map((Function<? super Object, ?>)LanguageTag::getAsLocale).collect((Collector<? super Object, ?, Collection<? extends Locale>>)Collectors.toList()));
                }
                catch (final ParseException e) {
                    throw exception("Accept-Language", value, e);
                }
            }
        }
        if (conversionApplied) {
            this.headers.put((Object)"Accept-Language", (Object)result.stream().map(locale -> locale).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        return Collections.unmodifiableList((List<? extends Locale>)result);
    }
    
    public Map<String, Cookie> getRequestCookies() {
        final List<Object> cookies = (List<Object>)this.headers.get((Object)"Cookie");
        if (cookies == null || cookies.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Cookie> result = new HashMap<String, Cookie>();
        for (final String cookie : HeaderUtils.asStringList(cookies, RuntimeDelegate.getInstance())) {
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
            return new HashSet<String>(HttpHeaderReader.readStringList(allowed));
        }
        catch (final ParseException e) {
            throw exception("Allow", allowed, e);
        }
    }
    
    public int getLength() {
        return this.singleHeader("Content-Length", Integer.class, input -> {
            try {
                if (input != null && !input.isEmpty()) {
                    final int i = Integer.parseInt(input);
                    if (i >= 0) {
                        return i;
                    }
                }
                return -1;
            }
            catch (final NumberFormatException ex) {
                throw new ProcessingException((Throwable)ex);
            }
        }, true);
    }
    
    public long getLengthLong() {
        return this.singleHeader("Content-Length", Long.class, input -> {
            try {
                if (input != null && !input.isEmpty()) {
                    final long l = Long.parseLong(input);
                    if (l >= 0L) {
                        return l;
                    }
                }
                return -1L;
            }
            catch (final NumberFormatException ex) {
                throw new ProcessingException((Throwable)ex);
            }
        }, true);
    }
    
    public Map<String, NewCookie> getResponseCookies() {
        final List<Object> cookies = (List<Object>)this.headers.get((Object)"Set-Cookie");
        if (cookies == null || cookies.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, NewCookie> result = new HashMap<String, NewCookie>();
        for (final String cookie : HeaderUtils.asStringList(cookies, RuntimeDelegate.getInstance())) {
            if (cookie != null) {
                final NewCookie newCookie = HttpHeaderReader.readNewCookie(cookie);
                result.put(newCookie.getName(), newCookie);
            }
        }
        return result;
    }
    
    public EntityTag getEntityTag() {
        return this.singleHeader("ETag", EntityTag.class, new Function<String, EntityTag>() {
            @Override
            public EntityTag apply(final String value) {
                try {
                    return (value == null) ? null : EntityTag.valueOf(value);
                }
                catch (final IllegalArgumentException ex) {
                    throw new ProcessingException((Throwable)ex);
                }
            }
        }, false);
    }
    
    public Date getLastModified() {
        return this.singleHeader("Last-Modified", Date.class, new Function<String, Date>() {
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
        return this.singleHeader("Location", URI.class, new Function<String, URI>() {
            @Override
            public URI apply(final String value) {
                try {
                    return (value == null) ? null : URI.create(value);
                }
                catch (final IllegalArgumentException ex) {
                    throw new ProcessingException((Throwable)ex);
                }
            }
        }, false);
    }
    
    public Set<Link> getLinks() {
        final List<Object> values = (List<Object>)this.headers.get((Object)"Link");
        if (values == null || values.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<Link> result = new HashSet<Link>(values.size());
        final RuntimeDelegate rd = RuntimeDelegate.getInstance();
        boolean conversionApplied = false;
        for (final Object value : values) {
            if (value instanceof Link) {
                result.add((Link)value);
            }
            else {
                conversionApplied = true;
                try {
                    result.add(Link.valueOf(HeaderUtils.asString(value, rd)));
                }
                catch (final IllegalArgumentException e) {
                    throw exception("Link", value, e);
                }
            }
        }
        if (conversionApplied) {
            this.headers.put((Object)"Link", (Object)result.stream().map(link -> link).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        return Collections.unmodifiableSet((Set<? extends Link>)result);
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
    
    public boolean hasEntity() {
        return this.entity != null;
    }
    
    public Object getEntity() {
        return this.entity;
    }
    
    public void setEntity(final Object entity) {
        this.setEntity(entity, (GenericType<?>)ReflectionHelper.genericTypeFor(entity));
    }
    
    public void setEntity(final Object entity, final Annotation[] annotations) {
        this.setEntity(entity, (GenericType<?>)ReflectionHelper.genericTypeFor(entity));
        this.setEntityAnnotations(annotations);
    }
    
    private void setEntity(final Object entity, final GenericType<?> type) {
        if (entity instanceof GenericEntity) {
            this.entity = ((GenericEntity)entity).getEntity();
        }
        else {
            this.entity = entity;
        }
        this.entityType = type;
    }
    
    public void setEntity(final Object entity, final Type type, final Annotation[] annotations) {
        this.setEntity(entity, (GenericType<?>)new GenericType(type));
        this.setEntityAnnotations(annotations);
    }
    
    public void setEntity(final Object entity, final Annotation[] annotations, final MediaType mediaType) {
        this.setEntity(entity, annotations);
        this.setMediaType(mediaType);
    }
    
    public void setMediaType(final MediaType mediaType) {
        this.headers.putSingle((Object)"Content-Type", (Object)mediaType);
    }
    
    public Class<?> getEntityClass() {
        return (this.entityType == null) ? null : this.entityType.getRawType();
    }
    
    public Type getEntityType() {
        return (this.entityType == null) ? null : this.entityType.getType();
    }
    
    public void setEntityType(final Type type) {
        this.entityType = (GenericType<?>)new GenericType(type);
    }
    
    public Annotation[] getEntityAnnotations() {
        return this.entityAnnotations.clone();
    }
    
    public void setEntityAnnotations(final Annotation[] annotations) {
        this.entityAnnotations = ((annotations == null) ? OutboundMessageContext.EMPTY_ANNOTATIONS : annotations);
    }
    
    public OutputStream getEntityStream() {
        return this.entityStream;
    }
    
    public void setEntityStream(final OutputStream outputStream) {
        this.entityStream = outputStream;
    }
    
    public void enableBuffering(final Configuration configuration) {
        final Integer bufferSize = CommonProperties.getValue((Map<String, ?>)configuration.getProperties(), configuration.getRuntimeType(), "jersey.config.contentLength.buffer", Integer.class);
        if (bufferSize != null) {
            this.committingOutputStream.enableBuffering(bufferSize);
        }
        else {
            this.committingOutputStream.enableBuffering();
        }
    }
    
    public void setStreamProvider(final StreamProvider streamProvider) {
        this.committingOutputStream.setStreamProvider(streamProvider);
    }
    
    public void commitStream() throws IOException {
        if (!this.committingOutputStream.isCommitted()) {
            this.entityStream.flush();
            if (!this.committingOutputStream.isCommitted()) {
                this.committingOutputStream.commit();
                this.committingOutputStream.flush();
            }
        }
    }
    
    public boolean isCommitted() {
        return this.committingOutputStream.isCommitted();
    }
    
    public void close() {
        if (this.hasEntity()) {
            try {
                final OutputStream es = this.getEntityStream();
                es.flush();
                es.close();
            }
            catch (final IOException e) {
                Logger.getLogger(OutboundMessageContext.class.getName()).log(Level.FINE, e.getMessage(), e);
                if (!this.committingOutputStream.isClosed()) {
                    try {
                        this.committingOutputStream.close();
                    }
                    catch (final IOException e) {
                        Logger.getLogger(OutboundMessageContext.class.getName()).log(Level.FINE, e.getMessage(), e);
                    }
                }
            }
            finally {
                if (!this.committingOutputStream.isClosed()) {
                    try {
                        this.committingOutputStream.close();
                    }
                    catch (final IOException e2) {
                        Logger.getLogger(OutboundMessageContext.class.getName()).log(Level.FINE, e2.getMessage(), e2);
                    }
                }
            }
        }
    }
    
    static {
        EMPTY_ANNOTATIONS = new Annotation[0];
        WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST = Collections.singletonList(MediaTypes.WILDCARD_ACCEPTABLE_TYPE);
    }
    
    public interface StreamProvider
    {
        OutputStream getOutputStream(final int p0) throws IOException;
    }
}
