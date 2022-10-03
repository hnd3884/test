package org.apache.lucene.index;

import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import java.io.IOException;

public final class MultiTerms extends Terms
{
    private final Terms[] subs;
    private final ReaderSlice[] subSlices;
    private final boolean hasFreqs;
    private final boolean hasOffsets;
    private final boolean hasPositions;
    private final boolean hasPayloads;
    
    public MultiTerms(final Terms[] subs, final ReaderSlice[] subSlices) throws IOException {
        this.subs = subs;
        this.subSlices = subSlices;
        assert subs.length > 0 : "inefficient: don't use MultiTerms over one sub";
        boolean _hasFreqs = true;
        boolean _hasOffsets = true;
        boolean _hasPositions = true;
        boolean _hasPayloads = false;
        for (int i = 0; i < subs.length; ++i) {
            _hasFreqs &= subs[i].hasFreqs();
            _hasOffsets &= subs[i].hasOffsets();
            _hasPositions &= subs[i].hasPositions();
            _hasPayloads |= subs[i].hasPayloads();
        }
        this.hasFreqs = _hasFreqs;
        this.hasOffsets = _hasOffsets;
        this.hasPositions = _hasPositions;
        this.hasPayloads = (this.hasPositions && _hasPayloads);
    }
    
    public Terms[] getSubTerms() {
        return this.subs;
    }
    
    public ReaderSlice[] getSubSlices() {
        return this.subSlices;
    }
    
    @Override
    public TermsEnum intersect(final CompiledAutomaton compiled, final BytesRef startTerm) throws IOException {
        final List<MultiTermsEnum.TermsEnumIndex> termsEnums = new ArrayList<MultiTermsEnum.TermsEnumIndex>();
        for (int i = 0; i < this.subs.length; ++i) {
            final TermsEnum termsEnum = this.subs[i].intersect(compiled, startTerm);
            if (termsEnum != null) {
                termsEnums.add(new MultiTermsEnum.TermsEnumIndex(termsEnum, i));
            }
        }
        if (termsEnums.size() > 0) {
            return new MultiTermsEnum(this.subSlices).reset(termsEnums.toArray(MultiTermsEnum.TermsEnumIndex.EMPTY_ARRAY));
        }
        return TermsEnum.EMPTY;
    }
    
    @Override
    public BytesRef getMin() throws IOException {
        BytesRef minTerm = null;
        for (final Terms terms : this.subs) {
            final BytesRef term = terms.getMin();
            if (minTerm == null || term.compareTo(minTerm) < 0) {
                minTerm = term;
            }
        }
        return minTerm;
    }
    
    @Override
    public BytesRef getMax() throws IOException {
        BytesRef maxTerm = null;
        for (final Terms terms : this.subs) {
            final BytesRef term = terms.getMax();
            if (maxTerm == null || term.compareTo(maxTerm) > 0) {
                maxTerm = term;
            }
        }
        return maxTerm;
    }
    
    @Override
    public TermsEnum iterator() throws IOException {
        final List<MultiTermsEnum.TermsEnumIndex> termsEnums = new ArrayList<MultiTermsEnum.TermsEnumIndex>();
        for (int i = 0; i < this.subs.length; ++i) {
            final TermsEnum termsEnum = this.subs[i].iterator();
            if (termsEnum != null) {
                termsEnums.add(new MultiTermsEnum.TermsEnumIndex(termsEnum, i));
            }
        }
        if (termsEnums.size() > 0) {
            return new MultiTermsEnum(this.subSlices).reset(termsEnums.toArray(MultiTermsEnum.TermsEnumIndex.EMPTY_ARRAY));
        }
        return TermsEnum.EMPTY;
    }
    
    @Override
    public long size() {
        return -1L;
    }
    
    @Override
    public long getSumTotalTermFreq() throws IOException {
        long sum = 0L;
        for (final Terms terms : this.subs) {
            final long v = terms.getSumTotalTermFreq();
            if (v == -1L) {
                return -1L;
            }
            sum += v;
        }
        return sum;
    }
    
    @Override
    public long getSumDocFreq() throws IOException {
        long sum = 0L;
        for (final Terms terms : this.subs) {
            final long v = terms.getSumDocFreq();
            if (v == -1L) {
                return -1L;
            }
            sum += v;
        }
        return sum;
    }
    
    @Override
    public int getDocCount() throws IOException {
        int sum = 0;
        for (final Terms terms : this.subs) {
            final int v = terms.getDocCount();
            if (v == -1) {
                return -1;
            }
            sum += v;
        }
        return sum;
    }
    
    @Override
    public boolean hasFreqs() {
        return this.hasFreqs;
    }
    
    @Override
    public boolean hasOffsets() {
        return this.hasOffsets;
    }
    
    @Override
    public boolean hasPositions() {
        return this.hasPositions;
    }
    
    @Override
    public boolean hasPayloads() {
        return this.hasPayloads;
    }
}
