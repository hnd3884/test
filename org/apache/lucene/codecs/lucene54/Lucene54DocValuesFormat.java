package org.apache.lucene.codecs.lucene54;

import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.DocValuesFormat;

public final class Lucene54DocValuesFormat extends DocValuesFormat
{
    static final String DATA_CODEC = "Lucene54DocValuesData";
    static final String DATA_EXTENSION = "dvd";
    static final String META_CODEC = "Lucene54DocValuesMetadata";
    static final String META_EXTENSION = "dvm";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    static final byte NUMERIC = 0;
    static final byte BINARY = 1;
    static final byte SORTED = 2;
    static final byte SORTED_SET = 3;
    static final byte SORTED_NUMERIC = 4;
    static final int INTERVAL_SHIFT = 4;
    static final int INTERVAL_COUNT = 16;
    static final int INTERVAL_MASK = 15;
    static final int REVERSE_INTERVAL_SHIFT = 10;
    static final int REVERSE_INTERVAL_COUNT = 1024;
    static final int REVERSE_INTERVAL_MASK = 1023;
    static final int BLOCK_INTERVAL_SHIFT = 6;
    static final int BLOCK_INTERVAL_COUNT = 64;
    static final int BLOCK_INTERVAL_MASK = 63;
    static final int DELTA_COMPRESSED = 0;
    static final int GCD_COMPRESSED = 1;
    static final int TABLE_COMPRESSED = 2;
    static final int MONOTONIC_COMPRESSED = 3;
    static final int CONST_COMPRESSED = 4;
    static final int SPARSE_COMPRESSED = 5;
    static final int BINARY_FIXED_UNCOMPRESSED = 0;
    static final int BINARY_VARIABLE_UNCOMPRESSED = 1;
    static final int BINARY_PREFIX_COMPRESSED = 2;
    static final int SORTED_WITH_ADDRESSES = 0;
    static final int SORTED_SINGLE_VALUED = 1;
    static final int SORTED_SET_TABLE = 2;
    static final int ALL_LIVE = -1;
    static final int ALL_MISSING = -2;
    static final int MONOTONIC_BLOCK_SIZE = 16384;
    static final int DIRECT_MONOTONIC_BLOCK_SHIFT = 16;
    
    public Lucene54DocValuesFormat() {
        super("Lucene54");
    }
    
    @Override
    public DocValuesConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        return new Lucene54DocValuesConsumer(state, "Lucene54DocValuesData", "dvd", "Lucene54DocValuesMetadata", "dvm");
    }
    
    @Override
    public DocValuesProducer fieldsProducer(final SegmentReadState state) throws IOException {
        return new Lucene54DocValuesProducer(state, "Lucene54DocValuesData", "dvd", "Lucene54DocValuesMetadata", "dvm");
    }
}
