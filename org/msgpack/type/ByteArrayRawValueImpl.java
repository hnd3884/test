package org.msgpack.type;

import java.util.Arrays;
import java.io.IOException;
import org.msgpack.packer.Packer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import org.msgpack.MessageTypeException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;

class ByteArrayRawValueImpl extends AbstractRawValue
{
    private static ByteArrayRawValueImpl emptyInstance;
    private byte[] bytes;
    
    public static RawValue getEmptyInstance() {
        return ByteArrayRawValueImpl.emptyInstance;
    }
    
    ByteArrayRawValueImpl(final byte[] bytes, final boolean gift) {
        if (gift) {
            this.bytes = bytes;
        }
        else {
            System.arraycopy(bytes, 0, this.bytes = new byte[bytes.length], 0, bytes.length);
        }
    }
    
    ByteArrayRawValueImpl(final byte[] b, final int off, final int len) {
        System.arraycopy(b, off, this.bytes = new byte[len], 0, len);
    }
    
    @Override
    public byte[] getByteArray() {
        return this.bytes;
    }
    
    @Override
    public String getString() {
        final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(this.bytes)).toString();
        }
        catch (final CharacterCodingException ex) {
            throw new MessageTypeException(ex);
        }
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.write(this.bytes);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        final Value v = (Value)o;
        return v.isRawValue() && Arrays.equals(this.bytes, v.asRawValue().getByteArray());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
    
    static {
        ByteArrayRawValueImpl.emptyInstance = new ByteArrayRawValueImpl(new byte[0], true);
    }
}
