package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FormalTypeParameter implements TypeTree
{
    private final String name;
    private final FieldTypeSignature[] bounds;
    
    private FormalTypeParameter(final String name, final FieldTypeSignature[] bounds) {
        this.name = name;
        this.bounds = bounds;
    }
    
    public static FormalTypeParameter make(final String s, final FieldTypeSignature[] array) {
        return new FormalTypeParameter(s, array);
    }
    
    public FieldTypeSignature[] getBounds() {
        return this.bounds;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitFormalTypeParameter(this);
    }
}
