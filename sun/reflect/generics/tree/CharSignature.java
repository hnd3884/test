package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class CharSignature implements BaseType
{
    private static final CharSignature singleton;
    
    private CharSignature() {
    }
    
    public static CharSignature make() {
        return CharSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitCharSignature(this);
    }
    
    static {
        singleton = new CharSignature();
    }
}
