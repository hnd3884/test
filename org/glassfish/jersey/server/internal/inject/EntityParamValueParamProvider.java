package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.BadRequestException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Request;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
class EntityParamValueParamProvider extends AbstractValueParamProvider
{
    EntityParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.ENTITY });
    }
    
    @Override
    protected Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        return new EntityValueSupplier(parameter);
    }
    
    private static class EntityValueSupplier implements Function<ContainerRequest, Object>
    {
        private final Parameter parameter;
        
        public EntityValueSupplier(final Parameter parameter) {
            this.parameter = parameter;
        }
        
        @Override
        public Object apply(final ContainerRequest containerRequest) {
            final Class<?> rawType = this.parameter.getRawType();
            Object value;
            if ((Request.class.isAssignableFrom(rawType) || ContainerRequestContext.class.isAssignableFrom(rawType)) && rawType.isInstance(containerRequest)) {
                value = containerRequest;
            }
            else {
                value = containerRequest.readEntity(rawType, this.parameter.getType(), this.parameter.getAnnotations());
                if (rawType.isPrimitive() && value == null) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity((Object)LocalizationMessages.ERROR_PRIMITIVE_TYPE_NULL()).build());
                }
            }
            return value;
        }
    }
}
