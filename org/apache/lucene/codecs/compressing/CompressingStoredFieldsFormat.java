package org.apache.lucene.codecs.compressing;

import org.apache.lucene.codecs.StoredFieldsWriter;
import java.io.IOException;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.StoredFieldsFormat;

public class CompressingStoredFieldsFormat extends StoredFieldsFormat
{
    private final String formatName;
    private final String segmentSuffix;
    private final CompressionMode compressionMode;
    private final int chunkSize;
    private final int maxDocsPerChunk;
    private final int blockSize;
    
    public CompressingStoredFieldsFormat(final String formatName, final CompressionMode compressionMode, final int chunkSize, final int maxDocsPerChunk, final int blockSize) {
        this(formatName, "", compressionMode, chunkSize, maxDocsPerChunk, blockSize);
    }
    
    public CompressingStoredFieldsFormat(final String formatName, final String segmentSuffix, final CompressionMode compressionMode, final int chunkSize, final int maxDocsPerChunk, final int blockSize) {
        this.formatName = formatName;
        this.segmentSuffix = segmentSuffix;
        this.compressionMode = compressionMode;
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be >= 1");
        }
        this.chunkSize = chunkSize;
        if (maxDocsPerChunk < 1) {
            throw new IllegalArgumentException("maxDocsPerChunk must be >= 1");
        }
        this.maxDocsPerChunk = maxDocsPerChunk;
        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize must be >= 1");
        }
        this.blockSize = blockSize;
    }
    
    @Override
    public StoredFieldsReader fieldsReader(final Directory directory, final SegmentInfo si, final FieldInfos fn, final IOContext context) throws IOException {
        return new CompressingStoredFieldsReader(directory, si, this.segmentSuffix, fn, context, this.formatName, this.compressionMode);
    }
    
    @Override
    public StoredFieldsWriter fieldsWriter(final Directory directory, final SegmentInfo si, final IOContext context) throws IOException {
        return new CompressingStoredFieldsWriter(directory, si, this.segmentSuffix, context, this.formatName, this.compressionMode, this.chunkSize, this.maxDocsPerChunk, this.blockSize);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(compressionMode=" + this.compressionMode + ", chunkSize=" + this.chunkSize + ", maxDocsPerChunk=" + this.maxDocsPerChunk + ", blockSize=" + this.blockSize + ")";
    }
}
