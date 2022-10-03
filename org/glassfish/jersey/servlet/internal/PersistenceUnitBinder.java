package org.glassfish.jersey.servlet.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.glassfish.jersey.server.ContainerException;
import javax.persistence.EntityManagerFactory;
import org.glassfish.jersey.internal.inject.Injectee;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import javax.servlet.ServletConfig;
import org.glassfish.jersey.internal.inject.AbstractBinder;

public class PersistenceUnitBinder extends AbstractBinder
{
    private final ServletConfig servletConfig;
    public static final String PERSISTENCE_UNIT_PREFIX = "unit:";
    
    public PersistenceUnitBinder(final ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }
    
    protected void configure() {
        this.bind((InjectionResolver)new PersistenceUnitInjectionResolver(this.servletConfig)).to((GenericType)new GenericType<InjectionResolver<PersistenceUnit>>() {});
    }
    
    @Singleton
    private static class PersistenceUnitInjectionResolver implements InjectionResolver<PersistenceUnit>
    {
        private final Map<String, String> persistenceUnits;
        
        private PersistenceUnitInjectionResolver(final ServletConfig servletConfig) {
            this.persistenceUnits = new HashMap<String, String>();
            final Enumeration parameterNames = servletConfig.getInitParameterNames();
            while (parameterNames.hasMoreElements()) {
                final String key = parameterNames.nextElement();
                if (key.startsWith("unit:")) {
                    this.persistenceUnits.put(key.substring("unit:".length()), "java:comp/env/" + servletConfig.getInitParameter(key));
                }
            }
        }
        
        public Object resolve(final Injectee injectee) {
            if (!injectee.getRequiredType().equals(EntityManagerFactory.class)) {
                return null;
            }
            final PersistenceUnit annotation = injectee.getParent().getAnnotation(PersistenceUnit.class);
            final String unitName = annotation.unitName();
            if (!this.persistenceUnits.containsKey(unitName)) {
                throw new ContainerException(LocalizationMessages.PERSISTENCE_UNIT_NOT_CONFIGURED(unitName));
            }
            return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { EntityManagerFactory.class }, new ThreadLocalNamedInvoker<Object>(this.persistenceUnits.get(unitName)));
        }
        
        public boolean isConstructorParameterIndicator() {
            return false;
        }
        
        public boolean isMethodParameterIndicator() {
            return false;
        }
        
        public Class<PersistenceUnit> getAnnotation() {
            return PersistenceUnit.class;
        }
    }
}
