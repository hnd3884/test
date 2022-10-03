package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobPrioritySupported extends IntegerSyntax implements SupportedValuesAttribute
{
    private static final long serialVersionUID = 2564840378013555894L;
    
    public JobPrioritySupported(final int n) {
        super(n, 1, 100);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobPrioritySupported;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobPrioritySupported.class;
    }
    
    @Override
    public final String getName() {
        return "job-priority-supported";
    }
}
