package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public class PairOutputs<A, B> extends Outputs<Pair<A, B>>
{
    private final Pair<A, B> NO_OUTPUT;
    private final Outputs<A> outputs1;
    private final Outputs<B> outputs2;
    private static final long BASE_NUM_BYTES;
    
    public PairOutputs(final Outputs<A> outputs1, final Outputs<B> outputs2) {
        this.outputs1 = outputs1;
        this.outputs2 = outputs2;
        this.NO_OUTPUT = new Pair<A, B>((Object)outputs1.getNoOutput(), (Object)outputs2.getNoOutput());
    }
    
    public Pair<A, B> newPair(A a, B b) {
        if (a.equals(this.outputs1.getNoOutput())) {
            a = this.outputs1.getNoOutput();
        }
        if (b.equals(this.outputs2.getNoOutput())) {
            b = this.outputs2.getNoOutput();
        }
        if (a == this.outputs1.getNoOutput() && b == this.outputs2.getNoOutput()) {
            return this.NO_OUTPUT;
        }
        final Pair<A, B> p = new Pair<A, B>((Object)a, (Object)b);
        assert this.valid(p);
        return p;
    }
    
    private boolean valid(final Pair<A, B> pair) {
        final boolean noOutput1 = pair.output1.equals(this.outputs1.getNoOutput());
        final boolean noOutput2 = pair.output2.equals(this.outputs2.getNoOutput());
        return (!noOutput1 || pair.output1 == this.outputs1.getNoOutput()) && (!noOutput2 || pair.output2 == this.outputs2.getNoOutput()) && (!noOutput1 || !noOutput2 || pair == this.NO_OUTPUT);
    }
    
    @Override
    public Pair<A, B> common(final Pair<A, B> pair1, final Pair<A, B> pair2) {
        assert this.valid(pair1);
        assert this.valid(pair2);
        return this.newPair(this.outputs1.common(pair1.output1, pair2.output1), this.outputs2.common(pair1.output2, pair2.output2));
    }
    
    @Override
    public Pair<A, B> subtract(final Pair<A, B> output, final Pair<A, B> inc) {
        assert this.valid(output);
        assert this.valid(inc);
        return this.newPair(this.outputs1.subtract(output.output1, inc.output1), this.outputs2.subtract(output.output2, inc.output2));
    }
    
    @Override
    public Pair<A, B> add(final Pair<A, B> prefix, final Pair<A, B> output) {
        assert this.valid(prefix);
        assert this.valid(output);
        return this.newPair(this.outputs1.add(prefix.output1, output.output1), this.outputs2.add(prefix.output2, output.output2));
    }
    
    @Override
    public void write(final Pair<A, B> output, final DataOutput writer) throws IOException {
        assert this.valid(output);
        this.outputs1.write(output.output1, writer);
        this.outputs2.write(output.output2, writer);
    }
    
    @Override
    public Pair<A, B> read(final DataInput in) throws IOException {
        final A output1 = this.outputs1.read(in);
        final B output2 = this.outputs2.read(in);
        return this.newPair(output1, output2);
    }
    
    @Override
    public void skipOutput(final DataInput in) throws IOException {
        this.outputs1.skipOutput(in);
        this.outputs2.skipOutput(in);
    }
    
    @Override
    public Pair<A, B> getNoOutput() {
        return this.NO_OUTPUT;
    }
    
    @Override
    public String outputToString(final Pair<A, B> output) {
        assert this.valid(output);
        return "<pair:" + this.outputs1.outputToString(output.output1) + "," + this.outputs2.outputToString(output.output2) + ">";
    }
    
    @Override
    public String toString() {
        return "PairOutputs<" + this.outputs1 + "," + this.outputs2 + ">";
    }
    
    @Override
    public long ramBytesUsed(final Pair<A, B> output) {
        long ramBytesUsed = PairOutputs.BASE_NUM_BYTES;
        if (output.output1 != null) {
            ramBytesUsed += this.outputs1.ramBytesUsed(output.output1);
        }
        if (output.output2 != null) {
            ramBytesUsed += this.outputs2.ramBytesUsed(output.output2);
        }
        return ramBytesUsed;
    }
    
    static {
        BASE_NUM_BYTES = RamUsageEstimator.shallowSizeOf(new Pair((Object)null, (Object)null));
    }
    
    public static class Pair<A, B>
    {
        public final A output1;
        public final B output2;
        
        private Pair(final A output1, final B output2) {
            this.output1 = output1;
            this.output2 = output2;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof Pair) {
                final Pair pair = (Pair)other;
                return this.output1.equals(pair.output1) && this.output2.equals(pair.output2);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.output1.hashCode() + this.output2.hashCode();
        }
        
        @Override
        public String toString() {
            return "Pair(" + this.output1 + "," + this.output2 + ")";
        }
    }
}
