package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public class DummyScope implements Scope
{
    private static final DummyScope singleton;
    
    private DummyScope() {
    }
    
    public static DummyScope make() {
        return DummyScope.singleton;
    }
    
    @Override
    public TypeVariable<?> lookup(final String s) {
        return null;
    }
    
    static {
        singleton = new DummyScope();
    }
}
