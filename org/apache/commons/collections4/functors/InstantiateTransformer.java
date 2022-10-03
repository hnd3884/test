package org.apache.commons.collections4.functors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

public class InstantiateTransformer<T> implements Transformer<Class<? extends T>, T>
{
    private static final Transformer NO_ARG_INSTANCE;
    private final Class<?>[] iParamTypes;
    private final Object[] iArgs;
    
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer() {
        return InstantiateTransformer.NO_ARG_INSTANCE;
    }
    
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer(final Class<?>[] paramTypes, final Object[] args) {
        if ((paramTypes == null && args != null) || (paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length)) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateTransformer<T>();
        }
        return new InstantiateTransformer<T>(paramTypes, args);
    }
    
    private InstantiateTransformer() {
        this.iParamTypes = null;
        this.iArgs = null;
    }
    
    public InstantiateTransformer(final Class<?>[] paramTypes, final Object[] args) {
        this.iParamTypes = (Class<?>[])((paramTypes != null) ? ((Class[])paramTypes.clone()) : null);
        this.iArgs = (Object[])((args != null) ? ((Object[])args.clone()) : null);
    }
    
    @Override
    public T transform(final Class<? extends T> input) {
        try {
            if (input == null) {
                throw new FunctorException("InstantiateTransformer: Input object was not an instanceof Class, it was a null object");
            }
            final Constructor<? extends T> con = input.getConstructor(this.iParamTypes);
            return (T)con.newInstance(this.iArgs);
        }
        catch (final NoSuchMethodException ex) {
            throw new FunctorException("InstantiateTransformer: The constructor must exist and be public ");
        }
        catch (final InstantiationException ex2) {
            throw new FunctorException("InstantiateTransformer: InstantiationException", ex2);
        }
        catch (final IllegalAccessException ex3) {
            throw new FunctorException("InstantiateTransformer: Constructor must be public", ex3);
        }
        catch (final InvocationTargetException ex4) {
            throw new FunctorException("InstantiateTransformer: Constructor threw an exception", ex4);
        }
    }
    
    static {
        NO_ARG_INSTANCE = new InstantiateTransformer();
    }
}
