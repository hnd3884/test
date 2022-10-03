package org.apache.lucene.uninverting;

import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.packed.PackedLongValues;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.packed.PackedInts;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.GrowableWriter;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import java.util.WeakHashMap;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.Bits;
import java.util.List;
import org.apache.lucene.util.Accountable;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import java.util.HashMap;
import java.io.PrintStream;
import org.apache.lucene.index.LeafReader;
import java.util.Map;

class FieldCacheImpl implements FieldCache
{
    private Map<Class<?>, Cache> caches;
    final LeafReader.CoreClosedListener purgeCore;
    private volatile PrintStream infoStream;
    
    FieldCacheImpl() {
        this.purgeCore = (LeafReader.CoreClosedListener)new LeafReader.CoreClosedListener() {
            public void onClose(final Object ownerCoreCacheKey) {
                FieldCacheImpl.this.purgeByCacheKey(ownerCoreCacheKey);
            }
        };
        this.init();
    }
    
    private synchronized void init() {
        (this.caches = new HashMap<Class<?>, Cache>(6)).put(Long.TYPE, new LongCache(this));
        this.caches.put(BinaryDocValues.class, new BinaryDocValuesCache(this));
        this.caches.put(SortedDocValues.class, new SortedDocValuesCache(this));
        this.caches.put(DocTermOrds.class, new DocTermOrdsCache(this));
        this.caches.put(DocsWithFieldCache.class, new DocsWithFieldCache(this));
    }
    
    @Override
    public synchronized void purgeAllCaches() {
        this.init();
    }
    
    @Override
    public synchronized void purgeByCacheKey(final Object coreCacheKey) {
        for (final Cache c : this.caches.values()) {
            c.purgeByCacheKey(coreCacheKey);
        }
    }
    
    @Override
    public synchronized CacheEntry[] getCacheEntries() {
        final List<CacheEntry> result = new ArrayList<CacheEntry>(17);
        for (final Map.Entry<Class<?>, Cache> cacheEntry : this.caches.entrySet()) {
            final Cache cache = cacheEntry.getValue();
            final Class<?> cacheType = cacheEntry.getKey();
            synchronized (cache.readerCache) {
                for (final Map.Entry<Object, Map<CacheKey, Accountable>> readerCacheEntry : cache.readerCache.entrySet()) {
                    final Object readerKey = readerCacheEntry.getKey();
                    if (readerKey == null) {
                        continue;
                    }
                    final Map<CacheKey, Accountable> innerCache = readerCacheEntry.getValue();
                    for (final Map.Entry<CacheKey, Accountable> mapEntry : innerCache.entrySet()) {
                        final CacheKey entry = mapEntry.getKey();
                        result.add(new CacheEntry(readerKey, entry.field, cacheType, entry.custom, mapEntry.getValue()));
                    }
                }
            }
        }
        return result.toArray(new CacheEntry[result.size()]);
    }
    
    private void initReader(final LeafReader reader) {
        reader.addCoreClosedListener(this.purgeCore);
    }
    
    void setDocsWithField(final LeafReader reader, final String field, final Bits docsWithField) {
        final int maxDoc = reader.maxDoc();
        Bits bits;
        if (docsWithField == null) {
            bits = (Bits)new Bits.MatchNoBits(maxDoc);
        }
        else if (docsWithField instanceof FixedBitSet) {
            final int numSet = ((FixedBitSet)docsWithField).cardinality();
            if (numSet >= maxDoc) {
                assert numSet == maxDoc;
                bits = (Bits)new Bits.MatchAllBits(maxDoc);
            }
            else {
                bits = docsWithField;
            }
        }
        else {
            bits = docsWithField;
        }
        this.caches.get(DocsWithFieldCache.class).put(reader, new CacheKey(field, null), (Accountable)new BitsEntry(bits));
    }
    
    @Override
    public Bits getDocsWithField(final LeafReader reader, final String field) throws IOException {
        final FieldInfo fieldInfo = reader.getFieldInfos().fieldInfo(field);
        if (fieldInfo == null) {
            return (Bits)new Bits.MatchNoBits(reader.maxDoc());
        }
        if (fieldInfo.getDocValuesType() != DocValuesType.NONE) {
            return reader.getDocsWithField(field);
        }
        if (fieldInfo.getIndexOptions() == IndexOptions.NONE) {
            return (Bits)new Bits.MatchNoBits(reader.maxDoc());
        }
        final BitsEntry bitsEntry = (BitsEntry)this.caches.get(DocsWithFieldCache.class).get(reader, new CacheKey(field, null), false);
        return bitsEntry.bits;
    }
    
    @Override
    public NumericDocValues getNumerics(final LeafReader reader, final String field, final Parser parser, final boolean setDocsWithField) throws IOException {
        if (parser == null) {
            throw new NullPointerException();
        }
        final NumericDocValues valuesIn = reader.getNumericDocValues(field);
        if (valuesIn != null) {
            return valuesIn;
        }
        final FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return DocValues.emptyNumeric();
        }
        if (info.getDocValuesType() != DocValuesType.NONE) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + info.getDocValuesType());
        }
        if (info.getIndexOptions() == IndexOptions.NONE) {
            return DocValues.emptyNumeric();
        }
        return (NumericDocValues)this.caches.get(Long.TYPE).get(reader, new CacheKey(field, parser), setDocsWithField);
    }
    
    @Override
    public SortedDocValues getTermsIndex(final LeafReader reader, final String field) throws IOException {
        return this.getTermsIndex(reader, field, 0.5f);
    }
    
    @Override
    public SortedDocValues getTermsIndex(final LeafReader reader, final String field, final float acceptableOverheadRatio) throws IOException {
        final SortedDocValues valuesIn = reader.getSortedDocValues(field);
        if (valuesIn != null) {
            return valuesIn;
        }
        final FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return DocValues.emptySorted();
        }
        if (info.getDocValuesType() != DocValuesType.NONE) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + info.getDocValuesType());
        }
        if (info.getIndexOptions() == IndexOptions.NONE) {
            return DocValues.emptySorted();
        }
        final SortedDocValuesImpl impl = (SortedDocValuesImpl)this.caches.get(SortedDocValues.class).get(reader, new CacheKey(field, acceptableOverheadRatio), false);
        return impl.iterator();
    }
    
    @Override
    public BinaryDocValues getTerms(final LeafReader reader, final String field, final boolean setDocsWithField) throws IOException {
        return this.getTerms(reader, field, setDocsWithField, 0.5f);
    }
    
    @Override
    public BinaryDocValues getTerms(final LeafReader reader, final String field, final boolean setDocsWithField, final float acceptableOverheadRatio) throws IOException {
        BinaryDocValues valuesIn = reader.getBinaryDocValues(field);
        if (valuesIn == null) {
            valuesIn = (BinaryDocValues)reader.getSortedDocValues(field);
        }
        if (valuesIn != null) {
            return valuesIn;
        }
        final FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return DocValues.emptyBinary();
        }
        if (info.getDocValuesType() != DocValuesType.NONE) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + info.getDocValuesType());
        }
        if (info.getIndexOptions() == IndexOptions.NONE) {
            return DocValues.emptyBinary();
        }
        final BinaryDocValuesImpl impl = (BinaryDocValuesImpl)this.caches.get(BinaryDocValues.class).get(reader, new CacheKey(field, acceptableOverheadRatio), setDocsWithField);
        return impl.iterator();
    }
    
    @Override
    public SortedSetDocValues getDocTermOrds(final LeafReader reader, final String field, final BytesRef prefix) throws IOException {
        assert prefix == FieldCacheImpl.INT64_TERM_PREFIX;
        final SortedSetDocValues dv = reader.getSortedSetDocValues(field);
        if (dv != null) {
            return dv;
        }
        final SortedDocValues sdv = reader.getSortedDocValues(field);
        if (sdv != null) {
            return (SortedSetDocValues)DocValues.singleton(sdv);
        }
        final FieldInfo info = reader.getFieldInfos().fieldInfo(field);
        if (info == null) {
            return (SortedSetDocValues)DocValues.emptySortedSet();
        }
        if (info.getDocValuesType() != DocValuesType.NONE) {
            throw new IllegalStateException("Type mismatch: " + field + " was indexed as " + info.getDocValuesType());
        }
        if (info.getIndexOptions() == IndexOptions.NONE) {
            return (SortedSetDocValues)DocValues.emptySortedSet();
        }
        final Terms terms = reader.terms(field);
        if (terms == null) {
            return (SortedSetDocValues)DocValues.emptySortedSet();
        }
        final long numPostings = terms.getSumDocFreq();
        if (numPostings != -1L && numPostings == terms.getDocCount()) {
            return (SortedSetDocValues)DocValues.singleton(this.getTermsIndex(reader, field));
        }
        final DocTermOrds dto = (DocTermOrds)this.caches.get(DocTermOrds.class).get(reader, new CacheKey(field, prefix), false);
        return dto.iterator(reader);
    }
    
    @Override
    public void setInfoStream(final PrintStream stream) {
        this.infoStream = stream;
    }
    
    @Override
    public PrintStream getInfoStream() {
        return this.infoStream;
    }
    
    abstract static class Cache
    {
        final FieldCacheImpl wrapper;
        final Map<Object, Map<CacheKey, Accountable>> readerCache;
        
        Cache(final FieldCacheImpl wrapper) {
            this.readerCache = new WeakHashMap<Object, Map<CacheKey, Accountable>>();
            this.wrapper = wrapper;
        }
        
        protected abstract Accountable createValue(final LeafReader p0, final CacheKey p1, final boolean p2) throws IOException;
        
        public void purgeByCacheKey(final Object coreCacheKey) {
            synchronized (this.readerCache) {
                this.readerCache.remove(coreCacheKey);
            }
        }
        
        public void put(final LeafReader reader, final CacheKey key, final Accountable value) {
            final Object readerKey = reader.getCoreCacheKey();
            synchronized (this.readerCache) {
                Map<CacheKey, Accountable> innerCache = this.readerCache.get(readerKey);
                if (innerCache == null) {
                    innerCache = new HashMap<CacheKey, Accountable>();
                    this.readerCache.put(readerKey, innerCache);
                    this.wrapper.initReader(reader);
                }
                if (innerCache.get(key) == null) {
                    innerCache.put(key, value);
                }
            }
        }
        
        public Object get(final LeafReader reader, final CacheKey key, final boolean setDocsWithField) throws IOException {
            final Object readerKey = reader.getCoreCacheKey();
            Map<CacheKey, Accountable> innerCache;
            Accountable value;
            synchronized (this.readerCache) {
                innerCache = this.readerCache.get(readerKey);
                if (innerCache == null) {
                    innerCache = new HashMap<CacheKey, Accountable>();
                    this.readerCache.put(readerKey, innerCache);
                    this.wrapper.initReader(reader);
                    value = null;
                }
                else {
                    value = innerCache.get(key);
                }
                if (value == null) {
                    value = (Accountable)new CreationPlaceholder();
                    innerCache.put(key, value);
                }
            }
            if (value instanceof CreationPlaceholder) {
                synchronized (value) {
                    final CreationPlaceholder progress = (CreationPlaceholder)value;
                    if (progress.value == null) {
                        progress.value = this.createValue(reader, key, setDocsWithField);
                        synchronized (this.readerCache) {
                            innerCache.put(key, progress.value);
                        }
                        if (key.custom != null && this.wrapper != null) {
                            final PrintStream infoStream = this.wrapper.getInfoStream();
                            if (infoStream != null) {
                                this.printNewInsanity(infoStream, progress.value);
                            }
                        }
                    }
                    return progress.value;
                }
            }
            return value;
        }
        
        private void printNewInsanity(final PrintStream infoStream, final Object value) {
            final FieldCacheSanityChecker.Insanity[] insanities = FieldCacheSanityChecker.checkSanity(this.wrapper);
            for (int i = 0; i < insanities.length; ++i) {
                final FieldCacheSanityChecker.Insanity insanity = insanities[i];
                final CacheEntry[] entries = insanity.getCacheEntries();
                for (int j = 0; j < entries.length; ++j) {
                    if (entries[j].getValue() == value) {
                        infoStream.println("WARNING: new FieldCache insanity created\nDetails: " + insanity.toString());
                        infoStream.println("\nStack:\n");
                        new Throwable().printStackTrace(infoStream);
                        break;
                    }
                }
            }
        }
    }
    
    static class CacheKey
    {
        final String field;
        final Object custom;
        
        CacheKey(final String field, final Object custom) {
            this.field = field;
            this.custom = custom;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof CacheKey) {
                final CacheKey other = (CacheKey)o;
                if (other.field.equals(this.field)) {
                    if (other.custom == null) {
                        if (this.custom == null) {
                            return true;
                        }
                    }
                    else if (other.custom.equals(this.custom)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.field.hashCode() ^ ((this.custom == null) ? 0 : this.custom.hashCode());
        }
    }
    
    private abstract static class Uninvert
    {
        public Bits docsWithField;
        
        public void uninvert(final LeafReader reader, final String field, boolean setDocsWithField) throws IOException {
            final int maxDoc = reader.maxDoc();
            final Terms terms = reader.terms(field);
            if (terms != null) {
                if (setDocsWithField) {
                    final int termsDocCount = terms.getDocCount();
                    assert termsDocCount <= maxDoc;
                    if (termsDocCount == maxDoc) {
                        this.docsWithField = (Bits)new Bits.MatchAllBits(maxDoc);
                        setDocsWithField = false;
                    }
                }
                final TermsEnum termsEnum = this.termsEnum(terms);
                PostingsEnum docs = null;
                FixedBitSet docsWithField = null;
                while (true) {
                    final BytesRef term = termsEnum.next();
                    if (term == null) {
                        break;
                    }
                    this.visitTerm(term);
                    docs = termsEnum.postings(docs, 0);
                    while (true) {
                        final int docID = docs.nextDoc();
                        if (docID == Integer.MAX_VALUE) {
                            break;
                        }
                        this.visitDoc(docID);
                        if (!setDocsWithField) {
                            continue;
                        }
                        if (docsWithField == null) {
                            docsWithField = (FixedBitSet)(this.docsWithField = (Bits)new FixedBitSet(maxDoc));
                        }
                        docsWithField.set(docID);
                    }
                }
            }
        }
        
        protected abstract TermsEnum termsEnum(final Terms p0) throws IOException;
        
        protected abstract void visitTerm(final BytesRef p0);
        
        protected abstract void visitDoc(final int p0);
    }
    
    private static class HoldsOneThing<T>
    {
        private T it;
        
        public void set(final T it) {
            this.it = it;
        }
        
        public T get() {
            return this.it;
        }
    }
    
    private static class GrowableWriterAndMinValue
    {
        public GrowableWriter writer;
        public long minValue;
        
        GrowableWriterAndMinValue(final GrowableWriter array, final long minValue) {
            this.writer = array;
            this.minValue = minValue;
        }
    }
    
    static class BitsEntry implements Accountable
    {
        final Bits bits;
        
        BitsEntry(final Bits bits) {
            this.bits = bits;
        }
        
        public long ramBytesUsed() {
            final long base = RamUsageEstimator.NUM_BYTES_OBJECT_REF;
            if (this.bits instanceof Bits.MatchAllBits || this.bits instanceof Bits.MatchNoBits) {
                return base;
            }
            return base + (this.bits.length() >>> 3);
        }
        
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
    
    static final class DocsWithFieldCache extends Cache
    {
        DocsWithFieldCache(final FieldCacheImpl wrapper) {
            super(wrapper);
        }
        
        protected BitsEntry createValue(final LeafReader reader, final CacheKey key, final boolean setDocsWithField) throws IOException {
            final String field = key.field;
            final int maxDoc = reader.maxDoc();
            FixedBitSet res = null;
            final Terms terms = reader.terms(field);
            if (terms != null) {
                final int termsDocCount = terms.getDocCount();
                assert termsDocCount <= maxDoc;
                if (termsDocCount == maxDoc) {
                    return new BitsEntry((Bits)new Bits.MatchAllBits(maxDoc));
                }
                final TermsEnum termsEnum = terms.iterator();
                PostingsEnum docs = null;
                while (true) {
                    final BytesRef term = termsEnum.next();
                    if (term == null) {
                        break;
                    }
                    if (res == null) {
                        res = new FixedBitSet(maxDoc);
                    }
                    docs = termsEnum.postings(docs, 0);
                    while (true) {
                        final int docID = docs.nextDoc();
                        if (docID == Integer.MAX_VALUE) {
                            break;
                        }
                        res.set(docID);
                    }
                }
            }
            if (res == null) {
                return new BitsEntry((Bits)new Bits.MatchNoBits(maxDoc));
            }
            final int numSet = res.cardinality();
            if (numSet < maxDoc) {
                return new BitsEntry((Bits)res);
            }
            assert numSet == maxDoc;
            return new BitsEntry((Bits)new Bits.MatchAllBits(maxDoc));
        }
    }
    
    static class LongsFromArray extends NumericDocValues implements Accountable
    {
        private final PackedInts.Reader values;
        private final long minValue;
        
        public LongsFromArray(final PackedInts.Reader values, final long minValue) {
            this.values = values;
            this.minValue = minValue;
        }
        
        public long get(final int docID) {
            return this.minValue + this.values.get(docID);
        }
        
        public long ramBytesUsed() {
            return this.values.ramBytesUsed() + RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8L;
        }
        
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
    
    static final class LongCache extends Cache
    {
        LongCache(final FieldCacheImpl wrapper) {
            super(wrapper);
        }
        
        @Override
        protected Accountable createValue(final LeafReader reader, final CacheKey key, final boolean setDocsWithField) throws IOException {
            final Parser parser = (Parser)key.custom;
            final HoldsOneThing<GrowableWriterAndMinValue> valuesRef = new HoldsOneThing<GrowableWriterAndMinValue>();
            final Uninvert u = new Uninvert() {
                private long minValue;
                private long currentValue;
                private GrowableWriter values;
                
                public void visitTerm(final BytesRef term) {
                    this.currentValue = parser.parseValue(term);
                    if (this.values == null) {
                        int startBitsPerValue;
                        if (this.currentValue < 0L) {
                            this.minValue = this.currentValue;
                            startBitsPerValue = ((this.minValue == Long.MIN_VALUE) ? 64 : PackedInts.bitsRequired(-this.minValue));
                        }
                        else {
                            this.minValue = 0L;
                            startBitsPerValue = PackedInts.bitsRequired(this.currentValue);
                        }
                        this.values = new GrowableWriter(startBitsPerValue, reader.maxDoc(), 0.5f);
                        if (this.minValue != 0L) {
                            this.values.fill(0, this.values.size(), -this.minValue);
                        }
                        valuesRef.set(new GrowableWriterAndMinValue(this.values, this.minValue));
                    }
                }
                
                public void visitDoc(final int docID) {
                    this.values.set(docID, this.currentValue - this.minValue);
                }
                
                @Override
                protected TermsEnum termsEnum(final Terms terms) throws IOException {
                    return parser.termsEnum(terms);
                }
            };
            u.uninvert(reader, key.field, setDocsWithField);
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, u.docsWithField);
            }
            final GrowableWriterAndMinValue values = valuesRef.get();
            if (values == null) {
                return (Accountable)new LongsFromArray((PackedInts.Reader)new PackedInts.NullReader(reader.maxDoc()), 0L);
            }
            return (Accountable)new LongsFromArray((PackedInts.Reader)values.writer.getMutable(), values.minValue);
        }
    }
    
    public static class SortedDocValuesImpl implements Accountable
    {
        private final PagedBytes.Reader bytes;
        private final PackedLongValues termOrdToBytesOffset;
        private final PackedInts.Reader docToTermOrd;
        private final int numOrd;
        
        public SortedDocValuesImpl(final PagedBytes.Reader bytes, final PackedLongValues termOrdToBytesOffset, final PackedInts.Reader docToTermOrd, final int numOrd) {
            this.bytes = bytes;
            this.docToTermOrd = docToTermOrd;
            this.termOrdToBytesOffset = termOrdToBytesOffset;
            this.numOrd = numOrd;
        }
        
        public SortedDocValues iterator() {
            final BytesRef term = new BytesRef();
            return new SortedDocValues() {
                public int getValueCount() {
                    return SortedDocValuesImpl.this.numOrd;
                }
                
                public int getOrd(final int docID) {
                    return (int)SortedDocValuesImpl.this.docToTermOrd.get(docID) - 1;
                }
                
                public BytesRef lookupOrd(final int ord) {
                    if (ord < 0) {
                        throw new IllegalArgumentException("ord must be >=0 (got ord=" + ord + ")");
                    }
                    SortedDocValuesImpl.this.bytes.fill(term, SortedDocValuesImpl.this.termOrdToBytesOffset.get(ord));
                    return term;
                }
            };
        }
        
        public long ramBytesUsed() {
            return this.bytes.ramBytesUsed() + this.termOrdToBytesOffset.ramBytesUsed() + this.docToTermOrd.ramBytesUsed() + 3 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 4L;
        }
        
        public Collection<Accountable> getChildResources() {
            final List<Accountable> resources = new ArrayList<Accountable>(3);
            resources.add(Accountables.namedAccountable("term bytes", (Accountable)this.bytes));
            resources.add(Accountables.namedAccountable("ord -> term", (Accountable)this.termOrdToBytesOffset));
            resources.add(Accountables.namedAccountable("doc -> ord", (Accountable)this.docToTermOrd));
            return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
        }
    }
    
    static class SortedDocValuesCache extends Cache
    {
        SortedDocValuesCache(final FieldCacheImpl wrapper) {
            super(wrapper);
        }
        
        @Override
        protected Accountable createValue(final LeafReader reader, final CacheKey key, final boolean setDocsWithField) throws IOException {
            final int maxDoc = reader.maxDoc();
            final Terms terms = reader.terms(key.field);
            final float acceptableOverheadRatio = (float)key.custom;
            final PagedBytes bytes = new PagedBytes(15);
            int startTermsBPV;
            if (terms != null) {
                final long numUniqueTerms = terms.size();
                if (numUniqueTerms != -1L) {
                    if (numUniqueTerms > maxDoc) {
                        throw new IllegalStateException("Type mismatch: " + key.field + " was indexed with multiple values per document, use SORTED_SET instead");
                    }
                    startTermsBPV = PackedInts.bitsRequired(numUniqueTerms);
                }
                else {
                    startTermsBPV = 1;
                }
            }
            else {
                startTermsBPV = 1;
            }
            final PackedLongValues.Builder termOrdToBytesOffset = PackedLongValues.monotonicBuilder(0.0f);
            final GrowableWriter docToTermOrd = new GrowableWriter(startTermsBPV, maxDoc, acceptableOverheadRatio);
            int termOrd = 0;
            if (terms != null) {
                final TermsEnum termsEnum = terms.iterator();
                PostingsEnum docs = null;
                while (true) {
                    final BytesRef term = termsEnum.next();
                    if (term == null) {
                        break;
                    }
                    if (termOrd >= maxDoc) {
                        throw new IllegalStateException("Type mismatch: " + key.field + " was indexed with multiple values per document, use SORTED_SET instead");
                    }
                    termOrdToBytesOffset.add(bytes.copyUsingLengthPrefix(term));
                    docs = termsEnum.postings(docs, 0);
                    while (true) {
                        final int docID = docs.nextDoc();
                        if (docID == Integer.MAX_VALUE) {
                            break;
                        }
                        docToTermOrd.set(docID, (long)(1 + termOrd));
                    }
                    ++termOrd;
                }
            }
            return (Accountable)new SortedDocValuesImpl(bytes.freeze(true), termOrdToBytesOffset.build(), (PackedInts.Reader)docToTermOrd.getMutable(), termOrd);
        }
    }
    
    private static class BinaryDocValuesImpl implements Accountable
    {
        private final PagedBytes.Reader bytes;
        private final PackedInts.Reader docToOffset;
        
        public BinaryDocValuesImpl(final PagedBytes.Reader bytes, final PackedInts.Reader docToOffset) {
            this.bytes = bytes;
            this.docToOffset = docToOffset;
        }
        
        public BinaryDocValues iterator() {
            final BytesRef term = new BytesRef();
            return new BinaryDocValues() {
                public BytesRef get(final int docID) {
                    final long pointer = BinaryDocValuesImpl.this.docToOffset.get(docID);
                    if (pointer == 0L) {
                        term.length = 0;
                    }
                    else {
                        BinaryDocValuesImpl.this.bytes.fill(term, pointer);
                    }
                    return term;
                }
            };
        }
        
        public long ramBytesUsed() {
            return this.bytes.ramBytesUsed() + this.docToOffset.ramBytesUsed() + 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        }
        
        public Collection<Accountable> getChildResources() {
            final List<Accountable> resources = new ArrayList<Accountable>(2);
            resources.add(Accountables.namedAccountable("term bytes", (Accountable)this.bytes));
            resources.add(Accountables.namedAccountable("addresses", (Accountable)this.docToOffset));
            return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
        }
    }
    
    static final class BinaryDocValuesCache extends Cache
    {
        BinaryDocValuesCache(final FieldCacheImpl wrapper) {
            super(wrapper);
        }
        
        @Override
        protected Accountable createValue(final LeafReader reader, final CacheKey key, final boolean setDocsWithField) throws IOException {
            final int maxDoc = reader.maxDoc();
            final Terms terms = reader.terms(key.field);
            final float acceptableOverheadRatio = (float)key.custom;
            final int termCountHardLimit = maxDoc;
            final PagedBytes bytes = new PagedBytes(15);
            int startBPV;
            if (terms != null) {
                long numUniqueTerms = terms.size();
                if (numUniqueTerms != -1L) {
                    if (numUniqueTerms > termCountHardLimit) {
                        numUniqueTerms = termCountHardLimit;
                    }
                    startBPV = PackedInts.bitsRequired(numUniqueTerms * 4L);
                }
                else {
                    startBPV = 1;
                }
            }
            else {
                startBPV = 1;
            }
            final GrowableWriter docToOffset = new GrowableWriter(startBPV, maxDoc, acceptableOverheadRatio);
            bytes.copyUsingLengthPrefix(new BytesRef());
            if (terms != null) {
                int termCount = 0;
                final TermsEnum termsEnum = terms.iterator();
                PostingsEnum docs = null;
                while (termCount++ != termCountHardLimit) {
                    final BytesRef term = termsEnum.next();
                    if (term == null) {
                        break;
                    }
                    final long pointer = bytes.copyUsingLengthPrefix(term);
                    docs = termsEnum.postings(docs, 0);
                    while (true) {
                        final int docID = docs.nextDoc();
                        if (docID == Integer.MAX_VALUE) {
                            break;
                        }
                        docToOffset.set(docID, pointer);
                    }
                }
            }
            final PackedInts.Reader offsetReader = (PackedInts.Reader)docToOffset.getMutable();
            if (setDocsWithField) {
                this.wrapper.setDocsWithField(reader, key.field, (Bits)new Bits() {
                    public boolean get(final int index) {
                        return offsetReader.get(index) != 0L;
                    }
                    
                    public int length() {
                        return maxDoc;
                    }
                });
            }
            return (Accountable)new BinaryDocValuesImpl(bytes.freeze(true), offsetReader);
        }
    }
    
    static final class DocTermOrdsCache extends Cache
    {
        DocTermOrdsCache(final FieldCacheImpl wrapper) {
            super(wrapper);
        }
        
        @Override
        protected Accountable createValue(final LeafReader reader, final CacheKey key, final boolean setDocsWithField) throws IOException {
            final BytesRef prefix = (BytesRef)key.custom;
            return (Accountable)new DocTermOrds(reader, null, key.field, prefix);
        }
    }
}
