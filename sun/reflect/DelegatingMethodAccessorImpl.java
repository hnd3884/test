package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingMethodAccessorImpl extends MethodAccessorImpl
{
    private MethodAccessorImpl delegate;
    
    DelegatingMethodAccessorImpl(final MethodAccessorImpl delegate) {
        this.setDelegate(delegate);
    }
    
    @Override
    public Object invoke(final Object o, final Object[] array) throws IllegalArgumentException, InvocationTargetException {
        return this.delegate.invoke(o, array);
    }
    
    void setDelegate(final MethodAccessorImpl delegate) {
        this.delegate = delegate;
    }
}
