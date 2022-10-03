package org.apache.lucene.index;

import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import java.io.IOException;

public abstract class Terms
{
    public static final Terms[] EMPTY_ARRAY;
    
    protected Terms() {
    }
    
    public abstract TermsEnum iterator() throws IOException;
    
    public TermsEnum intersect(final CompiledAutomaton compiled, final BytesRef startTerm) throws IOException {
        final TermsEnum termsEnum = this.iterator();
        if (compiled.type != CompiledAutomaton.AUTOMATON_TYPE.NORMAL) {
            throw new IllegalArgumentException("please use CompiledAutomaton.getTermsEnum instead");
        }
        if (startTerm == null) {
            return new AutomatonTermsEnum(termsEnum, compiled);
        }
        return new AutomatonTermsEnum(termsEnum, compiled) {
            @Override
            protected BytesRef nextSeekTerm(BytesRef term) throws IOException {
                if (term == null) {
                    term = startTerm;
                }
                return super.nextSeekTerm(term);
            }
        };
    }
    
    public abstract long size() throws IOException;
    
    public abstract long getSumTotalTermFreq() throws IOException;
    
    public abstract long getSumDocFreq() throws IOException;
    
    public abstract int getDocCount() throws IOException;
    
    public abstract boolean hasFreqs();
    
    public abstract boolean hasOffsets();
    
    public abstract boolean hasPositions();
    
    public abstract boolean hasPayloads();
    
    public BytesRef getMin() throws IOException {
        return this.iterator().next();
    }
    
    public BytesRef getMax() throws IOException {
        final long size = this.size();
        if (size == 0L) {
            return null;
        }
        if (size >= 0L) {
            try {
                final TermsEnum iterator = this.iterator();
                iterator.seekExact(size - 1L);
                return iterator.term();
            }
            catch (final UnsupportedOperationException ex) {}
        }
        final TermsEnum iterator = this.iterator();
        final BytesRef v = iterator.next();
        if (v == null) {
            return v;
        }
        final BytesRefBuilder scratch = new BytesRefBuilder();
        scratch.append((byte)0);
    Block_6:
        while (true) {
            int low = 0;
            int high = 256;
            while (low != high) {
                final int mid = low + high >>> 1;
                scratch.setByteAt(scratch.length() - 1, (byte)mid);
                if (iterator.seekCeil(scratch.get()) == TermsEnum.SeekStatus.END) {
                    if (mid == 0) {
                        break Block_6;
                    }
                    high = mid;
                }
                else {
                    if (low == mid) {
                        break;
                    }
                    low = mid;
                }
            }
            scratch.setLength(scratch.length() + 1);
            scratch.grow(scratch.length());
        }
        scratch.setLength(scratch.length() - 1);
        return scratch.get();
    }
    
    public Object getStats() throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("impl=" + this.getClass().getSimpleName());
        sb.append(",size=" + this.size());
        sb.append(",docCount=" + this.getDocCount());
        sb.append(",sumTotalTermFreq=" + this.getSumTotalTermFreq());
        sb.append(",sumDocFreq=" + this.getSumDocFreq());
        return sb.toString();
    }
    
    static {
        EMPTY_ARRAY = new Terms[0];
    }
}
