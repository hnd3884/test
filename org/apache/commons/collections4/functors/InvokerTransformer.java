package org.apache.commons.collections4.functors;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

public class InvokerTransformer<I, O> implements Transformer<I, O>
{
    private final String iMethodName;
    private final Class<?>[] iParamTypes;
    private final Object[] iArgs;
    
    public static <I, O> Transformer<I, O> invokerTransformer(final String methodName) {
        if (methodName == null) {
            throw new NullPointerException("The method to invoke must not be null");
        }
        return new InvokerTransformer<I, O>(methodName);
    }
    
    public static <I, O> Transformer<I, O> invokerTransformer(final String methodName, final Class<?>[] paramTypes, final Object[] args) {
        if (methodName == null) {
            throw new NullPointerException("The method to invoke must not be null");
        }
        if ((paramTypes == null && args != null) || (paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length)) {
            throw new IllegalArgumentException("The parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InvokerTransformer<I, O>(methodName);
        }
        return new InvokerTransformer<I, O>(methodName, paramTypes, args);
    }
    
    private InvokerTransformer(final String methodName) {
        this.iMethodName = methodName;
        this.iParamTypes = null;
        this.iArgs = null;
    }
    
    public InvokerTransformer(final String methodName, final Class<?>[] paramTypes, final Object[] args) {
        this.iMethodName = methodName;
        this.iParamTypes = (Class<?>[])((paramTypes != null) ? ((Class[])paramTypes.clone()) : null);
        this.iArgs = (Object[])((args != null) ? ((Object[])args.clone()) : null);
    }
    
    @Override
    public O transform(final Object input) {
        if (input == null) {
            return null;
        }
        try {
            final Class<?> cls = input.getClass();
            final Method method = cls.getMethod(this.iMethodName, this.iParamTypes);
            return (O)method.invoke(input, this.iArgs);
        }
        catch (final NoSuchMethodException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + input.getClass() + "' does not exist");
        }
        catch (final IllegalAccessException ex2) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + input.getClass() + "' cannot be accessed");
        }
        catch (final InvocationTargetException ex3) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + input.getClass() + "' threw an exception", ex3);
        }
    }
}
