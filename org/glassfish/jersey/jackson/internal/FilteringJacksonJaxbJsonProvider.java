package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;
import java.io.IOException;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterModifier;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.lang.reflect.Method;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JsonEndpointConfig;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.glassfish.jersey.message.filtering.spi.ObjectProvider;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

@Singleton
public final class FilteringJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider
{
    @Inject
    private Provider<ObjectProvider<FilterProvider>> provider;
    
    @Override
    protected JsonEndpointConfig _configForWriting(final ObjectMapper mapper, final Annotation[] annotations, final Class<?> defaultView) {
        final AnnotationIntrospector customIntrospector = mapper.getSerializationConfig().getAnnotationIntrospector();
        final ObjectMapper filteringMapper = mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(customIntrospector, (AnnotationIntrospector)new JacksonAnnotationIntrospector() {
            public Object findFilterId(final Annotated a) {
                final Object filterId = super.findFilterId(a);
                if (filterId != null) {
                    return filterId;
                }
                if (a instanceof AnnotatedMethod) {
                    final Method method = ((AnnotatedMethod)a).getAnnotated();
                    if (ReflectionHelper.isGetter(method)) {
                        return ReflectionHelper.getPropertyName(method);
                    }
                }
                if (a instanceof AnnotatedField || a instanceof AnnotatedClass) {
                    return a.getName();
                }
                return null;
            }
        }));
        return super._configForWriting(filteringMapper, annotations, defaultView);
    }
    
    @Override
    public void writeTo(final Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final FilterProvider filterProvider = (FilterProvider)((ObjectProvider)this.provider.get()).getFilteringObject(genericType, true, annotations);
        if (filterProvider != null) {
            ObjectWriterInjector.set(new FilteringObjectWriterModifier(filterProvider, ObjectWriterInjector.getAndClear()));
        }
        super.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
    
    private static final class FilteringObjectWriterModifier extends ObjectWriterModifier
    {
        private final ObjectWriterModifier original;
        private final FilterProvider filterProvider;
        
        private FilteringObjectWriterModifier(final FilterProvider filterProvider, final ObjectWriterModifier original) {
            this.original = original;
            this.filterProvider = filterProvider;
        }
        
        @Override
        public ObjectWriter modify(final EndpointConfigBase<?> endpoint, final MultivaluedMap<String, Object> responseHeaders, final Object valueToWrite, final ObjectWriter w, final JsonGenerator g) throws IOException {
            final ObjectWriter writer = (this.original == null) ? w : this.original.modify(endpoint, responseHeaders, valueToWrite, w, g);
            final FilterProvider customFilterProvider = writer.getConfig().getFilterProvider();
            return (customFilterProvider == null) ? writer.with(this.filterProvider) : writer.with((FilterProvider)new FilterProvider() {
                public BeanPropertyFilter findFilter(final Object filterId) {
                    return customFilterProvider.findFilter(filterId);
                }
                
                public PropertyFilter findPropertyFilter(final Object filterId, final Object valueToFilter) {
                    final PropertyFilter filter = customFilterProvider.findPropertyFilter(filterId, valueToFilter);
                    if (filter != null) {
                        return filter;
                    }
                    return FilteringObjectWriterModifier.this.filterProvider.findPropertyFilter(filterId, valueToFilter);
                }
            });
        }
    }
}
