package org.glassfish.jersey.inject.hk2;

import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.internal.inject.ForeignDescriptorImpl;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.internal.inject.InjecteeImpl;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Injectee;
import javax.inject.Singleton;
import org.glassfish.hk2.api.InjectionResolver;
import java.lang.annotation.Annotation;

@Singleton
public class InjectionResolverWrapper<T extends Annotation> implements InjectionResolver<T>
{
    private final org.glassfish.jersey.internal.inject.InjectionResolver jerseyResolver;
    
    InjectionResolverWrapper(final org.glassfish.jersey.internal.inject.InjectionResolver<T> jerseyResolver) {
        this.jerseyResolver = jerseyResolver;
    }
    
    public Object resolve(final Injectee injectee, final ServiceHandle root) {
        final InjecteeImpl injecteeWrapper = new InjecteeImpl();
        injecteeWrapper.setRequiredType(injectee.getRequiredType());
        injecteeWrapper.setParent(injectee.getParent());
        injecteeWrapper.setRequiredQualifiers(injectee.getRequiredQualifiers());
        injecteeWrapper.setOptional(injectee.isOptional());
        injecteeWrapper.setPosition(injectee.getPosition());
        injecteeWrapper.setFactory(ReflectionHelper.isSubClassOf(injectee.getRequiredType(), (Type)Factory.class));
        injecteeWrapper.setInjecteeDescriptor((ForeignDescriptor)new ForeignDescriptorImpl((Object)injectee.getInjecteeDescriptor()));
        final Object instance = this.jerseyResolver.resolve((org.glassfish.jersey.internal.inject.Injectee)injecteeWrapper);
        if (injecteeWrapper.isFactory()) {
            return this.asFactory(instance);
        }
        return instance;
    }
    
    private Factory asFactory(final Object instance) {
        return (Factory)new Factory() {
            public Object provide() {
                return instance;
            }
            
            public void dispose(final Object instance) {
            }
        };
    }
    
    public boolean isConstructorParameterIndicator() {
        return this.jerseyResolver.isConstructorParameterIndicator();
    }
    
    public boolean isMethodParameterIndicator() {
        return this.jerseyResolver.isMethodParameterIndicator();
    }
}
