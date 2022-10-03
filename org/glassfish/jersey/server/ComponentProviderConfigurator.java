package org.glassfish.jersey.server;

import org.glassfish.jersey.model.internal.RankedComparator;
import java.util.List;
import org.glassfish.jersey.internal.ServiceConfigurationError;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.internal.ServiceFinder;
import java.util.function.Consumer;
import java.util.Collection;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.ComponentProvider;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.util.Comparator;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class ComponentProviderConfigurator implements BootstrapConfigurator
{
    private static final Comparator<RankedProvider<ComponentProvider>> RANKED_COMPARATOR;
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final LazyValue<Collection<ComponentProvider>> componentProviders = (LazyValue<Collection<ComponentProvider>>)Values.lazy(() -> getRankedComponentProviders().stream().map((Function<? super RankedProvider<ComponentProvider>, ?>)RankedProvider::getProvider).peek(provider -> provider.initialize(injectionManager)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        serverBag.setComponentProviders(componentProviders);
    }
    
    public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        ((Collection)serverBag.getComponentProviders().get()).forEach(ComponentProvider::done);
    }
    
    private static Collection<RankedProvider<ComponentProvider>> getRankedComponentProviders() throws ServiceConfigurationError {
        return StreamSupport.stream((Spliterator<Object>)ServiceFinder.find((Class)ComponentProvider.class).spliterator(), false).map((Function<? super Object, ?>)RankedProvider::new).sorted((Comparator<? super Object>)ComponentProviderConfigurator.RANKED_COMPARATOR).collect((Collector<? super Object, ?, Collection<RankedProvider<ComponentProvider>>>)Collectors.toList());
    }
    
    static {
        RANKED_COMPARATOR = (Comparator)new RankedComparator(RankedComparator.Order.DESCENDING);
    }
}
