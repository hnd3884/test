package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ArrayTypeSignature implements FieldTypeSignature
{
    private final TypeSignature componentType;
    
    private ArrayTypeSignature(final TypeSignature componentType) {
        this.componentType = componentType;
    }
    
    public static ArrayTypeSignature make(final TypeSignature typeSignature) {
        return new ArrayTypeSignature(typeSignature);
    }
    
    public TypeSignature getComponentType() {
        return this.componentType;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitArrayTypeSignature(this);
    }
}
