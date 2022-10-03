package org.glassfish.jersey.message.filtering;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.model.Invocable;
import java.util.Iterator;
import org.glassfish.jersey.server.model.ResourceMethod;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.internal.util.collection.DataStructures;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.core.Configuration;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import org.glassfish.jersey.server.ExtendedUriInfo;
import javax.inject.Provider;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import javax.annotation.Priority;
import javax.inject.Singleton;

@Singleton
@Priority(4200)
@ConstrainedTo(RuntimeType.SERVER)
class ServerScopeProvider extends CommonScopeProvider
{
    @Inject
    private Provider<ExtendedUriInfo> uriInfoProvider;
    private final ConcurrentMap<String, Set<String>> uriToContexts;
    
    @Inject
    public ServerScopeProvider(final Configuration config, final InjectionManager injectionManager) {
        super(config, injectionManager);
        this.uriToContexts = DataStructures.createConcurrentMap();
    }
    
    @Override
    public Set<String> getFilteringScopes(final Annotation[] entityAnnotations, final boolean defaultIfNotFound) {
        final Set<String> filteringScope = super.getFilteringScopes(entityAnnotations, false);
        if (filteringScope.isEmpty()) {
            final ExtendedUriInfo uriInfo = (ExtendedUriInfo)this.uriInfoProvider.get();
            final String path = uriInfo.getPath();
            if (this.uriToContexts.containsKey(path)) {
                return this.uriToContexts.get(path);
            }
            for (final ResourceMethod method : getMatchedMethods(uriInfo)) {
                final Invocable invocable = method.getInvocable();
                this.mergeFilteringScopes(filteringScope, this.getFilteringScopes(invocable.getHandlingMethod(), invocable.getHandler().getHandlerClass()));
                if (!filteringScope.isEmpty()) {
                    this.uriToContexts.putIfAbsent(path, filteringScope);
                    return filteringScope;
                }
            }
        }
        return this.returnFilteringScopes(filteringScope, defaultIfNotFound);
    }
    
    protected Set<String> getFilteringScopes(final Method resourceMethod, final Class<?> resourceClass) {
        Set<String> scope = this.getFilteringScopes(resourceMethod.getAnnotations());
        if (scope.isEmpty()) {
            scope = this.getFilteringScopes(resourceClass.getAnnotations());
        }
        return scope;
    }
    
    private static List<ResourceMethod> getMatchedMethods(final ExtendedUriInfo uriInfo) {
        final List<ResourceMethod> matchedResourceLocators = uriInfo.getMatchedResourceLocators();
        final List<ResourceMethod> methods = new ArrayList<ResourceMethod>(1 + matchedResourceLocators.size());
        methods.add(uriInfo.getMatchedResourceMethod());
        methods.addAll(matchedResourceLocators);
        return methods;
    }
}
