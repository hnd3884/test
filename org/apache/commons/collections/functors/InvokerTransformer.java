package org.apache.commons.collections.functors;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class InvokerTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = -8653385846894047688L;
    private final String iMethodName;
    private final Class[] iParamTypes;
    private final Object[] iArgs;
    
    public static Transformer getInstance(final String methodName) {
        if (methodName == null) {
            throw new IllegalArgumentException("The method to invoke must not be null");
        }
        return new InvokerTransformer(methodName);
    }
    
    public static Transformer getInstance(final String methodName, Class[] paramTypes, Object[] args) {
        if (methodName == null) {
            throw new IllegalArgumentException("The method to invoke must not be null");
        }
        if ((paramTypes == null && args != null) || (paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length)) {
            throw new IllegalArgumentException("The parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InvokerTransformer(methodName);
        }
        paramTypes = paramTypes.clone();
        args = args.clone();
        return new InvokerTransformer(methodName, paramTypes, args);
    }
    
    private InvokerTransformer(final String methodName) {
        this.iMethodName = methodName;
        this.iParamTypes = null;
        this.iArgs = null;
    }
    
    public InvokerTransformer(final String methodName, final Class[] paramTypes, final Object[] args) {
        this.iMethodName = methodName;
        this.iParamTypes = paramTypes;
        this.iArgs = args;
    }
    
    public Object transform(final Object input) {
        if (input == null) {
            return null;
        }
        try {
            final Class cls = input.getClass();
            final Method method = cls.getMethod(this.iMethodName, (Class[])this.iParamTypes);
            return method.invoke(input, this.iArgs);
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
    
    private void writeObject(final ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(InvokerTransformer.class);
        os.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(InvokerTransformer.class);
        is.defaultReadObject();
    }
}
