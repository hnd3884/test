package org.glassfish.jersey.message.internal;

import java.util.Collections;
import java.text.ParseException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.Set;
import javax.ws.rs.core.EntityTag;

public class MatchingEntityTag extends EntityTag
{
    public static final Set<MatchingEntityTag> ANY_MATCH;
    
    public MatchingEntityTag(final String value) {
        super(value, false);
    }
    
    public MatchingEntityTag(final String value, final boolean weak) {
        super(value, weak);
    }
    
    public static MatchingEntityTag valueOf(final HttpHeaderReader reader) throws ParseException {
        final CharSequence tagString = reader.getRemainder();
        final HttpHeaderReader.Event e = reader.next(false);
        if (e == HttpHeaderReader.Event.QuotedString) {
            return new MatchingEntityTag(reader.getEventValue().toString());
        }
        if (e == HttpHeaderReader.Event.Token) {
            final CharSequence ev = reader.getEventValue();
            if (ev != null && ev.length() == 1 && 'W' == ev.charAt(0)) {
                reader.nextSeparator('/');
                return new MatchingEntityTag(reader.nextQuotedString().toString(), true);
            }
        }
        throw new ParseException(LocalizationMessages.ERROR_PARSING_ENTITY_TAG(tagString), reader.getIndex());
    }
    
    static {
        ANY_MATCH = Collections.emptySet();
    }
}
