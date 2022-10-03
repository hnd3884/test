package sun.management.counter.perf;

import sun.management.counter.Variability;
import sun.management.counter.Units;
import sun.management.counter.LongCounter;
import sun.management.counter.AbstractCounter;

class LongCounterSnapshot extends AbstractCounter implements LongCounter
{
    long value;
    private static final long serialVersionUID = 2054263861474565758L;
    
    LongCounterSnapshot(final String s, final Units units, final Variability variability, final int n, final long value) {
        super(s, units, variability, n);
        this.value = value;
    }
    
    @Override
    public Object getValue() {
        return new Long(this.value);
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
}
