package sun.tracing.dtrace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.tracing.ProbeSkeleton;

class DTraceProbe extends ProbeSkeleton
{
    private Object proxy;
    private Method declared_method;
    private Method implementing_method;
    
    DTraceProbe(final Object proxy, final Method declared_method) {
        super(declared_method.getParameterTypes());
        this.proxy = proxy;
        this.declared_method = declared_method;
        try {
            this.implementing_method = proxy.getClass().getMethod(declared_method.getName(), declared_method.getParameterTypes());
        }
        catch (final NoSuchMethodException ex) {
            throw new RuntimeException("Internal error, wrong proxy class");
        }
    }
    
    @Override
    public boolean isEnabled() {
        return JVM.isEnabled(this.implementing_method);
    }
    
    @Override
    public void uncheckedTrigger(final Object[] array) {
        try {
            this.implementing_method.invoke(this.proxy, array);
        }
        catch (final IllegalAccessException ex) {
            assert false;
        }
        catch (final InvocationTargetException ex2) {
            assert false;
        }
    }
    
    String getProbeName() {
        return DTraceProvider.getProbeName(this.declared_method);
    }
    
    String getFunctionName() {
        return DTraceProvider.getFunctionName(this.declared_method);
    }
    
    Method getMethod() {
        return this.implementing_method;
    }
    
    Class<?>[] getParameterTypes() {
        return this.parameters;
    }
}
