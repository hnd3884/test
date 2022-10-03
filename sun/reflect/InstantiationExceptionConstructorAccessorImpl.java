package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class InstantiationExceptionConstructorAccessorImpl extends ConstructorAccessorImpl
{
    private final String message;
    
    InstantiationExceptionConstructorAccessorImpl(final String message) {
        this.message = message;
    }
    
    @Override
    public Object newInstance(final Object[] array) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
        if (this.message == null) {
            throw new InstantiationException();
        }
        throw new InstantiationException(this.message);
    }
}
