package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import org.glassfish.jersey.internal.LocalizationMessages;
import javax.inject.Singleton;
import javax.ws.rs.core.EntityTag;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class EntityTagProvider implements HeaderDelegateProvider<EntityTag>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == EntityTag.class;
    }
    
    public String toString(final EntityTag header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.ENTITY_TAG_IS_NULL());
        final StringBuilder b = new StringBuilder();
        if (header.isWeak()) {
            b.append("W/");
        }
        StringBuilderUtils.appendQuoted(b, header.getValue());
        return b.toString();
    }
    
    public EntityTag fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.ENTITY_TAG_IS_NULL());
        try {
            final HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
            final HttpHeaderReader.Event e = reader.next(false);
            if (e == HttpHeaderReader.Event.QuotedString) {
                return new EntityTag(reader.getEventValue().toString());
            }
            if (e == HttpHeaderReader.Event.Token) {
                final CharSequence ev = reader.getEventValue();
                if (ev != null && ev.length() > 0 && ev.charAt(0) == 'W') {
                    reader.nextSeparator('/');
                    return new EntityTag(reader.nextQuotedString().toString(), true);
                }
            }
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException("Error parsing entity tag '" + header + "'", ex);
        }
        throw new IllegalArgumentException("Error parsing entity tag '" + header + "'");
    }
}
