package org.apache.lucene.codecs.compressing;

import org.apache.lucene.codecs.TermVectorsWriter;
import java.io.IOException;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.TermVectorsFormat;

public class CompressingTermVectorsFormat extends TermVectorsFormat
{
    private final String formatName;
    private final String segmentSuffix;
    private final CompressionMode compressionMode;
    private final int chunkSize;
    private final int blockSize;
    
    public CompressingTermVectorsFormat(final String formatName, final String segmentSuffix, final CompressionMode compressionMode, final int chunkSize, final int blockSize) {
        this.formatName = formatName;
        this.segmentSuffix = segmentSuffix;
        this.compressionMode = compressionMode;
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be >= 1");
        }
        this.chunkSize = chunkSize;
        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize must be >= 1");
        }
        this.blockSize = blockSize;
    }
    
    @Override
    public final TermVectorsReader vectorsReader(final Directory directory, final SegmentInfo segmentInfo, final FieldInfos fieldInfos, final IOContext context) throws IOException {
        return new CompressingTermVectorsReader(directory, segmentInfo, this.segmentSuffix, fieldInfos, context, this.formatName, this.compressionMode);
    }
    
    @Override
    public final TermVectorsWriter vectorsWriter(final Directory directory, final SegmentInfo segmentInfo, final IOContext context) throws IOException {
        return new CompressingTermVectorsWriter(directory, segmentInfo, this.segmentSuffix, context, this.formatName, this.compressionMode, this.chunkSize, this.blockSize);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(compressionMode=" + this.compressionMode + ", chunkSize=" + this.chunkSize + ", blockSize=" + this.blockSize + ")";
    }
}
