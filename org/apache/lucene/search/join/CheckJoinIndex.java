package org.apache.lucene.search.join;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BitSet;
import java.util.Iterator;
import org.apache.lucene.util.BitSetIterator;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.IndexReader;

public class CheckJoinIndex
{
    private CheckJoinIndex() {
    }
    
    public static void check(final IndexReader reader, final BitSetProducer parentsFilter) throws IOException {
        for (final LeafReaderContext context : reader.leaves()) {
            if (context.reader().maxDoc() == 0) {
                continue;
            }
            final BitSet parents = parentsFilter.getBitSet(context);
            if (parents == null || parents.cardinality() == 0) {
                throw new IllegalStateException("Every segment should have at least one parent, but " + context.reader() + " does not have any");
            }
            if (!parents.get(context.reader().maxDoc() - 1)) {
                throw new IllegalStateException("The last document of a segment must always be a parent, but " + context.reader() + " has a child as a last doc");
            }
            final Bits liveDocs = context.reader().getLiveDocs();
            if (liveDocs == null) {
                continue;
            }
            int prevParentDoc = -1;
            final DocIdSetIterator it = (DocIdSetIterator)new BitSetIterator(parents, 0L);
            for (int parentDoc = it.nextDoc(); parentDoc != Integer.MAX_VALUE; parentDoc = it.nextDoc()) {
                final boolean parentIsLive = liveDocs.get(parentDoc);
                int child = prevParentDoc + 1;
                while (child != parentDoc) {
                    final boolean childIsLive = liveDocs.get(child);
                    if (parentIsLive != childIsLive) {
                        if (childIsLive) {
                            throw new IllegalStateException("Parent doc " + parentDoc + " of segment " + context.reader() + " is live but has a deleted child document " + child);
                        }
                        throw new IllegalStateException("Parent doc " + parentDoc + " of segment " + context.reader() + " is deleted but has a live child document " + child);
                    }
                    else {
                        ++child;
                    }
                }
                prevParentDoc = parentDoc;
            }
        }
    }
}
