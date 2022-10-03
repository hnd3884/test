package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

final class TermVectorsConsumerPerField extends TermsHashPerField
{
    private TermVectorsPostingsArray termVectorsPostingsArray;
    final TermVectorsConsumer termsWriter;
    boolean doVectors;
    boolean doVectorPositions;
    boolean doVectorOffsets;
    boolean doVectorPayloads;
    OffsetAttribute offsetAttribute;
    PayloadAttribute payloadAttribute;
    boolean hasPayloads;
    
    public TermVectorsConsumerPerField(final FieldInvertState invertState, final TermVectorsConsumer termsWriter, final FieldInfo fieldInfo) {
        super(2, invertState, termsWriter, null, fieldInfo);
        this.termsWriter = termsWriter;
    }
    
    @Override
    void finish() {
        if (!this.doVectors || this.bytesHash.size() == 0) {
            return;
        }
        this.termsWriter.addFieldToFlush(this);
    }
    
    void finishDocument() throws IOException {
        if (!this.doVectors) {
            return;
        }
        this.doVectors = false;
        final int numPostings = this.bytesHash.size();
        final BytesRef flushTerm = this.termsWriter.flushTerm;
        assert numPostings >= 0;
        final TermVectorsPostingsArray postings = this.termVectorsPostingsArray;
        final TermVectorsWriter tv = this.termsWriter.writer;
        final int[] termIDs = this.sortPostings();
        tv.startField(this.fieldInfo, numPostings, this.doVectorPositions, this.doVectorOffsets, this.hasPayloads);
        final ByteSliceReader posReader = this.doVectorPositions ? this.termsWriter.vectorSliceReaderPos : null;
        final ByteSliceReader offReader = this.doVectorOffsets ? this.termsWriter.vectorSliceReaderOff : null;
        for (final int termID : termIDs) {
            final int freq = postings.freqs[termID];
            this.termBytePool.setBytesRef(flushTerm, postings.textStarts[termID]);
            tv.startTerm(flushTerm, freq);
            if (this.doVectorPositions || this.doVectorOffsets) {
                if (posReader != null) {
                    this.initReader(posReader, termID, 0);
                }
                if (offReader != null) {
                    this.initReader(offReader, termID, 1);
                }
                tv.addProx(freq, posReader, offReader);
            }
            tv.finishTerm();
        }
        tv.finishField();
        this.reset();
        this.fieldInfo.setStoreTermVectors();
    }
    
    @Override
    boolean start(final IndexableField field, final boolean first) {
        assert field.fieldType().indexOptions() != IndexOptions.NONE;
        if (first) {
            if (this.bytesHash.size() != 0) {
                this.reset();
            }
            this.bytesHash.reinit();
            this.hasPayloads = false;
            this.doVectors = field.fieldType().storeTermVectors();
            if (this.doVectors) {
                this.termsWriter.hasVectors = true;
                this.doVectorPositions = field.fieldType().storeTermVectorPositions();
                this.doVectorOffsets = field.fieldType().storeTermVectorOffsets();
                if (this.doVectorPositions) {
                    this.doVectorPayloads = field.fieldType().storeTermVectorPayloads();
                }
                else {
                    this.doVectorPayloads = false;
                    if (field.fieldType().storeTermVectorPayloads()) {
                        throw new IllegalArgumentException("cannot index term vector payloads without term vector positions (field=\"" + field.name() + "\")");
                    }
                }
            }
            else {
                if (field.fieldType().storeTermVectorOffsets()) {
                    throw new IllegalArgumentException("cannot index term vector offsets when term vectors are not indexed (field=\"" + field.name() + "\")");
                }
                if (field.fieldType().storeTermVectorPositions()) {
                    throw new IllegalArgumentException("cannot index term vector positions when term vectors are not indexed (field=\"" + field.name() + "\")");
                }
                if (field.fieldType().storeTermVectorPayloads()) {
                    throw new IllegalArgumentException("cannot index term vector payloads when term vectors are not indexed (field=\"" + field.name() + "\")");
                }
            }
        }
        else {
            if (this.doVectors != field.fieldType().storeTermVectors()) {
                throw new IllegalArgumentException("all instances of a given field name must have the same term vectors settings (storeTermVectors changed for field=\"" + field.name() + "\")");
            }
            if (this.doVectorPositions != field.fieldType().storeTermVectorPositions()) {
                throw new IllegalArgumentException("all instances of a given field name must have the same term vectors settings (storeTermVectorPositions changed for field=\"" + field.name() + "\")");
            }
            if (this.doVectorOffsets != field.fieldType().storeTermVectorOffsets()) {
                throw new IllegalArgumentException("all instances of a given field name must have the same term vectors settings (storeTermVectorOffsets changed for field=\"" + field.name() + "\")");
            }
            if (this.doVectorPayloads != field.fieldType().storeTermVectorPayloads()) {
                throw new IllegalArgumentException("all instances of a given field name must have the same term vectors settings (storeTermVectorPayloads changed for field=\"" + field.name() + "\")");
            }
        }
        if (this.doVectors) {
            if (this.doVectorOffsets) {
                this.offsetAttribute = this.fieldState.offsetAttribute;
                assert this.offsetAttribute != null;
            }
            if (this.doVectorPayloads) {
                this.payloadAttribute = this.fieldState.payloadAttribute;
            }
            else {
                this.payloadAttribute = null;
            }
        }
        return this.doVectors;
    }
    
    void writeProx(final TermVectorsPostingsArray postings, final int termID) {
        if (this.doVectorOffsets) {
            final int startOffset = this.fieldState.offset + this.offsetAttribute.startOffset();
            final int endOffset = this.fieldState.offset + this.offsetAttribute.endOffset();
            this.writeVInt(1, startOffset - postings.lastOffsets[termID]);
            this.writeVInt(1, endOffset - startOffset);
            postings.lastOffsets[termID] = endOffset;
        }
        if (this.doVectorPositions) {
            BytesRef payload;
            if (this.payloadAttribute == null) {
                payload = null;
            }
            else {
                payload = this.payloadAttribute.getPayload();
            }
            final int pos = this.fieldState.position - postings.lastPositions[termID];
            if (payload != null && payload.length > 0) {
                this.writeVInt(0, pos << 1 | 0x1);
                this.writeVInt(0, payload.length);
                this.writeBytes(0, payload.bytes, payload.offset, payload.length);
                this.hasPayloads = true;
            }
            else {
                this.writeVInt(0, pos << 1);
            }
            postings.lastPositions[termID] = this.fieldState.position;
        }
    }
    
    @Override
    void newTerm(final int termID) {
        final TermVectorsPostingsArray postings = this.termVectorsPostingsArray;
        postings.freqs[termID] = 1;
        postings.lastOffsets[termID] = 0;
        postings.lastPositions[termID] = 0;
        this.writeProx(postings, termID);
    }
    
    @Override
    void addTerm(final int termID) {
        final TermVectorsPostingsArray postings = this.termVectorsPostingsArray;
        final int[] freqs = postings.freqs;
        ++freqs[termID];
        this.writeProx(postings, termID);
    }
    
    public void newPostingsArray() {
        this.termVectorsPostingsArray = (TermVectorsPostingsArray)this.postingsArray;
    }
    
    @Override
    ParallelPostingsArray createPostingsArray(final int size) {
        return new TermVectorsPostingsArray(size);
    }
    
    static final class TermVectorsPostingsArray extends ParallelPostingsArray
    {
        int[] freqs;
        int[] lastOffsets;
        int[] lastPositions;
        
        public TermVectorsPostingsArray(final int size) {
            super(size);
            this.freqs = new int[size];
            this.lastOffsets = new int[size];
            this.lastPositions = new int[size];
        }
        
        @Override
        ParallelPostingsArray newInstance(final int size) {
            return new TermVectorsPostingsArray(size);
        }
        
        @Override
        void copyTo(final ParallelPostingsArray toArray, final int numToCopy) {
            assert toArray instanceof TermVectorsPostingsArray;
            final TermVectorsPostingsArray to = (TermVectorsPostingsArray)toArray;
            super.copyTo(toArray, numToCopy);
            System.arraycopy(this.freqs, 0, to.freqs, 0, this.size);
            System.arraycopy(this.lastOffsets, 0, to.lastOffsets, 0, this.size);
            System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, this.size);
        }
        
        @Override
        int bytesPerPosting() {
            return super.bytesPerPosting() + 12;
        }
    }
}
