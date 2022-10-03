package org.glassfish.jersey.message;

import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.core.MediaType;
import java.util.List;

public abstract class AbstractEntityProviderModel<T>
{
    private final T provider;
    private final List<MediaType> declaredTypes;
    private final boolean custom;
    private final Class<?> providedType;
    
    AbstractEntityProviderModel(final T provider, final List<MediaType> declaredTypes, final boolean custom, final Class<T> providerType) {
        this.provider = provider;
        this.declaredTypes = declaredTypes;
        this.custom = custom;
        this.providedType = getProviderClassParam(provider, providerType);
    }
    
    public T provider() {
        return this.provider;
    }
    
    public List<MediaType> declaredTypes() {
        return this.declaredTypes;
    }
    
    public boolean isCustom() {
        return this.custom;
    }
    
    public Class<?> providedType() {
        return this.providedType;
    }
    
    private static Class<?> getProviderClassParam(final Object provider, final Class<?> providerType) {
        final ReflectionHelper.DeclaringClassInterfacePair pair = ReflectionHelper.getClass(provider.getClass(), providerType);
        final Class[] classArgs = ReflectionHelper.getParameterizedClassArguments(pair);
        return (classArgs != null) ? classArgs[0] : Object.class;
    }
}
