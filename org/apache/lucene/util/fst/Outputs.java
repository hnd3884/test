package org.apache.lucene.util.fst;

import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public abstract class Outputs<T>
{
    public abstract T common(final T p0, final T p1);
    
    public abstract T subtract(final T p0, final T p1);
    
    public abstract T add(final T p0, final T p1);
    
    public abstract void write(final T p0, final DataOutput p1) throws IOException;
    
    public void writeFinalOutput(final T output, final DataOutput out) throws IOException {
        this.write(output, out);
    }
    
    public abstract T read(final DataInput p0) throws IOException;
    
    public void skipOutput(final DataInput in) throws IOException {
        this.read(in);
    }
    
    public T readFinalOutput(final DataInput in) throws IOException {
        return this.read(in);
    }
    
    public void skipFinalOutput(final DataInput in) throws IOException {
        this.skipOutput(in);
    }
    
    public abstract T getNoOutput();
    
    public abstract String outputToString(final T p0);
    
    public T merge(final T first, final T second) {
        throw new UnsupportedOperationException();
    }
    
    public abstract long ramBytesUsed(final T p0);
}
