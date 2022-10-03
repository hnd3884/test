package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.IntegerSyntax;

public final class NumberOfDocuments extends IntegerSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 7891881310684461097L;
    
    public NumberOfDocuments(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof NumberOfDocuments;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return NumberOfDocuments.class;
    }
    
    @Override
    public final String getName() {
        return "number-of-documents";
    }
}
