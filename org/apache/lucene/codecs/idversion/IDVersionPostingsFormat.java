package org.apache.lucene.codecs.idversion;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.Bits;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.blocktree.BlockTreeTermsWriter;
import org.apache.lucene.codecs.PostingsFormat;

public class IDVersionPostingsFormat extends PostingsFormat
{
    public static final long MIN_VERSION = 0L;
    public static final long MAX_VERSION = 4611686018427387903L;
    private final int minTermsInBlock;
    private final int maxTermsInBlock;
    
    public IDVersionPostingsFormat() {
        this(25, 48);
    }
    
    public IDVersionPostingsFormat(final int minTermsInBlock, final int maxTermsInBlock) {
        super("IDVersion");
        BlockTreeTermsWriter.validateSettings(this.minTermsInBlock = minTermsInBlock, this.maxTermsInBlock = maxTermsInBlock);
    }
    
    public FieldsConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        final PostingsWriterBase postingsWriter = (PostingsWriterBase)new IDVersionPostingsWriter((Bits)state.liveDocs);
        boolean success = false;
        try {
            final FieldsConsumer ret = new VersionBlockTreeTermsWriter(state, postingsWriter, this.minTermsInBlock, this.maxTermsInBlock);
            success = true;
            return ret;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)postingsWriter });
            }
        }
    }
    
    public FieldsProducer fieldsProducer(final SegmentReadState state) throws IOException {
        final PostingsReaderBase postingsReader = new IDVersionPostingsReader();
        boolean success = false;
        try {
            final FieldsProducer ret = new VersionBlockTreeTermsReader(postingsReader, state);
            success = true;
            return ret;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)postingsReader });
            }
        }
    }
    
    public static long bytesToLong(final BytesRef bytes) {
        return ((long)bytes.bytes[bytes.offset] & 0xFFL) << 56 | ((long)bytes.bytes[bytes.offset + 1] & 0xFFL) << 48 | ((long)bytes.bytes[bytes.offset + 2] & 0xFFL) << 40 | ((long)bytes.bytes[bytes.offset + 3] & 0xFFL) << 32 | ((long)bytes.bytes[bytes.offset + 4] & 0xFFL) << 24 | ((long)bytes.bytes[bytes.offset + 5] & 0xFFL) << 16 | ((long)bytes.bytes[bytes.offset + 6] & 0xFFL) << 8 | ((long)bytes.bytes[bytes.offset + 7] & 0xFFL);
    }
    
    public static void longToBytes(final long v, final BytesRef bytes) {
        if (v > 4611686018427387903L || v < 0L) {
            throw new IllegalArgumentException("version must be >= MIN_VERSION=0 and <= MAX_VERSION=4611686018427387903 (got: " + v + ")");
        }
        bytes.offset = 0;
        bytes.length = 8;
        bytes.bytes[0] = (byte)(v >> 56);
        bytes.bytes[1] = (byte)(v >> 48);
        bytes.bytes[2] = (byte)(v >> 40);
        bytes.bytes[3] = (byte)(v >> 32);
        bytes.bytes[4] = (byte)(v >> 24);
        bytes.bytes[5] = (byte)(v >> 16);
        bytes.bytes[6] = (byte)(v >> 8);
        bytes.bytes[7] = (byte)v;
        assert bytesToLong(bytes) == v : bytesToLong(bytes) + " vs " + v + " bytes=" + bytes;
    }
}
