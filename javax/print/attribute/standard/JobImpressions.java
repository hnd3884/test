package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobImpressions extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 8225537206784322464L;
    
    public JobImpressions(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobImpressions;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobImpressions.class;
    }
    
    @Override
    public final String getName() {
        return "job-impressions";
    }
}
