package sun.reflect.generics.repository;

import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.Tree;

public abstract class AbstractRepository<T extends Tree>
{
    private final GenericsFactory factory;
    private final T tree;
    
    private GenericsFactory getFactory() {
        return this.factory;
    }
    
    protected T getTree() {
        return this.tree;
    }
    
    protected Reifier getReifier() {
        return Reifier.make(this.getFactory());
    }
    
    protected AbstractRepository(final String s, final GenericsFactory factory) {
        this.tree = this.parse(s);
        this.factory = factory;
    }
    
    protected abstract T parse(final String p0);
}
