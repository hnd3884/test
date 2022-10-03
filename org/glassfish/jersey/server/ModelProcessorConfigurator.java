package org.glassfish.jersey.server;

import java.util.Collection;
import org.glassfish.jersey.model.internal.ComponentBag;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.server.wadl.processor.OptionsMethodProcessor;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.internal.inject.Binding;
import java.util.function.Predicate;
import org.glassfish.jersey.server.model.ModelProcessor;
import java.util.function.Function;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class ModelProcessorConfigurator implements BootstrapConfigurator
{
    private static final Function<Object, ModelProcessor> CAST_TO_MODEL_PROCESSOR;
    private static final Predicate<Binding> BINDING_MODEL_PROCESSOR_ONLY;
    private static final Predicate<ContractProvider> CONTRACT_PROVIDER_MODEL_PROCESSOR_ONLY;
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
        final ComponentBag componentBag = runtimeConfig.getComponentBag();
        final OptionsMethodProcessor optionsMethodProcessor = new OptionsMethodProcessor();
        injectionManager.register(Bindings.service((Object)optionsMethodProcessor).to((Class)ModelProcessor.class));
        final List<ModelProcessor> modelProcessors = Stream.concat((Stream<?>)componentBag.getClasses((Predicate)ModelProcessorConfigurator.CONTRACT_PROVIDER_MODEL_PROCESSOR_ONLY).stream().map(injectionManager::createAndInitialize), (Stream<?>)componentBag.getInstances((Predicate)ModelProcessorConfigurator.CONTRACT_PROVIDER_MODEL_PROCESSOR_ONLY).stream()).map((Function<? super Object, ?>)ModelProcessorConfigurator.CAST_TO_MODEL_PROCESSOR).collect((Collector<? super Object, ?, List<ModelProcessor>>)Collectors.toList());
        modelProcessors.add(optionsMethodProcessor);
        final List<ModelProcessor> modelProcessorsFromBinders = ComponentBag.getFromBinders(injectionManager, componentBag, (Function)ModelProcessorConfigurator.CAST_TO_MODEL_PROCESSOR, (Predicate)ModelProcessorConfigurator.BINDING_MODEL_PROCESSOR_ONLY);
        modelProcessors.addAll(modelProcessorsFromBinders);
        serverBag.setModelProcessors(modelProcessors);
    }
    
    static {
        CAST_TO_MODEL_PROCESSOR = ModelProcessor.class::cast;
        BINDING_MODEL_PROCESSOR_ONLY = (binding -> binding.getContracts().contains(ModelProcessor.class));
        CONTRACT_PROVIDER_MODEL_PROCESSOR_ONLY = (provider -> provider.getContracts().contains(ModelProcessor.class));
    }
}
