package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import java.util.Map;
import java.util.Comparator;
import javax.ws.rs.core.MediaType;

public class AcceptableMediaType extends MediaType implements Qualified
{
    public static final Comparator<AcceptableMediaType> COMPARATOR;
    private final int q;
    
    public AcceptableMediaType(final String type, final String subtype) {
        super(type, subtype);
        this.q = 1000;
    }
    
    public AcceptableMediaType(final String type, final String subtype, final int quality, final Map<String, String> parameters) {
        super(type, subtype, (Map)Quality.enhanceWithQualityParameter(parameters, "q", quality));
        this.q = quality;
    }
    
    private AcceptableMediaType(final String type, final String subtype, final Map<String, String> parameters, final int quality) {
        super(type, subtype, (Map)parameters);
        this.q = quality;
    }
    
    public int getQuality() {
        return this.q;
    }
    
    public static AcceptableMediaType valueOf(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        final String type = reader.nextToken().toString();
        String subType = "*";
        if (reader.hasNextSeparator('/', false)) {
            reader.next(false);
            subType = reader.nextToken().toString();
        }
        Map<String, String> parameters = null;
        int quality = 1000;
        if (reader.hasNext()) {
            parameters = HttpHeaderReader.readParameters(reader);
            if (parameters != null) {
                final String v = parameters.get("q");
                if (v != null) {
                    quality = HttpHeaderReader.readQualityFactor(v);
                }
            }
        }
        return new AcceptableMediaType(type, subType, parameters, quality);
    }
    
    public static AcceptableMediaType valueOf(final MediaType mediaType) throws ParseException {
        if (mediaType instanceof AcceptableMediaType) {
            return (AcceptableMediaType)mediaType;
        }
        final Map<String, String> parameters = mediaType.getParameters();
        int quality = 1000;
        if (parameters != null) {
            final String v = parameters.get("q");
            if (v != null) {
                quality = HttpHeaderReader.readQualityFactor(v);
            }
        }
        return new AcceptableMediaType(mediaType.getType(), mediaType.getSubtype(), parameters, quality);
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof AcceptableMediaType) {
            final AcceptableMediaType other = (AcceptableMediaType)obj;
            return this.q == other.q;
        }
        return this.q == 1000;
    }
    
    public int hashCode() {
        final int hash = super.hashCode();
        return (this.q == 1000) ? hash : (47 * hash + this.q);
    }
    
    static {
        COMPARATOR = new Comparator<AcceptableMediaType>() {
            @Override
            public int compare(final AcceptableMediaType o1, final AcceptableMediaType o2) {
                final int i = Quality.QUALIFIED_COMPARATOR.compare(o1, o2);
                if (i != 0) {
                    return i;
                }
                return MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1, o2);
            }
        };
    }
}
