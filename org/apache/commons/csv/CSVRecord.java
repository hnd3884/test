package org.apache.commons.csv;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

public final class CSVRecord implements Serializable, Iterable<String>
{
    private static final String[] EMPTY_STRING_ARRAY;
    private static final long serialVersionUID = 1L;
    private final long characterPosition;
    private final String comment;
    private final Map<String, Integer> mapping;
    private final long recordNumber;
    private final String[] values;
    
    CSVRecord(final String[] values, final Map<String, Integer> mapping, final String comment, final long recordNumber, final long characterPosition) {
        this.recordNumber = recordNumber;
        this.values = ((values != null) ? values : CSVRecord.EMPTY_STRING_ARRAY);
        this.mapping = mapping;
        this.comment = comment;
        this.characterPosition = characterPosition;
    }
    
    public String get(final Enum<?> e) {
        return this.get(e.toString());
    }
    
    public String get(final int i) {
        return this.values[i];
    }
    
    public String get(final String name) {
        if (this.mapping == null) {
            throw new IllegalStateException("No header mapping was specified, the record values can't be accessed by name");
        }
        final Integer index = this.mapping.get(name);
        if (index == null) {
            throw new IllegalArgumentException(String.format("Mapping for %s not found, expected one of %s", name, this.mapping.keySet()));
        }
        try {
            return this.values[index];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("Index for header '%s' is %d but CSVRecord only has %d values!", name, index, this.values.length));
        }
    }
    
    public long getCharacterPosition() {
        return this.characterPosition;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public long getRecordNumber() {
        return this.recordNumber;
    }
    
    public boolean isConsistent() {
        return this.mapping == null || this.mapping.size() == this.values.length;
    }
    
    public boolean hasComment() {
        return this.comment != null;
    }
    
    public boolean isMapped(final String name) {
        return this.mapping != null && this.mapping.containsKey(name);
    }
    
    public boolean isSet(final String name) {
        return this.isMapped(name) && this.mapping.get(name) < this.values.length;
    }
    
    @Override
    public Iterator<String> iterator() {
        return this.toList().iterator();
    }
    
     <M extends Map<String, String>> M putIn(final M map) {
        if (this.mapping == null) {
            return map;
        }
        for (final Map.Entry<String, Integer> entry : this.mapping.entrySet()) {
            final int col = entry.getValue();
            if (col < this.values.length) {
                map.put(entry.getKey(), this.values[col]);
            }
        }
        return map;
    }
    
    public int size() {
        return this.values.length;
    }
    
    private List<String> toList() {
        return Arrays.asList(this.values);
    }
    
    public Map<String, String> toMap() {
        return this.putIn(new HashMap(this.values.length));
    }
    
    @Override
    public String toString() {
        return "CSVRecord [comment=" + this.comment + ", mapping=" + this.mapping + ", recordNumber=" + this.recordNumber + ", values=" + Arrays.toString(this.values) + "]";
    }
    
    String[] values() {
        return this.values;
    }
    
    static {
        EMPTY_STRING_ARRAY = new String[0];
    }
}
