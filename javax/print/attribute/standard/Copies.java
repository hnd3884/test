package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.IntegerSyntax;

public final class Copies extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -6426631521680023833L;
    
    public Copies(final int n) {
        super(n, 1, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof Copies;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Copies.class;
    }
    
    @Override
    public final String getName() {
        return "copies";
    }
}
