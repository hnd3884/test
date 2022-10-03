package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.IntegerSyntax;

public final class NumberOfInterveningJobs extends IntegerSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 2568141124844982746L;
    
    public NumberOfInterveningJobs(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof NumberOfInterveningJobs;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return NumberOfInterveningJobs.class;
    }
    
    @Override
    public final String getName() {
        return "number-of-intervening-jobs";
    }
}
