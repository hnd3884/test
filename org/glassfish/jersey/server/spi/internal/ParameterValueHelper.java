package org.glassfish.jersey.server.spi.internal;

import java.util.function.Function;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import org.glassfish.jersey.server.model.Parameterized;
import java.util.Collection;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.process.MappableException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.WebApplicationException;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.List;

public final class ParameterValueHelper
{
    public static Object[] getParameterValues(final List<ParamValueFactoryWithSource<?>> valueProviders, final ContainerRequest request) {
        final Object[] params = new Object[valueProviders.size()];
        try {
            int entityProviderIndex = -1;
            int index = 0;
            for (final ParamValueFactoryWithSource<?> paramValProvider : valueProviders) {
                if (paramValProvider.getSource().equals(Parameter.Source.ENTITY)) {
                    entityProviderIndex = index++;
                }
                else {
                    params[index++] = paramValProvider.apply(request);
                }
            }
            if (entityProviderIndex != -1) {
                params[entityProviderIndex] = valueProviders.get(entityProviderIndex).apply(request);
            }
            return params;
        }
        catch (final WebApplicationException e) {
            throw e;
        }
        catch (final MessageBodyProviderNotFoundException e2) {
            throw new NotSupportedException((Throwable)e2);
        }
        catch (final ProcessingException e3) {
            throw e3;
        }
        catch (final RuntimeException e4) {
            if (e4.getCause() instanceof WebApplicationException) {
                throw (WebApplicationException)e4.getCause();
            }
            throw new MappableException("Exception obtaining parameters", e4);
        }
    }
    
    public static List<ParamValueFactoryWithSource<?>> createValueProviders(final Collection<ValueParamProvider> valueSuppliers, final Parameterized parameterized) {
        if (null == parameterized.getParameters() || 0 == parameterized.getParameters().size()) {
            return Collections.emptyList();
        }
        final List<ValueParamProvider> valueParamProviders = valueSuppliers.stream().sorted((o1, o2) -> o2.getPriority().getWeight() - o1.getPriority().getWeight()).collect((Collector<? super ValueParamProvider, ?, List<ValueParamProvider>>)Collectors.toList());
        boolean entityParamFound = false;
        final List<ParamValueFactoryWithSource<?>> providers = new ArrayList<ParamValueFactoryWithSource<?>>(parameterized.getParameters().size());
        for (final Parameter parameter : parameterized.getParameters()) {
            final Parameter.Source parameterSource = parameter.getSource();
            entityParamFound = (entityParamFound || Parameter.Source.ENTITY == parameterSource);
            final Function<ContainerRequest, ?> valueFunction = getParamValueProvider(valueParamProviders, parameter);
            if (valueFunction != null) {
                providers.add(wrapParamValueProvider(valueFunction, parameterSource));
            }
            else {
                providers.add(null);
            }
        }
        if (!entityParamFound && Collections.frequency(providers, null) == 1) {
            final int entityParamIndex = providers.lastIndexOf(null);
            final Parameter parameter = parameterized.getParameters().get(entityParamIndex);
            if (Parameter.Source.UNKNOWN == parameter.getSource() && !parameter.isQualified()) {
                final Parameter overriddenParameter = Parameter.overrideSource(parameter, Parameter.Source.ENTITY);
                final Function<ContainerRequest, ?> valueFunction = getParamValueProvider(valueParamProviders, overriddenParameter);
                if (valueFunction != null) {
                    providers.set(entityParamIndex, wrapParamValueProvider(valueFunction, overriddenParameter.getSource()));
                }
                else {
                    providers.set(entityParamIndex, null);
                }
            }
        }
        return providers;
    }
    
    private static <T> ParamValueFactoryWithSource<T> wrapParamValueProvider(final Function<ContainerRequest, T> factory, final Parameter.Source paramSource) {
        return new ParamValueFactoryWithSource<T>(factory, paramSource);
    }
    
    private static Function<ContainerRequest, ?> getParamValueProvider(final Collection<ValueParamProvider> valueProviders, final Parameter parameter) {
        Function<ContainerRequest, ?> valueProvider = null;
        for (Iterator<ValueParamProvider> vfpIterator = valueProviders.iterator(); valueProvider == null && vfpIterator.hasNext(); valueProvider = vfpIterator.next().getValueProvider(parameter)) {}
        return valueProvider;
    }
    
    private ParameterValueHelper() {
    }
}
