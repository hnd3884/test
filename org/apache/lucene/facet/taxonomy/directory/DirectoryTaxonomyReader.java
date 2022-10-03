package org.apache.lucene.facet.taxonomy.directory;

import java.util.logging.Level;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.facet.FacetsConfig;
import java.util.Map;
import org.apache.lucene.facet.taxonomy.ParallelTaxonomyArrays;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.facet.taxonomy.FacetLabel;
import org.apache.lucene.facet.taxonomy.LRUHashMap;
import org.apache.lucene.index.DirectoryReader;
import java.util.logging.Logger;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;

public class DirectoryTaxonomyReader extends TaxonomyReader
{
    private static final Logger logger;
    private static final int DEFAULT_CACHE_VALUE = 4000;
    private final DirectoryTaxonomyWriter taxoWriter;
    private final long taxoEpoch;
    private final DirectoryReader indexReader;
    private LRUHashMap<FacetLabel, Integer> ordinalCache;
    private LRUHashMap<Integer, FacetLabel> categoryCache;
    private volatile TaxonomyIndexArrays taxoArrays;
    
    DirectoryTaxonomyReader(final DirectoryReader indexReader, final DirectoryTaxonomyWriter taxoWriter, final LRUHashMap<FacetLabel, Integer> ordinalCache, final LRUHashMap<Integer, FacetLabel> categoryCache, final TaxonomyIndexArrays taxoArrays) throws IOException {
        this.indexReader = indexReader;
        this.taxoWriter = taxoWriter;
        this.taxoEpoch = ((taxoWriter == null) ? -1L : taxoWriter.getTaxonomyEpoch());
        this.ordinalCache = ((ordinalCache == null) ? new LRUHashMap<FacetLabel, Integer>(4000) : ordinalCache);
        this.categoryCache = ((categoryCache == null) ? new LRUHashMap<Integer, FacetLabel>(4000) : categoryCache);
        this.taxoArrays = ((taxoArrays != null) ? new TaxonomyIndexArrays((IndexReader)indexReader, taxoArrays) : null);
    }
    
    public DirectoryTaxonomyReader(final Directory directory) throws IOException {
        this.indexReader = this.openIndexReader(directory);
        this.taxoWriter = null;
        this.taxoEpoch = -1L;
        this.ordinalCache = new LRUHashMap<FacetLabel, Integer>(4000);
        this.categoryCache = new LRUHashMap<Integer, FacetLabel>(4000);
    }
    
    public DirectoryTaxonomyReader(final DirectoryTaxonomyWriter taxoWriter) throws IOException {
        this.taxoWriter = taxoWriter;
        this.taxoEpoch = taxoWriter.getTaxonomyEpoch();
        this.indexReader = this.openIndexReader(taxoWriter.getInternalIndexWriter());
        this.ordinalCache = new LRUHashMap<FacetLabel, Integer>(4000);
        this.categoryCache = new LRUHashMap<Integer, FacetLabel>(4000);
    }
    
    private synchronized void initTaxoArrays() throws IOException {
        if (this.taxoArrays == null) {
            final TaxonomyIndexArrays tmpArrays = new TaxonomyIndexArrays((IndexReader)this.indexReader);
            this.taxoArrays = tmpArrays;
        }
    }
    
    @Override
    protected void doClose() throws IOException {
        this.indexReader.close();
        this.taxoArrays = null;
        this.ordinalCache = null;
        this.categoryCache = null;
    }
    
    @Override
    protected DirectoryTaxonomyReader doOpenIfChanged() throws IOException {
        this.ensureOpen();
        final DirectoryReader r2 = DirectoryReader.openIfChanged(this.indexReader);
        if (r2 == null) {
            return null;
        }
        boolean success = false;
        try {
            boolean recreated = false;
            if (this.taxoWriter == null) {
                final String t1 = this.indexReader.getIndexCommit().getUserData().get("index.epoch");
                final String t2 = r2.getIndexCommit().getUserData().get("index.epoch");
                if (t1 == null) {
                    if (t2 != null) {
                        recreated = true;
                    }
                }
                else if (!t1.equals(t2)) {
                    recreated = true;
                }
            }
            else if (this.taxoEpoch != this.taxoWriter.getTaxonomyEpoch()) {
                recreated = true;
            }
            DirectoryTaxonomyReader newtr;
            if (recreated) {
                newtr = new DirectoryTaxonomyReader(r2, this.taxoWriter, null, null, null);
            }
            else {
                newtr = new DirectoryTaxonomyReader(r2, this.taxoWriter, this.ordinalCache, this.categoryCache, this.taxoArrays);
            }
            success = true;
            return newtr;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)r2 });
            }
        }
    }
    
    protected DirectoryReader openIndexReader(final Directory directory) throws IOException {
        return DirectoryReader.open(directory);
    }
    
    protected DirectoryReader openIndexReader(final IndexWriter writer) throws IOException {
        return DirectoryReader.open(writer);
    }
    
    DirectoryReader getInternalIndexReader() {
        this.ensureOpen();
        return this.indexReader;
    }
    
    @Override
    public ParallelTaxonomyArrays getParallelTaxonomyArrays() throws IOException {
        this.ensureOpen();
        if (this.taxoArrays == null) {
            this.initTaxoArrays();
        }
        return this.taxoArrays;
    }
    
    @Override
    public Map<String, String> getCommitUserData() throws IOException {
        this.ensureOpen();
        return this.indexReader.getIndexCommit().getUserData();
    }
    
    @Override
    public int getOrdinal(final FacetLabel cp) throws IOException {
        this.ensureOpen();
        if (cp.length == 0) {
            return 0;
        }
        synchronized (this.ordinalCache) {
            final Integer res = this.ordinalCache.get(cp);
            if (res != null) {
                if (res < this.indexReader.maxDoc()) {
                    return res;
                }
                return -1;
            }
        }
        int ret = -1;
        final PostingsEnum docs = MultiFields.getTermDocsEnum((IndexReader)this.indexReader, "$full_path$", new BytesRef((CharSequence)FacetsConfig.pathToString(cp.components, cp.length)), 0);
        if (docs != null && docs.nextDoc() != Integer.MAX_VALUE) {
            ret = docs.docID();
            synchronized (this.ordinalCache) {
                this.ordinalCache.put(cp, ret);
            }
        }
        return ret;
    }
    
    @Override
    public FacetLabel getPath(final int ordinal) throws IOException {
        this.ensureOpen();
        if (ordinal < 0 || ordinal >= this.indexReader.maxDoc()) {
            return null;
        }
        final Integer catIDInteger = ordinal;
        synchronized (this.categoryCache) {
            final FacetLabel res = this.categoryCache.get(catIDInteger);
            if (res != null) {
                return res;
            }
        }
        final Document doc = this.indexReader.document(ordinal);
        final FacetLabel ret = new FacetLabel(FacetsConfig.stringToPath(doc.get("$full_path$")));
        synchronized (this.categoryCache) {
            this.categoryCache.put(catIDInteger, ret);
        }
        return ret;
    }
    
    @Override
    public int getSize() {
        this.ensureOpen();
        return this.indexReader.numDocs();
    }
    
    public void setCacheSize(final int size) {
        this.ensureOpen();
        synchronized (this.categoryCache) {
            this.categoryCache.setMaxSize(size);
        }
        synchronized (this.ordinalCache) {
            this.ordinalCache.setMaxSize(size);
        }
    }
    
    public String toString(final int max) {
        this.ensureOpen();
        final StringBuilder sb = new StringBuilder();
        for (int upperl = Math.min(max, this.indexReader.maxDoc()), i = 0; i < upperl; ++i) {
            try {
                final FacetLabel category = this.getPath(i);
                if (category == null) {
                    sb.append(i + ": NULL!! \n");
                }
                else if (category.length == 0) {
                    sb.append(i + ": EMPTY STRING!! \n");
                }
                else {
                    sb.append(i + ": " + category.toString() + "\n");
                }
            }
            catch (final IOException e) {
                if (DirectoryTaxonomyReader.logger.isLoggable(Level.FINEST)) {
                    DirectoryTaxonomyReader.logger.log(Level.FINEST, e.getMessage(), e);
                }
            }
        }
        return sb.toString();
    }
    
    static {
        logger = Logger.getLogger(DirectoryTaxonomyReader.class.getName());
    }
}
