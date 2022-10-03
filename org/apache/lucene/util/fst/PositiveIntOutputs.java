package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public final class PositiveIntOutputs extends Outputs<Long>
{
    private static final Long NO_OUTPUT;
    private static final PositiveIntOutputs singleton;
    
    private PositiveIntOutputs() {
    }
    
    public static PositiveIntOutputs getSingleton() {
        return PositiveIntOutputs.singleton;
    }
    
    @Override
    public Long common(final Long output1, final Long output2) {
        assert this.valid(output1);
        assert this.valid(output2);
        if (output1 == PositiveIntOutputs.NO_OUTPUT || output2 == PositiveIntOutputs.NO_OUTPUT) {
            return PositiveIntOutputs.NO_OUTPUT;
        }
        assert output1 > 0L;
        assert output2 > 0L;
        return Math.min(output1, output2);
    }
    
    @Override
    public Long subtract(final Long output, final Long inc) {
        assert this.valid(output);
        assert this.valid(inc);
        assert output >= inc;
        if (inc == PositiveIntOutputs.NO_OUTPUT) {
            return output;
        }
        if (output.equals(inc)) {
            return PositiveIntOutputs.NO_OUTPUT;
        }
        return output - inc;
    }
    
    @Override
    public Long add(final Long prefix, final Long output) {
        assert this.valid(prefix);
        assert this.valid(output);
        if (prefix == PositiveIntOutputs.NO_OUTPUT) {
            return output;
        }
        if (output == PositiveIntOutputs.NO_OUTPUT) {
            return prefix;
        }
        return prefix + output;
    }
    
    @Override
    public void write(final Long output, final DataOutput out) throws IOException {
        assert this.valid(output);
        out.writeVLong(output);
    }
    
    @Override
    public Long read(final DataInput in) throws IOException {
        final long v = in.readVLong();
        if (v == 0L) {
            return PositiveIntOutputs.NO_OUTPUT;
        }
        return v;
    }
    
    private boolean valid(final Long o) {
        assert o != null;
        assert o > 0L : "o=" + o;
        return true;
    }
    
    @Override
    public Long getNoOutput() {
        return PositiveIntOutputs.NO_OUTPUT;
    }
    
    @Override
    public String outputToString(final Long output) {
        return output.toString();
    }
    
    @Override
    public String toString() {
        return "PositiveIntOutputs";
    }
    
    @Override
    public long ramBytesUsed(final Long output) {
        return RamUsageEstimator.sizeOf(output);
    }
    
    static {
        NO_OUTPUT = new Long(0L);
        singleton = new PositiveIntOutputs();
    }
}
