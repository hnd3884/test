package org.apache.lucene.codecs.lucene54;

import java.util.Objects;
import org.apache.lucene.codecs.lucene53.Lucene53NormsFormat;
import org.apache.lucene.codecs.perfield.PerFieldDocValuesFormat;
import org.apache.lucene.codecs.perfield.PerFieldPostingsFormat;
import org.apache.lucene.codecs.lucene50.Lucene50CompoundFormat;
import org.apache.lucene.codecs.lucene50.Lucene50LiveDocsFormat;
import org.apache.lucene.codecs.lucene50.Lucene50SegmentInfoFormat;
import org.apache.lucene.codecs.lucene50.Lucene50FieldInfosFormat;
import org.apache.lucene.codecs.lucene50.Lucene50TermVectorsFormat;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.CompoundFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.Codec;

public class Lucene54Codec extends Codec
{
    private final TermVectorsFormat vectorsFormat;
    private final FieldInfosFormat fieldInfosFormat;
    private final SegmentInfoFormat segmentInfosFormat;
    private final LiveDocsFormat liveDocsFormat;
    private final CompoundFormat compoundFormat;
    private final PostingsFormat postingsFormat;
    private final DocValuesFormat docValuesFormat;
    private final StoredFieldsFormat storedFieldsFormat;
    private final PostingsFormat defaultFormat;
    private final DocValuesFormat defaultDVFormat;
    private final NormsFormat normsFormat;
    
    public Lucene54Codec() {
        this(Lucene50StoredFieldsFormat.Mode.BEST_SPEED);
    }
    
    public Lucene54Codec(final Lucene50StoredFieldsFormat.Mode mode) {
        super("Lucene54");
        this.vectorsFormat = new Lucene50TermVectorsFormat();
        this.fieldInfosFormat = new Lucene50FieldInfosFormat();
        this.segmentInfosFormat = new Lucene50SegmentInfoFormat();
        this.liveDocsFormat = new Lucene50LiveDocsFormat();
        this.compoundFormat = new Lucene50CompoundFormat();
        this.postingsFormat = new PerFieldPostingsFormat() {
            @Override
            public PostingsFormat getPostingsFormatForField(final String field) {
                return Lucene54Codec.this.getPostingsFormatForField(field);
            }
        };
        this.docValuesFormat = new PerFieldDocValuesFormat() {
            @Override
            public DocValuesFormat getDocValuesFormatForField(final String field) {
                return Lucene54Codec.this.getDocValuesFormatForField(field);
            }
        };
        this.defaultFormat = PostingsFormat.forName("Lucene50");
        this.defaultDVFormat = DocValuesFormat.forName("Lucene54");
        this.normsFormat = new Lucene53NormsFormat();
        this.storedFieldsFormat = new Lucene50StoredFieldsFormat(Objects.requireNonNull(mode));
    }
    
    @Override
    public final StoredFieldsFormat storedFieldsFormat() {
        return this.storedFieldsFormat;
    }
    
    @Override
    public final TermVectorsFormat termVectorsFormat() {
        return this.vectorsFormat;
    }
    
    @Override
    public final PostingsFormat postingsFormat() {
        return this.postingsFormat;
    }
    
    @Override
    public final FieldInfosFormat fieldInfosFormat() {
        return this.fieldInfosFormat;
    }
    
    @Override
    public final SegmentInfoFormat segmentInfoFormat() {
        return this.segmentInfosFormat;
    }
    
    @Override
    public final LiveDocsFormat liveDocsFormat() {
        return this.liveDocsFormat;
    }
    
    @Override
    public final CompoundFormat compoundFormat() {
        return this.compoundFormat;
    }
    
    public PostingsFormat getPostingsFormatForField(final String field) {
        return this.defaultFormat;
    }
    
    public DocValuesFormat getDocValuesFormatForField(final String field) {
        return this.defaultDVFormat;
    }
    
    @Override
    public final DocValuesFormat docValuesFormat() {
        return this.docValuesFormat;
    }
    
    @Override
    public final NormsFormat normsFormat() {
        return this.normsFormat;
    }
}
