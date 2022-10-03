package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import java.io.IOException;
import org.apache.lucene.search.ReferenceManager;

public final class ReaderManager extends ReferenceManager<DirectoryReader>
{
    public ReaderManager(final IndexWriter writer) throws IOException {
        this(writer, true);
    }
    
    public ReaderManager(final IndexWriter writer, final boolean applyAllDeletes) throws IOException {
        this.current = (G)DirectoryReader.open(writer, applyAllDeletes);
    }
    
    public ReaderManager(final Directory dir) throws IOException {
        this.current = (G)DirectoryReader.open(dir);
    }
    
    public ReaderManager(final DirectoryReader reader) throws IOException {
        this.current = (G)reader;
    }
    
    @Override
    protected void decRef(final DirectoryReader reference) throws IOException {
        reference.decRef();
    }
    
    @Override
    protected DirectoryReader refreshIfNeeded(final DirectoryReader referenceToRefresh) throws IOException {
        return DirectoryReader.openIfChanged(referenceToRefresh);
    }
    
    @Override
    protected boolean tryIncRef(final DirectoryReader reference) {
        return reference.tryIncRef();
    }
    
    @Override
    protected int getRefCount(final DirectoryReader reference) {
        return reference.getRefCount();
    }
}
