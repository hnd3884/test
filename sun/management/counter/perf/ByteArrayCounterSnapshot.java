package sun.management.counter.perf;

import sun.management.counter.Variability;
import sun.management.counter.Units;
import sun.management.counter.ByteArrayCounter;
import sun.management.counter.AbstractCounter;

class ByteArrayCounterSnapshot extends AbstractCounter implements ByteArrayCounter
{
    byte[] value;
    private static final long serialVersionUID = 1444793459838438979L;
    
    ByteArrayCounterSnapshot(final String s, final Units units, final Variability variability, final int n, final int n2, final byte[] value) {
        super(s, units, variability, n, n2);
        this.value = value;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public byte[] byteArrayValue() {
        return this.value;
    }
    
    @Override
    public byte byteAt(final int n) {
        return this.value[n];
    }
}
