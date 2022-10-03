package org.glassfish.jersey.inject.hk2;

import org.glassfish.jersey.internal.inject.Bindings;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.annotation.Priority;
import org.glassfish.jersey.internal.inject.InjectionManagerFactory;

@Priority(10)
public class Hk2InjectionManagerFactory implements InjectionManagerFactory
{
    public static final String HK2_INJECTION_MANAGER_STRATEGY = "org.glassfish.jersey.hk2.injection.manager.strategy";
    
    public InjectionManager create(final Object parent) {
        return this.initInjectionManager(getStrategy().createInjectionManager(parent));
    }
    
    public static boolean isImmediateStrategy() {
        return getStrategy() == Hk2InjectionManagerStrategy.IMMEDIATE;
    }
    
    private static Hk2InjectionManagerStrategy getStrategy() {
        final String value = AccessController.doPrivileged((PrivilegedAction<String>)PropertiesHelper.getSystemProperty("org.glassfish.jersey.hk2.injection.manager.strategy"));
        if (value == null || value.isEmpty()) {
            return Hk2InjectionManagerStrategy.IMMEDIATE;
        }
        if ("immediate".equalsIgnoreCase(value)) {
            return Hk2InjectionManagerStrategy.IMMEDIATE;
        }
        if ("delayed".equalsIgnoreCase(value)) {
            return Hk2InjectionManagerStrategy.DELAYED;
        }
        throw new IllegalStateException("Illegal value of org.glassfish.jersey.hk2.injection.manager.strategy. Expected \"immediate\" or \"delayed\", the actual value is: " + value);
    }
    
    private InjectionManager initInjectionManager(final InjectionManager injectionManager) {
        injectionManager.register(Bindings.service((Object)injectionManager).to((Class)InjectionManager.class));
        return injectionManager;
    }
    
    private enum Hk2InjectionManagerStrategy
    {
        IMMEDIATE {
            @Override
            InjectionManager createInjectionManager(final Object parent) {
                return (InjectionManager)new ImmediateHk2InjectionManager(parent);
            }
        }, 
        DELAYED {
            @Override
            InjectionManager createInjectionManager(final Object parent) {
                return (InjectionManager)new DelayedHk2InjectionManager(parent);
            }
        };
        
        abstract InjectionManager createInjectionManager(final Object p0);
    }
}
