package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class DoubleSignature implements BaseType
{
    private static final DoubleSignature singleton;
    
    private DoubleSignature() {
    }
    
    public static DoubleSignature make() {
        return DoubleSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitDoubleSignature(this);
    }
    
    static {
        singleton = new DoubleSignature();
    }
}
