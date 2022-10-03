package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ParamException;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.ws.rs.core.PathSegment;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
final class PathParamValueParamProvider extends AbstractValueParamProvider
{
    public PathParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.PATH });
    }
    
    public Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        final String parameterName = parameter.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        final Class<?> rawParameterType = parameter.getRawType();
        if (rawParameterType == PathSegment.class) {
            return new PathParamPathSegmentValueSupplier(parameterName, !parameter.isEncoded());
        }
        if (rawParameterType == List.class && parameter.getType() instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)parameter.getType();
            final Type[] targs = pt.getActualTypeArguments();
            if (targs.length == 1 && targs[0] == PathSegment.class) {
                return new PathParamListPathSegmentValueSupplier(parameterName, !parameter.isEncoded());
            }
        }
        final MultivaluedParameterExtractor<?> e = this.get(parameter);
        if (e == null) {
            return null;
        }
        return new PathParamValueProvider(e, !parameter.isEncoded());
    }
    
    private static final class PathParamValueProvider implements Function<ContainerRequest, Object>
    {
        private final MultivaluedParameterExtractor<?> extractor;
        private final boolean decode;
        
        PathParamValueProvider(final MultivaluedParameterExtractor<?> extractor, final boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        @Override
        public Object apply(final ContainerRequest request) {
            try {
                return this.extractor.extract((MultivaluedMap<String, String>)request.getUriInfo().getPathParameters(this.decode));
            }
            catch (final ExtractorException e) {
                throw new ParamException.PathParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
        }
    }
    
    private static final class PathParamPathSegmentValueSupplier implements Function<ContainerRequest, PathSegment>
    {
        private final String name;
        private final boolean decode;
        
        PathParamPathSegmentValueSupplier(final String name, final boolean decode) {
            this.name = name;
            this.decode = decode;
        }
        
        @Override
        public PathSegment apply(final ContainerRequest request) {
            final List<PathSegment> ps = request.getUriInfo().getPathSegments(this.name, this.decode);
            if (ps.isEmpty()) {
                return null;
            }
            return ps.get(ps.size() - 1);
        }
    }
    
    private static final class PathParamListPathSegmentValueSupplier implements Function<ContainerRequest, List<PathSegment>>
    {
        private final String name;
        private final boolean decode;
        
        PathParamListPathSegmentValueSupplier(final String name, final boolean decode) {
            this.name = name;
            this.decode = decode;
        }
        
        @Override
        public List<PathSegment> apply(final ContainerRequest request) {
            return request.getUriInfo().getPathSegments(this.name, this.decode);
        }
    }
}
