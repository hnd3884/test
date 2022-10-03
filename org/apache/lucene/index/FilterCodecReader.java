package org.apache.lucene.index;

import org.apache.lucene.util.Bits;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.StoredFieldsReader;
import java.util.Objects;

public class FilterCodecReader extends CodecReader
{
    protected final CodecReader in;
    
    public FilterCodecReader(final CodecReader in) {
        this.in = Objects.requireNonNull(in);
    }
    
    @Override
    public StoredFieldsReader getFieldsReader() {
        return this.in.getFieldsReader();
    }
    
    @Override
    public TermVectorsReader getTermVectorsReader() {
        return this.in.getTermVectorsReader();
    }
    
    @Override
    public NormsProducer getNormsReader() {
        return this.in.getNormsReader();
    }
    
    @Override
    public DocValuesProducer getDocValuesReader() {
        return this.in.getDocValuesReader();
    }
    
    @Override
    public FieldsProducer getPostingsReader() {
        return this.in.getPostingsReader();
    }
    
    @Override
    public Bits getLiveDocs() {
        return this.in.getLiveDocs();
    }
    
    @Override
    public FieldInfos getFieldInfos() {
        return this.in.getFieldInfos();
    }
    
    @Override
    public int numDocs() {
        return this.in.numDocs();
    }
    
    @Override
    public int maxDoc() {
        return this.in.maxDoc();
    }
    
    @Override
    public void addCoreClosedListener(final CoreClosedListener listener) {
        this.in.addCoreClosedListener(listener);
    }
    
    @Override
    public void removeCoreClosedListener(final CoreClosedListener listener) {
        this.in.removeCoreClosedListener(listener);
    }
}
