package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobPriority extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -4599900369040602769L;
    
    public JobPriority(final int n) {
        super(n, 1, 100);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobPriority;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobPriority.class;
    }
    
    @Override
    public final String getName() {
        return "job-priority";
    }
}
