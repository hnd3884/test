package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCompositeReader<R extends IndexReader> extends CompositeReader
{
    private final R[] subReaders;
    private final int[] starts;
    private final int maxDoc;
    private final int numDocs;
    private final List<R> subReadersList;
    
    protected BaseCompositeReader(final R[] subReaders) throws IOException {
        this.subReaders = subReaders;
        this.subReadersList = Collections.unmodifiableList((List<? extends R>)Arrays.asList((T[])subReaders));
        this.starts = new int[subReaders.length + 1];
        long maxDoc = 0L;
        long numDocs = 0L;
        for (int i = 0; i < subReaders.length; ++i) {
            this.starts[i] = (int)maxDoc;
            final IndexReader r = subReaders[i];
            maxDoc += r.maxDoc();
            numDocs += r.numDocs();
            r.registerParentReader(this);
        }
        if (maxDoc <= IndexWriter.getActualMaxDocs()) {
            this.maxDoc = (int)maxDoc;
            this.starts[subReaders.length] = this.maxDoc;
            this.numDocs = (int)numDocs;
            return;
        }
        if (this instanceof DirectoryReader) {
            throw new CorruptIndexException("Too many documents: an index cannot exceed " + IndexWriter.getActualMaxDocs() + " but readers have total maxDoc=" + maxDoc, Arrays.toString(subReaders));
        }
        throw new IllegalArgumentException("Too many documents: composite IndexReaders cannot exceed " + IndexWriter.getActualMaxDocs() + " but readers have total maxDoc=" + maxDoc);
    }
    
    @Override
    public final Fields getTermVectors(final int docID) throws IOException {
        this.ensureOpen();
        final int i = this.readerIndex(docID);
        return this.subReaders[i].getTermVectors(docID - this.starts[i]);
    }
    
    @Override
    public final int numDocs() {
        return this.numDocs;
    }
    
    @Override
    public final int maxDoc() {
        return this.maxDoc;
    }
    
    @Override
    public final void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        final int i = this.readerIndex(docID);
        this.subReaders[i].document(docID - this.starts[i], visitor);
    }
    
    @Override
    public final int docFreq(final Term term) throws IOException {
        this.ensureOpen();
        int total = 0;
        for (int i = 0; i < this.subReaders.length; ++i) {
            total += this.subReaders[i].docFreq(term);
        }
        return total;
    }
    
    @Override
    public final long totalTermFreq(final Term term) throws IOException {
        this.ensureOpen();
        long total = 0L;
        for (int i = 0; i < this.subReaders.length; ++i) {
            final long sub = this.subReaders[i].totalTermFreq(term);
            if (sub == -1L) {
                return -1L;
            }
            total += sub;
        }
        return total;
    }
    
    @Override
    public final long getSumDocFreq(final String field) throws IOException {
        this.ensureOpen();
        long total = 0L;
        for (final R reader : this.subReaders) {
            final long sub = reader.getSumDocFreq(field);
            if (sub == -1L) {
                return -1L;
            }
            total += sub;
        }
        return total;
    }
    
    @Override
    public final int getDocCount(final String field) throws IOException {
        this.ensureOpen();
        int total = 0;
        for (final R reader : this.subReaders) {
            final int sub = reader.getDocCount(field);
            if (sub == -1) {
                return -1;
            }
            total += sub;
        }
        return total;
    }
    
    @Override
    public final long getSumTotalTermFreq(final String field) throws IOException {
        this.ensureOpen();
        long total = 0L;
        for (final R reader : this.subReaders) {
            final long sub = reader.getSumTotalTermFreq(field);
            if (sub == -1L) {
                return -1L;
            }
            total += sub;
        }
        return total;
    }
    
    protected final int readerIndex(final int docID) {
        if (docID < 0 || docID >= this.maxDoc) {
            throw new IllegalArgumentException("docID must be >= 0 and < maxDoc=" + this.maxDoc + " (got docID=" + docID + ")");
        }
        return ReaderUtil.subIndex(docID, this.starts);
    }
    
    protected final int readerBase(final int readerIndex) {
        if (readerIndex < 0 || readerIndex >= this.subReaders.length) {
            throw new IllegalArgumentException("readerIndex must be >= 0 and < getSequentialSubReaders().size()");
        }
        return this.starts[readerIndex];
    }
    
    @Override
    protected final List<? extends R> getSequentialSubReaders() {
        return (List<? extends R>)this.subReadersList;
    }
}
