package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.CharsRef;

public final class CharSequenceOutputs extends Outputs<CharsRef>
{
    private static final CharsRef NO_OUTPUT;
    private static final CharSequenceOutputs singleton;
    private static final long BASE_NUM_BYTES;
    
    private CharSequenceOutputs() {
    }
    
    public static CharSequenceOutputs getSingleton() {
        return CharSequenceOutputs.singleton;
    }
    
    @Override
    public CharsRef common(final CharsRef output1, final CharsRef output2) {
        assert output1 != null;
        assert output2 != null;
        int pos1 = output1.offset;
        int pos2 = output2.offset;
        for (int stopAt1 = pos1 + Math.min(output1.length, output2.length); pos1 < stopAt1 && output1.chars[pos1] == output2.chars[pos2]; ++pos1, ++pos2) {}
        if (pos1 == output1.offset) {
            return CharSequenceOutputs.NO_OUTPUT;
        }
        if (pos1 == output1.offset + output1.length) {
            return output1;
        }
        if (pos2 == output2.offset + output2.length) {
            return output2;
        }
        return new CharsRef(output1.chars, output1.offset, pos1 - output1.offset);
    }
    
    @Override
    public CharsRef subtract(final CharsRef output, final CharsRef inc) {
        assert output != null;
        assert inc != null;
        if (inc == CharSequenceOutputs.NO_OUTPUT) {
            return output;
        }
        if (inc.length == output.length) {
            return CharSequenceOutputs.NO_OUTPUT;
        }
        assert inc.length < output.length : "inc.length=" + inc.length + " vs output.length=" + output.length;
        assert inc.length > 0;
        return new CharsRef(output.chars, output.offset + inc.length, output.length - inc.length);
    }
    
    @Override
    public CharsRef add(final CharsRef prefix, final CharsRef output) {
        assert prefix != null;
        assert output != null;
        if (prefix == CharSequenceOutputs.NO_OUTPUT) {
            return output;
        }
        if (output == CharSequenceOutputs.NO_OUTPUT) {
            return prefix;
        }
        assert prefix.length > 0;
        assert output.length > 0;
        final CharsRef result = new CharsRef(prefix.length + output.length);
        System.arraycopy(prefix.chars, prefix.offset, result.chars, 0, prefix.length);
        System.arraycopy(output.chars, output.offset, result.chars, prefix.length, output.length);
        result.length = prefix.length + output.length;
        return result;
    }
    
    @Override
    public void write(final CharsRef prefix, final DataOutput out) throws IOException {
        assert prefix != null;
        out.writeVInt(prefix.length);
        for (int idx = 0; idx < prefix.length; ++idx) {
            out.writeVInt(prefix.chars[prefix.offset + idx]);
        }
    }
    
    @Override
    public CharsRef read(final DataInput in) throws IOException {
        final int len = in.readVInt();
        if (len == 0) {
            return CharSequenceOutputs.NO_OUTPUT;
        }
        final CharsRef output = new CharsRef(len);
        for (int idx = 0; idx < len; ++idx) {
            output.chars[idx] = (char)in.readVInt();
        }
        output.length = len;
        return output;
    }
    
    @Override
    public void skipOutput(final DataInput in) throws IOException {
        for (int len = in.readVInt(), idx = 0; idx < len; ++idx) {
            in.readVInt();
        }
    }
    
    @Override
    public CharsRef getNoOutput() {
        return CharSequenceOutputs.NO_OUTPUT;
    }
    
    @Override
    public String outputToString(final CharsRef output) {
        return output.toString();
    }
    
    @Override
    public long ramBytesUsed(final CharsRef output) {
        return CharSequenceOutputs.BASE_NUM_BYTES + RamUsageEstimator.sizeOf(output.chars);
    }
    
    static {
        NO_OUTPUT = new CharsRef();
        singleton = new CharSequenceOutputs();
        BASE_NUM_BYTES = RamUsageEstimator.shallowSizeOf(CharSequenceOutputs.NO_OUTPUT);
    }
}
