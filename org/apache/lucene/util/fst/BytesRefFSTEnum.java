package org.apache.lucene.util.fst;

import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;

public final class BytesRefFSTEnum<T> extends FSTEnum<T>
{
    private final BytesRef current;
    private final InputOutput<T> result;
    private BytesRef target;
    
    public BytesRefFSTEnum(final FST<T> fst) {
        super(fst);
        this.current = new BytesRef(10);
        this.result = new InputOutput<T>();
        this.result.input = this.current;
        this.current.offset = 1;
    }
    
    public InputOutput<T> current() {
        return this.result;
    }
    
    public InputOutput<T> next() throws IOException {
        this.doNext();
        return this.setResult();
    }
    
    public InputOutput<T> seekCeil(final BytesRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        super.doSeekCeil();
        return this.setResult();
    }
    
    public InputOutput<T> seekFloor(final BytesRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        super.doSeekFloor();
        return this.setResult();
    }
    
    public InputOutput<T> seekExact(final BytesRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        if (!super.doSeekExact()) {
            return null;
        }
        assert this.upto == 1 + target.length;
        return this.setResult();
    }
    
    @Override
    protected int getTargetLabel() {
        if (this.upto - 1 == this.target.length) {
            return -1;
        }
        return this.target.bytes[this.target.offset + this.upto - 1] & 0xFF;
    }
    
    @Override
    protected int getCurrentLabel() {
        return this.current.bytes[this.upto] & 0xFF;
    }
    
    @Override
    protected void setCurrentLabel(final int label) {
        this.current.bytes[this.upto] = (byte)label;
    }
    
    @Override
    protected void grow() {
        this.current.bytes = ArrayUtil.grow(this.current.bytes, this.upto + 1);
    }
    
    private InputOutput<T> setResult() {
        if (this.upto == 0) {
            return null;
        }
        this.current.length = this.upto - 1;
        this.result.output = this.output[this.upto];
        return this.result;
    }
    
    public static class InputOutput<T>
    {
        public BytesRef input;
        public T output;
    }
}
