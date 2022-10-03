package org.apache.lucene.index;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;

public final class Term implements Comparable<Term>
{
    String field;
    BytesRef bytes;
    
    public Term(final String fld, final BytesRef bytes) {
        this.field = fld;
        this.bytes = ((bytes == null) ? null : BytesRef.deepCopyOf(bytes));
    }
    
    public Term(final String fld, final BytesRefBuilder bytesBuilder) {
        this.field = fld;
        this.bytes = bytesBuilder.toBytesRef();
    }
    
    public Term(final String fld, final String text) {
        this(fld, new BytesRef(text));
    }
    
    public Term(final String fld) {
        this(fld, new BytesRef());
    }
    
    public final String field() {
        return this.field;
    }
    
    public final String text() {
        return toString(this.bytes);
    }
    
    public static final String toString(final BytesRef termText) {
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(termText.bytes, termText.offset, termText.length)).toString();
        }
        catch (final CharacterCodingException e) {
            return termText.toString();
        }
    }
    
    public final BytesRef bytes() {
        return this.bytes;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Term other = (Term)obj;
        if (this.field == null) {
            if (other.field != null) {
                return false;
            }
        }
        else if (!this.field.equals(other.field)) {
            return false;
        }
        if (this.bytes == null) {
            if (other.bytes != null) {
                return false;
            }
        }
        else if (!this.bytes.equals(other.bytes)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.field == null) ? 0 : this.field.hashCode());
        result = 31 * result + ((this.bytes == null) ? 0 : this.bytes.hashCode());
        return result;
    }
    
    @Override
    public final int compareTo(final Term other) {
        if (this.field.equals(other.field)) {
            return this.bytes.compareTo(other.bytes);
        }
        return this.field.compareTo(other.field);
    }
    
    final void set(final String fld, final BytesRef bytes) {
        this.field = fld;
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        return this.field + ":" + this.text();
    }
}
