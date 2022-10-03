package sun.management.counter.perf;

import java.io.ObjectStreamException;
import sun.management.counter.Variability;
import sun.management.counter.Units;
import java.nio.LongBuffer;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.AbstractCounter;

public class PerfLongArrayCounter extends AbstractCounter implements LongArrayCounter
{
    LongBuffer lb;
    private static final long serialVersionUID = -2733617913045487126L;
    
    PerfLongArrayCounter(final String s, final Units units, final Variability variability, final int n, final int n2, final LongBuffer lb) {
        super(s, units, variability, n, n2);
        this.lb = lb;
    }
    
    @Override
    public Object getValue() {
        return this.longArrayValue();
    }
    
    @Override
    public long[] longArrayValue() {
        this.lb.position(0);
        final long[] array = new long[this.lb.limit()];
        this.lb.get(array);
        return array;
    }
    
    @Override
    public long longAt(final int n) {
        this.lb.position(n);
        return this.lb.get();
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new LongArrayCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.getVectorLength(), this.longArrayValue());
    }
}
