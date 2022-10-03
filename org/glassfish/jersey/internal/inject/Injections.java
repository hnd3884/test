package org.glassfish.jersey.internal.inject;

import javax.ws.rs.WebApplicationException;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.util.LinkedList;
import java.util.Optional;
import org.glassfish.jersey.internal.LocalizationMessages;

public class Injections
{
    public static InjectionManager createInjectionManager() {
        return lookupInjectionManagerFactory().create();
    }
    
    public static InjectionManager createInjectionManager(final Binder binder) {
        final InjectionManagerFactory injectionManagerFactory = lookupInjectionManagerFactory();
        final InjectionManager injectionManager = injectionManagerFactory.create();
        injectionManager.register(binder);
        return injectionManager;
    }
    
    public static InjectionManager createInjectionManager(final Object parent) {
        return lookupInjectionManagerFactory().create(parent);
    }
    
    private static InjectionManagerFactory lookupInjectionManagerFactory() {
        return lookupService(InjectionManagerFactory.class).orElseThrow(() -> new IllegalStateException(LocalizationMessages.INJECTION_MANAGER_FACTORY_NOT_FOUND()));
    }
    
    private static <T> Optional<T> lookupService(final Class<T> clazz) {
        final List<RankedProvider<T>> providers = new LinkedList<RankedProvider<T>>();
        for (final T provider : ServiceFinder.find(clazz)) {
            providers.add(new RankedProvider<T>(provider));
        }
        providers.sort((Comparator<? super RankedProvider<T>>)new RankedComparator(RankedComparator.Order.DESCENDING));
        return providers.isEmpty() ? Optional.empty() : Optional.ofNullable(providers.get(0).getProvider());
    }
    
    public static <T> T getOrCreate(final InjectionManager injectionManager, final Class<T> clazz) {
        try {
            final T component = injectionManager.getInstance(clazz);
            return (component == null) ? injectionManager.createAndInitialize(clazz) : component;
        }
        catch (final RuntimeException e) {
            final Throwable throwable = e.getCause();
            if (throwable != null && WebApplicationException.class.isAssignableFrom(throwable.getClass())) {
                throw (WebApplicationException)throwable;
            }
            throw e;
        }
    }
}
