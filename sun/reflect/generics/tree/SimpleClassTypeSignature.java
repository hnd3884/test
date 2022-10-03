package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class SimpleClassTypeSignature implements FieldTypeSignature
{
    private final boolean dollar;
    private final String name;
    private final TypeArgument[] typeArgs;
    
    private SimpleClassTypeSignature(final String name, final boolean dollar, final TypeArgument[] typeArgs) {
        this.name = name;
        this.dollar = dollar;
        this.typeArgs = typeArgs;
    }
    
    public static SimpleClassTypeSignature make(final String s, final boolean b, final TypeArgument[] array) {
        return new SimpleClassTypeSignature(s, b, array);
    }
    
    public boolean getDollar() {
        return this.dollar;
    }
    
    public String getName() {
        return this.name;
    }
    
    public TypeArgument[] getTypeArguments() {
        return this.typeArgs;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitSimpleClassTypeSignature(this);
    }
}
