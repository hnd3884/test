package sun.reflect.generics.reflectiveObjects;

import java.util.Arrays;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class WildcardTypeImpl extends LazyReflectiveObjectGenerator implements WildcardType
{
    private Type[] upperBounds;
    private Type[] lowerBounds;
    private FieldTypeSignature[] upperBoundASTs;
    private FieldTypeSignature[] lowerBoundASTs;
    
    private WildcardTypeImpl(final FieldTypeSignature[] upperBoundASTs, final FieldTypeSignature[] lowerBoundASTs, final GenericsFactory genericsFactory) {
        super(genericsFactory);
        this.upperBoundASTs = upperBoundASTs;
        this.lowerBoundASTs = lowerBoundASTs;
    }
    
    public static WildcardTypeImpl make(final FieldTypeSignature[] array, final FieldTypeSignature[] array2, final GenericsFactory genericsFactory) {
        return new WildcardTypeImpl(array, array2, genericsFactory);
    }
    
    private FieldTypeSignature[] getUpperBoundASTs() {
        assert this.upperBounds == null;
        return this.upperBoundASTs;
    }
    
    private FieldTypeSignature[] getLowerBoundASTs() {
        assert this.lowerBounds == null;
        return this.lowerBoundASTs;
    }
    
    @Override
    public Type[] getUpperBounds() {
        if (this.upperBounds == null) {
            final FieldTypeSignature[] upperBoundASTs = this.getUpperBoundASTs();
            final Type[] upperBounds = new Type[upperBoundASTs.length];
            for (int i = 0; i < upperBoundASTs.length; ++i) {
                final Reifier reifier = this.getReifier();
                upperBoundASTs[i].accept(reifier);
                upperBounds[i] = reifier.getResult();
            }
            this.upperBounds = upperBounds;
        }
        return this.upperBounds.clone();
    }
    
    @Override
    public Type[] getLowerBounds() {
        if (this.lowerBounds == null) {
            final FieldTypeSignature[] lowerBoundASTs = this.getLowerBoundASTs();
            final Type[] lowerBounds = new Type[lowerBoundASTs.length];
            for (int i = 0; i < lowerBoundASTs.length; ++i) {
                final Reifier reifier = this.getReifier();
                lowerBoundASTs[i].accept(reifier);
                lowerBounds[i] = reifier.getResult();
            }
            this.lowerBounds = lowerBounds;
        }
        return this.lowerBounds.clone();
    }
    
    @Override
    public String toString() {
        Type[] lowerBounds;
        final Type[] array = lowerBounds = this.getLowerBounds();
        final StringBuilder sb = new StringBuilder();
        if (array.length > 0) {
            sb.append("? super ");
        }
        else {
            final Type[] upperBounds = this.getUpperBounds();
            if (upperBounds.length <= 0 || upperBounds[0].equals(Object.class)) {
                return "?";
            }
            lowerBounds = upperBounds;
            sb.append("? extends ");
        }
        assert lowerBounds.length > 0;
        int n = 1;
        for (final Type type : lowerBounds) {
            if (n == 0) {
                sb.append(" & ");
            }
            n = 0;
            sb.append(type.getTypeName());
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType)o;
            return Arrays.equals(this.getLowerBounds(), wildcardType.getLowerBounds()) && Arrays.equals(this.getUpperBounds(), wildcardType.getUpperBounds());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getLowerBounds()) ^ Arrays.hashCode(this.getUpperBounds());
    }
}
