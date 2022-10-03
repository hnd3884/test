package org.apache.lucene.index;

import org.apache.lucene.util.PriorityQueue;
import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import java.util.Comparator;

public final class MultiTermsEnum extends TermsEnum
{
    private static final Comparator<TermsEnumWithSlice> INDEX_COMPARATOR;
    private final TermMergeQueue queue;
    private final TermsEnumWithSlice[] subs;
    private final TermsEnumWithSlice[] currentSubs;
    private final TermsEnumWithSlice[] top;
    private final MultiPostingsEnum.EnumWithSlice[] subDocs;
    private BytesRef lastSeek;
    private boolean lastSeekExact;
    private final BytesRefBuilder lastSeekScratch;
    private int numTop;
    private int numSubs;
    private BytesRef current;
    
    public int getMatchCount() {
        return this.numTop;
    }
    
    public TermsEnumWithSlice[] getMatchArray() {
        return this.top;
    }
    
    public MultiTermsEnum(final ReaderSlice[] slices) {
        this.lastSeekScratch = new BytesRefBuilder();
        this.queue = new TermMergeQueue(slices.length);
        this.top = new TermsEnumWithSlice[slices.length];
        this.subs = new TermsEnumWithSlice[slices.length];
        this.subDocs = new MultiPostingsEnum.EnumWithSlice[slices.length];
        for (int i = 0; i < slices.length; ++i) {
            this.subs[i] = new TermsEnumWithSlice(i, slices[i]);
            this.subDocs[i] = new MultiPostingsEnum.EnumWithSlice();
            this.subDocs[i].slice = slices[i];
        }
        this.currentSubs = new TermsEnumWithSlice[slices.length];
    }
    
    @Override
    public BytesRef term() {
        return this.current;
    }
    
    public TermsEnum reset(final TermsEnumIndex[] termsEnumsIndex) throws IOException {
        assert termsEnumsIndex.length <= this.top.length;
        this.numSubs = 0;
        this.numTop = 0;
        this.queue.clear();
        for (int i = 0; i < termsEnumsIndex.length; ++i) {
            final TermsEnumIndex termsEnumIndex = termsEnumsIndex[i];
            assert termsEnumIndex != null;
            final BytesRef term = termsEnumIndex.termsEnum.next();
            if (term != null) {
                final TermsEnumWithSlice entry = this.subs[termsEnumIndex.subIndex];
                entry.reset(termsEnumIndex.termsEnum, term);
                this.queue.add(entry);
                this.currentSubs[this.numSubs++] = entry;
            }
        }
        if (this.queue.size() == 0) {
            return TermsEnum.EMPTY;
        }
        return this;
    }
    
    @Override
    public boolean seekExact(final BytesRef term) throws IOException {
        this.queue.clear();
        this.numTop = 0;
        boolean seekOpt = false;
        if (this.lastSeek != null && this.lastSeek.compareTo(term) <= 0) {
            seekOpt = true;
        }
        this.lastSeek = null;
        this.lastSeekExact = true;
        for (int i = 0; i < this.numSubs; ++i) {
            boolean status;
            if (seekOpt) {
                final BytesRef curTerm = this.currentSubs[i].current;
                if (curTerm != null) {
                    final int cmp = term.compareTo(curTerm);
                    status = (cmp == 0 || (cmp >= 0 && this.currentSubs[i].terms.seekExact(term)));
                }
                else {
                    status = false;
                }
            }
            else {
                status = this.currentSubs[i].terms.seekExact(term);
            }
            if (status) {
                this.top[this.numTop++] = this.currentSubs[i];
                final TermsEnumWithSlice termsEnumWithSlice = this.currentSubs[i];
                final BytesRef term2 = this.currentSubs[i].terms.term();
                termsEnumWithSlice.current = term2;
                this.current = term2;
                assert term.equals(this.currentSubs[i].current);
            }
        }
        return this.numTop > 0;
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef term) throws IOException {
        this.queue.clear();
        this.numTop = 0;
        this.lastSeekExact = false;
        boolean seekOpt = false;
        if (this.lastSeek != null && this.lastSeek.compareTo(term) <= 0) {
            seekOpt = true;
        }
        this.lastSeekScratch.copyBytes(term);
        this.lastSeek = this.lastSeekScratch.get();
        for (int i = 0; i < this.numSubs; ++i) {
            SeekStatus status;
            if (seekOpt) {
                final BytesRef curTerm = this.currentSubs[i].current;
                if (curTerm != null) {
                    final int cmp = term.compareTo(curTerm);
                    if (cmp == 0) {
                        status = SeekStatus.FOUND;
                    }
                    else if (cmp < 0) {
                        status = SeekStatus.NOT_FOUND;
                    }
                    else {
                        status = this.currentSubs[i].terms.seekCeil(term);
                    }
                }
                else {
                    status = SeekStatus.END;
                }
            }
            else {
                status = this.currentSubs[i].terms.seekCeil(term);
            }
            if (status == SeekStatus.FOUND) {
                this.top[this.numTop++] = this.currentSubs[i];
                final TermsEnumWithSlice termsEnumWithSlice = this.currentSubs[i];
                final BytesRef term2 = this.currentSubs[i].terms.term();
                termsEnumWithSlice.current = term2;
                this.current = term2;
                this.queue.add(this.currentSubs[i]);
            }
            else if (status == SeekStatus.NOT_FOUND) {
                this.currentSubs[i].current = this.currentSubs[i].terms.term();
                assert this.currentSubs[i].current != null;
                this.queue.add(this.currentSubs[i]);
            }
            else {
                assert status == SeekStatus.END;
                this.currentSubs[i].current = null;
            }
        }
        if (this.numTop > 0) {
            return SeekStatus.FOUND;
        }
        if (this.queue.size() > 0) {
            this.pullTop();
            return SeekStatus.NOT_FOUND;
        }
        return SeekStatus.END;
    }
    
    @Override
    public void seekExact(final long ord) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long ord() {
        throw new UnsupportedOperationException();
    }
    
    private void pullTop() {
        assert this.numTop == 0;
        this.numTop = this.queue.fillTop(this.top);
        this.current = this.top[0].current;
    }
    
    private void pushTop() throws IOException {
        for (int i = 0; i < this.numTop; ++i) {
            final TermsEnumWithSlice top = this.queue.top();
            top.current = top.terms.next();
            if (top.current == null) {
                this.queue.pop();
            }
            else {
                this.queue.updateTop();
            }
        }
        this.numTop = 0;
    }
    
    @Override
    public BytesRef next() throws IOException {
        if (this.lastSeekExact) {
            final SeekStatus status = this.seekCeil(this.current);
            assert status == SeekStatus.FOUND;
            this.lastSeekExact = false;
        }
        this.lastSeek = null;
        this.pushTop();
        if (this.queue.size() > 0) {
            this.pullTop();
        }
        else {
            this.current = null;
        }
        return this.current;
    }
    
    @Override
    public int docFreq() throws IOException {
        int sum = 0;
        for (int i = 0; i < this.numTop; ++i) {
            sum += this.top[i].terms.docFreq();
        }
        return sum;
    }
    
    @Override
    public long totalTermFreq() throws IOException {
        long sum = 0L;
        for (int i = 0; i < this.numTop; ++i) {
            final long v = this.top[i].terms.totalTermFreq();
            if (v == -1L) {
                return v;
            }
            sum += v;
        }
        return sum;
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
        MultiPostingsEnum docsEnum;
        if (reuse != null && reuse instanceof MultiPostingsEnum) {
            docsEnum = (MultiPostingsEnum)reuse;
            if (!docsEnum.canReuse(this)) {
                docsEnum = new MultiPostingsEnum(this, this.subs.length);
            }
        }
        else {
            docsEnum = new MultiPostingsEnum(this, this.subs.length);
        }
        int upto = 0;
        ArrayUtil.timSort(this.top, 0, this.numTop, MultiTermsEnum.INDEX_COMPARATOR);
        for (int i = 0; i < this.numTop; ++i) {
            final TermsEnumWithSlice entry = this.top[i];
            assert entry.index < docsEnum.subPostingsEnums.length : entry.index + " vs " + docsEnum.subPostingsEnums.length + "; " + this.subs.length;
            final PostingsEnum subPostingsEnum = entry.terms.postings(docsEnum.subPostingsEnums[entry.index], flags);
            assert subPostingsEnum != null;
            docsEnum.subPostingsEnums[entry.index] = subPostingsEnum;
            this.subDocs[upto].postingsEnum = subPostingsEnum;
            this.subDocs[upto].slice = entry.subSlice;
            ++upto;
        }
        return docsEnum.reset(this.subDocs, upto);
    }
    
    @Override
    public String toString() {
        return "MultiTermsEnum(" + Arrays.toString(this.subs) + ")";
    }
    
    static {
        INDEX_COMPARATOR = new Comparator<TermsEnumWithSlice>() {
            @Override
            public int compare(final TermsEnumWithSlice o1, final TermsEnumWithSlice o2) {
                return o1.index - o2.index;
            }
        };
    }
    
    static class TermsEnumIndex
    {
        public static final TermsEnumIndex[] EMPTY_ARRAY;
        final int subIndex;
        final TermsEnum termsEnum;
        
        public TermsEnumIndex(final TermsEnum termsEnum, final int subIndex) {
            this.termsEnum = termsEnum;
            this.subIndex = subIndex;
        }
        
        static {
            EMPTY_ARRAY = new TermsEnumIndex[0];
        }
    }
    
    static final class TermsEnumWithSlice
    {
        private final ReaderSlice subSlice;
        TermsEnum terms;
        public BytesRef current;
        final int index;
        
        public TermsEnumWithSlice(final int index, final ReaderSlice subSlice) {
            this.subSlice = subSlice;
            this.index = index;
            assert subSlice.length >= 0 : "length=" + subSlice.length;
        }
        
        public void reset(final TermsEnum terms, final BytesRef term) {
            this.terms = terms;
            this.current = term;
        }
        
        @Override
        public String toString() {
            return this.subSlice.toString() + ":" + this.terms;
        }
    }
    
    private static final class TermMergeQueue extends PriorityQueue<TermsEnumWithSlice>
    {
        final int[] stack;
        
        TermMergeQueue(final int size) {
            super(size);
            this.stack = new int[size];
        }
        
        @Override
        protected boolean lessThan(final TermsEnumWithSlice termsA, final TermsEnumWithSlice termsB) {
            return termsA.current.compareTo(termsB.current) < 0;
        }
        
        int fillTop(final TermsEnumWithSlice[] tops) {
            final int size = this.size();
            if (size == 0) {
                return 0;
            }
            tops[0] = this.top();
            int numTop = 1;
            this.stack[0] = 1;
            int stackLen = 1;
            while (stackLen != 0) {
                final int index = this.stack[--stackLen];
                int child;
                for (int leftChild = child = index << 1, end = Math.min(size, leftChild + 1); child <= end; ++child) {
                    final TermsEnumWithSlice te = this.get(child);
                    if (te.current.equals(tops[0].current)) {
                        tops[numTop++] = te;
                        this.stack[stackLen++] = child;
                    }
                }
            }
            return numTop;
        }
        
        private TermsEnumWithSlice get(final int i) {
            return (TermsEnumWithSlice)this.getHeapArray()[i];
        }
    }
}
