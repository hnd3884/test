package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public final class UpToTwoPositiveIntOutputs extends Outputs<Object>
{
    private static final Long NO_OUTPUT;
    private final boolean doShare;
    private static final UpToTwoPositiveIntOutputs singletonShare;
    private static final UpToTwoPositiveIntOutputs singletonNoShare;
    private static final long TWO_LONGS_NUM_BYTES;
    
    private UpToTwoPositiveIntOutputs(final boolean doShare) {
        this.doShare = doShare;
    }
    
    public static UpToTwoPositiveIntOutputs getSingleton(final boolean doShare) {
        return doShare ? UpToTwoPositiveIntOutputs.singletonShare : UpToTwoPositiveIntOutputs.singletonNoShare;
    }
    
    public Long get(final long v) {
        if (v == 0L) {
            return UpToTwoPositiveIntOutputs.NO_OUTPUT;
        }
        return v;
    }
    
    public TwoLongs get(final long first, final long second) {
        return new TwoLongs(first, second);
    }
    
    public Long common(final Object _output1, final Object _output2) {
        assert this.valid(_output1, false);
        assert this.valid(_output2, false);
        final Long output1 = (Long)_output1;
        final Long output2 = (Long)_output2;
        if (output1 == UpToTwoPositiveIntOutputs.NO_OUTPUT || output2 == UpToTwoPositiveIntOutputs.NO_OUTPUT) {
            return UpToTwoPositiveIntOutputs.NO_OUTPUT;
        }
        if (this.doShare) {
            assert output1 > 0L;
            assert output2 > 0L;
            return Math.min(output1, output2);
        }
        else {
            if (output1.equals(output2)) {
                return output1;
            }
            return UpToTwoPositiveIntOutputs.NO_OUTPUT;
        }
    }
    
    public Long subtract(final Object _output, final Object _inc) {
        assert this.valid(_output, false);
        assert this.valid(_inc, false);
        final Long output = (Long)_output;
        final Long inc = (Long)_inc;
        assert output >= inc;
        if (inc == UpToTwoPositiveIntOutputs.NO_OUTPUT) {
            return output;
        }
        if (output.equals(inc)) {
            return UpToTwoPositiveIntOutputs.NO_OUTPUT;
        }
        return output - inc;
    }
    
    public Object add(final Object _prefix, final Object _output) {
        assert this.valid(_prefix, false);
        assert this.valid(_output, true);
        final Long prefix = (Long)_prefix;
        if (!(_output instanceof Long)) {
            final TwoLongs output = (TwoLongs)_output;
            final long v = prefix;
            return new TwoLongs(output.first + v, output.second + v);
        }
        final Long output2 = (Long)_output;
        if (prefix == UpToTwoPositiveIntOutputs.NO_OUTPUT) {
            return output2;
        }
        if (output2 == UpToTwoPositiveIntOutputs.NO_OUTPUT) {
            return prefix;
        }
        return prefix + output2;
    }
    
    public void write(final Object _output, final DataOutput out) throws IOException {
        assert this.valid(_output, true);
        if (_output instanceof Long) {
            final Long output = (Long)_output;
            out.writeVLong((long)output << 1);
        }
        else {
            final TwoLongs output2 = (TwoLongs)_output;
            out.writeVLong(output2.first << 1 | 0x1L);
            out.writeVLong(output2.second);
        }
    }
    
    public Object read(final DataInput in) throws IOException {
        final long code = in.readVLong();
        if ((code & 0x1L) != 0x0L) {
            final long first = code >>> 1;
            final long second = in.readVLong();
            return new TwoLongs(first, second);
        }
        final long v = code >>> 1;
        if (v == 0L) {
            return UpToTwoPositiveIntOutputs.NO_OUTPUT;
        }
        return v;
    }
    
    private boolean valid(final Long o) {
        assert o != null;
        assert o instanceof Long;
        assert o > 0L;
        return true;
    }
    
    private boolean valid(final Object _o, final boolean allowDouble) {
        if (allowDouble) {
            return _o instanceof TwoLongs || this.valid((Long)_o);
        }
        assert _o instanceof Long;
        return this.valid((Long)_o);
    }
    
    public Object getNoOutput() {
        return UpToTwoPositiveIntOutputs.NO_OUTPUT;
    }
    
    public String outputToString(final Object output) {
        return output.toString();
    }
    
    public Object merge(final Object first, final Object second) {
        assert this.valid(first, false);
        assert this.valid(second, false);
        return new TwoLongs((long)first, (long)second);
    }
    
    public long ramBytesUsed(final Object o) {
        if (o instanceof Long) {
            return RamUsageEstimator.sizeOf((Long)o);
        }
        assert o instanceof TwoLongs;
        return UpToTwoPositiveIntOutputs.TWO_LONGS_NUM_BYTES;
    }
    
    static {
        NO_OUTPUT = new Long(0L);
        singletonShare = new UpToTwoPositiveIntOutputs(true);
        singletonNoShare = new UpToTwoPositiveIntOutputs(false);
        TWO_LONGS_NUM_BYTES = RamUsageEstimator.shallowSizeOf((Object)new TwoLongs(0L, 0L));
    }
    
    public static final class TwoLongs
    {
        public final long first;
        public final long second;
        
        public TwoLongs(final long first, final long second) {
            this.first = first;
            this.second = second;
            assert first >= 0L;
            assert second >= 0L;
        }
        
        @Override
        public String toString() {
            return "TwoLongs:" + this.first + "," + this.second;
        }
        
        @Override
        public boolean equals(final Object _other) {
            if (_other instanceof TwoLongs) {
                final TwoLongs other = (TwoLongs)_other;
                return this.first == other.first && this.second == other.second;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return (int)(this.first ^ this.first >>> 32 ^ (this.second ^ this.second >> 32));
        }
    }
}
