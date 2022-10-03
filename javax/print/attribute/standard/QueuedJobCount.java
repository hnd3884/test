package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.IntegerSyntax;

public final class QueuedJobCount extends IntegerSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 7499723077864047742L;
    
    public QueuedJobCount(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof QueuedJobCount;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return QueuedJobCount.class;
    }
    
    @Override
    public final String getName() {
        return "queued-job-count";
    }
}
