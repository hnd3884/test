package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.index.TermState;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.blocktree.BlockTreeTermsReader;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.blocktree.BlockTreeTermsWriter;
import org.apache.lucene.codecs.PostingsFormat;

public final class Lucene50PostingsFormat extends PostingsFormat
{
    public static final String ZDOC_EXTENSION = "zdoc";
    public static final String POS_EXTENSION = "pos";
    public static final String PAY_EXTENSION = "pay";
    static final int MAX_SKIP_LEVELS = 10;
    static final String TERMS_CODEC = "Lucene50PostingsWriterTerms";
    static final String DOC_CODEC = "Lucene50PostingsWriterDoc";
    static final String POS_CODEC = "Lucene50PostingsWriterPos";
    static final String PAY_CODEC = "Lucene50PostingsWriterPay";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    private final int minTermBlockSize;
    private final int maxTermBlockSize;
    public static final int BLOCK_SIZE = 128;
    
    public Lucene50PostingsFormat() {
        this(25, 48);
    }
    
    public Lucene50PostingsFormat(final int minTermBlockSize, final int maxTermBlockSize) {
        super("Lucene50");
        BlockTreeTermsWriter.validateSettings(minTermBlockSize, maxTermBlockSize);
        this.minTermBlockSize = minTermBlockSize;
        this.maxTermBlockSize = maxTermBlockSize;
    }
    
    public String toString() {
        return this.getName() + "(blocksize=" + 128 + ")";
    }
    
    public FieldsConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        final PostingsWriterBase postingsWriter = (PostingsWriterBase)new Lucene50PostingsWriter(state);
        boolean success = false;
        try {
            final FieldsConsumer ret = (FieldsConsumer)new BlockTreeTermsWriter(state, postingsWriter, this.minTermBlockSize, this.maxTermBlockSize);
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
        final PostingsReaderBase postingsReader = (PostingsReaderBase)new Lucene50PostingsReader(state);
        boolean success = false;
        try {
            final FieldsProducer ret = (FieldsProducer)new BlockTreeTermsReader(postingsReader, state);
            success = true;
            return ret;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)postingsReader });
            }
        }
    }
    
    static final class IntBlockTermState extends BlockTermState
    {
        long docStartFP;
        long posStartFP;
        long payStartFP;
        long skipOffset;
        long lastPosBlockOffset;
        int singletonDocID;
        
        IntBlockTermState() {
            this.docStartFP = 0L;
            this.posStartFP = 0L;
            this.payStartFP = 0L;
            this.skipOffset = -1L;
            this.lastPosBlockOffset = -1L;
            this.singletonDocID = -1;
        }
        
        public IntBlockTermState clone() {
            final IntBlockTermState other = new IntBlockTermState();
            other.copyFrom((TermState)this);
            return other;
        }
        
        public void copyFrom(final TermState termState) {
            super.copyFrom(termState);
            final IntBlockTermState other = (IntBlockTermState)termState;
            this.docStartFP = other.docStartFP;
            this.posStartFP = other.posStartFP;
            this.payStartFP = other.payStartFP;
            this.lastPosBlockOffset = other.lastPosBlockOffset;
            this.skipOffset = other.skipOffset;
            this.singletonDocID = other.singletonDocID;
        }
        
        public String toString() {
            return super.toString() + " docStartFP=" + this.docStartFP + " posStartFP=" + this.posStartFP + " payStartFP=" + this.payStartFP + " lastPosBlockOffset=" + this.lastPosBlockOffset + " singletonDocID=" + this.singletonDocID;
        }
    }
}
