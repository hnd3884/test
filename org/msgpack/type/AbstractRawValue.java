package org.msgpack.type;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import java.util.Arrays;

abstract class AbstractRawValue extends AbstractValue implements RawValue
{
    private static final char[] HEX_TABLE;
    
    @Override
    public ValueType getType() {
        return ValueType.RAW;
    }
    
    @Override
    public boolean isRawValue() {
        return true;
    }
    
    @Override
    public RawValue asRawValue() {
        return this;
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
        return v.isRawValue() && Arrays.equals(this.getByteArray(), v.asRawValue().getByteArray());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getByteArray());
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder()).toString();
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        String s;
        if (this.getClass() == StringRawValueImpl.class) {
            s = this.getString();
        }
        else {
            final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.IGNORE).onUnmappableCharacter(CodingErrorAction.IGNORE);
            try {
                s = decoder.decode(ByteBuffer.wrap(this.getByteArray())).toString();
            }
            catch (final CharacterCodingException ex) {
                s = new String(this.getByteArray());
            }
        }
        sb.append("\"");
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (ch < ' ') {
                switch (ch) {
                    case '\n': {
                        sb.append("\\n");
                        break;
                    }
                    case '\r': {
                        sb.append("\\r");
                        break;
                    }
                    case '\t': {
                        sb.append("\\t");
                        break;
                    }
                    case '\f': {
                        sb.append("\\f");
                        break;
                    }
                    case '\b': {
                        sb.append("\\b");
                        break;
                    }
                    default: {
                        this.escapeChar(sb, ch);
                        break;
                    }
                }
            }
            else if (ch <= '\u007f') {
                switch (ch) {
                    case '\\': {
                        sb.append("\\\\");
                        break;
                    }
                    case '\"': {
                        sb.append("\\\"");
                        break;
                    }
                    default: {
                        sb.append(ch);
                        break;
                    }
                }
            }
            else if (ch >= '\ud800' && ch <= '\udfff') {
                this.escapeChar(sb, ch);
            }
            else {
                sb.append(ch);
            }
        }
        sb.append("\"");
        return sb;
    }
    
    private void escapeChar(final StringBuilder sb, final int ch) {
        sb.append("\\u");
        sb.append(AbstractRawValue.HEX_TABLE[ch >> 12 & 0xF]);
        sb.append(AbstractRawValue.HEX_TABLE[ch >> 8 & 0xF]);
        sb.append(AbstractRawValue.HEX_TABLE[ch >> 4 & 0xF]);
        sb.append(AbstractRawValue.HEX_TABLE[ch & 0xF]);
    }
    
    static {
        HEX_TABLE = "0123456789ABCDEF".toCharArray();
    }
}
