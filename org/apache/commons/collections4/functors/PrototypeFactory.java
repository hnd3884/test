package org.apache.commons.collections4.functors;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.FunctorException;
import java.lang.reflect.Method;
import java.io.Serializable;
import org.apache.commons.collections4.Factory;

public class PrototypeFactory
{
    public static <T> Factory<T> prototypeFactory(final T prototype) {
        if (prototype == null) {
            return ConstantFactory.constantFactory((T)null);
        }
        try {
            final Method method = prototype.getClass().getMethod("clone", (Class<?>[])null);
            return new PrototypeCloneFactory<T>((Object)prototype, method);
        }
        catch (final NoSuchMethodException ex) {
            try {
                prototype.getClass().getConstructor(prototype.getClass());
                return new InstantiateFactory<T>((Class<T>)prototype.getClass(), new Class[] { prototype.getClass() }, new Object[] { prototype });
            }
            catch (final NoSuchMethodException ex2) {
                if (prototype instanceof Serializable) {
                    return new PrototypeSerializationFactory<T>((Serializable)prototype);
                }
                throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
            }
        }
    }
    
    private PrototypeFactory() {
    }
    
    static class PrototypeCloneFactory<T> implements Factory<T>
    {
        private final T iPrototype;
        private transient Method iCloneMethod;
        
        private PrototypeCloneFactory(final T prototype, final Method method) {
            this.iPrototype = prototype;
            this.iCloneMethod = method;
        }
        
        private void findCloneMethod() {
            try {
                this.iCloneMethod = this.iPrototype.getClass().getMethod("clone", (Class<?>[])null);
            }
            catch (final NoSuchMethodException ex) {
                throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
        }
        
        @Override
        public T create() {
            if (this.iCloneMethod == null) {
                this.findCloneMethod();
            }
            try {
                return (T)this.iCloneMethod.invoke(this.iPrototype, (Object[])null);
            }
            catch (final IllegalAccessException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
            }
            catch (final InvocationTargetException ex2) {
                throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex2);
            }
        }
    }
    
    static class PrototypeSerializationFactory<T extends Serializable> implements Factory<T>
    {
        private final T iPrototype;
        
        private PrototypeSerializationFactory(final T prototype) {
            this.iPrototype = prototype;
        }
        
        @Override
        public T create() {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ByteArrayInputStream bais = null;
            try {
                final ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(this.iPrototype);
                bais = new ByteArrayInputStream(baos.toByteArray());
                final ObjectInputStream in = new ObjectInputStream(bais);
                return (T)in.readObject();
            }
            catch (final ClassNotFoundException ex) {
                throw new FunctorException(ex);
            }
            catch (final IOException ex2) {
                throw new FunctorException(ex2);
            }
            finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                }
                catch (final IOException ex3) {}
                try {
                    baos.close();
                }
                catch (final IOException ex4) {}
            }
        }
    }
}
