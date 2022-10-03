package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobKOctets extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -8959710146498202869L;
    
    public JobKOctets(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobKOctets;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobKOctets.class;
    }
    
    @Override
    public final String getName() {
        return "job-k-octets";
    }
}
