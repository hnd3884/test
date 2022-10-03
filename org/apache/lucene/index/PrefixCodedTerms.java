package org.apache.lucene.index;

import org.apache.lucene.store.RAMInputStream;
import org.apache.lucene.store.IndexInput;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.store.RAMOutputStream;
import java.util.Collections;
import java.util.Collection;
import java.util.Objects;
import org.apache.lucene.store.RAMFile;
import org.apache.lucene.util.Accountable;

public class PrefixCodedTerms implements Accountable
{
    final RAMFile buffer;
    private final long size;
    private long delGen;
    
    private PrefixCodedTerms(final RAMFile buffer, final long size) {
        this.buffer = Objects.requireNonNull(buffer);
        this.size = size;
    }
    
    @Override
    public long ramBytesUsed() {
        return this.buffer.ramBytesUsed() + 16L;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public void setDelGen(final long delGen) {
        this.delGen = delGen;
    }
    
    public TermIterator iterator() {
        return new TermIterator(this.delGen, this.buffer);
    }
    
    public long size() {
        return this.size;
    }
    
    @Override
    public int hashCode() {
        int h = this.buffer.hashCode();
        h = 31 * h + (int)(this.delGen ^ this.delGen >>> 32);
        return h;
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
        final PrefixCodedTerms other = (PrefixCodedTerms)obj;
        return this.buffer.equals(other.buffer) && this.delGen == other.delGen;
    }
    
    public static class Builder
    {
        private RAMFile buffer;
        private RAMOutputStream output;
        private Term lastTerm;
        private BytesRefBuilder lastTermBytes;
        private long size;
        
        public Builder() {
            this.buffer = new RAMFile();
            this.output = new RAMOutputStream(this.buffer, false);
            this.lastTerm = new Term("");
            this.lastTermBytes = new BytesRefBuilder();
        }
        
        public void add(final Term term) {
            this.add(term.field(), term.bytes());
        }
        
        public void add(final String field, final BytesRef bytes) {
            assert new Term(field, bytes).compareTo(this.lastTerm) > 0;
            try {
                final int prefix = this.sharedPrefix(this.lastTerm.bytes, bytes);
                final int suffix = bytes.length - prefix;
                if (field.equals(this.lastTerm.field)) {
                    this.output.writeVInt(prefix << 1);
                }
                else {
                    this.output.writeVInt(prefix << 1 | 0x1);
                    this.output.writeString(field);
                }
                this.output.writeVInt(suffix);
                this.output.writeBytes(bytes.bytes, bytes.offset + prefix, suffix);
                this.lastTermBytes.copyBytes(bytes);
                this.lastTerm.bytes = this.lastTermBytes.get();
                this.lastTerm.field = field;
                ++this.size;
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        public PrefixCodedTerms finish() {
            try {
                this.output.close();
                return new PrefixCodedTerms(this.buffer, this.size, null);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        private int sharedPrefix(final BytesRef term1, final BytesRef term2) {
            int pos1 = 0;
            for (int pos1End = pos1 + Math.min(term1.length, term2.length), pos2 = 0; pos1 < pos1End; ++pos1, ++pos2) {
                if (term1.bytes[term1.offset + pos1] != term2.bytes[term2.offset + pos2]) {
                    return pos1;
                }
            }
            return pos1;
        }
    }
    
    public static class TermIterator extends FieldTermIterator
    {
        final IndexInput input;
        final BytesRefBuilder builder;
        final BytesRef bytes;
        final long end;
        final long delGen;
        String field;
        
        private TermIterator(final long delGen, final RAMFile buffer) {
            this.builder = new BytesRefBuilder();
            this.bytes = this.builder.get();
            this.field = "";
            try {
                this.input = new RAMInputStream("MergedPrefixCodedTermsIterator", buffer);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
            this.end = this.input.length();
            this.delGen = delGen;
        }
        
        @Override
        public BytesRef next() {
            if (this.input.getFilePointer() < this.end) {
                try {
                    final int code = this.input.readVInt();
                    final boolean newField = (code & 0x1) != 0x0;
                    if (newField) {
                        this.field = this.input.readString();
                    }
                    final int prefix = code >>> 1;
                    final int suffix = this.input.readVInt();
                    this.readTermBytes(prefix, suffix);
                    return this.bytes;
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.field = null;
            return null;
        }
        
        private void readTermBytes(final int prefix, final int suffix) throws IOException {
            this.builder.grow(prefix + suffix);
            this.input.readBytes(this.builder.bytes(), prefix, suffix);
            this.builder.setLength(prefix + suffix);
        }
        
        public String field() {
            return this.field;
        }
        
        public long delGen() {
            return this.delGen;
        }
    }
}
