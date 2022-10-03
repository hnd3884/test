package sun.management.counter.perf;

import java.io.ObjectStreamException;
import sun.management.counter.Variability;
import sun.management.counter.Units;
import java.nio.ByteBuffer;
import sun.management.counter.ByteArrayCounter;
import sun.management.counter.AbstractCounter;

public class PerfByteArrayCounter extends AbstractCounter implements ByteArrayCounter
{
    ByteBuffer bb;
    private static final long serialVersionUID = 2545474036937279921L;
    
    PerfByteArrayCounter(final String s, final Units units, final Variability variability, final int n, final int n2, final ByteBuffer bb) {
        super(s, units, variability, n, n2);
        this.bb = bb;
    }
    
    @Override
    public Object getValue() {
        return this.byteArrayValue();
    }
    
    @Override
    public byte[] byteArrayValue() {
        this.bb.position(0);
        final byte[] array = new byte[this.bb.limit()];
        this.bb.get(array);
        return array;
    }
    
    @Override
    public byte byteAt(final int n) {
        this.bb.position(n);
        return this.bb.get();
    }
    
    @Override
    public String toString() {
        final String string = this.getName() + ": " + new String(this.byteArrayValue()) + " " + this.getUnits();
        if (this.isInternal()) {
            return string + " [INTERNAL]";
        }
        return string;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new ByteArrayCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.getVectorLength(), this.byteArrayValue());
    }
}
