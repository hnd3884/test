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
final class QueryParamValueParamProvider extends AbstractValueParamProvider
{
    public QueryParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.QUERY });
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
        return new QueryParamValueProvider(e, !parameter.isEncoded());
    }
    
    private static final class QueryParamValueProvider implements Function<ContainerRequest, Object>
    {
        private final MultivaluedParameterExtractor<?> extractor;
        private final boolean decode;
        
        QueryParamValueProvider(final MultivaluedParameterExtractor<?> extractor, final boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        @Override
        public Object apply(final ContainerRequest containerRequest) {
            try {
                return this.extractor.extract((MultivaluedMap<String, String>)containerRequest.getUriInfo().getQueryParameters(this.decode));
            }
            catch (final ExtractorException e) {
                throw new ParamException.QueryParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
        }
    }
}
