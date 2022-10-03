package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class IntSignature implements BaseType
{
    private static final IntSignature singleton;
    
    private IntSignature() {
    }
    
    public static IntSignature make() {
        return IntSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitIntSignature(this);
    }
    
    static {
        singleton = new IntSignature();
    }
}
