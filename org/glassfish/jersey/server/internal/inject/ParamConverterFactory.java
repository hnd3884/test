package org.glassfish.jersey.server.internal.inject;

import java.util.Iterator;
import javax.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverterProvider;

@Singleton
public class ParamConverterFactory implements ParamConverterProvider
{
    private final List<ParamConverterProvider> converterProviders;
    
    ParamConverterFactory(final Set<ParamConverterProvider> providers, final Set<ParamConverterProvider> customProviders) {
        final Set<ParamConverterProvider> copyProviders = new HashSet<ParamConverterProvider>(providers);
        (this.converterProviders = new ArrayList<ParamConverterProvider>()).addAll(customProviders);
        copyProviders.removeAll(customProviders);
        this.converterProviders.addAll(copyProviders);
    }
    
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
        for (final ParamConverterProvider provider : this.converterProviders) {
            final ParamConverter<T> converter = (ParamConverter<T>)provider.getConverter((Class)rawType, genericType, annotations);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }
}
