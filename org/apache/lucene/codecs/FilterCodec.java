package org.apache.lucene.codecs;

public abstract class FilterCodec extends Codec
{
    protected final Codec delegate;
    
    protected FilterCodec(final String name, final Codec delegate) {
        super(name);
        this.delegate = delegate;
    }
    
    @Override
    public DocValuesFormat docValuesFormat() {
        return this.delegate.docValuesFormat();
    }
    
    @Override
    public FieldInfosFormat fieldInfosFormat() {
        return this.delegate.fieldInfosFormat();
    }
    
    @Override
    public LiveDocsFormat liveDocsFormat() {
        return this.delegate.liveDocsFormat();
    }
    
    @Override
    public NormsFormat normsFormat() {
        return this.delegate.normsFormat();
    }
    
    @Override
    public PostingsFormat postingsFormat() {
        return this.delegate.postingsFormat();
    }
    
    @Override
    public SegmentInfoFormat segmentInfoFormat() {
        return this.delegate.segmentInfoFormat();
    }
    
    @Override
    public StoredFieldsFormat storedFieldsFormat() {
        return this.delegate.storedFieldsFormat();
    }
    
    @Override
    public TermVectorsFormat termVectorsFormat() {
        return this.delegate.termVectorsFormat();
    }
    
    @Override
    public CompoundFormat compoundFormat() {
        return this.delegate.compoundFormat();
    }
}
