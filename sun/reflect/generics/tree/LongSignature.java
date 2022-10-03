package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class LongSignature implements BaseType
{
    private static final LongSignature singleton;
    
    private LongSignature() {
    }
    
    public static LongSignature make() {
        return LongSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitLongSignature(this);
    }
    
    static {
        singleton = new LongSignature();
    }
}
