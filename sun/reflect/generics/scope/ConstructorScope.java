package sun.reflect.generics.scope;

import java.lang.reflect.Constructor;

public class ConstructorScope extends AbstractScope<Constructor<?>>
{
    private ConstructorScope(final Constructor<?> constructor) {
        super(constructor);
    }
    
    private Class<?> getEnclosingClass() {
        return this.getRecvr().getDeclaringClass();
    }
    
    @Override
    protected Scope computeEnclosingScope() {
        return ClassScope.make(this.getEnclosingClass());
    }
    
    public static ConstructorScope make(final Constructor<?> constructor) {
        return new ConstructorScope(constructor);
    }
}
