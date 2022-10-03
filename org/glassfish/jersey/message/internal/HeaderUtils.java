package org.glassfish.jersey.message.internal;

import java.util.Set;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import org.glassfish.jersey.internal.util.collection.Views;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import javax.ws.rs.core.AbstractMultivaluedMap;
import java.util.logging.Logger;

public final class HeaderUtils
{
    private static final Logger LOGGER;
    
    public static AbstractMultivaluedMap<String, String> createInbound() {
        return (AbstractMultivaluedMap<String, String>)new StringKeyIgnoreCaseMultivaluedMap();
    }
    
    public static <V> MultivaluedMap<String, V> empty() {
        return (MultivaluedMap<String, V>)ImmutableMultivaluedMap.empty();
    }
    
    public static AbstractMultivaluedMap<String, Object> createOutbound() {
        return (AbstractMultivaluedMap<String, Object>)new StringKeyIgnoreCaseMultivaluedMap();
    }
    
    public static String asString(final Object headerValue, RuntimeDelegate rd) {
        if (headerValue == null) {
            return null;
        }
        if (headerValue instanceof String) {
            return (String)headerValue;
        }
        if (rd == null) {
            rd = RuntimeDelegate.getInstance();
        }
        final RuntimeDelegate.HeaderDelegate hp = rd.createHeaderDelegate((Class)headerValue.getClass());
        return (hp != null) ? hp.toString(headerValue) : headerValue.toString();
    }
    
    public static List<String> asStringList(final List<Object> headerValues, final RuntimeDelegate rd) {
        if (headerValues == null || headerValues.isEmpty()) {
            return Collections.emptyList();
        }
        if (rd == null) {
            final RuntimeDelegate delegate = RuntimeDelegate.getInstance();
        }
        else {
            final RuntimeDelegate delegate = rd;
        }
        RuntimeDelegate delegate;
        return Views.listView(headerValues, input -> (input == null) ? "[null]" : asString(input, delegate));
    }
    
    public static MultivaluedMap<String, String> asStringHeaders(final MultivaluedMap<String, Object> headers) {
        if (headers == null) {
            return null;
        }
        final RuntimeDelegate rd = RuntimeDelegate.getInstance();
        return (MultivaluedMap<String, String>)new AbstractMultivaluedMap<String, String>(Views.mapView((Map<Object, Object>)headers, input -> asStringList(input, rd))) {};
    }
    
    public static Map<String, String> asStringHeadersSingleValue(final MultivaluedMap<String, Object> headers) {
        if (headers == null) {
            return null;
        }
        final RuntimeDelegate rd = RuntimeDelegate.getInstance();
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)headers.entrySet().stream().collect(Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, entry -> asHeaderString(entry.getValue(), rd))));
    }
    
    public static String asHeaderString(final List<Object> values, final RuntimeDelegate rd) {
        if (values == null) {
            return null;
        }
        final Iterator<String> stringValues = asStringList(values, rd).iterator();
        if (!stringValues.hasNext()) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder(stringValues.next());
        while (stringValues.hasNext()) {
            buffer.append(',').append(stringValues.next());
        }
        return buffer.toString();
    }
    
    public static void checkHeaderChanges(final Map<String, String> headersSnapshot, final MultivaluedMap<String, Object> currentHeaders, final String connectorName) {
        if (HeaderUtils.LOGGER.isLoggable(Level.WARNING)) {
            final RuntimeDelegate rd = RuntimeDelegate.getInstance();
            final Set<String> changedHeaderNames = new HashSet<String>();
            for (final Map.Entry<? extends String, ? extends List<Object>> entry : currentHeaders.entrySet()) {
                if (!headersSnapshot.containsKey(entry.getKey())) {
                    changedHeaderNames.add((String)entry.getKey());
                }
                else {
                    final String prevValue = headersSnapshot.get(entry.getKey());
                    final String newValue = asHeaderString((List<Object>)currentHeaders.get((Object)entry.getKey()), rd);
                    if (prevValue.equals(newValue)) {
                        continue;
                    }
                    changedHeaderNames.add((String)entry.getKey());
                }
            }
            if (!changedHeaderNames.isEmpty() && HeaderUtils.LOGGER.isLoggable(Level.WARNING)) {
                HeaderUtils.LOGGER.warning(LocalizationMessages.SOME_HEADERS_NOT_SENT(connectorName, changedHeaderNames.toString()));
            }
        }
    }
    
    private HeaderUtils() {
        throw new AssertionError((Object)"No instances allowed.");
    }
    
    static {
        LOGGER = Logger.getLogger(HeaderUtils.class.getName());
    }
}
