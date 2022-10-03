package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.BytesRef;

public interface BytesTermAttribute extends TermToBytesRefAttribute
{
    void setBytesRef(final BytesRef p0);
}
