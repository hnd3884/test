package com.fasterxml.jackson.core.io;

import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import com.fasterxml.jackson.core.SerializableString;

public class SerializedString implements SerializableString, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final JsonStringEncoder JSON_ENCODER;
    protected final String _value;
    protected byte[] _quotedUTF8Ref;
    protected byte[] _unquotedUTF8Ref;
    protected char[] _quotedChars;
    protected transient String _jdkSerializeValue;
    
    public SerializedString(final String v) {
        if (v == null) {
            throw new IllegalStateException("Null String illegal for SerializedString");
        }
        this._value = v;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException {
        this._jdkSerializeValue = in.readUTF();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeUTF(this._value);
    }
    
    protected Object readResolve() {
        return new SerializedString(this._jdkSerializeValue);
    }
    
    @Override
    public final String getValue() {
        return this._value;
    }
    
    @Override
    public final int charLength() {
        return this._value.length();
    }
    
    @Override
    public final char[] asQuotedChars() {
        char[] result = this._quotedChars;
        if (result == null) {
            result = (this._quotedChars = SerializedString.JSON_ENCODER.quoteAsString(this._value));
        }
        return result;
    }
    
    @Override
    public final byte[] asQuotedUTF8() {
        byte[] result = this._quotedUTF8Ref;
        if (result == null) {
            result = (this._quotedUTF8Ref = SerializedString.JSON_ENCODER.quoteAsUTF8(this._value));
        }
        return result;
    }
    
    @Override
    public final byte[] asUnquotedUTF8() {
        byte[] result = this._unquotedUTF8Ref;
        if (result == null) {
            result = (this._unquotedUTF8Ref = SerializedString.JSON_ENCODER.encodeAsUTF8(this._value));
        }
        return result;
    }
    
    @Override
    public int appendQuoted(final char[] buffer, final int offset) {
        char[] result = this._quotedChars;
        if (result == null) {
            result = (this._quotedChars = SerializedString.JSON_ENCODER.quoteAsString(this._value));
        }
        final int length = result.length;
        if (offset + length > buffer.length) {
            return -1;
        }
        System.arraycopy(result, 0, buffer, offset, length);
        return length;
    }
    
    @Override
    public int appendQuotedUTF8(final byte[] buffer, final int offset) {
        byte[] result = this._quotedUTF8Ref;
        if (result == null) {
            result = (this._quotedUTF8Ref = SerializedString.JSON_ENCODER.quoteAsUTF8(this._value));
        }
        final int length = result.length;
        if (offset + length > buffer.length) {
            return -1;
        }
        System.arraycopy(result, 0, buffer, offset, length);
        return length;
    }
    
    @Override
    public int appendUnquoted(final char[] buffer, final int offset) {
        final String str = this._value;
        final int length = str.length();
        if (offset + length > buffer.length) {
            return -1;
        }
        str.getChars(0, length, buffer, offset);
        return length;
    }
    
    @Override
    public int appendUnquotedUTF8(final byte[] buffer, final int offset) {
        byte[] result = this._unquotedUTF8Ref;
        if (result == null) {
            result = (this._unquotedUTF8Ref = SerializedString.JSON_ENCODER.encodeAsUTF8(this._value));
        }
        final int length = result.length;
        if (offset + length > buffer.length) {
            return -1;
        }
        System.arraycopy(result, 0, buffer, offset, length);
        return length;
    }
    
    @Override
    public int writeQuotedUTF8(final OutputStream out) throws IOException {
        byte[] result = this._quotedUTF8Ref;
        if (result == null) {
            result = (this._quotedUTF8Ref = SerializedString.JSON_ENCODER.quoteAsUTF8(this._value));
        }
        final int length = result.length;
        out.write(result, 0, length);
        return length;
    }
    
    @Override
    public int writeUnquotedUTF8(final OutputStream out) throws IOException {
        byte[] result = this._unquotedUTF8Ref;
        if (result == null) {
            result = (this._unquotedUTF8Ref = SerializedString.JSON_ENCODER.encodeAsUTF8(this._value));
        }
        final int length = result.length;
        out.write(result, 0, length);
        return length;
    }
    
    @Override
    public int putQuotedUTF8(final ByteBuffer buffer) {
        byte[] result = this._quotedUTF8Ref;
        if (result == null) {
            result = (this._quotedUTF8Ref = SerializedString.JSON_ENCODER.quoteAsUTF8(this._value));
        }
        final int length = result.length;
        if (length > buffer.remaining()) {
            return -1;
        }
        buffer.put(result, 0, length);
        return length;
    }
    
    @Override
    public int putUnquotedUTF8(final ByteBuffer buffer) {
        byte[] result = this._unquotedUTF8Ref;
        if (result == null) {
            result = (this._unquotedUTF8Ref = SerializedString.JSON_ENCODER.encodeAsUTF8(this._value));
        }
        final int length = result.length;
        if (length > buffer.remaining()) {
            return -1;
        }
        buffer.put(result, 0, length);
        return length;
    }
    
    @Override
    public final String toString() {
        return this._value;
    }
    
    @Override
    public final int hashCode() {
        return this._value.hashCode();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final SerializedString other = (SerializedString)o;
        return this._value.equals(other._value);
    }
    
    static {
        JSON_ENCODER = JsonStringEncoder.getInstance();
    }
}
