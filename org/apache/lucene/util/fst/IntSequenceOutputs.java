package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.IntsRef;

public final class IntSequenceOutputs extends Outputs<IntsRef>
{
    private static final IntsRef NO_OUTPUT;
    private static final IntSequenceOutputs singleton;
    private static final long BASE_NUM_BYTES;
    
    private IntSequenceOutputs() {
    }
    
    public static IntSequenceOutputs getSingleton() {
        return IntSequenceOutputs.singleton;
    }
    
    @Override
    public IntsRef common(final IntsRef output1, final IntsRef output2) {
        assert output1 != null;
        assert output2 != null;
        int pos1 = output1.offset;
        int pos2 = output2.offset;
        for (int stopAt1 = pos1 + Math.min(output1.length, output2.length); pos1 < stopAt1 && output1.ints[pos1] == output2.ints[pos2]; ++pos1, ++pos2) {}
        if (pos1 == output1.offset) {
            return IntSequenceOutputs.NO_OUTPUT;
        }
        if (pos1 == output1.offset + output1.length) {
            return output1;
        }
        if (pos2 == output2.offset + output2.length) {
            return output2;
        }
        return new IntsRef(output1.ints, output1.offset, pos1 - output1.offset);
    }
    
    @Override
    public IntsRef subtract(final IntsRef output, final IntsRef inc) {
        assert output != null;
        assert inc != null;
        if (inc == IntSequenceOutputs.NO_OUTPUT) {
            return output;
        }
        if (inc.length == output.length) {
            return IntSequenceOutputs.NO_OUTPUT;
        }
        assert inc.length < output.length : "inc.length=" + inc.length + " vs output.length=" + output.length;
        assert inc.length > 0;
        return new IntsRef(output.ints, output.offset + inc.length, output.length - inc.length);
    }
    
    @Override
    public IntsRef add(final IntsRef prefix, final IntsRef output) {
        assert prefix != null;
        assert output != null;
        if (prefix == IntSequenceOutputs.NO_OUTPUT) {
            return output;
        }
        if (output == IntSequenceOutputs.NO_OUTPUT) {
            return prefix;
        }
        assert prefix.length > 0;
        assert output.length > 0;
        final IntsRef result = new IntsRef(prefix.length + output.length);
        System.arraycopy(prefix.ints, prefix.offset, result.ints, 0, prefix.length);
        System.arraycopy(output.ints, output.offset, result.ints, prefix.length, output.length);
        result.length = prefix.length + output.length;
        return result;
    }
    
    @Override
    public void write(final IntsRef prefix, final DataOutput out) throws IOException {
        assert prefix != null;
        out.writeVInt(prefix.length);
        for (int idx = 0; idx < prefix.length; ++idx) {
            out.writeVInt(prefix.ints[prefix.offset + idx]);
        }
    }
    
    @Override
    public IntsRef read(final DataInput in) throws IOException {
        final int len = in.readVInt();
        if (len == 0) {
            return IntSequenceOutputs.NO_OUTPUT;
        }
        final IntsRef output = new IntsRef(len);
        for (int idx = 0; idx < len; ++idx) {
            output.ints[idx] = in.readVInt();
        }
        output.length = len;
        return output;
    }
    
    @Override
    public void skipOutput(final DataInput in) throws IOException {
        final int len = in.readVInt();
        if (len == 0) {
            return;
        }
        for (int idx = 0; idx < len; ++idx) {
            in.readVInt();
        }
    }
    
    @Override
    public IntsRef getNoOutput() {
        return IntSequenceOutputs.NO_OUTPUT;
    }
    
    @Override
    public String outputToString(final IntsRef output) {
        return output.toString();
    }
    
    @Override
    public long ramBytesUsed(final IntsRef output) {
        return IntSequenceOutputs.BASE_NUM_BYTES + RamUsageEstimator.sizeOf(output.ints);
    }
    
    @Override
    public String toString() {
        return "IntSequenceOutputs";
    }
    
    static {
        NO_OUTPUT = new IntsRef();
        singleton = new IntSequenceOutputs();
        BASE_NUM_BYTES = RamUsageEstimator.shallowSizeOf(IntSequenceOutputs.NO_OUTPUT);
    }
}
