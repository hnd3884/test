package org.apache.lucene.index;

import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class FreqProxFields extends Fields
{
    final Map<String, FreqProxTermsWriterPerField> fields;
    
    public FreqProxFields(final List<FreqProxTermsWriterPerField> fieldList) {
        this.fields = new LinkedHashMap<String, FreqProxTermsWriterPerField>();
        for (final FreqProxTermsWriterPerField field : fieldList) {
            this.fields.put(field.fieldInfo.name, field);
        }
    }
    
    @Override
    public Iterator<String> iterator() {
        return this.fields.keySet().iterator();
    }
    
    @Override
    public Terms terms(final String field) throws IOException {
        final FreqProxTermsWriterPerField perField = this.fields.get(field);
        return (perField == null) ? null : new FreqProxTerms(perField);
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }
    
    private static class FreqProxTerms extends Terms
    {
        final FreqProxTermsWriterPerField terms;
        
        public FreqProxTerms(final FreqProxTermsWriterPerField terms) {
            this.terms = terms;
        }
        
        @Override
        public TermsEnum iterator() {
            final FreqProxTermsEnum termsEnum = new FreqProxTermsEnum(this.terms);
            termsEnum.reset();
            return termsEnum;
        }
        
        @Override
        public long size() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getSumTotalTermFreq() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getSumDocFreq() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getDocCount() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasFreqs() {
            return this.terms.fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS) >= 0;
        }
        
        @Override
        public boolean hasOffsets() {
            return this.terms.fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        }
        
        @Override
        public boolean hasPositions() {
            return this.terms.fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        }
        
        @Override
        public boolean hasPayloads() {
            return this.terms.sawPayloads;
        }
    }
    
    private static class FreqProxTermsEnum extends TermsEnum
    {
        final FreqProxTermsWriterPerField terms;
        final int[] sortedTermIDs;
        final FreqProxTermsWriterPerField.FreqProxPostingsArray postingsArray;
        final BytesRef scratch;
        final int numTerms;
        int ord;
        
        public FreqProxTermsEnum(final FreqProxTermsWriterPerField terms) {
            this.scratch = new BytesRef();
            this.terms = terms;
            this.numTerms = terms.bytesHash.size();
            this.sortedTermIDs = terms.sortedTermIDs;
            assert this.sortedTermIDs != null;
            this.postingsArray = (FreqProxTermsWriterPerField.FreqProxPostingsArray)terms.postingsArray;
        }
        
        public void reset() {
            this.ord = -1;
        }
        
        @Override
        public SeekStatus seekCeil(final BytesRef text) {
            int lo = 0;
            int hi = this.numTerms - 1;
            while (hi >= lo) {
                final int mid = lo + hi >>> 1;
                final int textStart = this.postingsArray.textStarts[this.sortedTermIDs[mid]];
                this.terms.bytePool.setBytesRef(this.scratch, textStart);
                final int cmp = this.scratch.compareTo(text);
                if (cmp < 0) {
                    lo = mid + 1;
                }
                else if (cmp > 0) {
                    hi = mid - 1;
                }
                else {
                    this.ord = mid;
                    assert this.term().compareTo(text) == 0;
                    return SeekStatus.FOUND;
                }
            }
            this.ord = lo;
            if (this.ord >= this.numTerms) {
                return SeekStatus.END;
            }
            final int textStart2 = this.postingsArray.textStarts[this.sortedTermIDs[this.ord]];
            this.terms.bytePool.setBytesRef(this.scratch, textStart2);
            assert this.term().compareTo(text) > 0;
            return SeekStatus.NOT_FOUND;
        }
        
        @Override
        public void seekExact(final long ord) {
            this.ord = (int)ord;
            final int textStart = this.postingsArray.textStarts[this.sortedTermIDs[this.ord]];
            this.terms.bytePool.setBytesRef(this.scratch, textStart);
        }
        
        @Override
        public BytesRef next() {
            ++this.ord;
            if (this.ord >= this.numTerms) {
                return null;
            }
            final int textStart = this.postingsArray.textStarts[this.sortedTermIDs[this.ord]];
            this.terms.bytePool.setBytesRef(this.scratch, textStart);
            return this.scratch;
        }
        
        @Override
        public BytesRef term() {
            return this.scratch;
        }
        
        @Override
        public long ord() {
            return this.ord;
        }
        
        @Override
        public int docFreq() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long totalTermFreq() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public PostingsEnum postings(final PostingsEnum reuse, final int flags) {
            if (PostingsEnum.featureRequested(flags, (short)24)) {
                if (!this.terms.hasProx) {
                    throw new IllegalArgumentException("did not index positions");
                }
                if (!this.terms.hasOffsets && PostingsEnum.featureRequested(flags, (short)56)) {
                    throw new IllegalArgumentException("did not index offsets");
                }
                FreqProxPostingsEnum posEnum;
                if (reuse instanceof FreqProxPostingsEnum) {
                    posEnum = (FreqProxPostingsEnum)reuse;
                    if (posEnum.postingsArray != this.postingsArray) {
                        posEnum = new FreqProxPostingsEnum(this.terms, this.postingsArray);
                    }
                }
                else {
                    posEnum = new FreqProxPostingsEnum(this.terms, this.postingsArray);
                }
                posEnum.reset(this.sortedTermIDs[this.ord]);
                return posEnum;
            }
            else {
                if (!this.terms.hasFreq && PostingsEnum.featureRequested(flags, (short)8)) {
                    throw new IllegalArgumentException("did not index freq");
                }
                FreqProxDocsEnum docsEnum;
                if (reuse instanceof FreqProxDocsEnum) {
                    docsEnum = (FreqProxDocsEnum)reuse;
                    if (docsEnum.postingsArray != this.postingsArray) {
                        docsEnum = new FreqProxDocsEnum(this.terms, this.postingsArray);
                    }
                }
                else {
                    docsEnum = new FreqProxDocsEnum(this.terms, this.postingsArray);
                }
                docsEnum.reset(this.sortedTermIDs[this.ord]);
                return docsEnum;
            }
        }
        
        @Override
        public TermState termState() throws IOException {
            return new TermState() {
                @Override
                public void copyFrom(final TermState other) {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
    private static class FreqProxDocsEnum extends PostingsEnum
    {
        final FreqProxTermsWriterPerField terms;
        final FreqProxTermsWriterPerField.FreqProxPostingsArray postingsArray;
        final ByteSliceReader reader;
        final boolean readTermFreq;
        int docID;
        int freq;
        boolean ended;
        int termID;
        
        public FreqProxDocsEnum(final FreqProxTermsWriterPerField terms, final FreqProxTermsWriterPerField.FreqProxPostingsArray postingsArray) {
            this.reader = new ByteSliceReader();
            this.docID = -1;
            this.terms = terms;
            this.postingsArray = postingsArray;
            this.readTermFreq = terms.hasFreq;
        }
        
        public void reset(final int termID) {
            this.termID = termID;
            this.terms.initReader(this.reader, termID, 0);
            this.ended = false;
            this.docID = -1;
        }
        
        @Override
        public int docID() {
            return this.docID;
        }
        
        @Override
        public int freq() {
            if (!this.readTermFreq) {
                throw new IllegalStateException("freq was not indexed");
            }
            return this.freq;
        }
        
        @Override
        public int nextPosition() throws IOException {
            return -1;
        }
        
        @Override
        public int startOffset() throws IOException {
            return -1;
        }
        
        @Override
        public int endOffset() throws IOException {
            return -1;
        }
        
        @Override
        public BytesRef getPayload() throws IOException {
            return null;
        }
        
        @Override
        public int nextDoc() throws IOException {
            if (this.docID == -1) {
                this.docID = 0;
            }
            if (this.reader.eof()) {
                if (this.ended) {
                    return Integer.MAX_VALUE;
                }
                this.ended = true;
                this.docID = this.postingsArray.lastDocIDs[this.termID];
                if (this.readTermFreq) {
                    this.freq = this.postingsArray.termFreqs[this.termID];
                }
            }
            else {
                final int code = this.reader.readVInt();
                if (!this.readTermFreq) {
                    this.docID += code;
                }
                else {
                    this.docID += code >>> 1;
                    if ((code & 0x1) != 0x0) {
                        this.freq = 1;
                    }
                    else {
                        this.freq = this.reader.readVInt();
                    }
                }
                assert this.docID != this.postingsArray.lastDocIDs[this.termID];
            }
            return this.docID;
        }
        
        @Override
        public int advance(final int target) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long cost() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class FreqProxPostingsEnum extends PostingsEnum
    {
        final FreqProxTermsWriterPerField terms;
        final FreqProxTermsWriterPerField.FreqProxPostingsArray postingsArray;
        final ByteSliceReader reader;
        final ByteSliceReader posReader;
        final boolean readOffsets;
        int docID;
        int freq;
        int pos;
        int startOffset;
        int endOffset;
        int posLeft;
        int termID;
        boolean ended;
        boolean hasPayload;
        BytesRefBuilder payload;
        
        public FreqProxPostingsEnum(final FreqProxTermsWriterPerField terms, final FreqProxTermsWriterPerField.FreqProxPostingsArray postingsArray) {
            this.reader = new ByteSliceReader();
            this.posReader = new ByteSliceReader();
            this.docID = -1;
            this.payload = new BytesRefBuilder();
            this.terms = terms;
            this.postingsArray = postingsArray;
            this.readOffsets = terms.hasOffsets;
            assert terms.hasProx;
            assert terms.hasFreq;
        }
        
        public void reset(final int termID) {
            this.termID = termID;
            this.terms.initReader(this.reader, termID, 0);
            this.terms.initReader(this.posReader, termID, 1);
            this.ended = false;
            this.docID = -1;
            this.posLeft = 0;
        }
        
        @Override
        public int docID() {
            return this.docID;
        }
        
        @Override
        public int freq() {
            return this.freq;
        }
        
        @Override
        public int nextDoc() throws IOException {
            if (this.docID == -1) {
                this.docID = 0;
            }
            while (this.posLeft != 0) {
                this.nextPosition();
            }
            if (this.reader.eof()) {
                if (this.ended) {
                    return Integer.MAX_VALUE;
                }
                this.ended = true;
                this.docID = this.postingsArray.lastDocIDs[this.termID];
                this.freq = this.postingsArray.termFreqs[this.termID];
            }
            else {
                final int code = this.reader.readVInt();
                this.docID += code >>> 1;
                if ((code & 0x1) != 0x0) {
                    this.freq = 1;
                }
                else {
                    this.freq = this.reader.readVInt();
                }
                assert this.docID != this.postingsArray.lastDocIDs[this.termID];
            }
            this.posLeft = this.freq;
            this.pos = 0;
            this.startOffset = 0;
            return this.docID;
        }
        
        @Override
        public int advance(final int target) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long cost() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int nextPosition() throws IOException {
            assert this.posLeft > 0;
            --this.posLeft;
            final int code = this.posReader.readVInt();
            this.pos += code >>> 1;
            if ((code & 0x1) != 0x0) {
                this.hasPayload = true;
                this.payload.setLength(this.posReader.readVInt());
                this.payload.grow(this.payload.length());
                this.posReader.readBytes(this.payload.bytes(), 0, this.payload.length());
            }
            else {
                this.hasPayload = false;
            }
            if (this.readOffsets) {
                this.startOffset += this.posReader.readVInt();
                this.endOffset = this.startOffset + this.posReader.readVInt();
            }
            return this.pos;
        }
        
        @Override
        public int startOffset() {
            if (!this.readOffsets) {
                throw new IllegalStateException("offsets were not indexed");
            }
            return this.startOffset;
        }
        
        @Override
        public int endOffset() {
            if (!this.readOffsets) {
                throw new IllegalStateException("offsets were not indexed");
            }
            return this.endOffset;
        }
        
        @Override
        public BytesRef getPayload() {
            if (this.hasPayload) {
                return this.payload.get();
            }
            return null;
        }
    }
}
