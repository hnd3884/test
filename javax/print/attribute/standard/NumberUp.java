package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.IntegerSyntax;

public final class NumberUp extends IntegerSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -3040436486786527811L;
    
    public NumberUp(final int n) {
        super(n, 1, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof NumberUp;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return NumberUp.class;
    }
    
    @Override
    public final String getName() {
        return "number-up";
    }
}
