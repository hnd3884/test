package org.apache.lucene.analysis.payloads;

import org.apache.lucene.util.BytesRef;

public abstract class AbstractEncoder implements PayloadEncoder
{
    @Override
    public BytesRef encode(final char[] buffer) {
        return this.encode(buffer, 0, buffer.length);
    }
}
