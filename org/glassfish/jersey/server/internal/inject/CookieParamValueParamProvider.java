package org.glassfish.jersey.server.internal.inject;

import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ParamException;
import java.util.Map;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import javax.ws.rs.core.Cookie;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
final class CookieParamValueParamProvider extends AbstractValueParamProvider
{
    public CookieParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.COOKIE });
    }
    
    public Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        final String parameterName = parameter.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        if (parameter.getRawType() == Cookie.class) {
            return new CookieTypeParamValueProvider(parameterName);
        }
        final MultivaluedParameterExtractor e = this.get(parameter);
        if (e == null) {
            return null;
        }
        return new CookieParamValueProvider(e);
    }
    
    private static final class CookieParamValueProvider implements Function<ContainerRequest, Object>
    {
        private final MultivaluedParameterExtractor<?> extractor;
        
        CookieParamValueProvider(final MultivaluedParameterExtractor<?> extractor) {
            this.extractor = extractor;
        }
        
        @Override
        public Object apply(final ContainerRequest containerRequest) {
            final MultivaluedMap<String, String> cookies = (MultivaluedMap<String, String>)new MultivaluedStringMap();
            for (final Map.Entry<String, Cookie> e : containerRequest.getCookies().entrySet()) {
                cookies.putSingle((Object)e.getKey(), (Object)e.getValue().getValue());
            }
            try {
                return this.extractor.extract(cookies);
            }
            catch (final ExtractorException ex) {
                throw new ParamException.CookieParamException(ex.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
        }
    }
    
    private static final class CookieTypeParamValueProvider implements Function<ContainerRequest, Cookie>
    {
        private final String name;
        
        CookieTypeParamValueProvider(final String name) {
            this.name = name;
        }
        
        @Override
        public Cookie apply(final ContainerRequest containerRequest) {
            return containerRequest.getCookies().get(this.name);
        }
    }
}
