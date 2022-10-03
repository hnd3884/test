package org.apache.lucene.util.fst;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.Collection;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public final class ListOfOutputs<T> extends Outputs<Object>
{
    private final Outputs<T> outputs;
    private static final long BASE_LIST_NUM_BYTES;
    
    public ListOfOutputs(final Outputs<T> outputs) {
        this.outputs = outputs;
    }
    
    public Object common(final Object output1, final Object output2) {
        return this.outputs.common(output1, output2);
    }
    
    public Object subtract(final Object object, final Object inc) {
        return this.outputs.subtract(object, inc);
    }
    
    public Object add(final Object prefix, final Object output) {
        assert !(prefix instanceof List);
        if (!(output instanceof List)) {
            return this.outputs.add(prefix, output);
        }
        final List<T> outputList = (List<T>)output;
        final List<T> addedList = new ArrayList<T>(outputList.size());
        for (final T _output : outputList) {
            addedList.add((T)this.outputs.add(prefix, (Object)_output));
        }
        return addedList;
    }
    
    public void write(final Object output, final DataOutput out) throws IOException {
        assert !(output instanceof List);
        this.outputs.write(output, out);
    }
    
    public void writeFinalOutput(final Object output, final DataOutput out) throws IOException {
        if (!(output instanceof List)) {
            out.writeVInt(1);
            this.outputs.write(output, out);
        }
        else {
            final List<T> outputList = (List<T>)output;
            out.writeVInt(outputList.size());
            for (final T eachOutput : outputList) {
                this.outputs.write((Object)eachOutput, out);
            }
        }
    }
    
    public Object read(final DataInput in) throws IOException {
        return this.outputs.read(in);
    }
    
    public void skipOutput(final DataInput in) throws IOException {
        this.outputs.skipOutput(in);
    }
    
    public Object readFinalOutput(final DataInput in) throws IOException {
        final int count = in.readVInt();
        if (count == 1) {
            return this.outputs.read(in);
        }
        final List<T> outputList = new ArrayList<T>(count);
        for (int i = 0; i < count; ++i) {
            outputList.add((T)this.outputs.read(in));
        }
        return outputList;
    }
    
    public void skipFinalOutput(final DataInput in) throws IOException {
        for (int count = in.readVInt(), i = 0; i < count; ++i) {
            this.outputs.skipOutput(in);
        }
    }
    
    public Object getNoOutput() {
        return this.outputs.getNoOutput();
    }
    
    public String outputToString(final Object output) {
        if (!(output instanceof List)) {
            return this.outputs.outputToString(output);
        }
        final List<T> outputList = (List<T>)output;
        final StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < outputList.size(); ++i) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(this.outputs.outputToString((Object)outputList.get(i)));
        }
        b.append(']');
        return b.toString();
    }
    
    public Object merge(final Object first, final Object second) {
        final List<T> outputList = new ArrayList<T>();
        if (!(first instanceof List)) {
            outputList.add((T)first);
        }
        else {
            outputList.addAll((Collection<? extends T>)first);
        }
        if (!(second instanceof List)) {
            outputList.add((T)second);
        }
        else {
            outputList.addAll((Collection<? extends T>)second);
        }
        return outputList;
    }
    
    public String toString() {
        return "OneOrMoreOutputs(" + this.outputs + ")";
    }
    
    public List<T> asList(final Object output) {
        if (!(output instanceof List)) {
            final List<T> result = new ArrayList<T>(1);
            result.add((T)output);
            return result;
        }
        return (List)output;
    }
    
    public long ramBytesUsed(final Object output) {
        long bytes = 0L;
        if (output instanceof List) {
            bytes += ListOfOutputs.BASE_LIST_NUM_BYTES;
            final List<T> outputList = (List<T>)output;
            for (final T _output : outputList) {
                bytes += this.outputs.ramBytesUsed((Object)_output);
            }
            bytes += 2 * outputList.size() * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        }
        else {
            bytes += this.outputs.ramBytesUsed(output);
        }
        return bytes;
    }
    
    static {
        BASE_LIST_NUM_BYTES = RamUsageEstimator.shallowSizeOf((Object)new ArrayList());
    }
}
