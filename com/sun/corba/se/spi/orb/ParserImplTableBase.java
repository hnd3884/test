package com.sun.corba.se.spi.orb;

import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Set;
import java.util.AbstractMap;
import java.util.Map;

public abstract class ParserImplTableBase extends ParserImplBase
{
    private final ParserData[] entries;
    
    public ParserImplTableBase(final ParserData[] entries) {
        this.entries = entries;
        this.setDefaultValues();
    }
    
    @Override
    protected PropertyParser makeParser() {
        final PropertyParser propertyParser = new PropertyParser();
        for (int i = 0; i < this.entries.length; ++i) {
            this.entries[i].addToParser(propertyParser);
        }
        return propertyParser;
    }
    
    protected void setDefaultValues() {
        this.setFields(new FieldMap(this.entries, true));
    }
    
    public void setTestValues() {
        this.setFields(new FieldMap(this.entries, false));
    }
    
    private static final class MapEntry implements Map.Entry
    {
        private Object key;
        private Object value;
        
        public MapEntry(final Object key) {
            this.key = key;
        }
        
        @Override
        public Object getKey() {
            return this.key;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public Object setValue(final Object value) {
            final Object value2 = this.value;
            this.value = value;
            return value2;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof MapEntry)) {
                return false;
            }
            final MapEntry mapEntry = (MapEntry)o;
            return this.key.equals(mapEntry.key) && this.value.equals(mapEntry.value);
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }
    }
    
    private static class FieldMap extends AbstractMap
    {
        private final ParserData[] entries;
        private final boolean useDefault;
        
        public FieldMap(final ParserData[] entries, final boolean useDefault) {
            this.entries = entries;
            this.useDefault = useDefault;
        }
        
        @Override
        public Set entrySet() {
            return new AbstractSet() {
                @Override
                public Iterator iterator() {
                    return new Iterator() {
                        int ctr = 0;
                        
                        @Override
                        public boolean hasNext() {
                            return this.ctr < FieldMap.this.entries.length;
                        }
                        
                        @Override
                        public Object next() {
                            final ParserData parserData = FieldMap.this.entries[this.ctr++];
                            final MapEntry mapEntry = new MapEntry(parserData.getFieldName());
                            if (FieldMap.this.useDefault) {
                                mapEntry.setValue(parserData.getDefaultValue());
                            }
                            else {
                                mapEntry.setValue(parserData.getTestValue());
                            }
                            return mapEntry;
                        }
                        
                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
                
                @Override
                public int size() {
                    return FieldMap.this.entries.length;
                }
            };
        }
    }
}
