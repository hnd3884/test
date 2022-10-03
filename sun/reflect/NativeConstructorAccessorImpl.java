package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Constructor;

class NativeConstructorAccessorImpl extends ConstructorAccessorImpl
{
    private final Constructor<?> c;
    private DelegatingConstructorAccessorImpl parent;
    private int numInvocations;
    
    NativeConstructorAccessorImpl(final Constructor<?> c) {
        this.c = c;
    }
    
    @Override
    public Object newInstance(final Object[] array) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
        if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.c.getDeclaringClass())) {
            this.parent.setDelegate((ConstructorAccessorImpl)new MethodAccessorGenerator().generateConstructor(this.c.getDeclaringClass(), this.c.getParameterTypes(), this.c.getExceptionTypes(), this.c.getModifiers()));
        }
        return newInstance0(this.c, array);
    }
    
    void setParent(final DelegatingConstructorAccessorImpl parent) {
        this.parent = parent;
    }
    
    private static native Object newInstance0(final Constructor<?> p0, final Object[] p1) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
