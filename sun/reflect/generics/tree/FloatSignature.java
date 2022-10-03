package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FloatSignature implements BaseType
{
    private static final FloatSignature singleton;
    
    private FloatSignature() {
    }
    
    public static FloatSignature make() {
        return FloatSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitFloatSignature(this);
    }
    
    static {
        singleton = new FloatSignature();
    }
}
