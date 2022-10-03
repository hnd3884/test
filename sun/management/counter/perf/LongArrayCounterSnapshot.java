package sun.management.counter.perf;

import sun.management.counter.Variability;
import sun.management.counter.Units;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.AbstractCounter;

class LongArrayCounterSnapshot extends AbstractCounter implements LongArrayCounter
{
    long[] value;
    private static final long serialVersionUID = 3585870271405924292L;
    
    LongArrayCounterSnapshot(final String s, final Units units, final Variability variability, final int n, final int n2, final long[] value) {
        super(s, units, variability, n, n2);
        this.value = value;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public long[] longArrayValue() {
        return this.value;
    }
    
    @Override
    public long longAt(final int n) {
        return this.value[n];
    }
}
