package org.apache.lucene.analysis.payloads;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;

public class IntegerEncoder extends AbstractEncoder implements PayloadEncoder
{
    @Override
    public BytesRef encode(final char[] buffer, final int offset, final int length) {
        final int payload = ArrayUtil.parseInt(buffer, offset, length);
        final byte[] bytes = PayloadHelper.encodeInt(payload);
        final BytesRef result = new BytesRef(bytes);
        return result;
    }
}
