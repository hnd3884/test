package org.apache.lucene.index.memory;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.index.OrdTermState;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.PostingsEnum;
import java.util.Comparator;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.util.BytesRefHash;
import java.util.Map;
import org.apache.lucene.index.FieldInfo;
import java.util.Collections;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.Collection;
import java.util.Iterator;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.RecyclingIntBlockAllocator;
import org.apache.lucene.util.RecyclingByteBlockAllocator;
import org.apache.lucene.search.IndexSearcher;
import java.util.TreeMap;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.BytesRefArray;
import org.apache.lucene.util.IntBlockPool;
import org.apache.lucene.util.ByteBlockPool;
import java.util.SortedMap;

public class MemoryIndex
{
    private static final boolean DEBUG = false;
    private final SortedMap<String, Info> fields;
    private final boolean storeOffsets;
    private final boolean storePayloads;
    private final ByteBlockPool byteBlockPool;
    private final IntBlockPool intBlockPool;
    private final IntBlockPool.SliceWriter postingsWriter;
    private final BytesRefArray payloadsBytesRefs;
    private Counter bytesUsed;
    private boolean frozen;
    private Similarity normSimilarity;
    
    public MemoryIndex() {
        this(false);
    }
    
    public MemoryIndex(final boolean storeOffsets) {
        this(storeOffsets, false);
    }
    
    public MemoryIndex(final boolean storeOffsets, final boolean storePayloads) {
        this(storeOffsets, storePayloads, 0L);
    }
    
    MemoryIndex(final boolean storeOffsets, final boolean storePayloads, final long maxReusedBytes) {
        this.fields = new TreeMap<String, Info>();
        this.frozen = false;
        this.normSimilarity = IndexSearcher.getDefaultSimilarity();
        this.storeOffsets = storeOffsets;
        this.storePayloads = storePayloads;
        this.bytesUsed = Counter.newCounter();
        final int maxBufferedByteBlocks = (int)(maxReusedBytes / 2L / 32768L);
        final int maxBufferedIntBlocks = (int)((maxReusedBytes - maxBufferedByteBlocks * 32768) / 32768L);
        assert maxBufferedByteBlocks * 32768 + maxBufferedIntBlocks * 8192 * 4 <= maxReusedBytes;
        this.byteBlockPool = new ByteBlockPool((ByteBlockPool.Allocator)new RecyclingByteBlockAllocator(32768, maxBufferedByteBlocks, this.bytesUsed));
        this.intBlockPool = new IntBlockPool((IntBlockPool.Allocator)new RecyclingIntBlockAllocator(8192, maxBufferedIntBlocks, this.bytesUsed));
        this.postingsWriter = new IntBlockPool.SliceWriter(this.intBlockPool);
        this.payloadsBytesRefs = (storePayloads ? new BytesRefArray(this.bytesUsed) : null);
    }
    
    public void addField(final String fieldName, final String text, final Analyzer analyzer) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (analyzer == null) {
            throw new IllegalArgumentException("analyzer must not be null");
        }
        final TokenStream stream = analyzer.tokenStream(fieldName, text);
        this.addField(fieldName, stream, 1.0f, analyzer.getPositionIncrementGap(fieldName), analyzer.getOffsetGap(fieldName));
    }
    
    public static MemoryIndex fromDocument(final Document document, final Analyzer analyzer) {
        return fromDocument(document, analyzer, false, false, 0L);
    }
    
    public static MemoryIndex fromDocument(final Document document, final Analyzer analyzer, final boolean storeOffsets, final boolean storePayloads) {
        return fromDocument(document, analyzer, storeOffsets, storePayloads, 0L);
    }
    
    public static MemoryIndex fromDocument(final Document document, final Analyzer analyzer, final boolean storeOffsets, final boolean storePayloads, final long maxReusedBytes) {
        final MemoryIndex mi = new MemoryIndex(storeOffsets, storePayloads, maxReusedBytes);
        for (final IndexableField field : document) {
            mi.addField(field, analyzer);
        }
        return mi;
    }
    
    public <T> TokenStream keywordTokenStream(final Collection<T> keywords) {
        if (keywords == null) {
            throw new IllegalArgumentException("keywords must not be null");
        }
        return new TokenStream() {
            private Iterator<T> iter = keywords.iterator();
            private int start = 0;
            private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
            private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
            
            public boolean incrementToken() {
                if (!this.iter.hasNext()) {
                    return false;
                }
                final T obj = this.iter.next();
                if (obj == null) {
                    throw new IllegalArgumentException("keyword must not be null");
                }
                final String term = obj.toString();
                this.clearAttributes();
                this.termAtt.setEmpty().append(term);
                this.offsetAtt.setOffset(this.start, this.start + this.termAtt.length());
                this.start += term.length() + 1;
                return true;
            }
        };
    }
    
    public void addField(final String fieldName, final TokenStream stream) {
        this.addField(fieldName, stream, 1.0f);
    }
    
    public void addField(final IndexableField field, final Analyzer analyzer) {
        this.addField(field, analyzer, 1.0f);
    }
    
    public void addField(final IndexableField field, final Analyzer analyzer, final float boost) {
        if (field.fieldType().docValuesType() != DocValuesType.NONE) {
            throw new IllegalArgumentException("MemoryIndex does not support DocValues fields");
        }
        if (analyzer == null) {
            this.addField(field.name(), field.tokenStream((Analyzer)null, (TokenStream)null), boost);
        }
        else {
            this.addField(field.name(), field.tokenStream(analyzer, (TokenStream)null), boost, analyzer.getPositionIncrementGap(field.name()), analyzer.getOffsetGap(field.name()));
        }
    }
    
    public void addField(final String fieldName, final TokenStream stream, final float boost) {
        this.addField(fieldName, stream, boost, 0);
    }
    
    public void addField(final String fieldName, final TokenStream stream, final float boost, final int positionIncrementGap) {
        this.addField(fieldName, stream, boost, positionIncrementGap, 1);
    }
    
    public void addField(final String fieldName, final TokenStream tokenStream, float boost, final int positionIncrementGap, final int offsetGap) {
        try (final TokenStream stream = tokenStream) {
            if (this.frozen) {
                throw new IllegalArgumentException("Cannot call addField() when MemoryIndex is frozen");
            }
            if (fieldName == null) {
                throw new IllegalArgumentException("fieldName must not be null");
            }
            if (stream == null) {
                throw new IllegalArgumentException("token stream must not be null");
            }
            if (boost <= 0.0f) {
                throw new IllegalArgumentException("boost factor must be greater than 0.0");
            }
            int numTokens = 0;
            int numOverlapTokens = 0;
            int pos = -1;
            long sumTotalTermFreq = 0L;
            int offset = 0;
            final Info info;
            FieldInfo fieldInfo;
            BytesRefHash terms;
            SliceByteStartArray sliceArray;
            if ((info = this.fields.get(fieldName)) != null) {
                fieldInfo = info.fieldInfo;
                numTokens = info.numTokens;
                numOverlapTokens = info.numOverlapTokens;
                pos = info.lastPosition + positionIncrementGap;
                offset = info.lastOffset + offsetGap;
                terms = info.terms;
                boost *= info.boost;
                sliceArray = info.sliceArray;
                sumTotalTermFreq = info.sumTotalTermFreq;
            }
            else {
                fieldInfo = new FieldInfo(fieldName, this.fields.size(), true, false, this.storePayloads, this.storeOffsets ? IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS : IndexOptions.DOCS_AND_FREQS_AND_POSITIONS, DocValuesType.NONE, -1L, (Map)Collections.emptyMap());
                sliceArray = new SliceByteStartArray(16);
                terms = new BytesRefHash(this.byteBlockPool, 16, (BytesRefHash.BytesStartArray)sliceArray);
            }
            final TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)stream.getAttribute((Class)TermToBytesRefAttribute.class);
            final PositionIncrementAttribute posIncrAttribute = (PositionIncrementAttribute)stream.addAttribute((Class)PositionIncrementAttribute.class);
            final OffsetAttribute offsetAtt = (OffsetAttribute)stream.addAttribute((Class)OffsetAttribute.class);
            final PayloadAttribute payloadAtt = this.storePayloads ? ((PayloadAttribute)stream.addAttribute((Class)PayloadAttribute.class)) : null;
            stream.reset();
            while (stream.incrementToken()) {
                ++numTokens;
                final int posIncr = posIncrAttribute.getPositionIncrement();
                if (posIncr == 0) {
                    ++numOverlapTokens;
                }
                pos += posIncr;
                int ord = terms.add(termAtt.getBytesRef());
                if (ord < 0) {
                    ord = -ord - 1;
                    this.postingsWriter.reset(sliceArray.end[ord]);
                }
                else {
                    sliceArray.start[ord] = this.postingsWriter.startNewSlice();
                }
                final int[] freq = sliceArray.freq;
                final int n = ord;
                ++freq[n];
                ++sumTotalTermFreq;
                this.postingsWriter.writeInt(pos);
                if (this.storeOffsets) {
                    this.postingsWriter.writeInt(offsetAtt.startOffset() + offset);
                    this.postingsWriter.writeInt(offsetAtt.endOffset() + offset);
                }
                if (this.storePayloads) {
                    final BytesRef payload = payloadAtt.getPayload();
                    int pIndex;
                    if (payload == null || payload.length == 0) {
                        pIndex = -1;
                    }
                    else {
                        pIndex = this.payloadsBytesRefs.append(payload);
                    }
                    this.postingsWriter.writeInt(pIndex);
                }
                sliceArray.end[ord] = this.postingsWriter.getCurrentOffset();
            }
            stream.end();
            if (numTokens > 0) {
                this.fields.put(fieldName, new Info(fieldInfo, terms, sliceArray, numTokens, numOverlapTokens, boost, pos, offsetAtt.endOffset() + offset, sumTotalTermFreq));
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setSimilarity(final Similarity similarity) {
        if (this.frozen) {
            throw new IllegalArgumentException("Cannot set Similarity when MemoryIndex is frozen");
        }
        if (this.normSimilarity == similarity) {
            return;
        }
        this.normSimilarity = similarity;
        for (final Info info : this.fields.values()) {
            info.norms = null;
        }
    }
    
    public IndexSearcher createSearcher() {
        final MemoryIndexReader reader = new MemoryIndexReader();
        final IndexSearcher searcher = new IndexSearcher((IndexReader)reader);
        searcher.setSimilarity(this.normSimilarity);
        return searcher;
    }
    
    public void freeze() {
        this.frozen = true;
        for (final Info info : this.fields.values()) {
            info.sortTerms();
            info.getNormDocValues();
        }
    }
    
    public float search(final Query query) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        final IndexSearcher searcher = this.createSearcher();
        try {
            final float[] scores = { 0.0f };
            searcher.search(query, (Collector)new SimpleCollector() {
                private Scorer scorer;
                
                public void collect(final int doc) throws IOException {
                    scores[0] = this.scorer.score();
                }
                
                public void setScorer(final Scorer scorer) {
                    this.scorer = scorer;
                }
                
                public boolean needsScores() {
                    return true;
                }
            });
            final float score = scores[0];
            return score;
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(256);
        int sumPositions = 0;
        int sumTerms = 0;
        final BytesRef spare = new BytesRef();
        for (final Map.Entry<String, Info> entry : this.fields.entrySet()) {
            final String fieldName = entry.getKey();
            final Info info = entry.getValue();
            info.sortTerms();
            result.append(fieldName + ":\n");
            final SliceByteStartArray sliceArray = info.sliceArray;
            int numPositions = 0;
            final IntBlockPool.SliceReader postingsReader = new IntBlockPool.SliceReader(this.intBlockPool);
            for (int j = 0; j < info.terms.size(); ++j) {
                final int ord = info.sortedTerms[j];
                info.terms.get(ord, spare);
                final int freq = sliceArray.freq[ord];
                result.append("\t'" + spare + "':" + freq + ":");
                postingsReader.reset(sliceArray.start[ord], sliceArray.end[ord]);
                result.append(" [");
                final int iters = this.storeOffsets ? 3 : 1;
                while (!postingsReader.endOfSlice()) {
                    result.append("(");
                    for (int k = 0; k < iters; ++k) {
                        result.append(postingsReader.readInt());
                        if (k < iters - 1) {
                            result.append(", ");
                        }
                    }
                    result.append(")");
                    if (!postingsReader.endOfSlice()) {
                        result.append(",");
                    }
                }
                result.append("]");
                result.append("\n");
                numPositions += freq;
            }
            result.append("\tterms=" + info.terms.size());
            result.append(", positions=" + numPositions);
            result.append("\n");
            sumPositions += numPositions;
            sumTerms += info.terms.size();
        }
        result.append("\nfields=" + this.fields.size());
        result.append(", terms=" + sumTerms);
        result.append(", positions=" + sumPositions);
        return result.toString();
    }
    
    public void reset() {
        this.fields.clear();
        this.normSimilarity = IndexSearcher.getDefaultSimilarity();
        this.byteBlockPool.reset(false, false);
        this.intBlockPool.reset(true, false);
        if (this.payloadsBytesRefs != null) {
            this.payloadsBytesRefs.clear();
        }
        this.frozen = false;
    }
    
    private final class Info
    {
        private final FieldInfo fieldInfo;
        private transient NumericDocValues norms;
        private final BytesRefHash terms;
        private final SliceByteStartArray sliceArray;
        private transient int[] sortedTerms;
        private final int numTokens;
        private final int numOverlapTokens;
        private final float boost;
        private final long sumTotalTermFreq;
        private final int lastPosition;
        private final int lastOffset;
        
        public Info(final FieldInfo fieldInfo, final BytesRefHash terms, final SliceByteStartArray sliceArray, final int numTokens, final int numOverlapTokens, final float boost, final int lastPosition, final int lastOffset, final long sumTotalTermFreq) {
            this.fieldInfo = fieldInfo;
            this.terms = terms;
            this.sliceArray = sliceArray;
            this.numTokens = numTokens;
            this.numOverlapTokens = numOverlapTokens;
            this.boost = boost;
            this.sumTotalTermFreq = sumTotalTermFreq;
            this.lastPosition = lastPosition;
            this.lastOffset = lastOffset;
        }
        
        public void sortTerms() {
            if (this.sortedTerms == null) {
                this.sortedTerms = this.terms.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
            }
        }
        
        public NumericDocValues getNormDocValues() {
            if (this.norms == null) {
                final FieldInvertState invertState = new FieldInvertState(this.fieldInfo.name, this.fieldInfo.number, this.numTokens, this.numOverlapTokens, 0, this.boost);
                final long value = MemoryIndex.this.normSimilarity.computeNorm(invertState);
                this.norms = new NumericDocValues() {
                    public long get(final int docID) {
                        if (docID != 0) {
                            throw new IndexOutOfBoundsException();
                        }
                        return value;
                    }
                };
            }
            return this.norms;
        }
    }
    
    private final class MemoryIndexReader extends LeafReader
    {
        final /* synthetic */ MemoryIndex this$0;
        
        public void addCoreClosedListener(final LeafReader.CoreClosedListener listener) {
            addCoreClosedListenerAsReaderClosedListener((IndexReader)this, listener);
        }
        
        public void removeCoreClosedListener(final LeafReader.CoreClosedListener listener) {
            removeCoreClosedListenerAsReaderClosedListener((IndexReader)this, listener);
        }
        
        private Info getInfo(final String fieldName) {
            return (Info)MemoryIndex.this.fields.get(fieldName);
        }
        
        public Bits getLiveDocs() {
            return null;
        }
        
        public FieldInfos getFieldInfos() {
            final FieldInfo[] fieldInfos = new FieldInfo[MemoryIndex.this.fields.size()];
            int i = 0;
            for (final Info info : MemoryIndex.this.fields.values()) {
                fieldInfos[i++] = info.fieldInfo;
            }
            return new FieldInfos(fieldInfos);
        }
        
        public NumericDocValues getNumericDocValues(final String field) {
            return null;
        }
        
        public BinaryDocValues getBinaryDocValues(final String field) {
            return null;
        }
        
        public SortedDocValues getSortedDocValues(final String field) {
            return null;
        }
        
        public SortedNumericDocValues getSortedNumericDocValues(final String field) {
            return null;
        }
        
        public SortedSetDocValues getSortedSetDocValues(final String field) {
            return null;
        }
        
        public Bits getDocsWithField(final String field) throws IOException {
            return null;
        }
        
        public void checkIntegrity() throws IOException {
        }
        
        public Fields fields() {
            return new MemoryFields();
        }
        
        public Fields getTermVectors(final int docID) {
            if (docID == 0) {
                return this.fields();
            }
            return null;
        }
        
        public int numDocs() {
            return 1;
        }
        
        public int maxDoc() {
            return 1;
        }
        
        public void document(final int docID, final StoredFieldVisitor visitor) {
        }
        
        protected void doClose() {
        }
        
        public NumericDocValues getNormValues(final String field) {
            final Info info = (Info)MemoryIndex.this.fields.get(field);
            if (info == null) {
                return null;
            }
            return info.getNormDocValues();
        }
        
        private class MemoryFields extends Fields
        {
            public Iterator<String> iterator() {
                return MemoryIndex.this.fields.keySet().iterator();
            }
            
            public Terms terms(final String field) {
                final Info info = (Info)MemoryIndex.this.fields.get(field);
                if (info == null) {
                    return null;
                }
                return new Terms() {
                    public TermsEnum iterator() {
                        return new MemoryTermsEnum(info);
                    }
                    
                    public long size() {
                        return info.terms.size();
                    }
                    
                    public long getSumTotalTermFreq() {
                        return info.sumTotalTermFreq;
                    }
                    
                    public long getSumDocFreq() {
                        return info.terms.size();
                    }
                    
                    public int getDocCount() {
                        return (this.size() > 0L) ? 1 : 0;
                    }
                    
                    public boolean hasFreqs() {
                        return true;
                    }
                    
                    public boolean hasOffsets() {
                        return MemoryIndex.this.storeOffsets;
                    }
                    
                    public boolean hasPositions() {
                        return true;
                    }
                    
                    public boolean hasPayloads() {
                        return MemoryIndex.this.storePayloads;
                    }
                };
            }
            
            public int size() {
                return MemoryIndex.this.fields.size();
            }
        }
        
        private class MemoryTermsEnum extends TermsEnum
        {
            private final Info info;
            private final BytesRef br;
            int termUpto;
            
            public MemoryTermsEnum(final Info info) {
                this.br = new BytesRef();
                this.termUpto = -1;
                (this.info = info).sortTerms();
            }
            
            private final int binarySearch(final BytesRef b, final BytesRef bytesRef, int low, int high, final BytesRefHash hash, final int[] ords, final Comparator<BytesRef> comparator) {
                int mid = 0;
                while (low <= high) {
                    mid = low + high >>> 1;
                    hash.get(ords[mid], bytesRef);
                    final int cmp = comparator.compare(bytesRef, b);
                    if (cmp < 0) {
                        low = mid + 1;
                    }
                    else {
                        if (cmp <= 0) {
                            return mid;
                        }
                        high = mid - 1;
                    }
                }
                assert comparator.compare(bytesRef, b) != 0;
                return -(low + 1);
            }
            
            public boolean seekExact(final BytesRef text) {
                this.termUpto = this.binarySearch(text, this.br, 0, this.info.terms.size() - 1, this.info.terms, this.info.sortedTerms, BytesRef.getUTF8SortedAsUnicodeComparator());
                return this.termUpto >= 0;
            }
            
            public TermsEnum.SeekStatus seekCeil(final BytesRef text) {
                this.termUpto = this.binarySearch(text, this.br, 0, this.info.terms.size() - 1, this.info.terms, this.info.sortedTerms, BytesRef.getUTF8SortedAsUnicodeComparator());
                if (this.termUpto >= 0) {
                    return TermsEnum.SeekStatus.FOUND;
                }
                this.termUpto = -this.termUpto - 1;
                if (this.termUpto >= this.info.terms.size()) {
                    return TermsEnum.SeekStatus.END;
                }
                this.info.terms.get(this.info.sortedTerms[this.termUpto], this.br);
                return TermsEnum.SeekStatus.NOT_FOUND;
            }
            
            public void seekExact(final long ord) {
                assert ord < this.info.terms.size();
                this.termUpto = (int)ord;
                this.info.terms.get(this.info.sortedTerms[this.termUpto], this.br);
            }
            
            public BytesRef next() {
                ++this.termUpto;
                if (this.termUpto >= this.info.terms.size()) {
                    return null;
                }
                this.info.terms.get(this.info.sortedTerms[this.termUpto], this.br);
                return this.br;
            }
            
            public BytesRef term() {
                return this.br;
            }
            
            public long ord() {
                return this.termUpto;
            }
            
            public int docFreq() {
                return 1;
            }
            
            public long totalTermFreq() {
                return this.info.sliceArray.freq[this.info.sortedTerms[this.termUpto]];
            }
            
            public PostingsEnum postings(PostingsEnum reuse, final int flags) {
                if (reuse == null || !(reuse instanceof MemoryPostingsEnum)) {
                    reuse = new MemoryPostingsEnum();
                }
                final int ord = this.info.sortedTerms[this.termUpto];
                return ((MemoryPostingsEnum)reuse).reset(this.info.sliceArray.start[ord], this.info.sliceArray.end[ord], this.info.sliceArray.freq[ord]);
            }
            
            public void seekExact(final BytesRef term, final TermState state) throws IOException {
                assert state != null;
                this.seekExact(((OrdTermState)state).ord);
            }
            
            public TermState termState() throws IOException {
                final OrdTermState ts = new OrdTermState();
                ts.ord = this.termUpto;
                return (TermState)ts;
            }
        }
        
        private class MemoryPostingsEnum extends PostingsEnum
        {
            private final IntBlockPool.SliceReader sliceReader;
            private int posUpto;
            private boolean hasNext;
            private int doc;
            private int freq;
            private int pos;
            private int startOffset;
            private int endOffset;
            private int payloadIndex;
            private final BytesRefBuilder payloadBuilder;
            
            public MemoryPostingsEnum() {
                this.doc = -1;
                this.sliceReader = new IntBlockPool.SliceReader(MemoryIndexReader.this.this$0.intBlockPool);
                this.payloadBuilder = (MemoryIndexReader.this.this$0.storePayloads ? new BytesRefBuilder() : null);
            }
            
            public PostingsEnum reset(final int start, final int end, final int freq) {
                this.sliceReader.reset(start, end);
                this.posUpto = 0;
                this.hasNext = true;
                this.doc = -1;
                this.freq = freq;
                return this;
            }
            
            public int docID() {
                return this.doc;
            }
            
            public int nextDoc() {
                this.pos = -1;
                if (this.hasNext) {
                    this.hasNext = false;
                    return this.doc = 0;
                }
                return this.doc = Integer.MAX_VALUE;
            }
            
            public int advance(final int target) throws IOException {
                return this.slowAdvance(target);
            }
            
            public int freq() throws IOException {
                return this.freq;
            }
            
            public int nextPosition() {
                ++this.posUpto;
                assert this.posUpto <= this.freq;
                assert !this.sliceReader.endOfSlice() : " stores offsets : " + this.startOffset;
                final int pos = this.sliceReader.readInt();
                if (MemoryIndex.this.storeOffsets) {
                    this.startOffset = this.sliceReader.readInt();
                    this.endOffset = this.sliceReader.readInt();
                }
                if (MemoryIndex.this.storePayloads) {
                    this.payloadIndex = this.sliceReader.readInt();
                }
                return pos;
            }
            
            public int startOffset() {
                return this.startOffset;
            }
            
            public int endOffset() {
                return this.endOffset;
            }
            
            public BytesRef getPayload() {
                if (this.payloadBuilder == null || this.payloadIndex == -1) {
                    return null;
                }
                return MemoryIndex.this.payloadsBytesRefs.get(this.payloadBuilder, this.payloadIndex);
            }
            
            public long cost() {
                return 1L;
            }
        }
    }
    
    private static final class SliceByteStartArray extends BytesRefHash.DirectBytesStartArray
    {
        int[] start;
        int[] end;
        int[] freq;
        
        public SliceByteStartArray(final int initSize) {
            super(initSize);
        }
        
        public int[] init() {
            final int[] ord = super.init();
            this.start = new int[ArrayUtil.oversize(ord.length, 4)];
            this.end = new int[ArrayUtil.oversize(ord.length, 4)];
            this.freq = new int[ArrayUtil.oversize(ord.length, 4)];
            assert this.start.length >= ord.length;
            assert this.end.length >= ord.length;
            assert this.freq.length >= ord.length;
            return ord;
        }
        
        public int[] grow() {
            final int[] ord = super.grow();
            if (this.start.length < ord.length) {
                this.start = ArrayUtil.grow(this.start, ord.length);
                this.end = ArrayUtil.grow(this.end, ord.length);
                this.freq = ArrayUtil.grow(this.freq, ord.length);
            }
            assert this.start.length >= ord.length;
            assert this.end.length >= ord.length;
            assert this.freq.length >= ord.length;
            return ord;
        }
        
        public int[] clear() {
            final int[] array = null;
            this.end = array;
            this.start = array;
            return super.clear();
        }
    }
}
