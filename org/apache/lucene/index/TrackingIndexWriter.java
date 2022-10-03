package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import org.apache.lucene.search.Query;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class TrackingIndexWriter
{
    private final IndexWriter writer;
    private final AtomicLong indexingGen;
    
    public TrackingIndexWriter(final IndexWriter writer) {
        this.indexingGen = new AtomicLong(1L);
        this.writer = writer;
    }
    
    public long updateDocument(final Term t, final Iterable<? extends IndexableField> d) throws IOException {
        this.writer.updateDocument(t, d);
        return this.indexingGen.get();
    }
    
    public long updateDocuments(final Term t, final Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.writer.updateDocuments(t, docs);
        return this.indexingGen.get();
    }
    
    public long deleteDocuments(final Term t) throws IOException {
        this.writer.deleteDocuments(t);
        return this.indexingGen.get();
    }
    
    public long deleteDocuments(final Term... terms) throws IOException {
        this.writer.deleteDocuments(terms);
        return this.indexingGen.get();
    }
    
    public long deleteDocuments(final Query q) throws IOException {
        this.writer.deleteDocuments(q);
        return this.indexingGen.get();
    }
    
    public long deleteDocuments(final Query... queries) throws IOException {
        this.writer.deleteDocuments(queries);
        return this.indexingGen.get();
    }
    
    public long deleteAll() throws IOException {
        this.writer.deleteAll();
        return this.indexingGen.get();
    }
    
    public long addDocument(final Iterable<? extends IndexableField> d) throws IOException {
        this.writer.addDocument(d);
        return this.indexingGen.get();
    }
    
    public long addDocuments(final Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.writer.addDocuments(docs);
        return this.indexingGen.get();
    }
    
    public long addIndexes(final Directory... dirs) throws IOException {
        this.writer.addIndexes(dirs);
        return this.indexingGen.get();
    }
    
    public long addIndexes(final CodecReader... readers) throws IOException {
        this.writer.addIndexes(readers);
        return this.indexingGen.get();
    }
    
    public long getGeneration() {
        return this.indexingGen.get();
    }
    
    public IndexWriter getIndexWriter() {
        return this.writer;
    }
    
    public long getAndIncrementGeneration() {
        return this.indexingGen.getAndIncrement();
    }
    
    public long tryDeleteDocument(final IndexReader reader, final int docID) throws IOException {
        if (this.writer.tryDeleteDocument(reader, docID)) {
            return this.indexingGen.get();
        }
        return -1L;
    }
}
