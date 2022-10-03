package sun.management.counter.perf;

import sun.management.counter.Variability;
import sun.management.counter.Units;
import sun.management.counter.StringCounter;
import sun.management.counter.AbstractCounter;

class StringCounterSnapshot extends AbstractCounter implements StringCounter
{
    String value;
    private static final long serialVersionUID = 1132921539085572034L;
    
    StringCounterSnapshot(final String s, final Units units, final Variability variability, final int n, final String value) {
        super(s, units, variability, n);
        this.value = value;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String stringValue() {
        return this.value;
    }
}
