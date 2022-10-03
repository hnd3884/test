package org.apache.lucene.index;

import org.apache.lucene.util.Bits;

final class MultiBits implements Bits
{
    private final Bits[] subs;
    private final int[] starts;
    private final boolean defaultValue;
    
    public MultiBits(final Bits[] subs, final int[] starts, final boolean defaultValue) {
        assert starts.length == 1 + subs.length;
        this.subs = subs;
        this.starts = starts;
        this.defaultValue = defaultValue;
    }
    
    private boolean checkLength(final int reader, final int doc) {
        final int length = this.starts[1 + reader] - this.starts[reader];
        assert doc - this.starts[reader] < length : "doc=" + doc + " reader=" + reader + " starts[reader]=" + this.starts[reader] + " length=" + length;
        return true;
    }
    
    @Override
    public boolean get(final int doc) {
        final int reader = ReaderUtil.subIndex(doc, this.starts);
        assert reader != -1;
        final Bits bits = this.subs[reader];
        if (bits == null) {
            return this.defaultValue;
        }
        assert this.checkLength(reader, doc);
        return bits.get(doc - this.starts[reader]);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.subs.length + " subs: ");
        for (int i = 0; i < this.subs.length; ++i) {
            if (i != 0) {
                b.append("; ");
            }
            if (this.subs[i] == null) {
                b.append("s=" + this.starts[i] + " l=null");
            }
            else {
                b.append("s=" + this.starts[i] + " l=" + this.subs[i].length() + " b=" + this.subs[i]);
            }
        }
        b.append(" end=" + this.starts[this.subs.length]);
        return b.toString();
    }
    
    public SubResult getMatchingSub(final ReaderSlice slice) {
        final int reader = ReaderUtil.subIndex(slice.start, this.starts);
        assert reader != -1;
        assert reader < this.subs.length : "slice=" + slice + " starts[-1]=" + this.starts[this.starts.length - 1];
        final SubResult subResult = new SubResult();
        if (this.starts[reader] == slice.start && this.starts[1 + reader] == slice.start + slice.length) {
            subResult.matches = true;
            subResult.result = this.subs[reader];
        }
        else {
            subResult.matches = false;
        }
        return subResult;
    }
    
    @Override
    public int length() {
        return this.starts[this.starts.length - 1];
    }
    
    public static final class SubResult
    {
        public boolean matches;
        public Bits result;
    }
}
