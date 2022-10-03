package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class VoidDescriptor implements ReturnType
{
    private static final VoidDescriptor singleton;
    
    private VoidDescriptor() {
    }
    
    public static VoidDescriptor make() {
        return VoidDescriptor.singleton;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitVoidDescriptor(this);
    }
    
    static {
        singleton = new VoidDescriptor();
    }
}
