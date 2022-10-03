package org.glassfish.jersey.server.filter;

import java.util.Iterator;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.HashMap;
import org.glassfish.jersey.message.internal.LanguageTag;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collections;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ContainerRequestFilter;

@PreMatching
@Priority(3000)
public final class UriConnegFilter implements ContainerRequestFilter
{
    protected final Map<String, MediaType> mediaTypeMappings;
    protected final Map<String, String> languageMappings;
    
    public UriConnegFilter(@Context final Configuration rc) {
        this(extractMediaTypeMappings(rc.getProperty("jersey.config.server.mediaTypeMappings")), extractLanguageMappings(rc.getProperty("jersey.config.server.languageMappings")));
    }
    
    public UriConnegFilter(Map<String, MediaType> mediaTypeMappings, Map<String, String> languageMappings) {
        if (mediaTypeMappings == null) {
            mediaTypeMappings = Collections.emptyMap();
        }
        if (languageMappings == null) {
            languageMappings = Collections.emptyMap();
        }
        this.mediaTypeMappings = mediaTypeMappings;
        this.languageMappings = languageMappings;
    }
    
    public void filter(final ContainerRequestContext rc) throws IOException {
        final UriInfo uriInfo = rc.getUriInfo();
        String path = uriInfo.getRequestUri().getRawPath();
        if (path.indexOf(46) == -1) {
            return;
        }
        final List<PathSegment> l = uriInfo.getPathSegments(false);
        if (l.isEmpty()) {
            return;
        }
        PathSegment segment = null;
        for (int i = l.size() - 1; i >= 0; --i) {
            segment = l.get(i);
            if (segment.getPath().length() > 0) {
                break;
            }
        }
        if (segment == null) {
            return;
        }
        final int length = path.length();
        final String[] suffixes = segment.getPath().split("\\.");
        for (int j = suffixes.length - 1; j >= 1; --j) {
            final String suffix = suffixes[j];
            if (suffix.length() != 0) {
                final MediaType accept = this.mediaTypeMappings.get(suffix);
                if (accept != null) {
                    rc.getHeaders().putSingle((Object)"Accept", (Object)accept.toString());
                    final int index = path.lastIndexOf('.' + suffix);
                    path = new StringBuilder(path).delete(index, index + suffix.length() + 1).toString();
                    suffixes[j] = "";
                    break;
                }
            }
        }
        for (int j = suffixes.length - 1; j >= 1; --j) {
            final String suffix = suffixes[j];
            if (suffix.length() != 0) {
                final String acceptLanguage = this.languageMappings.get(suffix);
                if (acceptLanguage != null) {
                    rc.getHeaders().putSingle((Object)"Accept-Language", (Object)acceptLanguage);
                    final int index = path.lastIndexOf('.' + suffix);
                    path = new StringBuilder(path).delete(index, index + suffix.length() + 1).toString();
                    suffixes[j] = "";
                    break;
                }
            }
        }
        if (length != path.length()) {
            rc.setRequestUri(uriInfo.getRequestUriBuilder().replacePath(path).build(new Object[0]));
        }
    }
    
    private static Map<String, MediaType> extractMediaTypeMappings(final Object mappings) {
        return parseAndValidateMappings("jersey.config.server.mediaTypeMappings", mappings, (TypeParser<MediaType>)new TypeParser<MediaType>() {
            @Override
            public MediaType valueOf(final String value) {
                return MediaType.valueOf(value);
            }
        });
    }
    
    private static Map<String, String> extractLanguageMappings(final Object mappings) {
        return parseAndValidateMappings("jersey.config.server.languageMappings", mappings, (TypeParser<String>)new TypeParser<String>() {
            @Override
            public String valueOf(final String value) {
                return LanguageTag.valueOf(value).toString();
            }
        });
    }
    
    private static <T> Map<String, T> parseAndValidateMappings(final String property, final Object mappings, final TypeParser<T> parser) {
        if (mappings == null) {
            return Collections.emptyMap();
        }
        if (mappings instanceof Map) {
            return (Map)mappings;
        }
        final HashMap<String, T> mappingsMap = new HashMap<String, T>();
        if (mappings instanceof String) {
            parseMappings(property, (String)mappings, mappingsMap, parser);
        }
        else {
            if (!(mappings instanceof String[])) {
                throw new IllegalArgumentException(LocalizationMessages.INVALID_MAPPING_TYPE(property));
            }
            final String[] array;
            final String[] mappingsArray = array = (String[])mappings;
            for (final String aMappingsArray : array) {
                parseMappings(property, aMappingsArray, mappingsMap, parser);
            }
        }
        encodeKeys(mappingsMap);
        return mappingsMap;
    }
    
    private static <T> void parseMappings(final String property, final String mappings, final Map<String, T> mappingsMap, final TypeParser<T> parser) {
        if (mappings == null) {
            return;
        }
        final String[] split;
        final String[] records = split = mappings.split(",");
        for (final String record : split) {
            final String[] mapping = record.split(":");
            if (mapping.length != 2) {
                throw new IllegalArgumentException(LocalizationMessages.INVALID_MAPPING_FORMAT(property, mappings));
            }
            final String trimmedSegment = mapping[0].trim();
            final String trimmedValue = mapping[1].trim();
            if (trimmedSegment.length() == 0) {
                throw new IllegalArgumentException(LocalizationMessages.INVALID_MAPPING_KEY_EMPTY(property, record));
            }
            if (trimmedValue.length() == 0) {
                throw new IllegalArgumentException(LocalizationMessages.INVALID_MAPPING_VALUE_EMPTY(property, record));
            }
            mappingsMap.put(trimmedSegment, parser.valueOf(trimmedValue));
        }
    }
    
    private static <T> void encodeKeys(final Map<String, T> map) {
        final Map<String, T> tempMap = new HashMap<String, T>();
        for (final Map.Entry<String, T> entry : map.entrySet()) {
            tempMap.put(UriComponent.contextualEncode((String)entry.getKey(), UriComponent.Type.PATH_SEGMENT), entry.getValue());
        }
        map.clear();
        map.putAll((Map<? extends String, ? extends T>)tempMap);
    }
    
    private interface TypeParser<T>
    {
        T valueOf(final String p0);
    }
}
