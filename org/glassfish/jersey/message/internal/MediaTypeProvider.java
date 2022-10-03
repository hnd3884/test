package org.glassfish.jersey.message.internal;

import org.glassfish.jersey.internal.LocalizationMessages;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class MediaTypeProvider implements HeaderDelegateProvider<MediaType>
{
    private static final String MEDIA_TYPE_IS_NULL;
    
    @Override
    public boolean supports(final Class<?> type) {
        return MediaType.class.isAssignableFrom(type);
    }
    
    public String toString(final MediaType header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, MediaTypeProvider.MEDIA_TYPE_IS_NULL);
        final StringBuilder b = new StringBuilder();
        b.append(header.getType()).append('/').append(header.getSubtype());
        for (final Map.Entry<String, String> e : header.getParameters().entrySet()) {
            b.append(";").append(e.getKey()).append('=');
            StringBuilderUtils.appendQuotedIfNonToken(b, e.getValue());
        }
        return b.toString();
    }
    
    public MediaType fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, MediaTypeProvider.MEDIA_TYPE_IS_NULL);
        try {
            return valueOf(HttpHeaderReader.newInstance(header));
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException("Error parsing media type '" + header + "'", ex);
        }
    }
    
    public static MediaType valueOf(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        final String type = reader.nextToken().toString();
        reader.nextSeparator('/');
        final String subType = reader.nextToken().toString();
        Map<String, String> params = null;
        if (reader.hasNext()) {
            params = HttpHeaderReader.readParameters(reader);
        }
        return new MediaType(type, subType, (Map)params);
    }
    
    static {
        MEDIA_TYPE_IS_NULL = LocalizationMessages.MEDIA_TYPE_IS_NULL();
    }
}
