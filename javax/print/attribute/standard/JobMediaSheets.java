package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.IntegerSyntax;

public class JobMediaSheets extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 408871131531979741L;
    
    public JobMediaSheets(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobMediaSheets;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobMediaSheets.class;
    }
    
    @Override
    public final String getName() {
        return "job-media-sheets";
    }
}
