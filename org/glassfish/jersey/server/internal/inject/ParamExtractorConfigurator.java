package org.glassfish.jersey.server.internal.inject;

import java.util.Set;
import org.glassfish.jersey.internal.inject.Providers;
import javax.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.ServerBootstrapBag;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class ParamExtractorConfigurator implements BootstrapConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final LazyValue<ParamConverterFactory> lazyParamConverterFactory = (LazyValue<ParamConverterFactory>)Values.lazy(() -> new ParamConverterFactory(Providers.getProviders(injectionManager, (Class)ParamConverterProvider.class), Providers.getCustomProviders(injectionManager, (Class)ParamConverterProvider.class)));
        final MultivaluedParameterExtractorFactory multiExtractor = new MultivaluedParameterExtractorFactory(lazyParamConverterFactory);
        serverBag.setMultivaluedParameterExtractorProvider(multiExtractor);
        injectionManager.register(Bindings.service((Object)multiExtractor).to((Class)MultivaluedParameterExtractorProvider.class));
    }
}
