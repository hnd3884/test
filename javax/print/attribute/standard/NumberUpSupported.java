package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.SetOfIntegerSyntax;

public final class NumberUpSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute
{
    private static final long serialVersionUID = -1041573395759141805L;
    
    public NumberUpSupported(final int[][] array) {
        super(array);
        if (array == null) {
            throw new NullPointerException("members is null");
        }
        final int[][] members = this.getMembers();
        final int length = members.length;
        if (length == 0) {
            throw new IllegalArgumentException("members is zero-length");
        }
        for (int i = 0; i < length; ++i) {
            if (members[i][0] < 1) {
                throw new IllegalArgumentException("Number up value must be > 0");
            }
        }
    }
    
    public NumberUpSupported(final int n) {
        super(n);
        if (n < 1) {
            throw new IllegalArgumentException("Number up value must be > 0");
        }
    }
    
    public NumberUpSupported(final int n, final int n2) {
        super(n, n2);
        if (n > n2) {
            throw new IllegalArgumentException("Null range specified");
        }
        if (n < 1) {
            throw new IllegalArgumentException("Number up value must be > 0");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof NumberUpSupported;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return NumberUpSupported.class;
    }
    
    @Override
    public final String getName() {
        return "number-up-supported";
    }
}
