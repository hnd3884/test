package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import org.glassfish.jersey.internal.LocalizationMessages;
import javax.inject.Singleton;
import java.util.Locale;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class LocaleProvider implements HeaderDelegateProvider<Locale>
{
    @Override
    public boolean supports(final Class<?> type) {
        return Locale.class.isAssignableFrom(type);
    }
    
    public String toString(final Locale header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.LOCALE_IS_NULL());
        if (header.getCountry().length() == 0) {
            return header.getLanguage();
        }
        return header.getLanguage() + '-' + header.getCountry();
    }
    
    public Locale fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.LOCALE_IS_NULL());
        try {
            final LanguageTag lt = new LanguageTag(header);
            return lt.getAsLocale();
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException("Error parsing date '" + header + "'", ex);
        }
    }
}
