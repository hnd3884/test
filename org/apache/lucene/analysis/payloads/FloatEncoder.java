package org.apache.lucene.analysis.payloads;

import org.apache.lucene.util.BytesRef;

public class FloatEncoder extends AbstractEncoder implements PayloadEncoder
{
    @Override
    public BytesRef encode(final char[] buffer, final int offset, final int length) {
        final float payload = Float.parseFloat(new String(buffer, offset, length));
        final byte[] bytes = PayloadHelper.encodeFloat(payload);
        final BytesRef result = new BytesRef(bytes);
        return result;
    }
}
