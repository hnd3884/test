package sun.management.counter.perf;

import java.io.ObjectStreamException;
import sun.management.counter.Units;
import java.nio.ByteBuffer;
import sun.management.counter.Variability;
import java.nio.charset.Charset;
import sun.management.counter.StringCounter;

public class PerfStringCounter extends PerfByteArrayCounter implements StringCounter
{
    private static Charset defaultCharset;
    private static final long serialVersionUID = 6802913433363692452L;
    
    PerfStringCounter(final String s, final Variability variability, final int n, final ByteBuffer byteBuffer) {
        this(s, variability, n, byteBuffer.limit(), byteBuffer);
    }
    
    PerfStringCounter(final String s, final Variability variability, final int n, final int n2, final ByteBuffer byteBuffer) {
        super(s, Units.STRING, variability, n, n2, byteBuffer);
    }
    
    @Override
    public boolean isVector() {
        return false;
    }
    
    @Override
    public int getVectorLength() {
        return 0;
    }
    
    @Override
    public Object getValue() {
        return this.stringValue();
    }
    
    @Override
    public String stringValue() {
        final String s = "";
        final byte[] byteArrayValue = this.byteArrayValue();
        if (byteArrayValue == null || byteArrayValue.length <= 1) {
            return s;
        }
        int n;
        for (n = 0; n < byteArrayValue.length && byteArrayValue[n] != 0; ++n) {}
        return new String(byteArrayValue, 0, n, PerfStringCounter.defaultCharset);
    }
    
    @Override
    protected Object writeReplace() throws ObjectStreamException {
        return new StringCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.stringValue());
    }
    
    static {
        PerfStringCounter.defaultCharset = Charset.defaultCharset();
    }
}
