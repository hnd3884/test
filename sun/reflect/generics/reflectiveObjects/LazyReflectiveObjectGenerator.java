package sun.reflect.generics.reflectiveObjects;

import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.factory.GenericsFactory;

public abstract class LazyReflectiveObjectGenerator
{
    private final GenericsFactory factory;
    
    protected LazyReflectiveObjectGenerator(final GenericsFactory factory) {
        this.factory = factory;
    }
    
    private GenericsFactory getFactory() {
        return this.factory;
    }
    
    protected Reifier getReifier() {
        return Reifier.make(this.getFactory());
    }
}
