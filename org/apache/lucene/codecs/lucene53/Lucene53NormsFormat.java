package org.apache.lucene.codecs.lucene53;

import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.NormsConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.NormsFormat;

public class Lucene53NormsFormat extends NormsFormat
{
    private static final String DATA_CODEC = "Lucene53NormsData";
    private static final String DATA_EXTENSION = "nvd";
    private static final String METADATA_CODEC = "Lucene53NormsMetadata";
    private static final String METADATA_EXTENSION = "nvm";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    
    @Override
    public NormsConsumer normsConsumer(final SegmentWriteState state) throws IOException {
        return new Lucene53NormsConsumer(state, "Lucene53NormsData", "nvd", "Lucene53NormsMetadata", "nvm");
    }
    
    @Override
    public NormsProducer normsProducer(final SegmentReadState state) throws IOException {
        return new Lucene53NormsProducer(state, "Lucene53NormsData", "nvd", "Lucene53NormsMetadata", "nvm");
    }
}
