package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class TypeVariableSignature implements FieldTypeSignature
{
    private final String identifier;
    
    private TypeVariableSignature(final String identifier) {
        this.identifier = identifier;
    }
    
    public static TypeVariableSignature make(final String s) {
        return new TypeVariableSignature(s);
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitTypeVariableSignature(this);
    }
}
