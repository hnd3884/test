package org.apache.lucene.index;

import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMInputStream;
import java.util.Arrays;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.store.RAMFile;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.TimSorter;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.search.Sort;

public class SortingLeafReader extends FilterLeafReader
{
    final Sorter.DocMap docMap;
    
    public static LeafReader wrap(final LeafReader reader, final Sort sort) throws IOException {
        return wrap(reader, new Sorter(sort).sort(reader));
    }
    
    static LeafReader wrap(final LeafReader reader, final Sorter.DocMap docMap) {
        if (docMap == null) {
            return reader;
        }
        if (reader.maxDoc() != docMap.size()) {
            throw new IllegalArgumentException("reader.maxDoc() should be equal to docMap.size(), got" + reader.maxDoc() + " != " + docMap.size());
        }
        assert Sorter.isConsistent(docMap);
        return (LeafReader)new SortingLeafReader(reader, docMap);
    }
    
    private SortingLeafReader(final LeafReader in, final Sorter.DocMap docMap) {
        super(in);
        this.docMap = docMap;
    }
    
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.in.document(this.docMap.newToOld(docID), visitor);
    }
    
    public Fields fields() throws IOException {
        return (Fields)new SortingFields(this.in.fields(), this.in.getFieldInfos(), this.docMap);
    }
    
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        final BinaryDocValues oldDocValues = this.in.getBinaryDocValues(field);
        if (oldDocValues == null) {
            return null;
        }
        return new SortingBinaryDocValues(oldDocValues, this.docMap);
    }
    
    public Bits getLiveDocs() {
        final Bits inLiveDocs = this.in.getLiveDocs();
        if (inLiveDocs == null) {
            return null;
        }
        return (Bits)new SortingBits(inLiveDocs, this.docMap);
    }
    
    public NumericDocValues getNormValues(final String field) throws IOException {
        final NumericDocValues norm = this.in.getNormValues(field);
        if (norm == null) {
            return null;
        }
        return new SortingNumericDocValues(norm, this.docMap);
    }
    
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        final NumericDocValues oldDocValues = this.in.getNumericDocValues(field);
        if (oldDocValues == null) {
            return null;
        }
        return new SortingNumericDocValues(oldDocValues, this.docMap);
    }
    
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        final SortedNumericDocValues oldDocValues = this.in.getSortedNumericDocValues(field);
        if (oldDocValues == null) {
            return null;
        }
        return new SortingSortedNumericDocValues(oldDocValues, this.docMap);
    }
    
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        final SortedDocValues sortedDV = this.in.getSortedDocValues(field);
        if (sortedDV == null) {
            return null;
        }
        return new SortingSortedDocValues(sortedDV, this.docMap);
    }
    
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        final SortedSetDocValues sortedSetDV = this.in.getSortedSetDocValues(field);
        if (sortedSetDV == null) {
            return null;
        }
        return new SortingSortedSetDocValues(sortedSetDV, this.docMap);
    }
    
    public Bits getDocsWithField(final String field) throws IOException {
        final Bits bits = this.in.getDocsWithField(field);
        if (bits == null || bits instanceof Bits.MatchAllBits || bits instanceof Bits.MatchNoBits) {
            return bits;
        }
        return (Bits)new SortingBits(bits, this.docMap);
    }
    
    public Fields getTermVectors(final int docID) throws IOException {
        return this.in.getTermVectors(this.docMap.newToOld(docID));
    }
    
    public String toString() {
        return "SortingLeafReader(" + this.in + ")";
    }
    
    private static class SortingFields extends FilterLeafReader.FilterFields
    {
        private final Sorter.DocMap docMap;
        private final FieldInfos infos;
        
        public SortingFields(final Fields in, final FieldInfos infos, final Sorter.DocMap docMap) {
            super(in);
            this.docMap = docMap;
            this.infos = infos;
        }
        
        public Terms terms(final String field) throws IOException {
            final Terms terms = this.in.terms(field);
            if (terms == null) {
                return null;
            }
            return (Terms)new SortingTerms(terms, this.infos.fieldInfo(field).getIndexOptions(), this.docMap);
        }
    }
    
    private static class SortingTerms extends FilterLeafReader.FilterTerms
    {
        private final Sorter.DocMap docMap;
        private final IndexOptions indexOptions;
        
        public SortingTerms(final Terms in, final IndexOptions indexOptions, final Sorter.DocMap docMap) {
            super(in);
            this.docMap = docMap;
            this.indexOptions = indexOptions;
        }
        
        public TermsEnum iterator() throws IOException {
            return (TermsEnum)new SortingTermsEnum(this.in.iterator(), this.docMap, this.indexOptions, this.hasPositions());
        }
        
        public TermsEnum intersect(final CompiledAutomaton compiled, final BytesRef startTerm) throws IOException {
            return (TermsEnum)new SortingTermsEnum(this.in.intersect(compiled, startTerm), this.docMap, this.indexOptions, this.hasPositions());
        }
    }
    
    private static class SortingTermsEnum extends FilterLeafReader.FilterTermsEnum
    {
        final Sorter.DocMap docMap;
        private final IndexOptions indexOptions;
        private final boolean hasPositions;
        
        public SortingTermsEnum(final TermsEnum in, final Sorter.DocMap docMap, final IndexOptions indexOptions, final boolean hasPositions) {
            super(in);
            this.docMap = docMap;
            this.indexOptions = indexOptions;
            this.hasPositions = hasPositions;
        }
        
        Bits newToOld(final Bits liveDocs) {
            if (liveDocs == null) {
                return null;
            }
            return (Bits)new Bits() {
                public boolean get(final int index) {
                    return liveDocs.get(SortingTermsEnum.this.docMap.oldToNew(index));
                }
                
                public int length() {
                    return liveDocs.length();
                }
            };
        }
        
        public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
            if (this.hasPositions && PostingsEnum.featureRequested(flags, (short)24)) {
                SortingPostingsEnum wrapReuse;
                PostingsEnum inReuse;
                if (reuse != null && reuse instanceof SortingPostingsEnum) {
                    wrapReuse = (SortingPostingsEnum)reuse;
                    inReuse = wrapReuse.getWrapped();
                }
                else {
                    wrapReuse = null;
                    inReuse = reuse;
                }
                final PostingsEnum inDocsAndPositions = this.in.postings(inReuse, flags);
                final boolean storeOffsets = this.indexOptions.compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
                return (PostingsEnum)new SortingPostingsEnum(this.docMap.size(), wrapReuse, inDocsAndPositions, this.docMap, storeOffsets);
            }
            PostingsEnum inReuse;
            SortingDocsEnum wrapReuse2;
            if (reuse != null && reuse instanceof SortingDocsEnum) {
                wrapReuse2 = (SortingDocsEnum)reuse;
                inReuse = wrapReuse2.getWrapped();
            }
            else {
                wrapReuse2 = null;
                inReuse = reuse;
            }
            final PostingsEnum inDocs = this.in.postings(inReuse, flags);
            final boolean withFreqs = this.indexOptions.compareTo((Enum)IndexOptions.DOCS_AND_FREQS) >= 0 && PostingsEnum.featureRequested(flags, (short)8);
            return (PostingsEnum)new SortingDocsEnum(this.docMap.size(), wrapReuse2, inDocs, withFreqs, this.docMap);
        }
    }
    
    private static class SortingBinaryDocValues extends BinaryDocValues
    {
        private final BinaryDocValues in;
        private final Sorter.DocMap docMap;
        
        SortingBinaryDocValues(final BinaryDocValues in, final Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }
        
        public BytesRef get(final int docID) {
            return this.in.get(this.docMap.newToOld(docID));
        }
    }
    
    private static class SortingNumericDocValues extends NumericDocValues
    {
        private final NumericDocValues in;
        private final Sorter.DocMap docMap;
        
        public SortingNumericDocValues(final NumericDocValues in, final Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }
        
        public long get(final int docID) {
            return this.in.get(this.docMap.newToOld(docID));
        }
    }
    
    private static class SortingSortedNumericDocValues extends SortedNumericDocValues
    {
        private final SortedNumericDocValues in;
        private final Sorter.DocMap docMap;
        
        SortingSortedNumericDocValues(final SortedNumericDocValues in, final Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }
        
        public int count() {
            return this.in.count();
        }
        
        public void setDocument(final int doc) {
            this.in.setDocument(this.docMap.newToOld(doc));
        }
        
        public long valueAt(final int index) {
            return this.in.valueAt(index);
        }
    }
    
    private static class SortingBits implements Bits
    {
        private final Bits in;
        private final Sorter.DocMap docMap;
        
        public SortingBits(final Bits in, final Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }
        
        public boolean get(final int index) {
            return this.in.get(this.docMap.newToOld(index));
        }
        
        public int length() {
            return this.in.length();
        }
    }
    
    private static class SortingSortedDocValues extends SortedDocValues
    {
        private final SortedDocValues in;
        private final Sorter.DocMap docMap;
        
        SortingSortedDocValues(final SortedDocValues in, final Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }
        
        public int getOrd(final int docID) {
            return this.in.getOrd(this.docMap.newToOld(docID));
        }
        
        public BytesRef lookupOrd(final int ord) {
            return this.in.lookupOrd(ord);
        }
        
        public int getValueCount() {
            return this.in.getValueCount();
        }
        
        public BytesRef get(final int docID) {
            return this.in.get(this.docMap.newToOld(docID));
        }
        
        public int lookupTerm(final BytesRef key) {
            return this.in.lookupTerm(key);
        }
    }
    
    private static class SortingSortedSetDocValues extends SortedSetDocValues
    {
        private final SortedSetDocValues in;
        private final Sorter.DocMap docMap;
        
        SortingSortedSetDocValues(final SortedSetDocValues in, final Sorter.DocMap docMap) {
            this.in = in;
            this.docMap = docMap;
        }
        
        public long nextOrd() {
            return this.in.nextOrd();
        }
        
        public void setDocument(final int docID) {
            this.in.setDocument(this.docMap.newToOld(docID));
        }
        
        public BytesRef lookupOrd(final long ord) {
            return this.in.lookupOrd(ord);
        }
        
        public long getValueCount() {
            return this.in.getValueCount();
        }
        
        public long lookupTerm(final BytesRef key) {
            return this.in.lookupTerm(key);
        }
    }
    
    static class SortingDocsEnum extends FilterLeafReader.FilterPostingsEnum
    {
        private final int maxDoc;
        private final DocFreqSorter sorter;
        private int[] docs;
        private int[] freqs;
        private int docIt;
        private final int upto;
        private final boolean withFreqs;
        
        SortingDocsEnum(final int maxDoc, final SortingDocsEnum reuse, final PostingsEnum in, final boolean withFreqs, final Sorter.DocMap docMap) throws IOException {
            super(in);
            this.docIt = -1;
            this.maxDoc = maxDoc;
            this.withFreqs = withFreqs;
            if (reuse != null) {
                if (reuse.maxDoc == maxDoc) {
                    this.sorter = reuse.sorter;
                }
                else {
                    this.sorter = new DocFreqSorter(maxDoc);
                }
                this.docs = reuse.docs;
                this.freqs = reuse.freqs;
            }
            else {
                this.docs = new int[64];
                this.sorter = new DocFreqSorter(maxDoc);
            }
            this.docIt = -1;
            int i = 0;
            if (withFreqs) {
                if (this.freqs == null || this.freqs.length < this.docs.length) {
                    this.freqs = new int[this.docs.length];
                }
                int doc;
                while ((doc = in.nextDoc()) != Integer.MAX_VALUE) {
                    if (i >= this.docs.length) {
                        this.docs = ArrayUtil.grow(this.docs, this.docs.length + 1);
                        this.freqs = ArrayUtil.grow(this.freqs, this.freqs.length + 1);
                    }
                    this.docs[i] = docMap.oldToNew(doc);
                    this.freqs[i] = in.freq();
                    ++i;
                }
            }
            else {
                this.freqs = null;
                int doc;
                while ((doc = in.nextDoc()) != Integer.MAX_VALUE) {
                    if (i >= this.docs.length) {
                        this.docs = ArrayUtil.grow(this.docs, this.docs.length + 1);
                    }
                    this.docs[i++] = docMap.oldToNew(doc);
                }
            }
            this.sorter.reset(this.docs, this.freqs);
            this.sorter.sort(0, i);
            this.upto = i;
        }
        
        boolean reused(final PostingsEnum other) {
            return other != null && other instanceof SortingDocsEnum && this.docs == ((SortingDocsEnum)other).docs;
        }
        
        public int advance(final int target) throws IOException {
            return this.slowAdvance(target);
        }
        
        public int docID() {
            return (this.docIt < 0) ? -1 : ((this.docIt >= this.upto) ? Integer.MAX_VALUE : this.docs[this.docIt]);
        }
        
        public int freq() throws IOException {
            return (this.withFreqs && this.docIt < this.upto) ? this.freqs[this.docIt] : 1;
        }
        
        public int nextDoc() throws IOException {
            if (++this.docIt >= this.upto) {
                return Integer.MAX_VALUE;
            }
            return this.docs[this.docIt];
        }
        
        PostingsEnum getWrapped() {
            return this.in;
        }
        
        public int nextPosition() throws IOException {
            return -1;
        }
        
        public int startOffset() throws IOException {
            return -1;
        }
        
        public int endOffset() throws IOException {
            return -1;
        }
        
        public BytesRef getPayload() throws IOException {
            return null;
        }
        
        private static final class DocFreqSorter extends TimSorter
        {
            private int[] docs;
            private int[] freqs;
            private final int[] tmpDocs;
            private int[] tmpFreqs;
            
            public DocFreqSorter(final int maxDoc) {
                super(maxDoc / 64);
                this.tmpDocs = new int[maxDoc / 64];
            }
            
            public void reset(final int[] docs, final int[] freqs) {
                this.docs = docs;
                this.freqs = freqs;
                if (freqs != null && this.tmpFreqs == null) {
                    this.tmpFreqs = new int[this.tmpDocs.length];
                }
            }
            
            protected int compare(final int i, final int j) {
                return this.docs[i] - this.docs[j];
            }
            
            protected void swap(final int i, final int j) {
                final int tmpDoc = this.docs[i];
                this.docs[i] = this.docs[j];
                this.docs[j] = tmpDoc;
                if (this.freqs != null) {
                    final int tmpFreq = this.freqs[i];
                    this.freqs[i] = this.freqs[j];
                    this.freqs[j] = tmpFreq;
                }
            }
            
            protected void copy(final int src, final int dest) {
                this.docs[dest] = this.docs[src];
                if (this.freqs != null) {
                    this.freqs[dest] = this.freqs[src];
                }
            }
            
            protected void save(final int i, final int len) {
                System.arraycopy(this.docs, i, this.tmpDocs, 0, len);
                if (this.freqs != null) {
                    System.arraycopy(this.freqs, i, this.tmpFreqs, 0, len);
                }
            }
            
            protected void restore(final int i, final int j) {
                this.docs[j] = this.tmpDocs[i];
                if (this.freqs != null) {
                    this.freqs[j] = this.tmpFreqs[i];
                }
            }
            
            protected int compareSaved(final int i, final int j) {
                return this.tmpDocs[i] - this.docs[j];
            }
        }
    }
    
    static class SortingPostingsEnum extends FilterLeafReader.FilterPostingsEnum
    {
        private final int maxDoc;
        private final DocOffsetSorter sorter;
        private int[] docs;
        private long[] offsets;
        private final int upto;
        private final IndexInput postingInput;
        private final boolean storeOffsets;
        private int docIt;
        private int pos;
        private int startOffset;
        private int endOffset;
        private final BytesRef payload;
        private int currFreq;
        private final RAMFile file;
        
        SortingPostingsEnum(final int maxDoc, final SortingPostingsEnum reuse, final PostingsEnum in, final Sorter.DocMap docMap, final boolean storeOffsets) throws IOException {
            super(in);
            this.docIt = -1;
            this.startOffset = -1;
            this.endOffset = -1;
            this.maxDoc = maxDoc;
            this.storeOffsets = storeOffsets;
            if (reuse != null) {
                this.docs = reuse.docs;
                this.offsets = reuse.offsets;
                this.payload = reuse.payload;
                this.file = reuse.file;
                if (reuse.maxDoc == maxDoc) {
                    this.sorter = reuse.sorter;
                }
                else {
                    this.sorter = new DocOffsetSorter(maxDoc);
                }
            }
            else {
                this.docs = new int[32];
                this.offsets = new long[32];
                this.payload = new BytesRef(32);
                this.file = new RAMFile();
                this.sorter = new DocOffsetSorter(maxDoc);
            }
            final IndexOutput out = (IndexOutput)new RAMOutputStream(this.file, false);
            int i = 0;
            int doc;
            while ((doc = in.nextDoc()) != Integer.MAX_VALUE) {
                if (i == this.docs.length) {
                    final int newLength = ArrayUtil.oversize(i + 1, 4);
                    this.docs = Arrays.copyOf(this.docs, newLength);
                    this.offsets = Arrays.copyOf(this.offsets, newLength);
                }
                this.docs[i] = docMap.oldToNew(doc);
                this.offsets[i] = out.getFilePointer();
                this.addPositions(in, out);
                ++i;
            }
            this.upto = i;
            this.sorter.reset(this.docs, this.offsets);
            this.sorter.sort(0, this.upto);
            out.close();
            this.postingInput = (IndexInput)new RAMInputStream("", this.file);
        }
        
        boolean reused(final PostingsEnum other) {
            return other != null && other instanceof SortingPostingsEnum && this.docs == ((SortingPostingsEnum)other).docs;
        }
        
        private void addPositions(final PostingsEnum in, final IndexOutput out) throws IOException {
            final int freq = in.freq();
            out.writeVInt(freq);
            int previousPosition = 0;
            int previousEndOffset = 0;
            for (int i = 0; i < freq; ++i) {
                final int pos = in.nextPosition();
                final BytesRef payload = in.getPayload();
                final int token = pos - previousPosition << 1 | ((payload != null) ? 1 : 0);
                out.writeVInt(token);
                previousPosition = pos;
                if (this.storeOffsets) {
                    final int startOffset = in.startOffset();
                    final int endOffset = in.endOffset();
                    out.writeVInt(startOffset - previousEndOffset);
                    out.writeVInt(endOffset - startOffset);
                    previousEndOffset = endOffset;
                }
                if (payload != null) {
                    out.writeVInt(payload.length);
                    out.writeBytes(payload.bytes, payload.offset, payload.length);
                }
            }
        }
        
        public int advance(final int target) throws IOException {
            return this.slowAdvance(target);
        }
        
        public int docID() {
            return (this.docIt < 0) ? -1 : ((this.docIt >= this.upto) ? Integer.MAX_VALUE : this.docs[this.docIt]);
        }
        
        public int endOffset() throws IOException {
            return this.endOffset;
        }
        
        public int freq() throws IOException {
            return this.currFreq;
        }
        
        public BytesRef getPayload() throws IOException {
            return (this.payload.length == 0) ? null : this.payload;
        }
        
        public int nextDoc() throws IOException {
            if (++this.docIt >= this.upto) {
                return Integer.MAX_VALUE;
            }
            this.postingInput.seek(this.offsets[this.docIt]);
            this.currFreq = this.postingInput.readVInt();
            this.pos = 0;
            this.endOffset = 0;
            return this.docs[this.docIt];
        }
        
        public int nextPosition() throws IOException {
            final int token = this.postingInput.readVInt();
            this.pos += token >>> 1;
            if (this.storeOffsets) {
                this.startOffset = this.endOffset + this.postingInput.readVInt();
                this.endOffset = this.startOffset + this.postingInput.readVInt();
            }
            if ((token & 0x1) != 0x0) {
                this.payload.offset = 0;
                this.payload.length = this.postingInput.readVInt();
                if (this.payload.length > this.payload.bytes.length) {
                    this.payload.bytes = new byte[ArrayUtil.oversize(this.payload.length, 1)];
                }
                this.postingInput.readBytes(this.payload.bytes, 0, this.payload.length);
            }
            else {
                this.payload.length = 0;
            }
            return this.pos;
        }
        
        public int startOffset() throws IOException {
            return this.startOffset;
        }
        
        PostingsEnum getWrapped() {
            return this.in;
        }
        
        private static final class DocOffsetSorter extends TimSorter
        {
            private int[] docs;
            private long[] offsets;
            private final int[] tmpDocs;
            private final long[] tmpOffsets;
            
            public DocOffsetSorter(final int maxDoc) {
                super(maxDoc / 64);
                this.tmpDocs = new int[maxDoc / 64];
                this.tmpOffsets = new long[maxDoc / 64];
            }
            
            public void reset(final int[] docs, final long[] offsets) {
                this.docs = docs;
                this.offsets = offsets;
            }
            
            protected int compare(final int i, final int j) {
                return this.docs[i] - this.docs[j];
            }
            
            protected void swap(final int i, final int j) {
                final int tmpDoc = this.docs[i];
                this.docs[i] = this.docs[j];
                this.docs[j] = tmpDoc;
                final long tmpOffset = this.offsets[i];
                this.offsets[i] = this.offsets[j];
                this.offsets[j] = tmpOffset;
            }
            
            protected void copy(final int src, final int dest) {
                this.docs[dest] = this.docs[src];
                this.offsets[dest] = this.offsets[src];
            }
            
            protected void save(final int i, final int len) {
                System.arraycopy(this.docs, i, this.tmpDocs, 0, len);
                System.arraycopy(this.offsets, i, this.tmpOffsets, 0, len);
            }
            
            protected void restore(final int i, final int j) {
                this.docs[j] = this.tmpDocs[i];
                this.offsets[j] = this.tmpOffsets[i];
            }
            
            protected int compareSaved(final int i, final int j) {
                return this.tmpDocs[i] - this.docs[j];
            }
        }
    }
}
