package sun.reflect.generics.scope;

import java.lang.reflect.Method;

public class MethodScope extends AbstractScope<Method>
{
    private MethodScope(final Method method) {
        super(method);
    }
    
    private Class<?> getEnclosingClass() {
        return this.getRecvr().getDeclaringClass();
    }
    
    @Override
    protected Scope computeEnclosingScope() {
        return ClassScope.make(this.getEnclosingClass());
    }
    
    public static MethodScope make(final Method method) {
        return new MethodScope(method);
    }
}
