package org.msgpack.type;

import java.util.Arrays;
import java.io.IOException;
import org.msgpack.packer.Packer;
import java.io.UnsupportedEncodingException;
import org.msgpack.MessageTypeException;

class StringRawValueImpl extends AbstractRawValue
{
    private String string;
    
    StringRawValueImpl(final String string) {
        this.string = string;
    }
    
    @Override
    public byte[] getByteArray() {
        try {
            return this.string.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new MessageTypeException(ex);
        }
    }
    
    @Override
    public String getString() {
        return this.string;
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.write(this.string);
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
        if (!v.isRawValue()) {
            return false;
        }
        if (v.getClass() == StringRawValueImpl.class) {
            return this.string.equals(((StringRawValueImpl)v).string);
        }
        return Arrays.equals(this.getByteArray(), v.asRawValue().getByteArray());
    }
}
