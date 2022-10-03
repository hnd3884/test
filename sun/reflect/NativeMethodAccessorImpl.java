package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Method;

class NativeMethodAccessorImpl extends MethodAccessorImpl
{
    private final Method method;
    private DelegatingMethodAccessorImpl parent;
    private int numInvocations;
    
    NativeMethodAccessorImpl(final Method method) {
        this.method = method;
    }
    
    @Override
    public Object invoke(final Object o, final Object[] array) throws IllegalArgumentException, InvocationTargetException {
        if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.method.getDeclaringClass())) {
            this.parent.setDelegate((MethodAccessorImpl)new MethodAccessorGenerator().generateMethod(this.method.getDeclaringClass(), this.method.getName(), this.method.getParameterTypes(), this.method.getReturnType(), this.method.getExceptionTypes(), this.method.getModifiers()));
        }
        return invoke0(this.method, o, array);
    }
    
    void setParent(final DelegatingMethodAccessorImpl parent) {
        this.parent = parent;
    }
    
    private static native Object invoke0(final Method p0, final Object p1, final Object[] p2);
}
