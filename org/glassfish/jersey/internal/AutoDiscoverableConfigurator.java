package org.glassfish.jersey.internal;

import javax.ws.rs.core.Configuration;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import java.util.Map;
import java.util.List;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

public class AutoDiscoverableConfigurator extends AbstractServiceFinderConfigurator<AutoDiscoverable>
{
    public AutoDiscoverableConfigurator(final RuntimeType runtimeType) {
        super(AutoDiscoverable.class, runtimeType);
    }
    
    @Override
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final Configuration configuration = bootstrapBag.getConfiguration();
        final List<AutoDiscoverable> autoDiscoverables = this.loadImplementations(configuration.getProperties()).stream().peek(implClass -> injectionManager.register(Bindings.service((Class<Object>)implClass).to((Class<? super Object>)AutoDiscoverable.class))).map((Function<? super Object, ?>)injectionManager::createAndInitialize).collect((Collector<? super Object, ?, List<AutoDiscoverable>>)Collectors.toList());
        bootstrapBag.setAutoDiscoverables(autoDiscoverables);
    }
}
