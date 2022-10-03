package org.apache.lucene.index;

import java.util.Iterator;
import java.io.IOException;

public class MultiReader extends BaseCompositeReader<IndexReader>
{
    private final boolean closeSubReaders;
    
    public MultiReader(final IndexReader... subReaders) throws IOException {
        this(subReaders, true);
    }
    
    public MultiReader(final IndexReader[] subReaders, final boolean closeSubReaders) throws IOException {
        super(subReaders.clone());
        if (!(this.closeSubReaders = closeSubReaders)) {
            for (int i = 0; i < subReaders.length; ++i) {
                subReaders[i].incRef();
            }
        }
    }
    
    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (final IndexReader r : this.getSequentialSubReaders()) {
            try {
                if (this.closeSubReaders) {
                    r.close();
                }
                else {
                    r.decRef();
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
