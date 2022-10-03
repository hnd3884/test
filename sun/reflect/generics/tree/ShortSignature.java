package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ShortSignature implements BaseType
{
    private static final ShortSignature singleton;
    
    private ShortSignature() {
    }
    
    public static ShortSignature make() {
        return ShortSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitShortSignature(this);
    }
    
    static {
        singleton = new ShortSignature();
    }
}
