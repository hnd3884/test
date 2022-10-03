package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Collection;
import javax.print.attribute.PrintJobAttribute;
import java.util.HashSet;

public final class JobStateReasons extends HashSet<JobStateReason> implements PrintJobAttribute
{
    private static final long serialVersionUID = 8849088261264331812L;
    
    public JobStateReasons() {
    }
    
    public JobStateReasons(final int n) {
        super(n);
    }
    
    public JobStateReasons(final int n, final float n2) {
        super(n, n2);
    }
    
    public JobStateReasons(final Collection<JobStateReason> collection) {
        super(collection);
    }
    
    @Override
    public boolean add(final JobStateReason jobStateReason) {
        if (jobStateReason == null) {
            throw new NullPointerException();
        }
        return super.add(jobStateReason);
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobStateReasons.class;
    }
    
    @Override
    public final String getName() {
        return "job-state-reasons";
    }
}
