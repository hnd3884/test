package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobImpressionsCompleted extends IntegerSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 6722648442432393294L;
    
    public JobImpressionsCompleted(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobImpressionsCompleted;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobImpressionsCompleted.class;
    }
    
    @Override
    public final String getName() {
        return "job-impressions-completed";
    }
}
