package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import org.glassfish.jersey.internal.LocalizationMessages;
import javax.inject.Singleton;
import java.util.Date;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class DateProvider implements HeaderDelegateProvider<Date>
{
    @Override
    public boolean supports(final Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }
    
    public String toString(final Date header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.DATE_IS_NULL());
        return HttpDateFormat.getPreferredDateFormat().format(header);
    }
    
    public Date fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.DATE_IS_NULL());
        try {
            return HttpHeaderReader.readDate(header);
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException("Error parsing date '" + header + "'", ex);
        }
    }
}
