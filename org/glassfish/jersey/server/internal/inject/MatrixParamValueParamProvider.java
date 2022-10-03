package org.glassfish.jersey.server.internal.inject;

import java.util.List;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ParamException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
final class MatrixParamValueParamProvider extends AbstractValueParamProvider
{
    public MatrixParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.MATRIX });
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
        return new MatrixParamValueProvider(e, !parameter.isEncoded());
    }
    
    private static final class MatrixParamValueProvider implements Function<ContainerRequest, Object>
    {
        private final MultivaluedParameterExtractor<?> extractor;
        private final boolean decode;
        
        MatrixParamValueProvider(final MultivaluedParameterExtractor<?> extractor, final boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        @Override
        public Object apply(final ContainerRequest containerRequest) {
            final List<PathSegment> l = containerRequest.getUriInfo().getPathSegments(this.decode);
            final PathSegment p = l.get(l.size() - 1);
            try {
                return this.extractor.extract((MultivaluedMap<String, String>)p.getMatrixParameters());
            }
            catch (final ExtractorException e) {
                throw new ParamException.MatrixParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
        }
    }
}
