package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.internal.inject.ExtractorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ParamConverter;

final class SingleValueExtractor<T> extends AbstractParamValueExtractor<T> implements MultivaluedParameterExtractor<T>
{
    public SingleValueExtractor(final ParamConverter<T> converter, final String parameterName, final String defaultStringValue) {
        super(converter, parameterName, defaultStringValue);
    }
    
    @Override
    public T extract(final MultivaluedMap<String, String> parameters) {
        final String value = (String)parameters.getFirst((Object)this.getName());
        try {
            return this.fromString((value == null && this.isDefaultValueRegistered()) ? this.getDefaultValueString() : value);
        }
        catch (final WebApplicationException | ProcessingException ex) {
            throw ex;
        }
        catch (final IllegalArgumentException ex2) {
            return this.defaultValue();
        }
        catch (final Exception ex3) {
            throw new ExtractorException((Throwable)ex3);
        }
    }
}
