package org.apache.lucene.codecs;

import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.PostingsEnum;

public abstract class PushPostingsWriterBase extends PostingsWriterBase
{
    private PostingsEnum postingsEnum;
    private int enumFlags;
    protected FieldInfo fieldInfo;
    protected IndexOptions indexOptions;
    protected boolean writeFreqs;
    protected boolean writePositions;
    protected boolean writePayloads;
    protected boolean writeOffsets;
    
    protected PushPostingsWriterBase() {
    }
    
    public abstract BlockTermState newTermState() throws IOException;
    
    public abstract void startTerm() throws IOException;
    
    public abstract void finishTerm(final BlockTermState p0) throws IOException;
    
    @Override
    public int setField(final FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
        this.indexOptions = fieldInfo.getIndexOptions();
        this.writeFreqs = (this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS) >= 0);
        this.writePositions = (this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0);
        this.writeOffsets = (this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0);
        this.writePayloads = fieldInfo.hasPayloads();
        if (!this.writeFreqs) {
            this.enumFlags = 0;
        }
        else if (!this.writePositions) {
            this.enumFlags = 8;
        }
        else if (!this.writeOffsets) {
            if (this.writePayloads) {
                this.enumFlags = 88;
            }
            else {
                this.enumFlags = 24;
            }
        }
        else if (this.writePayloads) {
            this.enumFlags = 120;
        }
        else {
            this.enumFlags = 56;
        }
        return 0;
    }
    
    @Override
    public final BlockTermState writeTerm(final BytesRef term, final TermsEnum termsEnum, final FixedBitSet docsSeen) throws IOException {
        this.startTerm();
        this.postingsEnum = termsEnum.postings(this.postingsEnum, this.enumFlags);
        assert this.postingsEnum != null;
        int docFreq = 0;
        long totalTermFreq = 0L;
        while (true) {
            final int docID = this.postingsEnum.nextDoc();
            if (docID == Integer.MAX_VALUE) {
                break;
            }
            ++docFreq;
            docsSeen.set(docID);
            int freq;
            if (this.writeFreqs) {
                freq = this.postingsEnum.freq();
                totalTermFreq += freq;
            }
            else {
                freq = -1;
            }
            this.startDoc(docID, freq);
            if (this.writePositions) {
                for (int i = 0; i < freq; ++i) {
                    final int pos = this.postingsEnum.nextPosition();
                    final BytesRef payload = this.writePayloads ? this.postingsEnum.getPayload() : null;
                    int startOffset;
                    int endOffset;
                    if (this.writeOffsets) {
                        startOffset = this.postingsEnum.startOffset();
                        endOffset = this.postingsEnum.endOffset();
                    }
                    else {
                        startOffset = -1;
                        endOffset = -1;
                    }
                    this.addPosition(pos, payload, startOffset, endOffset);
                }
            }
            this.finishDoc();
        }
        if (docFreq == 0) {
            return null;
        }
        final BlockTermState state = this.newTermState();
        state.docFreq = docFreq;
        state.totalTermFreq = (this.writeFreqs ? totalTermFreq : -1L);
        this.finishTerm(state);
        return state;
    }
    
    public abstract void startDoc(final int p0, final int p1) throws IOException;
    
    public abstract void addPosition(final int p0, final BytesRef p1, final int p2, final int p3) throws IOException;
    
    public abstract void finishDoc() throws IOException;
}
