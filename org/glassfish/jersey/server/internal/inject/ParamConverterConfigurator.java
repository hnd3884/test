package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.internal.inject.Binding;
import javax.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class ParamConverterConfigurator implements BootstrapConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final InstanceBinding<ParamConverters.AggregatedProvider> aggregatedConverters = (InstanceBinding<ParamConverters.AggregatedProvider>)Bindings.service((Object)new ParamConverters.AggregatedProvider()).to((Class)ParamConverterProvider.class);
        injectionManager.register((Binding)aggregatedConverters);
    }
}
