package org.apache.commons.collections.functors;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections.FunctorException;
import java.lang.reflect.Constructor;
import java.io.Serializable;
import org.apache.commons.collections.Factory;

public class InstantiateFactory implements Factory, Serializable
{
    private static final long serialVersionUID = -7732226881069447957L;
    private final Class iClassToInstantiate;
    private final Class[] iParamTypes;
    private final Object[] iArgs;
    private transient Constructor iConstructor;
    
    public static Factory getInstance(final Class classToInstantiate, Class[] paramTypes, Object[] args) {
        if (classToInstantiate == null) {
            throw new IllegalArgumentException("Class to instantiate must not be null");
        }
        if ((paramTypes == null && args != null) || (paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length)) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateFactory(classToInstantiate);
        }
        paramTypes = paramTypes.clone();
        args = args.clone();
        return new InstantiateFactory(classToInstantiate, paramTypes, args);
    }
    
    public InstantiateFactory(final Class classToInstantiate) {
        this.iConstructor = null;
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = null;
        this.iArgs = null;
        this.findConstructor();
    }
    
    public InstantiateFactory(final Class classToInstantiate, final Class[] paramTypes, final Object[] args) {
        this.iConstructor = null;
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = paramTypes;
        this.iArgs = args;
        this.findConstructor();
    }
    
    private void findConstructor() {
        try {
            this.iConstructor = this.iClassToInstantiate.getConstructor((Class[])this.iParamTypes);
        }
        catch (final NoSuchMethodException ex) {
            throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
        }
    }
    
    public Object create() {
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
    
    private void writeObject(final ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(InstantiateFactory.class);
        os.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(InstantiateFactory.class);
        is.defaultReadObject();
    }
}
