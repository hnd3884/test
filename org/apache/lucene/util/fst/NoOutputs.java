package org.apache.lucene.util.fst;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

public final class NoOutputs extends Outputs<Object>
{
    static final Object NO_OUTPUT;
    private static final NoOutputs singleton;
    
    private NoOutputs() {
    }
    
    public static NoOutputs getSingleton() {
        return NoOutputs.singleton;
    }
    
    @Override
    public Object common(final Object output1, final Object output2) {
        assert output1 == NoOutputs.NO_OUTPUT;
        assert output2 == NoOutputs.NO_OUTPUT;
        return NoOutputs.NO_OUTPUT;
    }
    
    @Override
    public Object subtract(final Object output, final Object inc) {
        assert output == NoOutputs.NO_OUTPUT;
        assert inc == NoOutputs.NO_OUTPUT;
        return NoOutputs.NO_OUTPUT;
    }
    
    @Override
    public Object add(final Object prefix, final Object output) {
        assert prefix == NoOutputs.NO_OUTPUT : "got " + prefix;
        assert output == NoOutputs.NO_OUTPUT;
        return NoOutputs.NO_OUTPUT;
    }
    
    @Override
    public Object merge(final Object first, final Object second) {
        assert first == NoOutputs.NO_OUTPUT;
        assert second == NoOutputs.NO_OUTPUT;
        return NoOutputs.NO_OUTPUT;
    }
    
    @Override
    public void write(final Object prefix, final DataOutput out) {
    }
    
    @Override
    public Object read(final DataInput in) {
        return NoOutputs.NO_OUTPUT;
    }
    
    @Override
    public Object getNoOutput() {
        return NoOutputs.NO_OUTPUT;
    }
    
    @Override
    public String outputToString(final Object output) {
        return "";
    }
    
    @Override
    public long ramBytesUsed(final Object output) {
        return 0L;
    }
    
    @Override
    public String toString() {
        return "NoOutputs";
    }
    
    static {
        NO_OUTPUT = new Object() {
            @Override
            public int hashCode() {
                return 42;
            }
            
            @Override
            public boolean equals(final Object other) {
                return other == this;
            }
        };
        singleton = new NoOutputs();
    }
}
