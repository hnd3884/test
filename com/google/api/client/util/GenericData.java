package com.google.api.client.util;

import java.util.AbstractSet;
import java.util.Objects;
import java.util.Set;
import java.util.Iterator;
import java.util.Locale;
import java.util.EnumSet;
import java.util.Map;
import java.util.AbstractMap;

public class GenericData extends AbstractMap<String, Object> implements Cloneable
{
    Map<String, Object> unknownFields;
    final ClassInfo classInfo;
    
    public GenericData() {
        this(EnumSet.noneOf(Flags.class));
    }
    
    public GenericData(final EnumSet<Flags> flags) {
        this.unknownFields = (Map<String, Object>)ArrayMap.create();
        this.classInfo = ClassInfo.of(this.getClass(), flags.contains(Flags.IGNORE_CASE));
    }
    
    @Override
    public final Object get(final Object name) {
        if (!(name instanceof String)) {
            return null;
        }
        String fieldName = (String)name;
        final FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            return fieldInfo.getValue(this);
        }
        if (this.classInfo.getIgnoreCase()) {
            fieldName = fieldName.toLowerCase(Locale.US);
        }
        return this.unknownFields.get(fieldName);
    }
    
    @Override
    public final Object put(String fieldName, final Object value) {
        final FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            final Object oldValue = fieldInfo.getValue(this);
            fieldInfo.setValue(this, value);
            return oldValue;
        }
        if (this.classInfo.getIgnoreCase()) {
            fieldName = fieldName.toLowerCase(Locale.US);
        }
        return this.unknownFields.put(fieldName, value);
    }
    
    public GenericData set(String fieldName, final Object value) {
        final FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            fieldInfo.setValue(this, value);
        }
        else {
            if (this.classInfo.getIgnoreCase()) {
                fieldName = fieldName.toLowerCase(Locale.US);
            }
            this.unknownFields.put(fieldName, value);
        }
        return this;
    }
    
    @Override
    public final void putAll(final Map<? extends String, ?> map) {
        for (final Map.Entry<? extends String, ?> entry : map.entrySet()) {
            this.set((String)entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public final Object remove(final Object name) {
        if (!(name instanceof String)) {
            return null;
        }
        String fieldName = (String)name;
        final FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            throw new UnsupportedOperationException();
        }
        if (this.classInfo.getIgnoreCase()) {
            fieldName = fieldName.toLowerCase(Locale.US);
        }
        return this.unknownFields.remove(fieldName);
    }
    
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return new EntrySet();
    }
    
    public GenericData clone() {
        try {
            final GenericData result = (GenericData)super.clone();
            Data.deepCopy(this, result);
            result.unknownFields = Data.clone(this.unknownFields);
            return result;
        }
        catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final Map<String, Object> getUnknownKeys() {
        return this.unknownFields;
    }
    
    public final void setUnknownKeys(final Map<String, Object> unknownFields) {
        this.unknownFields = unknownFields;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof GenericData)) {
            return false;
        }
        final GenericData that = (GenericData)o;
        return super.equals(that) && Objects.equals(this.classInfo, that.classInfo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.classInfo);
    }
    
    @Override
    public String toString() {
        return "GenericData{classInfo=" + this.classInfo.names + ", " + super.toString() + "}";
    }
    
    public final ClassInfo getClassInfo() {
        return this.classInfo;
    }
    
    public enum Flags
    {
        IGNORE_CASE;
    }
    
    final class EntrySet extends AbstractSet<Map.Entry<String, Object>>
    {
        private final DataMap.EntrySet dataEntrySet;
        
        EntrySet() {
            this.dataEntrySet = new DataMap(GenericData.this, GenericData.this.classInfo.getIgnoreCase()).entrySet();
        }
        
        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            return new EntryIterator(this.dataEntrySet);
        }
        
        @Override
        public int size() {
            return GenericData.this.unknownFields.size() + this.dataEntrySet.size();
        }
        
        @Override
        public void clear() {
            GenericData.this.unknownFields.clear();
            this.dataEntrySet.clear();
        }
    }
    
    final class EntryIterator implements Iterator<Map.Entry<String, Object>>
    {
        private boolean startedUnknown;
        private final Iterator<Map.Entry<String, Object>> fieldIterator;
        private final Iterator<Map.Entry<String, Object>> unknownIterator;
        
        EntryIterator(final DataMap.EntrySet dataEntrySet) {
            this.fieldIterator = dataEntrySet.iterator();
            this.unknownIterator = GenericData.this.unknownFields.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.fieldIterator.hasNext() || this.unknownIterator.hasNext();
        }
        
        @Override
        public Map.Entry<String, Object> next() {
            if (!this.startedUnknown) {
                if (this.fieldIterator.hasNext()) {
                    return this.fieldIterator.next();
                }
                this.startedUnknown = true;
            }
            return this.unknownIterator.next();
        }
        
        @Override
        public void remove() {
            if (this.startedUnknown) {
                this.unknownIterator.remove();
            }
            this.fieldIterator.remove();
        }
    }
}
