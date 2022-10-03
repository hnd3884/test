package org.apache.lucene.util.fst;

import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.util.IntsRef;

public final class IntsRefFSTEnum<T> extends FSTEnum<T>
{
    private final IntsRef current;
    private final InputOutput<T> result;
    private IntsRef target;
    
    public IntsRefFSTEnum(final FST<T> fst) {
        super(fst);
        this.current = new IntsRef(10);
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
    
    public InputOutput<T> seekCeil(final IntsRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        super.doSeekCeil();
        return this.setResult();
    }
    
    public InputOutput<T> seekFloor(final IntsRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        super.doSeekFloor();
        return this.setResult();
    }
    
    public InputOutput<T> seekExact(final IntsRef target) throws IOException {
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
        return this.target.ints[this.target.offset + this.upto - 1];
    }
    
    @Override
    protected int getCurrentLabel() {
        return this.current.ints[this.upto];
    }
    
    @Override
    protected void setCurrentLabel(final int label) {
        this.current.ints[this.upto] = label;
    }
    
    @Override
    protected void grow() {
        this.current.ints = ArrayUtil.grow(this.current.ints, this.upto + 1);
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
        public IntsRef input;
        public T output;
    }
}
