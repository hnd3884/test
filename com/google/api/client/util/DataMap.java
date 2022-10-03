package com.google.api.client.util;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Map;
import java.util.AbstractSet;
import java.util.Set;
import java.util.AbstractMap;

final class DataMap extends AbstractMap<String, Object>
{
    final Object object;
    final ClassInfo classInfo;
    
    DataMap(final Object object, final boolean ignoreCase) {
        this.object = object;
        this.classInfo = ClassInfo.of(object.getClass(), ignoreCase);
    }
    
    @Override
    public EntrySet entrySet() {
        return new EntrySet();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    @Override
    public Object get(final Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        final FieldInfo fieldInfo = this.classInfo.getFieldInfo((String)key);
        if (fieldInfo == null) {
            return null;
        }
        return fieldInfo.getValue(this.object);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        final FieldInfo fieldInfo = this.classInfo.getFieldInfo(key);
        Preconditions.checkNotNull(fieldInfo, (Object)("no field of key " + key));
        final Object oldValue = fieldInfo.getValue(this.object);
        fieldInfo.setValue(this.object, Preconditions.checkNotNull(value));
        return oldValue;
    }
    
    final class EntrySet extends AbstractSet<Map.Entry<String, Object>>
    {
        @Override
        public EntryIterator iterator() {
            return new EntryIterator();
        }
        
        @Override
        public int size() {
            int result = 0;
            for (final String name : DataMap.this.classInfo.names) {
                if (DataMap.this.classInfo.getFieldInfo(name).getValue(DataMap.this.object) != null) {
                    ++result;
                }
            }
            return result;
        }
        
        @Override
        public void clear() {
            for (final String name : DataMap.this.classInfo.names) {
                DataMap.this.classInfo.getFieldInfo(name).setValue(DataMap.this.object, null);
            }
        }
        
        @Override
        public boolean isEmpty() {
            for (final String name : DataMap.this.classInfo.names) {
                if (DataMap.this.classInfo.getFieldInfo(name).getValue(DataMap.this.object) != null) {
                    return false;
                }
            }
            return true;
        }
    }
    
    final class EntryIterator implements Iterator<Map.Entry<String, Object>>
    {
        private int nextKeyIndex;
        private FieldInfo nextFieldInfo;
        private Object nextFieldValue;
        private boolean isRemoved;
        private boolean isComputed;
        private FieldInfo currentFieldInfo;
        
        EntryIterator() {
            this.nextKeyIndex = -1;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.isComputed) {
                this.isComputed = true;
                this.nextFieldValue = null;
                while (this.nextFieldValue == null && ++this.nextKeyIndex < DataMap.this.classInfo.names.size()) {
                    this.nextFieldInfo = DataMap.this.classInfo.getFieldInfo(DataMap.this.classInfo.names.get(this.nextKeyIndex));
                    this.nextFieldValue = this.nextFieldInfo.getValue(DataMap.this.object);
                }
            }
            return this.nextFieldValue != null;
        }
        
        @Override
        public Map.Entry<String, Object> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.currentFieldInfo = this.nextFieldInfo;
            final Object currentFieldValue = this.nextFieldValue;
            this.isComputed = false;
            this.isRemoved = false;
            this.nextFieldInfo = null;
            this.nextFieldValue = null;
            return new Entry(this.currentFieldInfo, currentFieldValue);
        }
        
        @Override
        public void remove() {
            Preconditions.checkState(this.currentFieldInfo != null && !this.isRemoved);
            this.isRemoved = true;
            this.currentFieldInfo.setValue(DataMap.this.object, null);
        }
    }
    
    final class Entry implements Map.Entry<String, Object>
    {
        private Object fieldValue;
        private final FieldInfo fieldInfo;
        
        Entry(final FieldInfo fieldInfo, final Object fieldValue) {
            this.fieldInfo = fieldInfo;
            this.fieldValue = Preconditions.checkNotNull(fieldValue);
        }
        
        @Override
        public String getKey() {
            String result = this.fieldInfo.getName();
            if (DataMap.this.classInfo.getIgnoreCase()) {
                result = result.toLowerCase(Locale.US);
            }
            return result;
        }
        
        @Override
        public Object getValue() {
            return this.fieldValue;
        }
        
        @Override
        public Object setValue(final Object value) {
            final Object oldValue = this.fieldValue;
            this.fieldValue = Preconditions.checkNotNull(value);
            this.fieldInfo.setValue(DataMap.this.object, value);
            return oldValue;
        }
        
        @Override
        public int hashCode() {
            return this.getKey().hashCode() ^ this.getValue().hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>)obj;
            return this.getKey().equals(other.getKey()) && this.getValue().equals(other.getValue());
        }
    }
}
