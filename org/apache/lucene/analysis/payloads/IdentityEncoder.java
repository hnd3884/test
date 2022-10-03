package org.apache.lucene.analysis.payloads;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.apache.lucene.util.BytesRef;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public class IdentityEncoder extends AbstractEncoder implements PayloadEncoder
{
    protected Charset charset;
    
    public IdentityEncoder() {
        this.charset = StandardCharsets.UTF_8;
    }
    
    public IdentityEncoder(final Charset charset) {
        this.charset = StandardCharsets.UTF_8;
        this.charset = charset;
    }
    
    @Override
    public BytesRef encode(final char[] buffer, final int offset, final int length) {
        final ByteBuffer bb = this.charset.encode(CharBuffer.wrap(buffer, offset, length));
        if (bb.hasArray()) {
            return new BytesRef(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
        }
        final byte[] b = new byte[bb.remaining()];
        bb.get(b);
        return new BytesRef(b);
    }
}
