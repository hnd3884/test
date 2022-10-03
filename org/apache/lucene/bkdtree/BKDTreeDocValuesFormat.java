package org.apache.lucene.bkdtree;

import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.lucene54.Lucene54DocValuesFormat;
import org.apache.lucene.codecs.DocValuesFormat;

@Deprecated
public class BKDTreeDocValuesFormat extends DocValuesFormat
{
    static final String DATA_CODEC_NAME = "BKDData";
    static final int DATA_VERSION_START = 0;
    static final int DATA_VERSION_CURRENT = 0;
    static final String DATA_EXTENSION = "kdd";
    static final String META_CODEC_NAME = "BKDMeta";
    static final int META_VERSION_START = 0;
    static final int META_VERSION_CURRENT = 0;
    static final String META_EXTENSION = "kdm";
    private final int maxPointsInLeafNode;
    private final int maxPointsSortInHeap;
    private final DocValuesFormat delegate;
    
    public BKDTreeDocValuesFormat() {
        this(1024, 131072);
    }
    
    public BKDTreeDocValuesFormat(final int maxPointsInLeafNode, final int maxPointsSortInHeap) {
        super("BKDTree");
        this.delegate = (DocValuesFormat)new Lucene54DocValuesFormat();
        BKDTreeWriter.verifyParams(maxPointsInLeafNode, maxPointsSortInHeap);
        this.maxPointsInLeafNode = maxPointsInLeafNode;
        this.maxPointsSortInHeap = maxPointsSortInHeap;
    }
    
    public DocValuesConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        return new BKDTreeDocValuesConsumer(this.delegate.fieldsConsumer(state), state, this.maxPointsInLeafNode, this.maxPointsSortInHeap);
    }
    
    public DocValuesProducer fieldsProducer(final SegmentReadState state) throws IOException {
        return new BKDTreeDocValuesProducer(this.delegate.fieldsProducer(state), state);
    }
}
