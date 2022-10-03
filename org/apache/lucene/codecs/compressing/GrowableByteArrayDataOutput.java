package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.store.DataOutput;

public final class GrowableByteArrayDataOutput extends DataOutput
{
    static final int MIN_UTF8_SIZE_TO_ENABLE_DOUBLE_PASS_ENCODING = 65536;
    public byte[] bytes;
    public int length;
    byte[] scratchBytes;
    
    public GrowableByteArrayDataOutput(final int cp) {
        this.scratchBytes = new byte[16];
        this.bytes = new byte[ArrayUtil.oversize(cp, 1)];
        this.length = 0;
    }
    
    @Override
    public void writeByte(final byte b) {
        if (this.length >= this.bytes.length) {
            this.bytes = ArrayUtil.grow(this.bytes);
        }
        this.bytes[this.length++] = b;
    }
    
    @Override
    public void writeBytes(final byte[] b, final int off, final int len) {
        final int newLength = this.length + len;
        System.arraycopy(b, off, this.bytes = ArrayUtil.grow(this.bytes, newLength), this.length, len);
        this.length = newLength;
    }
    
    @Override
    public void writeString(final String string) throws IOException {
        final int maxLen = string.length() * 3;
        if (maxLen <= 65536) {
            this.scratchBytes = ArrayUtil.grow(this.scratchBytes, maxLen);
            final int len = UnicodeUtil.UTF16toUTF8(string, 0, string.length(), this.scratchBytes);
            this.writeVInt(len);
            this.writeBytes(this.scratchBytes, len);
        }
        else {
            final int numBytes = UnicodeUtil.calcUTF16toUTF8Length(string, 0, string.length());
            this.writeVInt(numBytes);
            this.bytes = ArrayUtil.grow(this.bytes, this.length + numBytes);
            this.length = UnicodeUtil.UTF16toUTF8(string, 0, string.length(), this.bytes, this.length);
        }
    }
}
