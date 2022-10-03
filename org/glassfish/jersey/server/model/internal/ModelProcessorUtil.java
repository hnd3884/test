package org.glassfish.jersey.server.model.internal;

import java.util.Collections;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.process.Inflector;
import java.util.Collection;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import java.util.List;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.server.model.RuntimeResource;

public final class ModelProcessorUtil
{
    private ModelProcessorUtil() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
    
    public static Set<String> getAllowedMethods(final RuntimeResource resource) {
        boolean getFound = false;
        final Set<String> allowedMethods = new HashSet<String>();
        for (final ResourceMethod resourceMethod : resource.getResourceMethods()) {
            final String httpMethod = resourceMethod.getHttpMethod();
            allowedMethods.add(httpMethod);
            if (!getFound && httpMethod.equals("GET")) {
                getFound = true;
            }
        }
        allowedMethods.add("OPTIONS");
        if (getFound) {
            allowedMethods.add("HEAD");
        }
        return allowedMethods;
    }
    
    private static boolean isMethodOverridden(final ResourceMethod resourceMethod, final String httpMethod, final MediaType consumes, final MediaType produces) {
        if (!resourceMethod.getHttpMethod().equals(httpMethod)) {
            return false;
        }
        final boolean consumesMatch = overrides(resourceMethod.getConsumedTypes(), consumes);
        final boolean producesMatch = overrides(resourceMethod.getProducedTypes(), produces);
        return consumesMatch && producesMatch;
    }
    
    private static boolean overrides(final List<MediaType> mediaTypes, final MediaType mediaType) {
        if (mediaTypes.isEmpty()) {
            return true;
        }
        for (final MediaType mt : mediaTypes) {
            if (overrides(mt, mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean overrides(final MediaType mt1, final MediaType mt2) {
        return mt1.isWildcardType() || (mt1.getType().equals(mt2.getType()) && (mt1.isWildcardSubtype() || mt1.getSubtype().equals(mt2.getSubtype())));
    }
    
    public static ResourceModel.Builder enhanceResourceModel(final ResourceModel resourceModel, final boolean subResourceModel, final List<Method> methods, final boolean extendedFlag) {
        final ResourceModel.Builder newModelBuilder = new ResourceModel.Builder(resourceModel, subResourceModel);
        for (final RuntimeResource resource : resourceModel.getRuntimeResourceModel().getRuntimeResources()) {
            enhanceResource(resource, newModelBuilder, methods, extendedFlag);
        }
        return newModelBuilder;
    }
    
    public static void enhanceResource(final RuntimeResource resource, final ResourceModel.Builder enhancedModelBuilder, final List<Method> methods, final boolean extended) {
        final Resource firstResource = resource.getResources().get(0);
        if (methodsSuitableForResource(firstResource, methods)) {
            for (final Method method : methods) {
                final Set<MediaType> produces = new HashSet<MediaType>(method.produces);
                for (final ResourceMethod resourceMethod : resource.getResourceMethods()) {
                    for (final MediaType produce : method.produces) {
                        if (isMethodOverridden(resourceMethod, method.httpMethod, method.consumes.get(0), produce)) {
                            produces.remove(produce);
                        }
                    }
                }
                if (!produces.isEmpty()) {
                    final Resource parentResource = resource.getParentResources().get(0);
                    if (parentResource != null && method.path != null) {
                        continue;
                    }
                    final Resource.Builder resourceBuilder = Resource.builder(firstResource.getPath());
                    final Resource.Builder builder = (method.path != null) ? resourceBuilder.addChildResource(method.path) : resourceBuilder;
                    final ResourceMethod.Builder methodBuilder = builder.addMethod(method.httpMethod).consumes(method.consumes).produces(produces);
                    if (method.inflector != null) {
                        methodBuilder.handledBy(method.inflector);
                    }
                    else {
                        methodBuilder.handledBy(method.inflectorClass);
                    }
                    methodBuilder.extended(extended);
                    final Resource newResource = resourceBuilder.build();
                    if (parentResource != null) {
                        final Resource.Builder parentBuilder = Resource.builder(parentResource.getPath());
                        parentBuilder.addChildResource(newResource);
                        enhancedModelBuilder.addResource(parentBuilder.build());
                    }
                    else {
                        enhancedModelBuilder.addResource(newResource);
                    }
                }
            }
        }
        for (final RuntimeResource child : resource.getChildRuntimeResources()) {
            enhanceResource(child, enhancedModelBuilder, methods, extended);
        }
    }
    
    private static boolean methodsSuitableForResource(final Resource resource, final List<Method> methods) {
        if (!resource.getResourceMethods().isEmpty()) {
            return true;
        }
        if (resource.getHandlerInstances().isEmpty() && resource.getHandlerClasses().isEmpty()) {
            for (final Method method : methods) {
                if (!"HEAD".equals(method.httpMethod) && !"OPTIONS".equals(method.httpMethod)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static class Method
    {
        private final String httpMethod;
        private final String path;
        private final List<MediaType> consumes;
        private final List<MediaType> produces;
        private final Class<? extends Inflector<ContainerRequestContext, Response>> inflectorClass;
        private final Inflector<ContainerRequestContext, Response> inflector;
        
        public Method(final String path, final String httpMethod, final MediaType consumes, final MediaType produces, final Class<? extends Inflector<ContainerRequestContext, Response>> inflector) {
            this(path, httpMethod, Collections.singletonList(consumes), Collections.singletonList(produces), inflector);
        }
        
        public Method(final String path, final String httpMethod, final List<MediaType> consumes, final List<MediaType> produces, final Class<? extends Inflector<ContainerRequestContext, Response>> inflectorClass) {
            this.path = path;
            this.httpMethod = httpMethod;
            this.consumes = consumes;
            this.produces = produces;
            this.inflectorClass = inflectorClass;
            this.inflector = null;
        }
        
        public Method(final String httpMethod, final MediaType consumes, final MediaType produces, final Class<? extends Inflector<ContainerRequestContext, Response>> inflector) {
            this(null, httpMethod, consumes, produces, inflector);
        }
        
        public Method(final String httpMethod, final List<MediaType> consumes, final List<MediaType> produces, final Class<? extends Inflector<ContainerRequestContext, Response>> inflector) {
            this(null, httpMethod, consumes, produces, inflector);
        }
        
        public Method(final String path, final String httpMethod, final List<MediaType> consumes, final List<MediaType> produces, final Inflector<ContainerRequestContext, Response> inflector) {
            this.path = path;
            this.httpMethod = httpMethod;
            this.consumes = consumes;
            this.produces = produces;
            this.inflectorClass = null;
            this.inflector = inflector;
        }
        
        public Method(final String path, final String httpMethod, final MediaType consumes, final MediaType produces, final Inflector<ContainerRequestContext, Response> inflector) {
            this(path, httpMethod, Collections.singletonList(consumes), Collections.singletonList(produces), inflector);
        }
        
        public Method(final String httpMethod, final MediaType consumes, final MediaType produces, final Inflector<ContainerRequestContext, Response> inflector) {
            this(null, httpMethod, consumes, produces, inflector);
        }
        
        public Method(final String httpMethod, final List<MediaType> consumes, final List<MediaType> produces, final Inflector<ContainerRequestContext, Response> inflector) {
            this(null, httpMethod, consumes, produces, inflector);
        }
    }
}
