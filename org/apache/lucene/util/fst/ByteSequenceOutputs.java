package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.BytesRef;

public final class ByteSequenceOutputs extends Outputs<BytesRef>
{
    private static final BytesRef NO_OUTPUT;
    private static final ByteSequenceOutputs singleton;
    private static final long BASE_NUM_BYTES;
    
    private ByteSequenceOutputs() {
    }
    
    public static ByteSequenceOutputs getSingleton() {
        return ByteSequenceOutputs.singleton;
    }
    
    @Override
    public BytesRef common(final BytesRef output1, final BytesRef output2) {
        assert output1 != null;
        assert output2 != null;
        int pos1 = output1.offset;
        int pos2 = output2.offset;
        for (int stopAt1 = pos1 + Math.min(output1.length, output2.length); pos1 < stopAt1 && output1.bytes[pos1] == output2.bytes[pos2]; ++pos1, ++pos2) {}
        if (pos1 == output1.offset) {
            return ByteSequenceOutputs.NO_OUTPUT;
        }
        if (pos1 == output1.offset + output1.length) {
            return output1;
        }
        if (pos2 == output2.offset + output2.length) {
            return output2;
        }
        return new BytesRef(output1.bytes, output1.offset, pos1 - output1.offset);
    }
    
    @Override
    public BytesRef subtract(final BytesRef output, final BytesRef inc) {
        assert output != null;
        assert inc != null;
        if (inc == ByteSequenceOutputs.NO_OUTPUT) {
            return output;
        }
        assert StringHelper.startsWith(output, inc);
        if (inc.length == output.length) {
            return ByteSequenceOutputs.NO_OUTPUT;
        }
        assert inc.length < output.length : "inc.length=" + inc.length + " vs output.length=" + output.length;
        assert inc.length > 0;
        return new BytesRef(output.bytes, output.offset + inc.length, output.length - inc.length);
    }
    
    @Override
    public BytesRef add(final BytesRef prefix, final BytesRef output) {
        assert prefix != null;
        assert output != null;
        if (prefix == ByteSequenceOutputs.NO_OUTPUT) {
            return output;
        }
        if (output == ByteSequenceOutputs.NO_OUTPUT) {
            return prefix;
        }
        assert prefix.length > 0;
        assert output.length > 0;
        final BytesRef result = new BytesRef(prefix.length + output.length);
        System.arraycopy(prefix.bytes, prefix.offset, result.bytes, 0, prefix.length);
        System.arraycopy(output.bytes, output.offset, result.bytes, prefix.length, output.length);
        result.length = prefix.length + output.length;
        return result;
    }
    
    @Override
    public void write(final BytesRef prefix, final DataOutput out) throws IOException {
        assert prefix != null;
        out.writeVInt(prefix.length);
        out.writeBytes(prefix.bytes, prefix.offset, prefix.length);
    }
    
    @Override
    public BytesRef read(final DataInput in) throws IOException {
        final int len = in.readVInt();
        if (len == 0) {
            return ByteSequenceOutputs.NO_OUTPUT;
        }
        final BytesRef output = new BytesRef(len);
        in.readBytes(output.bytes, 0, len);
        output.length = len;
        return output;
    }
    
    @Override
    public void skipOutput(final DataInput in) throws IOException {
        final int len = in.readVInt();
        if (len != 0) {
            in.skipBytes(len);
        }
    }
    
    @Override
    public BytesRef getNoOutput() {
        return ByteSequenceOutputs.NO_OUTPUT;
    }
    
    @Override
    public String outputToString(final BytesRef output) {
        return output.toString();
    }
    
    @Override
    public long ramBytesUsed(final BytesRef output) {
        return ByteSequenceOutputs.BASE_NUM_BYTES + RamUsageEstimator.sizeOf(output.bytes);
    }
    
    @Override
    public String toString() {
        return "ByteSequenceOutputs";
    }
    
    static {
        NO_OUTPUT = new BytesRef();
        singleton = new ByteSequenceOutputs();
        BASE_NUM_BYTES = RamUsageEstimator.shallowSizeOf(ByteSequenceOutputs.NO_OUTPUT);
    }
}
