package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

class BootstrapConstructorAccessorImpl extends ConstructorAccessorImpl
{
    private final Constructor<?> constructor;
    
    BootstrapConstructorAccessorImpl(final Constructor<?> constructor) {
        this.constructor = constructor;
    }
    
    @Override
    public Object newInstance(final Object[] array) throws IllegalArgumentException, InvocationTargetException {
        try {
            return UnsafeFieldAccessorImpl.unsafe.allocateInstance(this.constructor.getDeclaringClass());
        }
        catch (final InstantiationException ex) {
            throw new InvocationTargetException(ex);
        }
    }
}
