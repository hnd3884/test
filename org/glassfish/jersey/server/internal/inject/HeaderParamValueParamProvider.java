package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ParamException;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
final class HeaderParamValueParamProvider extends AbstractValueParamProvider
{
    public HeaderParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.HEADER });
    }
    
    public Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        final String parameterName = parameter.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        final MultivaluedParameterExtractor e = this.get(parameter);
        if (e == null) {
            return null;
        }
        return new HeaderParamValueProvider(e);
    }
    
    private static final class HeaderParamValueProvider implements Function<ContainerRequest, Object>
    {
        private final MultivaluedParameterExtractor<?> extractor;
        
        HeaderParamValueProvider(final MultivaluedParameterExtractor<?> extractor) {
            this.extractor = extractor;
        }
        
        @Override
        public Object apply(final ContainerRequest containerRequest) {
            try {
                return this.extractor.extract((MultivaluedMap<String, String>)containerRequest.getHeaders());
            }
            catch (final ExtractorException e) {
                throw new ParamException.HeaderParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
        }
    }
}
