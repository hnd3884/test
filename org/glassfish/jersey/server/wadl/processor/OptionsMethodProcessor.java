package org.glassfish.jersey.server.wadl.processor;

import java.util.Set;
import org.glassfish.jersey.server.model.RuntimeResource;
import javax.inject.Inject;
import org.glassfish.jersey.server.ExtendedUriInfo;
import javax.inject.Provider;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.model.ResourceModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.process.Inflector;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import org.glassfish.jersey.server.model.internal.ModelProcessorUtil;
import java.util.List;
import javax.annotation.Priority;
import org.glassfish.jersey.server.model.ModelProcessor;

@Priority(Integer.MAX_VALUE)
public class OptionsMethodProcessor implements ModelProcessor
{
    private final List<ModelProcessorUtil.Method> methodList;
    
    public OptionsMethodProcessor() {
        (this.methodList = new ArrayList<ModelProcessorUtil.Method>()).add(new ModelProcessorUtil.Method("OPTIONS", MediaType.WILDCARD_TYPE, MediaType.TEXT_PLAIN_TYPE, (Class<? extends Inflector<ContainerRequestContext, Response>>)PlainTextOptionsInflector.class));
        this.methodList.add(new ModelProcessorUtil.Method("OPTIONS", MediaType.WILDCARD_TYPE, MediaType.WILDCARD_TYPE, (Class<? extends Inflector<ContainerRequestContext, Response>>)GenericOptionsInflector.class));
    }
    
    @Override
    public ResourceModel processResourceModel(final ResourceModel resourceModel, final Configuration configuration) {
        return ModelProcessorUtil.enhanceResourceModel(resourceModel, false, this.methodList, true).build();
    }
    
    @Override
    public ResourceModel processSubResource(final ResourceModel subResourceModel, final Configuration configuration) {
        return ModelProcessorUtil.enhanceResourceModel(subResourceModel, true, this.methodList, true).build();
    }
    
    private static class PlainTextOptionsInflector implements Inflector<ContainerRequestContext, Response>
    {
        @Inject
        private Provider<ExtendedUriInfo> extendedUriInfo;
        
        public Response apply(final ContainerRequestContext containerRequestContext) {
            final Set<String> allowedMethods = ModelProcessorUtil.getAllowedMethods(((ExtendedUriInfo)this.extendedUriInfo.get()).getMatchedRuntimeResources().get(0));
            final String allowedList = allowedMethods.toString();
            final String optionsBody = allowedList.substring(1, allowedList.length() - 1);
            return Response.ok((Object)optionsBody, MediaType.TEXT_PLAIN_TYPE).allow((Set)allowedMethods).build();
        }
    }
    
    private static class GenericOptionsInflector implements Inflector<ContainerRequestContext, Response>
    {
        @Inject
        private Provider<ExtendedUriInfo> extendedUriInfo;
        
        public Response apply(final ContainerRequestContext containerRequestContext) {
            final Set<String> allowedMethods = ModelProcessorUtil.getAllowedMethods(((ExtendedUriInfo)this.extendedUriInfo.get()).getMatchedRuntimeResources().get(0));
            return Response.ok().allow((Set)allowedMethods).header("Content-Length", (Object)"0").type((MediaType)containerRequestContext.getAcceptableMediaTypes().get(0)).build();
        }
    }
}
