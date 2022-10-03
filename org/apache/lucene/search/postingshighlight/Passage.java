package org.apache.lucene.search.postingshighlight;

import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.BytesRef;

public final class Passage
{
    int startOffset;
    int endOffset;
    float score;
    int[] matchStarts;
    int[] matchEnds;
    BytesRef[] matchTerms;
    int numMatches;
    
    public Passage() {
        this.startOffset = -1;
        this.endOffset = -1;
        this.score = 0.0f;
        this.matchStarts = new int[8];
        this.matchEnds = new int[8];
        this.matchTerms = new BytesRef[8];
        this.numMatches = 0;
    }
    
    void addMatch(final int startOffset, final int endOffset, final BytesRef term) {
        assert startOffset >= this.startOffset && startOffset <= this.endOffset;
        if (this.numMatches == this.matchStarts.length) {
            final int newLength = ArrayUtil.oversize(this.numMatches + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
            final int[] newMatchStarts = new int[newLength];
            final int[] newMatchEnds = new int[newLength];
            final BytesRef[] newMatchTerms = new BytesRef[newLength];
            System.arraycopy(this.matchStarts, 0, newMatchStarts, 0, this.numMatches);
            System.arraycopy(this.matchEnds, 0, newMatchEnds, 0, this.numMatches);
            System.arraycopy(this.matchTerms, 0, newMatchTerms, 0, this.numMatches);
            this.matchStarts = newMatchStarts;
            this.matchEnds = newMatchEnds;
            this.matchTerms = newMatchTerms;
        }
        assert this.matchStarts.length == this.matchEnds.length && this.matchEnds.length == this.matchTerms.length;
        this.matchStarts[this.numMatches] = startOffset;
        this.matchEnds[this.numMatches] = endOffset;
        this.matchTerms[this.numMatches] = term;
        ++this.numMatches;
    }
    
    void sort() {
        final int[] starts = this.matchStarts;
        final int[] ends = this.matchEnds;
        final BytesRef[] terms = this.matchTerms;
        new InPlaceMergeSorter() {
            protected void swap(final int i, final int j) {
                int temp = starts[i];
                starts[i] = starts[j];
                starts[j] = temp;
                temp = ends[i];
                ends[i] = ends[j];
                ends[j] = temp;
                final BytesRef tempTerm = terms[i];
                terms[i] = terms[j];
                terms[j] = tempTerm;
            }
            
            protected int compare(final int i, final int j) {
                return Integer.compare(starts[i], starts[j]);
            }
        }.sort(0, this.numMatches);
    }
    
    void reset() {
        final int n = -1;
        this.endOffset = n;
        this.startOffset = n;
        this.score = 0.0f;
        this.numMatches = 0;
    }
    
    public int getStartOffset() {
        return this.startOffset;
    }
    
    public int getEndOffset() {
        return this.endOffset;
    }
    
    public float getScore() {
        return this.score;
    }
    
    public int getNumMatches() {
        return this.numMatches;
    }
    
    public int[] getMatchStarts() {
        return this.matchStarts;
    }
    
    public int[] getMatchEnds() {
        return this.matchEnds;
    }
    
    public BytesRef[] getMatchTerms() {
        return this.matchTerms;
    }
}
