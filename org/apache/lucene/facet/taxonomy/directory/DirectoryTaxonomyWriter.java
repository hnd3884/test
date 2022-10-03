package org.apache.lucene.facet.taxonomy.directory;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.DataOutputStream;
import java.nio.file.Path;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.IndexReader;
import java.util.HashMap;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.writercache.Cl2oTaxonomyWriterCache;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.facet.taxonomy.FacetLabel;
import org.apache.lucene.document.StringField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.index.IndexWriterConfig;
import java.io.IOException;
import org.apache.lucene.index.SegmentInfos;
import java.util.Map;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.document.Field;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.facet.taxonomy.writercache.TaxonomyWriterCache;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;

public class DirectoryTaxonomyWriter implements TaxonomyWriter
{
    public static final String INDEX_EPOCH = "index.epoch";
    private final Directory dir;
    private final IndexWriter indexWriter;
    private final TaxonomyWriterCache cache;
    private final AtomicInteger cacheMisses;
    private long indexEpoch;
    private SinglePositionTokenStream parentStream;
    private Field parentStreamField;
    private Field fullPathField;
    private int cacheMissesUntilFill;
    private boolean shouldFillCache;
    private ReaderManager readerManager;
    private volatile boolean initializedReaderManager;
    private volatile boolean shouldRefreshReaderManager;
    private volatile boolean cacheIsComplete;
    private volatile boolean isClosed;
    private volatile TaxonomyIndexArrays taxoArrays;
    private volatile int nextID;
    
    private static Map<String, String> readCommitData(final Directory dir) throws IOException {
        final SegmentInfos infos = SegmentInfos.readLatestCommit(dir);
        return infos.getUserData();
    }
    
    public DirectoryTaxonomyWriter(final Directory directory, IndexWriterConfig.OpenMode openMode, TaxonomyWriterCache cache) throws IOException {
        this.cacheMisses = new AtomicInteger(0);
        this.parentStream = new SinglePositionTokenStream("p");
        this.cacheMissesUntilFill = 11;
        this.shouldFillCache = true;
        this.initializedReaderManager = false;
        this.isClosed = false;
        this.dir = directory;
        final IndexWriterConfig config = this.createIndexWriterConfig(openMode);
        this.indexWriter = this.openIndexWriter(this.dir, config);
        assert !(this.indexWriter.getConfig().getMergePolicy() instanceof TieredMergePolicy) : "for preserving category docids, merging none-adjacent segments is not allowed";
        openMode = config.getOpenMode();
        if (!DirectoryReader.indexExists(directory)) {
            this.indexEpoch = 1L;
        }
        else {
            String epochStr = null;
            final Map<String, String> commitData = readCommitData(directory);
            if (commitData != null) {
                epochStr = commitData.get("index.epoch");
            }
            this.indexEpoch = ((epochStr == null) ? 1L : Long.parseLong(epochStr, 16));
        }
        if (openMode == IndexWriterConfig.OpenMode.CREATE) {
            ++this.indexEpoch;
        }
        final FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
        ft.setOmitNorms(true);
        this.parentStreamField = new Field("$payloads$", (TokenStream)this.parentStream, ft);
        this.fullPathField = (Field)new StringField("$full_path$", "", Field.Store.YES);
        this.nextID = this.indexWriter.maxDoc();
        if (cache == null) {
            cache = defaultTaxonomyWriterCache();
        }
        this.cache = cache;
        if (this.nextID == 0) {
            this.cacheIsComplete = true;
            this.addCategory(new FacetLabel(new String[0]));
        }
        else {
            this.cacheIsComplete = false;
        }
    }
    
    protected IndexWriter openIndexWriter(final Directory directory, final IndexWriterConfig config) throws IOException {
        return new IndexWriter(directory, config);
    }
    
    protected IndexWriterConfig createIndexWriterConfig(final IndexWriterConfig.OpenMode openMode) {
        return new IndexWriterConfig((Analyzer)null).setOpenMode(openMode).setMergePolicy((MergePolicy)new LogByteSizeMergePolicy());
    }
    
    private void initReaderManager() throws IOException {
        if (!this.initializedReaderManager) {
            synchronized (this) {
                this.ensureOpen();
                if (!this.initializedReaderManager) {
                    this.readerManager = new ReaderManager(this.indexWriter, false);
                    this.shouldRefreshReaderManager = false;
                    this.initializedReaderManager = true;
                }
            }
        }
    }
    
    public DirectoryTaxonomyWriter(final Directory directory, final IndexWriterConfig.OpenMode openMode) throws IOException {
        this(directory, openMode, defaultTaxonomyWriterCache());
    }
    
    public static TaxonomyWriterCache defaultTaxonomyWriterCache() {
        return new Cl2oTaxonomyWriterCache(1024, 0.15f, 3);
    }
    
    public DirectoryTaxonomyWriter(final Directory d) throws IOException {
        this(d, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.isClosed) {
            this.commit();
            this.indexWriter.close();
            this.doClose();
        }
    }
    
    private void doClose() throws IOException {
        this.isClosed = true;
        this.closeResources();
    }
    
    protected synchronized void closeResources() throws IOException {
        if (this.initializedReaderManager) {
            this.readerManager.close();
            this.readerManager = null;
            this.initializedReaderManager = false;
        }
        if (this.cache != null) {
            this.cache.close();
        }
    }
    
    protected synchronized int findCategory(final FacetLabel categoryPath) throws IOException {
        int res = this.cache.get(categoryPath);
        if (res >= 0 || this.cacheIsComplete) {
            return res;
        }
        this.cacheMisses.incrementAndGet();
        this.perhapsFillCache();
        res = this.cache.get(categoryPath);
        if (res >= 0 || this.cacheIsComplete) {
            return res;
        }
        this.initReaderManager();
        int doc = -1;
        final DirectoryReader reader = (DirectoryReader)this.readerManager.acquire();
        try {
            final BytesRef catTerm = new BytesRef((CharSequence)FacetsConfig.pathToString(categoryPath.components, categoryPath.length));
            PostingsEnum docs = null;
            for (final LeafReaderContext ctx : reader.leaves()) {
                final Terms terms = ctx.reader().terms("$full_path$");
                if (terms != null) {
                    final TermsEnum termsEnum = terms.iterator();
                    if (termsEnum.seekExact(catTerm)) {
                        docs = termsEnum.postings(docs, 0);
                        doc = docs.nextDoc() + ctx.docBase;
                        break;
                    }
                    continue;
                }
            }
        }
        finally {
            this.readerManager.release((Object)reader);
        }
        if (doc > 0) {
            this.addToCache(categoryPath, doc);
        }
        return doc;
    }
    
    @Override
    public int addCategory(final FacetLabel categoryPath) throws IOException {
        this.ensureOpen();
        int res = this.cache.get(categoryPath);
        if (res < 0) {
            synchronized (this) {
                res = this.findCategory(categoryPath);
                if (res < 0) {
                    res = this.internalAddCategory(categoryPath);
                }
            }
        }
        return res;
    }
    
    private int internalAddCategory(final FacetLabel cp) throws IOException {
        int parent;
        if (cp.length > 1) {
            final FacetLabel parentPath = cp.subpath(cp.length - 1);
            parent = this.findCategory(parentPath);
            if (parent < 0) {
                parent = this.internalAddCategory(parentPath);
            }
        }
        else if (cp.length == 1) {
            parent = 0;
        }
        else {
            parent = -1;
        }
        final int id = this.addCategoryDocument(cp, parent);
        return id;
    }
    
    protected final void ensureOpen() {
        if (this.isClosed) {
            throw new AlreadyClosedException("The taxonomy writer has already been closed");
        }
    }
    
    private int addCategoryDocument(final FacetLabel categoryPath, final int parent) throws IOException {
        this.parentStream.set(Math.max(parent + 1, 1));
        final Document d = new Document();
        d.add((IndexableField)this.parentStreamField);
        this.fullPathField.setStringValue(FacetsConfig.pathToString(categoryPath.components, categoryPath.length));
        d.add((IndexableField)this.fullPathField);
        this.indexWriter.addDocument((Iterable)d);
        final int id = this.nextID++;
        this.shouldRefreshReaderManager = true;
        this.taxoArrays = this.getTaxoArrays().add(id, parent);
        this.addToCache(categoryPath, id);
        return id;
    }
    
    private void addToCache(final FacetLabel categoryPath, final int id) throws IOException {
        if (this.cache.put(categoryPath, id)) {
            this.refreshReaderManager();
            this.cacheIsComplete = false;
        }
    }
    
    private synchronized void refreshReaderManager() throws IOException {
        if (this.shouldRefreshReaderManager && this.initializedReaderManager) {
            this.readerManager.maybeRefresh();
            this.shouldRefreshReaderManager = false;
        }
    }
    
    public synchronized void commit() throws IOException {
        this.ensureOpen();
        final String epochStr = this.indexWriter.getCommitData().get("index.epoch");
        if (epochStr == null || Long.parseLong(epochStr, 16) != this.indexEpoch) {
            this.indexWriter.setCommitData((Map)this.combinedCommitData(this.indexWriter.getCommitData()));
        }
        this.indexWriter.commit();
    }
    
    private Map<String, String> combinedCommitData(final Map<String, String> commitData) {
        final Map<String, String> m = new HashMap<String, String>();
        if (commitData != null) {
            m.putAll(commitData);
        }
        m.put("index.epoch", Long.toString(this.indexEpoch, 16));
        return m;
    }
    
    @Override
    public void setCommitData(final Map<String, String> commitUserData) {
        this.indexWriter.setCommitData((Map)this.combinedCommitData(commitUserData));
    }
    
    @Override
    public Map<String, String> getCommitData() {
        return this.combinedCommitData(this.indexWriter.getCommitData());
    }
    
    public synchronized void prepareCommit() throws IOException {
        this.ensureOpen();
        final String epochStr = this.indexWriter.getCommitData().get("index.epoch");
        if (epochStr == null || Long.parseLong(epochStr, 16) != this.indexEpoch) {
            this.indexWriter.setCommitData((Map)this.combinedCommitData(this.indexWriter.getCommitData()));
        }
        this.indexWriter.prepareCommit();
    }
    
    @Override
    public int getSize() {
        this.ensureOpen();
        return this.nextID;
    }
    
    public void setCacheMissesUntilFill(final int i) {
        this.ensureOpen();
        this.cacheMissesUntilFill = i;
    }
    
    private synchronized void perhapsFillCache() throws IOException {
        if (this.cacheMisses.get() < this.cacheMissesUntilFill) {
            return;
        }
        if (!this.shouldFillCache) {
            return;
        }
        this.shouldFillCache = false;
        this.initReaderManager();
        boolean aborted = false;
        final DirectoryReader reader = (DirectoryReader)this.readerManager.acquire();
        try {
            PostingsEnum postingsEnum = null;
            for (final LeafReaderContext ctx : reader.leaves()) {
                final Terms terms = ctx.reader().terms("$full_path$");
                if (terms != null) {
                    final TermsEnum termsEnum = terms.iterator();
                    while (termsEnum.next() != null) {
                        if (this.cache.isFull()) {
                            aborted = true;
                            break;
                        }
                        final BytesRef t = termsEnum.term();
                        final FacetLabel cp = new FacetLabel(FacetsConfig.stringToPath(t.utf8ToString()));
                        postingsEnum = termsEnum.postings(postingsEnum, 0);
                        final boolean res = this.cache.put(cp, postingsEnum.nextDoc() + ctx.docBase);
                        assert !res : "entries should not have been evicted from the cache";
                    }
                }
                if (aborted) {
                    break;
                }
            }
        }
        finally {
            this.readerManager.release((Object)reader);
        }
        this.cacheIsComplete = !aborted;
        if (this.cacheIsComplete) {
            synchronized (this) {
                this.readerManager.close();
                this.readerManager = null;
                this.initializedReaderManager = false;
            }
        }
    }
    
    private TaxonomyIndexArrays getTaxoArrays() throws IOException {
        if (this.taxoArrays == null) {
            synchronized (this) {
                if (this.taxoArrays == null) {
                    this.initReaderManager();
                    final DirectoryReader reader = (DirectoryReader)this.readerManager.acquire();
                    try {
                        final TaxonomyIndexArrays tmpArrays = new TaxonomyIndexArrays((IndexReader)reader);
                        this.taxoArrays = tmpArrays;
                    }
                    finally {
                        this.readerManager.release((Object)reader);
                    }
                }
            }
        }
        return this.taxoArrays;
    }
    
    @Override
    public int getParent(final int ordinal) throws IOException {
        this.ensureOpen();
        if (ordinal >= this.nextID) {
            throw new ArrayIndexOutOfBoundsException("requested ordinal is bigger than the largest ordinal in the taxonomy");
        }
        final int[] parents = this.getTaxoArrays().parents();
        assert ordinal < parents.length : "requested ordinal (" + ordinal + "); parents.length (" + parents.length + ") !";
        return parents[ordinal];
    }
    
    public void addTaxonomy(final Directory taxoDir, final OrdinalMap map) throws IOException {
        this.ensureOpen();
        final DirectoryReader r = DirectoryReader.open(taxoDir);
        try {
            final int size = r.numDocs();
            final OrdinalMap ordinalMap = map;
            ordinalMap.setSize(size);
            int base = 0;
            PostingsEnum docs = null;
            for (final LeafReaderContext ctx : r.leaves()) {
                final LeafReader ar = ctx.reader();
                final Terms terms = ar.terms("$full_path$");
                final TermsEnum te = terms.iterator();
                while (te.next() != null) {
                    final FacetLabel cp = new FacetLabel(FacetsConfig.stringToPath(te.term().utf8ToString()));
                    final int ordinal = this.addCategory(cp);
                    docs = te.postings(docs, 0);
                    ordinalMap.addMapping(docs.nextDoc() + base, ordinal);
                }
                base += ar.maxDoc();
            }
            ordinalMap.addDone();
        }
        finally {
            r.close();
        }
    }
    
    public synchronized void rollback() throws IOException {
        this.ensureOpen();
        this.indexWriter.rollback();
        this.doClose();
    }
    
    public synchronized void replaceTaxonomy(final Directory taxoDir) throws IOException {
        this.indexWriter.deleteAll();
        this.indexWriter.addIndexes(new Directory[] { taxoDir });
        this.shouldRefreshReaderManager = true;
        this.initReaderManager();
        this.refreshReaderManager();
        this.nextID = this.indexWriter.maxDoc();
        this.taxoArrays = null;
        this.cache.clear();
        this.cacheIsComplete = false;
        this.shouldFillCache = true;
        this.cacheMisses.set(0);
        ++this.indexEpoch;
    }
    
    public Directory getDirectory() {
        return this.dir;
    }
    
    final IndexWriter getInternalIndexWriter() {
        return this.indexWriter;
    }
    
    public final long getTaxonomyEpoch() {
        return this.indexEpoch;
    }
    
    private static class SinglePositionTokenStream extends TokenStream
    {
        private CharTermAttribute termAtt;
        private PositionIncrementAttribute posIncrAtt;
        private boolean returned;
        private int val;
        private final String word;
        
        public SinglePositionTokenStream(final String word) {
            this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
            this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
            this.word = word;
            this.returned = true;
        }
        
        public void set(final int val) {
            this.val = val;
            this.returned = false;
        }
        
        public boolean incrementToken() throws IOException {
            if (this.returned) {
                return false;
            }
            this.clearAttributes();
            this.posIncrAtt.setPositionIncrement(this.val);
            this.termAtt.setEmpty();
            this.termAtt.append(this.word);
            return this.returned = true;
        }
    }
    
    public static final class MemoryOrdinalMap implements OrdinalMap
    {
        int[] map;
        
        @Override
        public void setSize(final int taxonomySize) {
            this.map = new int[taxonomySize];
        }
        
        @Override
        public void addMapping(final int origOrdinal, final int newOrdinal) {
            this.map[origOrdinal] = newOrdinal;
        }
        
        @Override
        public void addDone() {
        }
        
        @Override
        public int[] getMap() {
            return this.map;
        }
    }
    
    public static final class DiskOrdinalMap implements OrdinalMap
    {
        Path tmpfile;
        DataOutputStream out;
        int[] map;
        
        public DiskOrdinalMap(final Path tmpfile) throws IOException {
            this.map = null;
            this.tmpfile = tmpfile;
            this.out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(tmpfile, new OpenOption[0])));
        }
        
        @Override
        public void addMapping(final int origOrdinal, final int newOrdinal) throws IOException {
            this.out.writeInt(origOrdinal);
            this.out.writeInt(newOrdinal);
        }
        
        @Override
        public void setSize(final int taxonomySize) throws IOException {
            this.out.writeInt(taxonomySize);
        }
        
        @Override
        public void addDone() throws IOException {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        }
        
        @Override
        public int[] getMap() throws IOException {
            if (this.map != null) {
                return this.map;
            }
            this.addDone();
            final DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(this.tmpfile, new OpenOption[0])));
            this.map = new int[in.readInt()];
            for (int i = 0; i < this.map.length; ++i) {
                final int origordinal = in.readInt();
                final int newordinal = in.readInt();
                this.map[origordinal] = newordinal;
            }
            in.close();
            Files.delete(this.tmpfile);
            return this.map;
        }
    }
    
    public interface OrdinalMap
    {
        void setSize(final int p0) throws IOException;
        
        void addMapping(final int p0, final int p1) throws IOException;
        
        void addDone() throws IOException;
        
        int[] getMap() throws IOException;
    }
}
