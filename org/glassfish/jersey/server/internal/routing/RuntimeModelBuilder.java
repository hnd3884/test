package org.glassfish.jersey.server.internal.routing;

import java.util.Collections;
import java.util.ArrayList;
import org.glassfish.jersey.uri.UriTemplate;
import org.glassfish.jersey.server.model.Resource;
import java.util.Iterator;
import java.util.List;
import org.glassfish.jersey.uri.PathPattern;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.RuntimeResourceModel;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.internal.util.collection.Values;
import java.util.function.Function;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;

final class RuntimeModelBuilder
{
    private final ResourceMethodInvoker.Builder resourceMethodInvokerBuilder;
    private final MessageBodyWorkers messageBodyWorkers;
    private final ProcessingProviders processingProviders;
    private final Value<RuntimeLocatorModelBuilder> locatorBuilder;
    
    public RuntimeModelBuilder(final JerseyResourceContext resourceContext, final Configuration config, final MessageBodyWorkers messageBodyWorkers, final Collection<ValueParamProvider> valueSuppliers, final ProcessingProviders processingProviders, final ResourceMethodInvoker.Builder resourceMethodInvokerBuilder, final Iterable<ModelProcessor> modelProcessors, final Function<Class<?>, ?> createServiceFunction) {
        this.resourceMethodInvokerBuilder = resourceMethodInvokerBuilder;
        this.messageBodyWorkers = messageBodyWorkers;
        this.processingProviders = processingProviders;
        this.locatorBuilder = (Value<RuntimeLocatorModelBuilder>)Values.lazy(() -> new RuntimeLocatorModelBuilder(config, messageBodyWorkers, valueSuppliers, resourceContext, this, modelProcessors, createServiceFunction));
    }
    
    private Router createMethodRouter(final ResourceMethod resourceMethod) {
        Router methodAcceptor = null;
        switch (resourceMethod.getType()) {
            case RESOURCE_METHOD:
            case SUB_RESOURCE_METHOD: {
                methodAcceptor = Routers.endpoint(this.createInflector(resourceMethod));
                break;
            }
            case SUB_RESOURCE_LOCATOR: {
                methodAcceptor = ((RuntimeLocatorModelBuilder)this.locatorBuilder.get()).getRouter(resourceMethod);
                break;
            }
        }
        return new PushMethodHandlerRouter(resourceMethod.getInvocable().getHandler(), methodAcceptor);
    }
    
    private Endpoint createInflector(final ResourceMethod method) {
        return this.resourceMethodInvokerBuilder.build(method, this.processingProviders);
    }
    
    private Router createRootRouter(final PathMatchingRouterBuilder lastRoutedBuilder, final boolean subResourceMode) {
        Router routingRoot;
        if (lastRoutedBuilder != null) {
            routingRoot = lastRoutedBuilder.build();
        }
        else {
            routingRoot = Routers.noop();
        }
        if (subResourceMode) {
            return routingRoot;
        }
        return new MatchResultInitializerRouter(routingRoot);
    }
    
    public Router buildModel(final RuntimeResourceModel resourceModel, final boolean subResourceMode) {
        final List<RuntimeResource> runtimeResources = resourceModel.getRuntimeResources();
        final PushMatchedUriRouter uriPushingRouter = new PushMatchedUriRouter();
        PathMatchingRouterBuilder currentRouterBuilder = null;
        for (final RuntimeResource resource : runtimeResources) {
            final PushMatchedRuntimeResourceRouter resourcePushingRouter = new PushMatchedRuntimeResourceRouter(resource);
            if (!resource.getResourceMethods().isEmpty()) {
                final List<MethodRouting> methodRoutings = this.createResourceMethodRouters(resource, subResourceMode);
                final Router methodSelectingRouter = new MethodSelectingRouter(this.messageBodyWorkers, methodRoutings);
                if (subResourceMode) {
                    currentRouterBuilder = this.startNextRoute(currentRouterBuilder, PathPattern.END_OF_PATH_PATTERN).to(resourcePushingRouter).to(methodSelectingRouter);
                }
                else {
                    currentRouterBuilder = this.startNextRoute(currentRouterBuilder, PathPattern.asClosed(resource.getPathPattern())).to(uriPushingRouter).to(resourcePushingRouter).to(methodSelectingRouter);
                }
            }
            PathMatchingRouterBuilder srRoutedBuilder = null;
            if (!resource.getChildRuntimeResources().isEmpty()) {
                for (final RuntimeResource childResource : resource.getChildRuntimeResources()) {
                    final PathPattern childOpenPattern = childResource.getPathPattern();
                    final PathPattern childClosedPattern = PathPattern.asClosed(childOpenPattern);
                    final PushMatchedRuntimeResourceRouter childResourcePushingRouter = new PushMatchedRuntimeResourceRouter(childResource);
                    if (!childResource.getResourceMethods().isEmpty()) {
                        final List<MethodRouting> childMethodRoutings = this.createResourceMethodRouters(childResource, subResourceMode);
                        srRoutedBuilder = this.startNextRoute(srRoutedBuilder, childClosedPattern).to(uriPushingRouter).to(childResourcePushingRouter).to(new MethodSelectingRouter(this.messageBodyWorkers, childMethodRoutings));
                    }
                    if (childResource.getResourceLocator() != null) {
                        final PushMatchedTemplateRouter locTemplateRouter = this.getTemplateRouterForChildLocator(subResourceMode, childResource);
                        srRoutedBuilder = this.startNextRoute(srRoutedBuilder, childOpenPattern).to(uriPushingRouter).to(locTemplateRouter).to(childResourcePushingRouter).to(new PushMatchedMethodRouter(childResource.getResourceLocator())).to(this.createMethodRouter(childResource.getResourceLocator()));
                    }
                }
            }
            if (resource.getResourceLocator() != null) {
                final PushMatchedTemplateRouter resourceTemplateRouter = this.getTemplateRouter(subResourceMode, this.getLocatorResource(resource).getPathPattern().getTemplate(), PathPattern.OPEN_ROOT_PATH_PATTERN.getTemplate());
                srRoutedBuilder = this.startNextRoute(srRoutedBuilder, PathPattern.OPEN_ROOT_PATH_PATTERN).to(uriPushingRouter).to(resourceTemplateRouter).to(new PushMatchedMethodRouter(resource.getResourceLocator())).to(this.createMethodRouter(resource.getResourceLocator()));
            }
            if (srRoutedBuilder != null) {
                final Router methodRouter = srRoutedBuilder.build();
                if (subResourceMode) {
                    currentRouterBuilder = this.startNextRoute(currentRouterBuilder, PathPattern.OPEN_ROOT_PATH_PATTERN).to(resourcePushingRouter).to(methodRouter);
                }
                else {
                    currentRouterBuilder = this.startNextRoute(currentRouterBuilder, resource.getPathPattern()).to(uriPushingRouter).to(resourcePushingRouter).to(methodRouter);
                }
            }
        }
        return this.createRootRouter(currentRouterBuilder, subResourceMode);
    }
    
    private PushMatchedTemplateRouter getTemplateRouterForChildLocator(final boolean subResourceMode, final RuntimeResource child) {
        int i = 0;
        for (final Resource res : child.getResources()) {
            if (res.getResourceLocator() != null) {
                return this.getTemplateRouter(subResourceMode, child.getParentResources().get(i).getPathPattern().getTemplate(), res.getPathPattern().getTemplate());
            }
            ++i;
        }
        return null;
    }
    
    private PushMatchedTemplateRouter getTemplateRouter(final boolean subResourceMode, final UriTemplate parentTemplate, final UriTemplate childTemplate) {
        if (childTemplate != null) {
            return new PushMatchedTemplateRouter(subResourceMode ? PathPattern.OPEN_ROOT_PATH_PATTERN.getTemplate() : parentTemplate, childTemplate);
        }
        return new PushMatchedTemplateRouter(subResourceMode ? PathPattern.END_OF_PATH_PATTERN.getTemplate() : parentTemplate);
    }
    
    private Resource getLocatorResource(final RuntimeResource resource) {
        for (final Resource res : resource.getResources()) {
            if (res.getResourceLocator() != null) {
                return res;
            }
        }
        return null;
    }
    
    private List<MethodRouting> createResourceMethodRouters(final RuntimeResource runtimeResource, final boolean subResourceMode) {
        final List<MethodRouting> methodRoutings = new ArrayList<MethodRouting>();
        int i = 0;
        for (final Resource resource : runtimeResource.getResources()) {
            final Resource parentResource = (runtimeResource.getParent() == null) ? null : runtimeResource.getParentResources().get(i++);
            final UriTemplate template = resource.getPathPattern().getTemplate();
            final PushMatchedTemplateRouter templateRouter = (parentResource == null) ? this.getTemplateRouter(subResourceMode, template, null) : this.getTemplateRouter(subResourceMode, parentResource.getPathPattern().getTemplate(), template);
            for (final ResourceMethod resourceMethod : resource.getResourceMethods()) {
                methodRoutings.add(new MethodRouting(resourceMethod, new Router[] { templateRouter, new PushMatchedMethodRouter(resourceMethod), this.createMethodRouter(resourceMethod) }));
            }
        }
        return methodRoutings.isEmpty() ? Collections.emptyList() : methodRoutings;
    }
    
    private PathToRouterBuilder startNextRoute(final PathMatchingRouterBuilder currentRouterBuilder, final PathPattern routingPattern) {
        return (currentRouterBuilder == null) ? PathMatchingRouterBuilder.newRoute(routingPattern) : currentRouterBuilder.route(routingPattern);
    }
}
