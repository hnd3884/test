package org.apache.lucene.codecs;

import java.util.List;
import org.apache.lucene.index.MappedMultiFields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.ReaderSlice;
import java.util.ArrayList;
import org.apache.lucene.index.MergeState;
import java.io.IOException;
import org.apache.lucene.index.Fields;
import java.io.Closeable;

public abstract class FieldsConsumer implements Closeable
{
    protected FieldsConsumer() {
    }
    
    public abstract void write(final Fields p0) throws IOException;
    
    public void merge(final MergeState mergeState) throws IOException {
        final List<Fields> fields = new ArrayList<Fields>();
        final List<ReaderSlice> slices = new ArrayList<ReaderSlice>();
        int docBase = 0;
        for (int readerIndex = 0; readerIndex < mergeState.fieldsProducers.length; ++readerIndex) {
            final FieldsProducer f = mergeState.fieldsProducers[readerIndex];
            final int maxDoc = mergeState.maxDocs[readerIndex];
            f.checkIntegrity();
            slices.add(new ReaderSlice(docBase, maxDoc, readerIndex));
            fields.add(f);
            docBase += maxDoc;
        }
        final Fields mergedFields = new MappedMultiFields(mergeState, new MultiFields(fields.toArray(Fields.EMPTY_ARRAY), slices.toArray(ReaderSlice.EMPTY_ARRAY)));
        this.write(mergedFields);
    }
    
    @Override
    public abstract void close() throws IOException;
}
