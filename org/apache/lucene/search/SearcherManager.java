package org.apache.lucene.search;

import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import java.io.IOException;
import org.apache.lucene.index.IndexWriter;

public final class SearcherManager extends ReferenceManager<IndexSearcher>
{
    private final SearcherFactory searcherFactory;
    
    public SearcherManager(final IndexWriter writer, final SearcherFactory searcherFactory) throws IOException {
        this(writer, true, searcherFactory);
    }
    
    public SearcherManager(final IndexWriter writer, final boolean applyAllDeletes, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = (G)getSearcher(searcherFactory, DirectoryReader.open(writer, applyAllDeletes), null);
    }
    
    public SearcherManager(final Directory dir, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = (G)getSearcher(searcherFactory, DirectoryReader.open(dir), null);
    }
    
    public SearcherManager(final DirectoryReader reader, SearcherFactory searcherFactory) throws IOException {
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = (G)getSearcher(searcherFactory, reader, null);
    }
    
    @Override
    protected void decRef(final IndexSearcher reference) throws IOException {
        reference.getIndexReader().decRef();
    }
    
    @Override
    protected IndexSearcher refreshIfNeeded(final IndexSearcher referenceToRefresh) throws IOException {
        final IndexReader r = referenceToRefresh.getIndexReader();
        assert r instanceof DirectoryReader : "searcher's IndexReader should be a DirectoryReader, but got " + r;
        final IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader)r);
        if (newReader == null) {
            return null;
        }
        return getSearcher(this.searcherFactory, newReader, r);
    }
    
    @Override
    protected boolean tryIncRef(final IndexSearcher reference) {
        return reference.getIndexReader().tryIncRef();
    }
    
    @Override
    protected int getRefCount(final IndexSearcher reference) {
        return reference.getIndexReader().getRefCount();
    }
    
    public boolean isSearcherCurrent() throws IOException {
        final IndexSearcher searcher = this.acquire();
        try {
            final IndexReader r = searcher.getIndexReader();
            assert r instanceof DirectoryReader : "searcher's IndexReader should be a DirectoryReader, but got " + r;
            return ((DirectoryReader)r).isCurrent();
        }
        finally {
            this.release(searcher);
        }
    }
    
    public static IndexSearcher getSearcher(final SearcherFactory searcherFactory, final IndexReader reader, final IndexReader previousReader) throws IOException {
        boolean success = false;
        IndexSearcher searcher;
        try {
            searcher = searcherFactory.newSearcher(reader, previousReader);
            if (searcher.getIndexReader() != reader) {
                throw new IllegalStateException("SearcherFactory must wrap exactly the provided reader (got " + searcher.getIndexReader() + " but expected " + reader + ")");
            }
            success = true;
        }
        finally {
            if (!success) {
                reader.decRef();
            }
        }
        return searcher;
    }
}
