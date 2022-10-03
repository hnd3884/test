package org.msgpack.type;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import java.io.IOException;
import org.msgpack.packer.Packer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

class SequentialMapValueImpl extends AbstractMapValue
{
    private static SequentialMapValueImpl emptyInstance;
    private Value[] array;
    
    public static MapValue getEmptyInstance() {
        return SequentialMapValueImpl.emptyInstance;
    }
    
    @Override
    public Value[] getKeyValueArray() {
        return this.array;
    }
    
    SequentialMapValueImpl(final Value[] array, final boolean gift) {
        if (array.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        if (gift) {
            this.array = array;
        }
        else {
            System.arraycopy(array, 0, this.array = new Value[array.length], 0, array.length);
        }
    }
    
    @Override
    public Value get(final Object key) {
        if (key == null) {
            return null;
        }
        for (int i = this.array.length - 2; i >= 0; i -= 2) {
            if (this.array[i].equals(key)) {
                return this.array[i + 1];
            }
        }
        return null;
    }
    
    @Override
    public Set<Map.Entry<Value, Value>> entrySet() {
        return new EntrySet(this.array);
    }
    
    @Override
    public Set<Value> keySet() {
        return new KeySet(this.array);
    }
    
    @Override
    public Collection<Value> values() {
        return new ValueCollection(this.array);
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.writeMapBegin(this.array.length / 2);
        for (int i = 0; i < this.array.length; ++i) {
            this.array[i].writeTo(pk);
        }
        pk.writeMapEnd();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        final Value v = (Value)o;
        if (!v.isMapValue()) {
            return false;
        }
        final Map<Value, Value> om = v.asMapValue();
        if (om.size() != this.array.length / 2) {
            return false;
        }
        try {
            for (int i = 0; i < this.array.length; i += 2) {
                final Value key = this.array[i];
                final Value value = this.array[i + 1];
                if (!value.equals(om.get(key))) {
                    return false;
                }
            }
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NullPointerException ex2) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < this.array.length; i += 2) {
            h += (this.array[i].hashCode() ^ this.array[i + 1].hashCode());
        }
        return h;
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder()).toString();
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        if (this.array.length == 0) {
            return sb.append("{}");
        }
        sb.append("{");
        sb.append(this.array[0]);
        sb.append(":");
        sb.append(this.array[1]);
        for (int i = 2; i < this.array.length; i += 2) {
            sb.append(",");
            this.array[i].toString(sb);
            sb.append(":");
            this.array[i + 1].toString(sb);
        }
        sb.append("}");
        return sb;
    }
    
    static {
        SequentialMapValueImpl.emptyInstance = new SequentialMapValueImpl(new Value[0], true);
    }
    
    private static class EntrySet extends AbstractSet<Map.Entry<Value, Value>>
    {
        private Value[] array;
        
        EntrySet(final Value[] array) {
            this.array = array;
        }
        
        @Override
        public int size() {
            return this.array.length / 2;
        }
        
        @Override
        public Iterator<Map.Entry<Value, Value>> iterator() {
            return new EntrySetIterator(this.array);
        }
    }
    
    private static class EntrySetIterator implements Iterator<Map.Entry<Value, Value>>
    {
        private Value[] array;
        private int pos;
        
        EntrySetIterator(final Value[] array) {
            this.array = array;
            this.pos = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this.pos < this.array.length;
        }
        
        @Override
        public Map.Entry<Value, Value> next() {
            if (this.pos >= this.array.length) {
                throw new NoSuchElementException();
            }
            final Map.Entry<Value, Value> pair = new SimpleImmutableEntry<Value, Value>(this.array[this.pos], this.array[this.pos + 1]);
            this.pos += 2;
            return pair;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class KeySet extends AbstractSet<Value>
    {
        private Value[] array;
        
        KeySet(final Value[] array) {
            this.array = array;
        }
        
        @Override
        public int size() {
            return this.array.length / 2;
        }
        
        @Override
        public Iterator<Value> iterator() {
            return new ValueIterator(this.array, 0);
        }
    }
    
    private static class ValueCollection extends AbstractCollection<Value>
    {
        private Value[] array;
        
        ValueCollection(final Value[] array) {
            this.array = array;
        }
        
        @Override
        public int size() {
            return this.array.length / 2;
        }
        
        @Override
        public Iterator<Value> iterator() {
            return new ValueIterator(this.array, 1);
        }
    }
    
    private static class ValueIterator implements Iterator<Value>
    {
        private Value[] array;
        private int pos;
        
        ValueIterator(final Value[] array, final int offset) {
            this.array = array;
            this.pos = offset;
        }
        
        @Override
        public boolean hasNext() {
            return this.pos < this.array.length;
        }
        
        @Override
        public Value next() {
            if (this.pos >= this.array.length) {
                throw new NoSuchElementException();
            }
            final Value v = this.array[this.pos];
            this.pos += 2;
            return v;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
