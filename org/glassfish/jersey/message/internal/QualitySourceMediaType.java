package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import java.util.Map;
import java.util.Comparator;
import javax.ws.rs.core.MediaType;

public class QualitySourceMediaType extends MediaType implements Qualified
{
    public static final Comparator<QualitySourceMediaType> COMPARATOR;
    private final int qs;
    
    public QualitySourceMediaType(final String type, final String subtype) {
        super(type, subtype);
        this.qs = 1000;
    }
    
    public QualitySourceMediaType(final String type, final String subtype, final int quality, final Map<String, String> parameters) {
        super(type, subtype, (Map)Quality.enhanceWithQualityParameter(parameters, "qs", quality));
        this.qs = quality;
    }
    
    private QualitySourceMediaType(final String type, final String subtype, final Map<String, String> parameters, final int quality) {
        super(type, subtype, (Map)parameters);
        this.qs = quality;
    }
    
    public int getQuality() {
        return this.qs;
    }
    
    public static QualitySourceMediaType valueOf(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        final String type = reader.nextToken().toString();
        reader.nextSeparator('/');
        final String subType = reader.nextToken().toString();
        int qs = 1000;
        Map<String, String> parameters = null;
        if (reader.hasNext()) {
            parameters = HttpHeaderReader.readParameters(reader);
            if (parameters != null) {
                qs = getQs(parameters.get("qs"));
            }
        }
        return new QualitySourceMediaType(type, subType, parameters, qs);
    }
    
    public static int getQualitySource(final MediaType mediaType) throws IllegalArgumentException {
        if (mediaType instanceof QualitySourceMediaType) {
            return ((QualitySourceMediaType)mediaType).getQuality();
        }
        return getQs(mediaType);
    }
    
    private static int getQs(final MediaType mt) throws IllegalArgumentException {
        try {
            return getQs(mt.getParameters().get("qs"));
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private static int getQs(final String v) throws ParseException {
        if (v == null) {
            return 1000;
        }
        return HttpHeaderReader.readQualityFactor(v);
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof QualitySourceMediaType) {
            final QualitySourceMediaType other = (QualitySourceMediaType)obj;
            return this.qs == other.qs;
        }
        return this.qs == 1000;
    }
    
    public int hashCode() {
        final int hash = super.hashCode();
        return (this.qs == 1000) ? hash : (47 * hash + this.qs);
    }
    
    public String toString() {
        return "{" + super.toString() + ", qs=" + this.qs + "}";
    }
    
    static {
        COMPARATOR = new Comparator<QualitySourceMediaType>() {
            @Override
            public int compare(final QualitySourceMediaType o1, final QualitySourceMediaType o2) {
                final int i = Quality.QUALIFIED_COMPARATOR.compare(o1, o2);
                if (i != 0) {
                    return i;
                }
                return MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1, o2);
            }
        };
    }
}
