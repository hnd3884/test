package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BooleanSignature implements BaseType
{
    private static final BooleanSignature singleton;
    
    private BooleanSignature() {
    }
    
    public static BooleanSignature make() {
        return BooleanSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitBooleanSignature(this);
    }
    
    static {
        singleton = new BooleanSignature();
    }
}
