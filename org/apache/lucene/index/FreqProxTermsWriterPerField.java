package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;

final class FreqProxTermsWriterPerField extends TermsHashPerField
{
    private FreqProxPostingsArray freqProxPostingsArray;
    final boolean hasFreq;
    final boolean hasProx;
    final boolean hasOffsets;
    PayloadAttribute payloadAttribute;
    OffsetAttribute offsetAttribute;
    long sumTotalTermFreq;
    long sumDocFreq;
    int docCount;
    boolean sawPayloads;
    
    public FreqProxTermsWriterPerField(final FieldInvertState invertState, final TermsHash termsHash, final FieldInfo fieldInfo, final TermsHashPerField nextPerField) {
        super((fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0) ? 2 : 1, invertState, termsHash, nextPerField, fieldInfo);
        final IndexOptions indexOptions = fieldInfo.getIndexOptions();
        assert indexOptions != IndexOptions.NONE;
        this.hasFreq = (indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS) >= 0);
        this.hasProx = (indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0);
        this.hasOffsets = (indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0);
    }
    
    @Override
    void finish() throws IOException {
        super.finish();
        this.sumDocFreq += this.fieldState.uniqueTermCount;
        this.sumTotalTermFreq += this.fieldState.length;
        if (this.fieldState.length > 0) {
            ++this.docCount;
        }
        if (this.sawPayloads) {
            this.fieldInfo.setStorePayloads();
        }
    }
    
    @Override
    boolean start(final IndexableField f, final boolean first) {
        super.start(f, first);
        this.payloadAttribute = this.fieldState.payloadAttribute;
        this.offsetAttribute = this.fieldState.offsetAttribute;
        return true;
    }
    
    void writeProx(final int termID, final int proxCode) {
        if (this.payloadAttribute == null) {
            this.writeVInt(1, proxCode << 1);
        }
        else {
            final BytesRef payload = this.payloadAttribute.getPayload();
            if (payload != null && payload.length > 0) {
                this.writeVInt(1, proxCode << 1 | 0x1);
                this.writeVInt(1, payload.length);
                this.writeBytes(1, payload.bytes, payload.offset, payload.length);
                this.sawPayloads = true;
            }
            else {
                this.writeVInt(1, proxCode << 1);
            }
        }
        assert this.postingsArray == this.freqProxPostingsArray;
        this.freqProxPostingsArray.lastPositions[termID] = this.fieldState.position;
    }
    
    void writeOffsets(final int termID, final int offsetAccum) {
        final int startOffset = offsetAccum + this.offsetAttribute.startOffset();
        final int endOffset = offsetAccum + this.offsetAttribute.endOffset();
        assert startOffset - this.freqProxPostingsArray.lastOffsets[termID] >= 0;
        this.writeVInt(1, startOffset - this.freqProxPostingsArray.lastOffsets[termID]);
        this.writeVInt(1, endOffset - startOffset);
        this.freqProxPostingsArray.lastOffsets[termID] = startOffset;
    }
    
    @Override
    void newTerm(final int termID) {
        final FreqProxPostingsArray postings = this.freqProxPostingsArray;
        postings.lastDocIDs[termID] = this.docState.docID;
        if (!this.hasFreq) {
            assert postings.termFreqs == null;
            postings.lastDocCodes[termID] = this.docState.docID;
        }
        else {
            postings.lastDocCodes[termID] = this.docState.docID << 1;
            postings.termFreqs[termID] = 1;
            if (this.hasProx) {
                this.writeProx(termID, this.fieldState.position);
                if (this.hasOffsets) {
                    this.writeOffsets(termID, this.fieldState.offset);
                }
            }
            else {
                assert !this.hasOffsets;
            }
        }
        this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
        final FieldInvertState fieldState = this.fieldState;
        ++fieldState.uniqueTermCount;
    }
    
    @Override
    void addTerm(final int termID) {
        final FreqProxPostingsArray postings = this.freqProxPostingsArray;
        assert postings.termFreqs[termID] > 0;
        if (!this.hasFreq) {
            assert postings.termFreqs == null;
            if (this.docState.docID != postings.lastDocIDs[termID]) {
                assert this.docState.docID > postings.lastDocIDs[termID];
                this.writeVInt(0, postings.lastDocCodes[termID]);
                postings.lastDocCodes[termID] = this.docState.docID - postings.lastDocIDs[termID];
                postings.lastDocIDs[termID] = this.docState.docID;
                final FieldInvertState fieldState = this.fieldState;
                ++fieldState.uniqueTermCount;
            }
        }
        else if (this.docState.docID != postings.lastDocIDs[termID]) {
            assert this.docState.docID > postings.lastDocIDs[termID] : "id: " + this.docState.docID + " postings ID: " + postings.lastDocIDs[termID] + " termID: " + termID;
            if (1 == postings.termFreqs[termID]) {
                this.writeVInt(0, postings.lastDocCodes[termID] | 0x1);
            }
            else {
                this.writeVInt(0, postings.lastDocCodes[termID]);
                this.writeVInt(0, postings.termFreqs[termID]);
            }
            postings.termFreqs[termID] = 1;
            this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
            postings.lastDocCodes[termID] = this.docState.docID - postings.lastDocIDs[termID] << 1;
            postings.lastDocIDs[termID] = this.docState.docID;
            if (this.hasProx) {
                this.writeProx(termID, this.fieldState.position);
                if (this.hasOffsets) {
                    postings.lastOffsets[termID] = 0;
                    this.writeOffsets(termID, this.fieldState.offset);
                }
            }
            else {
                assert !this.hasOffsets;
            }
            final FieldInvertState fieldState2 = this.fieldState;
            ++fieldState2.uniqueTermCount;
        }
        else {
            this.fieldState.maxTermFrequency = Math.max(this.fieldState.maxTermFrequency, ++postings.termFreqs[termID]);
            if (this.hasProx) {
                this.writeProx(termID, this.fieldState.position - postings.lastPositions[termID]);
                if (this.hasOffsets) {
                    this.writeOffsets(termID, this.fieldState.offset);
                }
            }
        }
    }
    
    public void newPostingsArray() {
        this.freqProxPostingsArray = (FreqProxPostingsArray)this.postingsArray;
    }
    
    @Override
    ParallelPostingsArray createPostingsArray(final int size) {
        final IndexOptions indexOptions = this.fieldInfo.getIndexOptions();
        assert indexOptions != IndexOptions.NONE;
        final boolean hasFreq = indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS) >= 0;
        final boolean hasProx = indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        final boolean hasOffsets = indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        return new FreqProxPostingsArray(size, hasFreq, hasProx, hasOffsets);
    }
    
    static final class FreqProxPostingsArray extends ParallelPostingsArray
    {
        int[] termFreqs;
        int[] lastDocIDs;
        int[] lastDocCodes;
        int[] lastPositions;
        int[] lastOffsets;
        
        public FreqProxPostingsArray(final int size, final boolean writeFreqs, final boolean writeProx, final boolean writeOffsets) {
            super(size);
            if (writeFreqs) {
                this.termFreqs = new int[size];
            }
            this.lastDocIDs = new int[size];
            this.lastDocCodes = new int[size];
            if (writeProx) {
                this.lastPositions = new int[size];
                if (writeOffsets) {
                    this.lastOffsets = new int[size];
                }
            }
            else {
                assert !writeOffsets;
            }
        }
        
        @Override
        ParallelPostingsArray newInstance(final int size) {
            return new FreqProxPostingsArray(size, this.termFreqs != null, this.lastPositions != null, this.lastOffsets != null);
        }
        
        @Override
        void copyTo(final ParallelPostingsArray toArray, final int numToCopy) {
            assert toArray instanceof FreqProxPostingsArray;
            final FreqProxPostingsArray to = (FreqProxPostingsArray)toArray;
            super.copyTo(toArray, numToCopy);
            System.arraycopy(this.lastDocIDs, 0, to.lastDocIDs, 0, numToCopy);
            System.arraycopy(this.lastDocCodes, 0, to.lastDocCodes, 0, numToCopy);
            if (this.lastPositions != null) {
                assert to.lastPositions != null;
                System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, numToCopy);
            }
            if (this.lastOffsets != null) {
                assert to.lastOffsets != null;
                System.arraycopy(this.lastOffsets, 0, to.lastOffsets, 0, numToCopy);
            }
            if (this.termFreqs != null) {
                assert to.termFreqs != null;
                System.arraycopy(this.termFreqs, 0, to.termFreqs, 0, numToCopy);
            }
        }
        
        @Override
        int bytesPerPosting() {
            int bytes = 20;
            if (this.lastPositions != null) {
                bytes += 4;
            }
            if (this.lastOffsets != null) {
                bytes += 4;
            }
            if (this.termFreqs != null) {
                bytes += 4;
            }
            return bytes;
        }
    }
}
