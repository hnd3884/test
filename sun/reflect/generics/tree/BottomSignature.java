package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BottomSignature implements FieldTypeSignature
{
    private static final BottomSignature singleton;
    
    private BottomSignature() {
    }
    
    public static BottomSignature make() {
        return BottomSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitBottomSignature(this);
    }
    
    static {
        singleton = new BottomSignature();
    }
}
