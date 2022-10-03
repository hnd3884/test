package sun.management.counter.perf;

import java.io.ObjectStreamException;
import sun.management.counter.Variability;
import sun.management.counter.Units;
import java.nio.LongBuffer;
import sun.management.counter.LongCounter;
import sun.management.counter.AbstractCounter;

public class PerfLongCounter extends AbstractCounter implements LongCounter
{
    LongBuffer lb;
    private static final long serialVersionUID = 857711729279242948L;
    
    PerfLongCounter(final String s, final Units units, final Variability variability, final int n, final LongBuffer lb) {
        super(s, units, variability, n);
        this.lb = lb;
    }
    
    @Override
    public Object getValue() {
        return new Long(this.lb.get(0));
    }
    
    @Override
    public long longValue() {
        return this.lb.get(0);
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new LongCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.longValue());
    }
}
