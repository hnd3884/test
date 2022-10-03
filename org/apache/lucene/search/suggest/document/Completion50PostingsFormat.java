package org.apache.lucene.search.suggest.document;

import org.apache.lucene.codecs.PostingsFormat;

public class Completion50PostingsFormat extends CompletionPostingsFormat
{
    @Override
    protected PostingsFormat delegatePostingsFormat() {
        return PostingsFormat.forName("Lucene50");
    }
}
