package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.IntegerSyntax;

public final class JobMediaSheetsCompleted extends IntegerSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 1739595973810840475L;
    
    public JobMediaSheetsCompleted(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobMediaSheetsCompleted;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobMediaSheetsCompleted.class;
    }
    
    @Override
    public final String getName() {
        return "job-media-sheets-completed";
    }
}
