package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class Wildcard implements TypeArgument
{
    private FieldTypeSignature[] upperBounds;
    private FieldTypeSignature[] lowerBounds;
    private static final FieldTypeSignature[] emptyBounds;
    
    private Wildcard(final FieldTypeSignature[] upperBounds, final FieldTypeSignature[] lowerBounds) {
        this.upperBounds = upperBounds;
        this.lowerBounds = lowerBounds;
    }
    
    public static Wildcard make(final FieldTypeSignature[] array, final FieldTypeSignature[] array2) {
        return new Wildcard(array, array2);
    }
    
    public FieldTypeSignature[] getUpperBounds() {
        return this.upperBounds;
    }
    
    public FieldTypeSignature[] getLowerBounds() {
        if (this.lowerBounds.length == 1 && this.lowerBounds[0] == BottomSignature.make()) {
            return Wildcard.emptyBounds;
        }
        return this.lowerBounds;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitWildcard(this);
    }
    
    static {
        emptyBounds = new FieldTypeSignature[0];
    }
}
