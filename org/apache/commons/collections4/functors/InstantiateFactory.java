package org.apache.commons.collections4.functors;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.FunctorException;
import java.lang.reflect.Constructor;
import org.apache.commons.collections4.Factory;

public class InstantiateFactory<T> implements Factory<T>
{
    private final Class<T> iClassToInstantiate;
    private final Class<?>[] iParamTypes;
    private final Object[] iArgs;
    private transient Constructor<T> iConstructor;
    
    public static <T> Factory<T> instantiateFactory(final Class<T> classToInstantiate, final Class<?>[] paramTypes, final Object[] args) {
        if (classToInstantiate == null) {
            throw new NullPointerException("Class to instantiate must not be null");
        }
        if ((paramTypes == null && args != null) || (paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length)) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateFactory<T>(classToInstantiate);
        }
        return new InstantiateFactory<T>(classToInstantiate, paramTypes, args);
    }
    
    public InstantiateFactory(final Class<T> classToInstantiate) {
        this.iConstructor = null;
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = null;
        this.iArgs = null;
        this.findConstructor();
    }
    
    public InstantiateFactory(final Class<T> classToInstantiate, final Class<?>[] paramTypes, final Object[] args) {
        this.iConstructor = null;
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = paramTypes.clone();
        this.iArgs = args.clone();
        this.findConstructor();
    }
    
    private void findConstructor() {
        try {
            this.iConstructor = this.iClassToInstantiate.getConstructor(this.iParamTypes);
        }
        catch (final NoSuchMethodException ex) {
            throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
        }
    }
    
    @Override
    public T create() {
        if (this.iConstructor == null) {
            this.findConstructor();
        }
        try {
            return this.iConstructor.newInstance(this.iArgs);
        }
        catch (final InstantiationException ex) {
            throw new FunctorException("InstantiateFactory: InstantiationException", ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new FunctorException("InstantiateFactory: Constructor must be public", ex2);
        }
        catch (final InvocationTargetException ex3) {
            throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex3);
        }
    }
}
