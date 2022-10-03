package sun.reflect.generics.scope;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class ClassScope extends AbstractScope<Class<?>> implements Scope
{
    private ClassScope(final Class<?> clazz) {
        super(clazz);
    }
    
    @Override
    protected Scope computeEnclosingScope() {
        final Class clazz = ((AbstractScope<Class>)this).getRecvr();
        final Method enclosingMethod = clazz.getEnclosingMethod();
        if (enclosingMethod != null) {
            return MethodScope.make(enclosingMethod);
        }
        final Constructor enclosingConstructor = clazz.getEnclosingConstructor();
        if (enclosingConstructor != null) {
            return ConstructorScope.make(enclosingConstructor);
        }
        final Class enclosingClass = clazz.getEnclosingClass();
        if (enclosingClass != null) {
            return make(enclosingClass);
        }
        return DummyScope.make();
    }
    
    public static ClassScope make(final Class<?> clazz) {
        return new ClassScope(clazz);
    }
}
