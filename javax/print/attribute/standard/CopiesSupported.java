package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.SetOfIntegerSyntax;

public final class CopiesSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute
{
    private static final long serialVersionUID = 6927711687034846001L;
    
    public CopiesSupported(final int n) {
        super(n);
        if (n < 1) {
            throw new IllegalArgumentException("Copies value < 1 specified");
        }
    }
    
    public CopiesSupported(final int n, final int n2) {
        super(n, n2);
        if (n > n2) {
            throw new IllegalArgumentException("Null range specified");
        }
        if (n < 1) {
            throw new IllegalArgumentException("Copies value < 1 specified");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof CopiesSupported;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return CopiesSupported.class;
    }
    
    @Override
    public final String getName() {
        return "copies-supported";
    }
}
