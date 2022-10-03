package org.glassfish.jersey.inject.hk2;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.ClassBinding;
import java.util.function.Consumer;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import java.lang.reflect.Type;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.inject.ServiceHolderImpl;
import org.glassfish.jersey.internal.inject.ServiceHolder;
import java.util.List;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import java.util.logging.Level;
import java.lang.annotation.Annotation;
import org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.InjectionManager;

abstract class AbstractHk2InjectionManager implements InjectionManager
{
    private static final Logger LOGGER;
    private static final ServiceLocatorFactory factory;
    private ServiceLocator locator;
    
    AbstractHk2InjectionManager(final Object parent) {
        final ServiceLocator parentLocator = resolveServiceLocatorParent(parent);
        ServiceLocatorUtilities.bind(this.locator = createLocator(parentLocator), new Binder[] { (Binder)new Hk2BootstrapBinder(this.locator) });
        this.locator.setDefaultClassAnalyzerName("JerseyClassAnalyzer");
        final ServiceLocatorRuntimeBean serviceLocatorRuntimeBean = (ServiceLocatorRuntimeBean)this.locator.getService((Class)ServiceLocatorRuntimeBean.class, new Annotation[0]);
        if (serviceLocatorRuntimeBean != null) {
            if (AbstractHk2InjectionManager.LOGGER.isLoggable(Level.FINE)) {
                AbstractHk2InjectionManager.LOGGER.fine(LocalizationMessages.HK_2_CLEARING_CACHE(serviceLocatorRuntimeBean.getServiceCacheSize(), serviceLocatorRuntimeBean.getReflectionCacheSize()));
            }
            serviceLocatorRuntimeBean.clearReflectionCache();
            serviceLocatorRuntimeBean.clearServiceCache();
        }
    }
    
    private static ServiceLocator createLocator(final ServiceLocator parentLocator) {
        final ServiceLocator result = AbstractHk2InjectionManager.factory.create((String)null, parentLocator, (ServiceLocatorGenerator)null, ServiceLocatorFactory.CreatePolicy.DESTROY);
        result.setNeutralContextClassLoader(false);
        ServiceLocatorUtilities.enablePerThreadScope(result);
        return result;
    }
    
    private static ServiceLocator resolveServiceLocatorParent(final Object parent) {
        assertParentLocatorType(parent);
        ServiceLocator parentLocator = null;
        if (parent != null) {
            if (parent instanceof ServiceLocator) {
                parentLocator = (ServiceLocator)parent;
            }
            else if (parent instanceof AbstractHk2InjectionManager) {
                parentLocator = ((AbstractHk2InjectionManager)parent).getServiceLocator();
            }
        }
        return parentLocator;
    }
    
    private static void assertParentLocatorType(final Object parent) {
        if (parent != null && !(parent instanceof ServiceLocator) && !(parent instanceof AbstractHk2InjectionManager)) {
            throw new IllegalArgumentException(LocalizationMessages.HK_2_UNKNOWN_PARENT_INJECTION_MANAGER(parent.getClass().getSimpleName()));
        }
    }
    
    public ServiceLocator getServiceLocator() {
        return this.locator;
    }
    
    public boolean isRegistrable(final Class<?> clazz) {
        return Binder.class.isAssignableFrom(clazz);
    }
    
    public <T> List<ServiceHolder<T>> getAllServiceHolders(final Class<T> contract, final Annotation... qualifiers) {
        return (List)this.getServiceLocator().getAllServiceHandles((Class)contract, qualifiers).stream().map(sh -> new ServiceHolderImpl(sh.getService(), sh.getActiveDescriptor().getImplementationClass(), sh.getActiveDescriptor().getContractTypes(), sh.getActiveDescriptor().getRanking())).collect(Collectors.toList());
    }
    
    public <T> T getInstance(final Class<T> clazz, final Annotation... annotations) {
        return (T)this.getServiceLocator().getService((Class)clazz, annotations);
    }
    
    public <T> T getInstance(final Type clazz) {
        return (T)this.getServiceLocator().getService(clazz, new Annotation[0]);
    }
    
    public Object getInstance(final ForeignDescriptor foreignDescriptor) {
        return this.getServiceLocator().getServiceHandle((ActiveDescriptor)foreignDescriptor.get()).getService();
    }
    
    public <T> T getInstance(final Class<T> clazz) {
        return (T)this.getServiceLocator().getService((Class)clazz, new Annotation[0]);
    }
    
    public <T> T getInstance(final Class<T> clazz, final String classAnalyzer) {
        return (T)this.getServiceLocator().getService((Class)clazz, classAnalyzer, new Annotation[0]);
    }
    
    public <T> List<T> getAllInstances(final Type clazz) {
        return this.getServiceLocator().getAllServices(clazz, new Annotation[0]);
    }
    
    public void preDestroy(final Object preDestroyMe) {
        this.getServiceLocator().preDestroy(preDestroyMe);
    }
    
    public void shutdown() {
        if (AbstractHk2InjectionManager.factory.find(this.getServiceLocator().getName()) != null) {
            AbstractHk2InjectionManager.factory.destroy(this.getServiceLocator().getName());
        }
        else {
            this.getServiceLocator().shutdown();
        }
    }
    
    public <U> U createAndInitialize(final Class<U> clazz) {
        return (U)this.getServiceLocator().createAndInitialize((Class)clazz);
    }
    
    public ForeignDescriptor createForeignDescriptor(final Binding binding) {
        final ForeignDescriptor foreignDescriptor = this.createAndTranslateForeignDescriptor(binding);
        final ActiveDescriptor<Object> activeDescriptor = (ActiveDescriptor<Object>)ServiceLocatorUtilities.addOneDescriptor(this.getServiceLocator(), (Descriptor)foreignDescriptor.get(), false);
        return ForeignDescriptor.wrap((Object)activeDescriptor, (Consumer)activeDescriptor::dispose);
    }
    
    public void inject(final Object injectMe) {
        this.getServiceLocator().inject(injectMe);
    }
    
    public void inject(final Object injectMe, final String classAnalyzer) {
        this.getServiceLocator().inject(injectMe, classAnalyzer);
    }
    
    private ForeignDescriptor createAndTranslateForeignDescriptor(final Binding binding) {
        ActiveDescriptor activeDescriptor;
        if (ClassBinding.class.isAssignableFrom(binding.getClass())) {
            activeDescriptor = Hk2Helper.translateToActiveDescriptor((ClassBinding<?>)binding);
        }
        else {
            if (!InstanceBinding.class.isAssignableFrom(binding.getClass())) {
                throw new RuntimeException(org.glassfish.jersey.internal.LocalizationMessages.UNKNOWN_DESCRIPTOR_TYPE((Object)binding.getClass().getSimpleName()));
            }
            activeDescriptor = Hk2Helper.translateToActiveDescriptor((InstanceBinding<?>)binding, new Type[0]);
        }
        return ForeignDescriptor.wrap((Object)activeDescriptor, (Consumer)activeDescriptor::dispose);
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractHk2InjectionManager.class.getName());
        factory = ServiceLocatorFactory.getInstance();
    }
}
