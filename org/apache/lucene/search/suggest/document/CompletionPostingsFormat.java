package org.apache.lucene.search.suggest.document;

import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.PostingsFormat;

public abstract class CompletionPostingsFormat extends PostingsFormat
{
    static final String CODEC_NAME = "completion";
    static final int COMPLETION_CODEC_VERSION = 1;
    static final int COMPLETION_VERSION_CURRENT = 1;
    static final String INDEX_EXTENSION = "cmp";
    static final String DICT_EXTENSION = "lkp";
    
    public CompletionPostingsFormat() {
        super("completion");
    }
    
    protected abstract PostingsFormat delegatePostingsFormat();
    
    public FieldsConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        final PostingsFormat delegatePostingsFormat = this.delegatePostingsFormat();
        if (delegatePostingsFormat == null) {
            throw new UnsupportedOperationException("Error - " + this.getClass().getName() + " has been constructed without a choice of PostingsFormat");
        }
        return new CompletionFieldsConsumer(delegatePostingsFormat, state);
    }
    
    public FieldsProducer fieldsProducer(final SegmentReadState state) throws IOException {
        return new CompletionFieldsProducer(state);
    }
}
