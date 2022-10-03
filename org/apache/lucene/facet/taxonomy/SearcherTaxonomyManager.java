package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import java.io.IOException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.ReferenceManager;

public class SearcherTaxonomyManager extends ReferenceManager<SearcherAndTaxonomy>
{
    private final SearcherFactory searcherFactory;
    private final long taxoEpoch;
    private final DirectoryTaxonomyWriter taxoWriter;
    
    public SearcherTaxonomyManager(final IndexWriter writer, final SearcherFactory searcherFactory, final DirectoryTaxonomyWriter taxoWriter) throws IOException {
        this(writer, true, searcherFactory, taxoWriter);
    }
    
    public SearcherTaxonomyManager(final IndexWriter writer, final boolean applyAllDeletes, SearcherFactory searcherFactory, final DirectoryTaxonomyWriter taxoWriter) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.taxoWriter = taxoWriter;
        final DirectoryTaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoWriter);
        this.current = new SearcherAndTaxonomy(SearcherManager.getSearcher(searcherFactory, (IndexReader)DirectoryReader.open(writer, applyAllDeletes), (IndexReader)null), taxoReader);
        this.taxoEpoch = taxoWriter.getTaxonomyEpoch();
    }
    
    public SearcherTaxonomyManager(final Directory indexDir, final Directory taxoDir, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        final DirectoryTaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);
        this.current = new SearcherAndTaxonomy(SearcherManager.getSearcher(searcherFactory, (IndexReader)DirectoryReader.open(indexDir), (IndexReader)null), taxoReader);
        this.taxoWriter = null;
        this.taxoEpoch = -1L;
    }
    
    protected void decRef(final SearcherAndTaxonomy ref) throws IOException {
        ref.searcher.getIndexReader().decRef();
        ref.taxonomyReader.decRef();
    }
    
    protected boolean tryIncRef(final SearcherAndTaxonomy ref) throws IOException {
        if (ref.searcher.getIndexReader().tryIncRef()) {
            if (ref.taxonomyReader.tryIncRef()) {
                return true;
            }
            ref.searcher.getIndexReader().decRef();
        }
        return false;
    }
    
    protected SearcherAndTaxonomy refreshIfNeeded(final SearcherAndTaxonomy ref) throws IOException {
        final IndexReader r = ref.searcher.getIndexReader();
        final IndexReader newReader = (IndexReader)DirectoryReader.openIfChanged((DirectoryReader)r);
        if (newReader == null) {
            return null;
        }
        DirectoryTaxonomyReader tr = TaxonomyReader.openIfChanged(ref.taxonomyReader);
        if (tr == null) {
            ref.taxonomyReader.incRef();
            tr = ref.taxonomyReader;
        }
        else if (this.taxoWriter != null && this.taxoWriter.getTaxonomyEpoch() != this.taxoEpoch) {
            IOUtils.close(new Closeable[] { (Closeable)newReader, tr });
            throw new IllegalStateException("DirectoryTaxonomyWriter.replaceTaxonomy was called, which is not allowed when using SearcherTaxonomyManager");
        }
        return new SearcherAndTaxonomy(SearcherManager.getSearcher(this.searcherFactory, newReader, r), tr);
    }
    
    protected int getRefCount(final SearcherAndTaxonomy reference) {
        return reference.searcher.getIndexReader().getRefCount();
    }
    
    public static class SearcherAndTaxonomy
    {
        public final IndexSearcher searcher;
        public final DirectoryTaxonomyReader taxonomyReader;
        
        public SearcherAndTaxonomy(final IndexSearcher searcher, final DirectoryTaxonomyReader taxonomyReader) {
            this.searcher = searcher;
            this.taxonomyReader = taxonomyReader;
        }
    }
}
