package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ByteSignature implements BaseType
{
    private static final ByteSignature singleton;
    
    private ByteSignature() {
    }
    
    public static ByteSignature make() {
        return ByteSignature.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitByteSignature(this);
    }
    
    static {
        singleton = new ByteSignature();
    }
}
