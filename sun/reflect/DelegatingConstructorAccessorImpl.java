package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingConstructorAccessorImpl extends ConstructorAccessorImpl
{
    private ConstructorAccessorImpl delegate;
    
    DelegatingConstructorAccessorImpl(final ConstructorAccessorImpl delegate) {
        this.setDelegate(delegate);
    }
    
    @Override
    public Object newInstance(final Object[] array) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
        return this.delegate.newInstance(array);
    }
    
    void setDelegate(final ConstructorAccessorImpl delegate) {
        this.delegate = delegate;
    }
}
