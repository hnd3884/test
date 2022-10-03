package org.glassfish.jersey.message.internal;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Collections;
import java.text.ParseException;
import java.util.ArrayList;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import javax.ws.rs.core.MediaType;

public final class MediaTypes
{
    public static final MediaType WADL_TYPE;
    public static final Comparator<MediaType> PARTIAL_ORDER_COMPARATOR;
    public static final Comparator<List<? extends MediaType>> MEDIA_TYPE_LIST_COMPARATOR;
    public static final List<MediaType> WILDCARD_TYPE_SINGLETON_LIST;
    public static final AcceptableMediaType WILDCARD_ACCEPTABLE_TYPE;
    public static final QualitySourceMediaType WILDCARD_QS_TYPE;
    public static final List<MediaType> WILDCARD_QS_TYPE_SINGLETON_LIST;
    private static final Map<String, MediaType> WILDCARD_SUBTYPE_CACHE;
    private static final Predicate<String> QUALITY_PARAM_FILTERING_PREDICATE;
    
    private MediaTypes() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
    
    public static boolean typeEqual(final MediaType m1, final MediaType m2) {
        return m1 != null && m2 != null && m1.getSubtype().equalsIgnoreCase(m2.getSubtype()) && m1.getType().equalsIgnoreCase(m2.getType());
    }
    
    public static boolean intersect(final List<? extends MediaType> ml1, final List<? extends MediaType> ml2) {
        for (final MediaType m1 : ml1) {
            for (final MediaType m2 : ml2) {
                if (typeEqual(m1, m2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static MediaType mostSpecific(final MediaType m1, final MediaType m2) {
        if (m1.isWildcardType() && !m2.isWildcardType()) {
            return m2;
        }
        if (m1.isWildcardSubtype() && !m2.isWildcardSubtype()) {
            return m2;
        }
        if (m2.getParameters().size() > m1.getParameters().size()) {
            return m2;
        }
        return m1;
    }
    
    public static List<MediaType> createFrom(final Consumes annotation) {
        if (annotation == null) {
            return MediaTypes.WILDCARD_TYPE_SINGLETON_LIST;
        }
        return createFrom(annotation.value());
    }
    
    public static List<MediaType> createFrom(final Produces annotation) {
        if (annotation == null) {
            return MediaTypes.WILDCARD_TYPE_SINGLETON_LIST;
        }
        return createFrom(annotation.value());
    }
    
    public static List<MediaType> createFrom(final String[] mediaTypes) {
        final List<MediaType> result = new ArrayList<MediaType>();
        try {
            for (final String mediaType : mediaTypes) {
                HttpHeaderReader.readMediaTypes(result, mediaType);
            }
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
        Collections.sort(result, MediaTypes.PARTIAL_ORDER_COMPARATOR);
        return Collections.unmodifiableList((List<? extends MediaType>)result);
    }
    
    public static List<MediaType> createQualitySourceMediaTypes(final Produces mime) {
        if (mime == null || mime.value().length == 0) {
            return MediaTypes.WILDCARD_QS_TYPE_SINGLETON_LIST;
        }
        return new ArrayList<MediaType>(createQualitySourceMediaTypes(mime.value()));
    }
    
    public static List<QualitySourceMediaType> createQualitySourceMediaTypes(final String[] mediaTypes) {
        try {
            return HttpHeaderReader.readQualitySourceMediaType(mediaTypes);
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public static int getQuality(final MediaType mt) {
        final String qParam = mt.getParameters().get("q");
        return readQualityFactor(qParam);
    }
    
    private static int readQualityFactor(final String qParam) throws IllegalArgumentException {
        if (qParam == null) {
            return 1000;
        }
        try {
            return HttpHeaderReader.readQualityFactor(qParam);
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public static MediaType stripQualityParams(final MediaType mediaType) {
        final Map<String, String> oldParameters = mediaType.getParameters();
        if (oldParameters.isEmpty() || (!oldParameters.containsKey("qs") && !oldParameters.containsKey("q"))) {
            return mediaType;
        }
        return new MediaType(mediaType.getType(), mediaType.getSubtype(), (Map)oldParameters.entrySet().stream().filter(entry -> MediaTypes.QUALITY_PARAM_FILTERING_PREDICATE.test(entry.getKey())).collect(Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue)));
    }
    
    public static MediaType getTypeWildCart(final MediaType mediaType) {
        MediaType mt = MediaTypes.WILDCARD_SUBTYPE_CACHE.get(mediaType.getType());
        if (mt == null) {
            mt = new MediaType(mediaType.getType(), "*");
        }
        return mt;
    }
    
    public static String convertToString(final Iterable<MediaType> mediaTypes) {
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (final MediaType mediaType : mediaTypes) {
            if (!isFirst) {
                sb.append(", ");
            }
            else {
                isFirst = false;
            }
            sb.append("\"").append(mediaType.toString()).append("\"");
        }
        return sb.toString();
    }
    
    public static boolean isWildcard(final MediaType mediaType) {
        return mediaType.isWildcardType() || mediaType.isWildcardSubtype();
    }
    
    static {
        WADL_TYPE = MediaType.valueOf("application/vnd.sun.wadl+xml");
        PARTIAL_ORDER_COMPARATOR = new Comparator<MediaType>() {
            private int rank(final MediaType type) {
                return (type.isWildcardType() ? 1 : 0) << 1 | (type.isWildcardSubtype() ? 1 : 0);
            }
            
            @Override
            public int compare(final MediaType typeA, final MediaType typeB) {
                return this.rank(typeA) - this.rank(typeB);
            }
        };
        MEDIA_TYPE_LIST_COMPARATOR = new Comparator<List<? extends MediaType>>() {
            @Override
            public int compare(final List<? extends MediaType> o1, final List<? extends MediaType> o2) {
                return MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(this.getLeastSpecific(o1), this.getLeastSpecific(o2));
            }
            
            private MediaType getLeastSpecific(final List<? extends MediaType> l) {
                return (MediaType)l.get(l.size() - 1);
            }
        };
        WILDCARD_TYPE_SINGLETON_LIST = Collections.singletonList(MediaType.WILDCARD_TYPE);
        WILDCARD_ACCEPTABLE_TYPE = new AcceptableMediaType("*", "*");
        WILDCARD_QS_TYPE = new QualitySourceMediaType("*", "*");
        WILDCARD_QS_TYPE_SINGLETON_LIST = Collections.singletonList(MediaTypes.WILDCARD_QS_TYPE);
        WILDCARD_SUBTYPE_CACHE = new HashMap<String, MediaType>() {
            private static final long serialVersionUID = 3109256773218160485L;
            
            {
                this.put("application", new MediaType("application", "*"));
                this.put("multipart", new MediaType("multipart", "*"));
                this.put("text", new MediaType("text", "*"));
            }
        };
        QUALITY_PARAM_FILTERING_PREDICATE = (input -> !"qs".equals(input) && !"q".equals(input));
    }
}
