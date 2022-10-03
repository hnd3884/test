package org.apache.lucene.rangetree;

import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.lucene54.Lucene54DocValuesFormat;
import org.apache.lucene.codecs.DocValuesFormat;

public class RangeTreeDocValuesFormat extends DocValuesFormat
{
    static final String DATA_CODEC_NAME = "RangeTreeData";
    static final int DATA_VERSION_START = 0;
    static final int DATA_VERSION_CURRENT = 0;
    static final String DATA_EXTENSION = "ndd";
    static final String META_CODEC_NAME = "RangeTreeMeta";
    static final int META_VERSION_START = 0;
    static final int META_VERSION_CURRENT = 0;
    static final String META_EXTENSION = "ndm";
    private final int maxPointsInLeafNode;
    private final int maxPointsSortInHeap;
    private final DocValuesFormat delegate;
    
    public RangeTreeDocValuesFormat() {
        this(1024, 131072);
    }
    
    public RangeTreeDocValuesFormat(final int maxPointsInLeafNode, final int maxPointsSortInHeap) {
        super("RangeTree");
        this.delegate = (DocValuesFormat)new Lucene54DocValuesFormat();
        RangeTreeWriter.verifyParams(maxPointsInLeafNode, maxPointsSortInHeap);
        this.maxPointsInLeafNode = maxPointsInLeafNode;
        this.maxPointsSortInHeap = maxPointsSortInHeap;
    }
    
    public DocValuesConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        return new RangeTreeDocValuesConsumer(this.delegate.fieldsConsumer(state), state, this.maxPointsInLeafNode, this.maxPointsSortInHeap);
    }
    
    public DocValuesProducer fieldsProducer(final SegmentReadState state) throws IOException {
        return new RangeTreeDocValuesProducer(this.delegate.fieldsProducer(state), state);
    }
}
