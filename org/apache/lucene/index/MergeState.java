package org.apache.lucene.index;

import org.apache.lucene.util.packed.PackedLongValues;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.util.Bits;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.StoredFieldsReader;

public class MergeState
{
    public final SegmentInfo segmentInfo;
    public FieldInfos mergeFieldInfos;
    public final StoredFieldsReader[] storedFieldsReaders;
    public final TermVectorsReader[] termVectorsReaders;
    public final NormsProducer[] normsProducers;
    public final DocValuesProducer[] docValuesProducers;
    public final FieldInfos[] fieldInfos;
    public final Bits[] liveDocs;
    public final DocMap[] docMaps;
    public final FieldsProducer[] fieldsProducers;
    public final int[] docBase;
    public final int[] maxDocs;
    public final InfoStream infoStream;
    
    MergeState(final List<CodecReader> readers, final SegmentInfo segmentInfo, final InfoStream infoStream) throws IOException {
        final int numReaders = readers.size();
        this.docMaps = new DocMap[numReaders];
        this.docBase = new int[numReaders];
        this.maxDocs = new int[numReaders];
        this.fieldsProducers = new FieldsProducer[numReaders];
        this.normsProducers = new NormsProducer[numReaders];
        this.storedFieldsReaders = new StoredFieldsReader[numReaders];
        this.termVectorsReaders = new TermVectorsReader[numReaders];
        this.docValuesProducers = new DocValuesProducer[numReaders];
        this.fieldInfos = new FieldInfos[numReaders];
        this.liveDocs = new Bits[numReaders];
        for (int i = 0; i < numReaders; ++i) {
            final CodecReader reader = readers.get(i);
            this.maxDocs[i] = reader.maxDoc();
            this.liveDocs[i] = reader.getLiveDocs();
            this.fieldInfos[i] = reader.getFieldInfos();
            this.normsProducers[i] = reader.getNormsReader();
            if (this.normsProducers[i] != null) {
                this.normsProducers[i] = this.normsProducers[i].getMergeInstance();
            }
            this.docValuesProducers[i] = reader.getDocValuesReader();
            if (this.docValuesProducers[i] != null) {
                this.docValuesProducers[i] = this.docValuesProducers[i].getMergeInstance();
            }
            this.storedFieldsReaders[i] = reader.getFieldsReader();
            if (this.storedFieldsReaders[i] != null) {
                this.storedFieldsReaders[i] = this.storedFieldsReaders[i].getMergeInstance();
            }
            this.termVectorsReaders[i] = reader.getTermVectorsReader();
            if (this.termVectorsReaders[i] != null) {
                this.termVectorsReaders[i] = this.termVectorsReaders[i].getMergeInstance();
            }
            this.fieldsProducers[i] = reader.getPostingsReader().getMergeInstance();
        }
        this.segmentInfo = segmentInfo;
        this.infoStream = infoStream;
        this.setDocMaps(readers);
    }
    
    private void setDocMaps(final List<CodecReader> readers) throws IOException {
        final int numReaders = this.maxDocs.length;
        int docBase = 0;
        for (int i = 0; i < numReaders; ++i) {
            final CodecReader reader = readers.get(i);
            this.docBase[i] = docBase;
            final DocMap docMap = DocMap.build(reader);
            this.docMaps[i] = docMap;
            docBase += docMap.numDocs();
        }
        this.segmentInfo.setMaxDoc(docBase);
    }
    
    public abstract static class DocMap
    {
        DocMap() {
        }
        
        public abstract int get(final int p0);
        
        public abstract int maxDoc();
        
        public final int numDocs() {
            return this.maxDoc() - this.numDeletedDocs();
        }
        
        public abstract int numDeletedDocs();
        
        public boolean hasDeletions() {
            return this.numDeletedDocs() > 0;
        }
        
        public static DocMap build(final CodecReader reader) {
            final int maxDoc = reader.maxDoc();
            if (!reader.hasDeletions()) {
                return new NoDelDocMap(maxDoc);
            }
            final Bits liveDocs = reader.getLiveDocs();
            return build(maxDoc, liveDocs);
        }
        
        static DocMap build(final int maxDoc, final Bits liveDocs) {
            assert liveDocs != null;
            final PackedLongValues.Builder docMapBuilder = PackedLongValues.monotonicBuilder(0.0f);
            int del = 0;
            for (int i = 0; i < maxDoc; ++i) {
                docMapBuilder.add(i - del);
                if (!liveDocs.get(i)) {
                    ++del;
                }
            }
            final PackedLongValues docMap = docMapBuilder.build();
            final int numDeletedDocs = del;
            assert docMap.size() == maxDoc;
            return new DocMap() {
                @Override
                public int get(final int docID) {
                    if (!liveDocs.get(docID)) {
                        return -1;
                    }
                    return (int)docMap.get(docID);
                }
                
                @Override
                public int maxDoc() {
                    return maxDoc;
                }
                
                @Override
                public int numDeletedDocs() {
                    return numDeletedDocs;
                }
            };
        }
    }
    
    private static final class NoDelDocMap extends DocMap
    {
        private final int maxDoc;
        
        NoDelDocMap(final int maxDoc) {
            this.maxDoc = maxDoc;
        }
        
        @Override
        public int get(final int docID) {
            return docID;
        }
        
        @Override
        public int maxDoc() {
            return this.maxDoc;
        }
        
        @Override
        public int numDeletedDocs() {
            return 0;
        }
    }
}
