package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.server.internal.process.RequestProcessingContextReference;
import java.util.List;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.jersey.server.ContainerRequest;
import javax.ws.rs.core.Configuration;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.inject.ContextInjectionResolver;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.server.AsyncContext;
import javax.inject.Provider;
import javax.ws.rs.BeanParam;
import org.glassfish.jersey.server.Uri;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.CookieParam;
import java.lang.annotation.Annotation;
import javax.ws.rs.container.Suspended;
import org.glassfish.jersey.internal.inject.Bindings;
import java.util.Collection;
import java.util.Collections;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.ArrayList;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.ServerBootstrapBag;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class ValueParamProviderConfigurator implements BootstrapConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final Provider<AsyncContext> asyncContextProvider = (Provider<AsyncContext>)(() -> {
            final RequestProcessingContextReference reference = (RequestProcessingContextReference)injectionManager.getInstance((Class)RequestProcessingContextReference.class);
            return reference.get().asyncContext();
        });
        final LazyValue<ContextInjectionResolver> lazyContextResolver = (LazyValue<ContextInjectionResolver>)Values.lazy(() -> (ContextInjectionResolver)injectionManager.getInstance((Class)ContextInjectionResolver.class));
        final Supplier<Configuration> configuration = serverBag::getConfiguration;
        final Provider<MultivaluedParameterExtractorProvider> paramExtractor = (Provider<MultivaluedParameterExtractorProvider>)serverBag::getMultivaluedParameterExtractorProvider;
        final Collection<ValueParamProvider> suppliers = new ArrayList<ValueParamProvider>();
        final AsyncResponseValueParamProvider asyncProvider = new AsyncResponseValueParamProvider(asyncContextProvider);
        suppliers.add(asyncProvider);
        final CookieParamValueParamProvider cookieProvider = new CookieParamValueParamProvider(paramExtractor);
        suppliers.add(cookieProvider);
        final EntityParamValueParamProvider entityProvider = new EntityParamValueParamProvider(paramExtractor);
        suppliers.add(entityProvider);
        final FormParamValueParamProvider formProvider = new FormParamValueParamProvider(paramExtractor);
        suppliers.add(formProvider);
        final HeaderParamValueParamProvider headerProvider = new HeaderParamValueParamProvider(paramExtractor);
        suppliers.add(headerProvider);
        final MatrixParamValueParamProvider matrixProvider = new MatrixParamValueParamProvider(paramExtractor);
        suppliers.add(matrixProvider);
        final PathParamValueParamProvider pathProvider = new PathParamValueParamProvider(paramExtractor);
        suppliers.add(pathProvider);
        final QueryParamValueParamProvider queryProvider = new QueryParamValueParamProvider(paramExtractor);
        suppliers.add(queryProvider);
        final BeanParamValueParamProvider beanProvider = new BeanParamValueParamProvider(paramExtractor, injectionManager);
        suppliers.add(beanProvider);
        final WebTargetValueParamProvider webTargetProvider = new WebTargetValueParamProvider(configuration, clientConfigClass -> Injections.getOrCreate(injectionManager, clientConfigClass));
        suppliers.add(webTargetProvider);
        final DelegatedInjectionValueParamProvider contextProvider = new DelegatedInjectionValueParamProvider(lazyContextResolver, injectionManager::createForeignDescriptor);
        suppliers.add(contextProvider);
        serverBag.setValueParamProviders(Collections.unmodifiableCollection((Collection<? extends ValueParamProvider>)suppliers));
        injectionManager.register(Bindings.service((Object)asyncProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)cookieProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)formProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)headerProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)matrixProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)pathProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)queryProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)webTargetProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)beanProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)entityProvider).to((Class)ValueParamProvider.class));
        injectionManager.register(Bindings.service((Object)contextProvider).to((Class)ValueParamProvider.class));
        final Provider<ContainerRequest> request = (Provider<ContainerRequest>)(() -> {
            final RequestProcessingContextReference reference = (RequestProcessingContextReference)injectionManager.getInstance((Class)RequestProcessingContextReference.class);
            return reference.get().request();
        });
        this.registerResolver(injectionManager, asyncProvider, (Class<? extends Annotation>)Suspended.class, request);
        this.registerResolver(injectionManager, cookieProvider, (Class<? extends Annotation>)CookieParam.class, request);
        this.registerResolver(injectionManager, formProvider, (Class<? extends Annotation>)FormParam.class, request);
        this.registerResolver(injectionManager, headerProvider, (Class<? extends Annotation>)HeaderParam.class, request);
        this.registerResolver(injectionManager, matrixProvider, (Class<? extends Annotation>)MatrixParam.class, request);
        this.registerResolver(injectionManager, pathProvider, (Class<? extends Annotation>)PathParam.class, request);
        this.registerResolver(injectionManager, queryProvider, (Class<? extends Annotation>)QueryParam.class, request);
        this.registerResolver(injectionManager, webTargetProvider, Uri.class, request);
        this.registerResolver(injectionManager, beanProvider, (Class<? extends Annotation>)BeanParam.class, request);
    }
    
    private void registerResolver(final InjectionManager im, final ValueParamProvider vfp, final Class<? extends Annotation> annotation, final Provider<ContainerRequest> request) {
        im.register((Binding)Bindings.injectionResolver((InjectionResolver)new ParamInjectionResolver(vfp, (Class<Annotation>)annotation, request)));
    }
    
    public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final List<ValueParamProvider> addedInstances = injectionManager.getAllInstances((Type)ValueParamProvider.class);
        if (!addedInstances.isEmpty()) {
            final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
            addedInstances.addAll(serverBag.getValueParamProviders());
            serverBag.setValueParamProviders(Collections.unmodifiableCollection((Collection<? extends ValueParamProvider>)addedInstances));
        }
    }
}
