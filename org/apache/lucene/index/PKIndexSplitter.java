package org.apache.lucene.index;

import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitSetIterator;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.Bits;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Filter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.Query;

public class PKIndexSplitter
{
    private final Query docsInFirstIndex;
    private final Directory input;
    private final Directory dir1;
    private final Directory dir2;
    private final IndexWriterConfig config1;
    private final IndexWriterConfig config2;
    
    public PKIndexSplitter(final Directory input, final Directory dir1, final Directory dir2, final Query docsInFirstIndex) {
        this(input, dir1, dir2, docsInFirstIndex, newDefaultConfig(), newDefaultConfig());
    }
    
    public PKIndexSplitter(final Directory input, final Directory dir1, final Directory dir2, final Filter docsInFirstIndex) {
        this(input, dir1, dir2, (Query)docsInFirstIndex);
    }
    
    private static IndexWriterConfig newDefaultConfig() {
        return new IndexWriterConfig((Analyzer)null).setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    }
    
    public PKIndexSplitter(final Directory input, final Directory dir1, final Directory dir2, final Query docsInFirstIndex, final IndexWriterConfig config1, final IndexWriterConfig config2) {
        this.input = input;
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.docsInFirstIndex = docsInFirstIndex;
        this.config1 = config1;
        this.config2 = config2;
    }
    
    public PKIndexSplitter(final Directory input, final Directory dir1, final Directory dir2, final Filter docsInFirstIndex, final IndexWriterConfig config1, final IndexWriterConfig config2) {
        this(input, dir1, dir2, (Query)docsInFirstIndex, config1, config2);
    }
    
    public PKIndexSplitter(final Directory input, final Directory dir1, final Directory dir2, final Term midTerm) {
        this(input, dir1, dir2, (Query)new TermRangeQuery(midTerm.field(), (BytesRef)null, midTerm.bytes(), true, false));
    }
    
    public PKIndexSplitter(final Directory input, final Directory dir1, final Directory dir2, final Term midTerm, final IndexWriterConfig config1, final IndexWriterConfig config2) {
        this(input, dir1, dir2, (Query)new TermRangeQuery(midTerm.field(), (BytesRef)null, midTerm.bytes(), true, false), config1, config2);
    }
    
    public void split() throws IOException {
        boolean success = false;
        final DirectoryReader reader = DirectoryReader.open(this.input);
        try {
            this.createIndex(this.config1, this.dir1, reader, this.docsInFirstIndex, false);
            this.createIndex(this.config2, this.dir2, reader, this.docsInFirstIndex, true);
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { (Closeable)reader });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)reader });
            }
        }
    }
    
    private void createIndex(final IndexWriterConfig config, final Directory target, final DirectoryReader reader, final Query preserveFilter, final boolean negateFilter) throws IOException {
        boolean success = false;
        final IndexWriter w = new IndexWriter(target, config);
        try {
            final IndexSearcher searcher = new IndexSearcher((IndexReader)reader);
            searcher.setQueryCache((QueryCache)null);
            final boolean needsScores = false;
            final Weight preserveWeight = searcher.createNormalizedWeight(preserveFilter, false);
            final List<LeafReaderContext> leaves = reader.leaves();
            final CodecReader[] subReaders = new CodecReader[leaves.size()];
            int i = 0;
            for (final LeafReaderContext ctx : leaves) {
                subReaders[i++] = (CodecReader)new DocumentFilteredLeafIndexReader(ctx, preserveWeight, negateFilter);
            }
            w.addIndexes(subReaders);
            success = true;
        }
        finally {
            if (success) {
                w.close();
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)w });
            }
        }
    }
    
    private static class DocumentFilteredLeafIndexReader extends FilterCodecReader
    {
        final Bits liveDocs;
        final int numDocs;
        
        public DocumentFilteredLeafIndexReader(final LeafReaderContext context, final Weight preserveWeight, final boolean negateFilter) throws IOException {
            super((CodecReader)context.reader());
            final int maxDoc = this.in.maxDoc();
            final FixedBitSet bits = new FixedBitSet(maxDoc);
            final Scorer preverveScorer = preserveWeight.scorer(context);
            if (preverveScorer != null) {
                bits.or(preverveScorer.iterator());
            }
            if (negateFilter) {
                bits.flip(0, maxDoc);
            }
            if (this.in.hasDeletions()) {
                final Bits oldLiveDocs = this.in.getLiveDocs();
                assert oldLiveDocs != null;
                final DocIdSetIterator it = (DocIdSetIterator)new BitSetIterator((BitSet)bits, 0L);
                for (int i = it.nextDoc(); i != Integer.MAX_VALUE; i = it.nextDoc()) {
                    if (!oldLiveDocs.get(i)) {
                        bits.clear(i);
                    }
                }
            }
            this.liveDocs = (Bits)bits;
            this.numDocs = bits.cardinality();
        }
        
        public int numDocs() {
            return this.numDocs;
        }
        
        public Bits getLiveDocs() {
            return this.liveDocs;
        }
    }
}
