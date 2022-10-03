package org.apache.lucene.index;

import java.util.Arrays;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;

public final class MultiPostingsEnum extends PostingsEnum
{
    private final MultiTermsEnum parent;
    final PostingsEnum[] subPostingsEnums;
    private final EnumWithSlice[] subs;
    int numSubs;
    int upto;
    PostingsEnum current;
    int currentBase;
    int doc;
    
    public MultiPostingsEnum(final MultiTermsEnum parent, final int subReaderCount) {
        this.doc = -1;
        this.parent = parent;
        this.subPostingsEnums = new PostingsEnum[subReaderCount];
        this.subs = new EnumWithSlice[subReaderCount];
        for (int i = 0; i < this.subs.length; ++i) {
            this.subs[i] = new EnumWithSlice();
        }
    }
    
    public boolean canReuse(final MultiTermsEnum parent) {
        return this.parent == parent;
    }
    
    public MultiPostingsEnum reset(final EnumWithSlice[] subs, final int numSubs) {
        this.numSubs = numSubs;
        for (int i = 0; i < numSubs; ++i) {
            this.subs[i].postingsEnum = subs[i].postingsEnum;
            this.subs[i].slice = subs[i].slice;
        }
        this.upto = -1;
        this.doc = -1;
        this.current = null;
        return this;
    }
    
    public int getNumSubs() {
        return this.numSubs;
    }
    
    public EnumWithSlice[] getSubs() {
        return this.subs;
    }
    
    @Override
    public int freq() throws IOException {
        assert this.current != null;
        return this.current.freq();
    }
    
    @Override
    public int docID() {
        return this.doc;
    }
    
    @Override
    public int advance(final int target) throws IOException {
        assert target > this.doc;
        while (true) {
            if (this.current != null) {
                int doc;
                if (target < this.currentBase) {
                    doc = this.current.nextDoc();
                }
                else {
                    doc = this.current.advance(target - this.currentBase);
                }
                if (doc != Integer.MAX_VALUE) {
                    return this.doc = doc + this.currentBase;
                }
                this.current = null;
            }
            else {
                if (this.upto == this.numSubs - 1) {
                    return this.doc = Integer.MAX_VALUE;
                }
                ++this.upto;
                this.current = this.subs[this.upto].postingsEnum;
                this.currentBase = this.subs[this.upto].slice.start;
            }
        }
    }
    
    @Override
    public int nextDoc() throws IOException {
        while (true) {
            if (this.current == null) {
                if (this.upto == this.numSubs - 1) {
                    return this.doc = Integer.MAX_VALUE;
                }
                ++this.upto;
                this.current = this.subs[this.upto].postingsEnum;
                this.currentBase = this.subs[this.upto].slice.start;
            }
            final int doc = this.current.nextDoc();
            if (doc != Integer.MAX_VALUE) {
                return this.doc = this.currentBase + doc;
            }
            this.current = null;
        }
    }
    
    @Override
    public int nextPosition() throws IOException {
        return this.current.nextPosition();
    }
    
    @Override
    public int startOffset() throws IOException {
        return this.current.startOffset();
    }
    
    @Override
    public int endOffset() throws IOException {
        return this.current.endOffset();
    }
    
    @Override
    public BytesRef getPayload() throws IOException {
        return this.current.getPayload();
    }
    
    @Override
    public long cost() {
        long cost = 0L;
        for (int i = 0; i < this.numSubs; ++i) {
            cost += this.subs[i].postingsEnum.cost();
        }
        return cost;
    }
    
    @Override
    public String toString() {
        return "MultiDocsAndPositionsEnum(" + Arrays.toString(this.getSubs()) + ")";
    }
    
    public static final class EnumWithSlice
    {
        public PostingsEnum postingsEnum;
        public ReaderSlice slice;
        
        EnumWithSlice() {
        }
        
        @Override
        public String toString() {
            return this.slice.toString() + ":" + this.postingsEnum;
        }
    }
}
