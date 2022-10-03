package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobKOctetsProcessed extends IntegerSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = -6265238509657881806L;
    
    public JobKOctetsProcessed(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobKOctetsProcessed;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobKOctetsProcessed.class;
    }
    
    @Override
    public final String getName() {
        return "job-k-octets-processed";
    }
}
