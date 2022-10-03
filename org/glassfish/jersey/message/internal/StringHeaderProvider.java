package org.glassfish.jersey.message.internal;

import org.glassfish.jersey.internal.LocalizationMessages;
import javax.inject.Singleton;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class StringHeaderProvider implements HeaderDelegateProvider<String>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == String.class;
    }
    
    public String toString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.STRING_IS_NULL());
        return header;
    }
    
    public String fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.STRING_IS_NULL());
        return header;
    }
}
