package org.apache.lucene.index;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.io.IOException;
import java.util.Set;

public class ParallelCompositeReader extends BaseCompositeReader<LeafReader>
{
    private final boolean closeSubReaders;
    private final Set<IndexReader> completeReaderSet;
    
    public ParallelCompositeReader(final CompositeReader... readers) throws IOException {
        this(true, readers);
    }
    
    public ParallelCompositeReader(final boolean closeSubReaders, final CompositeReader... readers) throws IOException {
        this(closeSubReaders, readers, readers);
    }
    
    public ParallelCompositeReader(final boolean closeSubReaders, final CompositeReader[] readers, final CompositeReader[] storedFieldReaders) throws IOException {
        super(prepareLeafReaders(readers, storedFieldReaders));
        this.completeReaderSet = Collections.newSetFromMap(new IdentityHashMap<IndexReader, Boolean>());
        this.closeSubReaders = closeSubReaders;
        Collections.addAll(this.completeReaderSet, readers);
        Collections.addAll(this.completeReaderSet, storedFieldReaders);
        if (!closeSubReaders) {
            for (final IndexReader reader : this.completeReaderSet) {
                reader.incRef();
            }
        }
        this.completeReaderSet.addAll(this.getSequentialSubReaders());
    }
    
    private static LeafReader[] prepareLeafReaders(final CompositeReader[] readers, final CompositeReader[] storedFieldsReaders) throws IOException {
        if (readers.length != 0) {
            final List<? extends LeafReaderContext> firstLeaves = readers[0].leaves();
            final int maxDoc = readers[0].maxDoc();
            final int noLeaves = firstLeaves.size();
            final int[] leafMaxDoc = new int[noLeaves];
            for (int i = 0; i < noLeaves; ++i) {
                final LeafReader r = ((LeafReaderContext)firstLeaves.get(i)).reader();
                leafMaxDoc[i] = r.maxDoc();
            }
            validate(readers, maxDoc, leafMaxDoc);
            validate(storedFieldsReaders, maxDoc, leafMaxDoc);
            final LeafReader[] wrappedLeaves = new LeafReader[noLeaves];
            for (int j = 0; j < wrappedLeaves.length; ++j) {
                final LeafReader[] subs = new LeafReader[readers.length];
                for (int k = 0; k < readers.length; ++k) {
                    subs[k] = readers[k].leaves().get(j).reader();
                }
                final LeafReader[] storedSubs = new LeafReader[storedFieldsReaders.length];
                for (int l = 0; l < storedFieldsReaders.length; ++l) {
                    storedSubs[l] = storedFieldsReaders[l].leaves().get(j).reader();
                }
                wrappedLeaves[j] = new ParallelLeafReader(true, subs, storedSubs) {
                    @Override
                    protected void doClose() {
                    }
                };
            }
            return wrappedLeaves;
        }
        if (storedFieldsReaders.length > 0) {
            throw new IllegalArgumentException("There must be at least one main reader if storedFieldsReaders are used.");
        }
        return new LeafReader[0];
    }
    
    private static void validate(final CompositeReader[] readers, final int maxDoc, final int[] leafMaxDoc) {
        for (int i = 0; i < readers.length; ++i) {
            final CompositeReader reader = readers[i];
            final List<? extends LeafReaderContext> subs = reader.leaves();
            if (reader.maxDoc() != maxDoc) {
                throw new IllegalArgumentException("All readers must have same maxDoc: " + maxDoc + "!=" + reader.maxDoc());
            }
            final int noSubs = subs.size();
            if (noSubs != leafMaxDoc.length) {
                throw new IllegalArgumentException("All readers must have same number of leaf readers");
            }
            for (int subIDX = 0; subIDX < noSubs; ++subIDX) {
                final LeafReader r = ((LeafReaderContext)subs.get(subIDX)).reader();
                if (r.maxDoc() != leafMaxDoc[subIDX]) {
                    throw new IllegalArgumentException("All leaf readers must have same corresponding subReader maxDoc");
                }
            }
        }
    }
    
    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (final IndexReader reader : this.completeReaderSet) {
            try {
                if (this.closeSubReaders) {
                    reader.close();
                }
                else {
                    reader.decRef();
                }
            }
            catch (final IOException e) {
                if (ioe != null) {
                    continue;
                }
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }
}
