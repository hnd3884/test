package org.glassfish.jersey.message.internal;

import java.net.URISyntaxException;
import org.glassfish.jersey.internal.LocalizationMessages;
import javax.inject.Singleton;
import java.net.URI;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class UriProvider implements HeaderDelegateProvider<URI>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == URI.class;
    }
    
    public String toString(final URI header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.URI_IS_NULL());
        return header.toASCIIString();
    }
    
    public URI fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.URI_IS_NULL());
        try {
            return new URI(header);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Error parsing uri '" + header + "'", e);
        }
    }
}
