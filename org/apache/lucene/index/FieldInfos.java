package org.apache.lucene.index;

import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.util.ArrayUtil;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.SortedMap;

public class FieldInfos implements Iterable<FieldInfo>
{
    private final boolean hasFreq;
    private final boolean hasProx;
    private final boolean hasPayloads;
    private final boolean hasOffsets;
    private final boolean hasVectors;
    private final boolean hasNorms;
    private final boolean hasDocValues;
    private final FieldInfo[] byNumberTable;
    private final SortedMap<Integer, FieldInfo> byNumberMap;
    private final HashMap<String, FieldInfo> byName;
    private final Collection<FieldInfo> values;
    
    public FieldInfos(final FieldInfo[] infos) {
        this.byName = new HashMap<String, FieldInfo>();
        boolean hasVectors = false;
        boolean hasProx = false;
        boolean hasPayloads = false;
        boolean hasOffsets = false;
        boolean hasFreq = false;
        boolean hasNorms = false;
        boolean hasDocValues = false;
        final TreeMap<Integer, FieldInfo> byNumber = new TreeMap<Integer, FieldInfo>();
        for (final FieldInfo info : infos) {
            if (info.number < 0) {
                throw new IllegalArgumentException("illegal field number: " + info.number + " for field " + info.name);
            }
            FieldInfo previous = byNumber.put(info.number, info);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate field numbers: " + previous.name + " and " + info.name + " have: " + info.number);
            }
            previous = this.byName.put(info.name, info);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate field names: " + previous.number + " and " + info.number + " have: " + info.name);
            }
            hasVectors |= info.hasVectors();
            hasProx |= (info.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0);
            hasFreq |= (info.getIndexOptions() != IndexOptions.DOCS);
            hasOffsets |= (info.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0);
            hasNorms |= info.hasNorms();
            hasDocValues |= (info.getDocValuesType() != DocValuesType.NONE);
            hasPayloads |= info.hasPayloads();
        }
        this.hasVectors = hasVectors;
        this.hasProx = hasProx;
        this.hasPayloads = hasPayloads;
        this.hasOffsets = hasOffsets;
        this.hasFreq = hasFreq;
        this.hasNorms = hasNorms;
        this.hasDocValues = hasDocValues;
        this.values = Collections.unmodifiableCollection((Collection<? extends FieldInfo>)byNumber.values());
        final Integer max = byNumber.isEmpty() ? null : Collections.max((Collection<? extends Integer>)byNumber.keySet());
        if (max != null && max < ArrayUtil.MAX_ARRAY_LENGTH && max < 16L * byNumber.size()) {
            this.byNumberMap = null;
            this.byNumberTable = new FieldInfo[max + 1];
            for (final Map.Entry<Integer, FieldInfo> entry : byNumber.entrySet()) {
                this.byNumberTable[entry.getKey()] = entry.getValue();
            }
        }
        else {
            this.byNumberMap = byNumber;
            this.byNumberTable = null;
        }
    }
    
    public boolean hasFreq() {
        return this.hasFreq;
    }
    
    public boolean hasProx() {
        return this.hasProx;
    }
    
    public boolean hasPayloads() {
        return this.hasPayloads;
    }
    
    public boolean hasOffsets() {
        return this.hasOffsets;
    }
    
    public boolean hasVectors() {
        return this.hasVectors;
    }
    
    public boolean hasNorms() {
        return this.hasNorms;
    }
    
    public boolean hasDocValues() {
        return this.hasDocValues;
    }
    
    public int size() {
        return this.byName.size();
    }
    
    @Override
    public Iterator<FieldInfo> iterator() {
        return this.values.iterator();
    }
    
    public FieldInfo fieldInfo(final String fieldName) {
        return this.byName.get(fieldName);
    }
    
    public FieldInfo fieldInfo(final int fieldNumber) {
        if (fieldNumber < 0) {
            throw new IllegalArgumentException("Illegal field number: " + fieldNumber);
        }
        if (this.byNumberTable == null) {
            return this.byNumberMap.get(fieldNumber);
        }
        if (fieldNumber >= this.byNumberTable.length) {
            return null;
        }
        return this.byNumberTable[fieldNumber];
    }
    
    static final class FieldNumbers
    {
        private final Map<Integer, String> numberToName;
        private final Map<String, Integer> nameToNumber;
        private final Map<String, DocValuesType> docValuesType;
        private int lowestUnassignedFieldNumber;
        
        FieldNumbers() {
            this.lowestUnassignedFieldNumber = -1;
            this.nameToNumber = new HashMap<String, Integer>();
            this.numberToName = new HashMap<Integer, String>();
            this.docValuesType = new HashMap<String, DocValuesType>();
        }
        
        synchronized int addOrGet(final String fieldName, final int preferredFieldNumber, final DocValuesType dvType) {
            if (dvType != DocValuesType.NONE) {
                final DocValuesType currentDVType = this.docValuesType.get(fieldName);
                if (currentDVType == null) {
                    this.docValuesType.put(fieldName, dvType);
                }
                else if (currentDVType != DocValuesType.NONE && currentDVType != dvType) {
                    throw new IllegalArgumentException("cannot change DocValues type from " + currentDVType + " to " + dvType + " for field \"" + fieldName + "\"");
                }
            }
            Integer fieldNumber = this.nameToNumber.get(fieldName);
            if (fieldNumber == null) {
                final Integer preferredBoxed = preferredFieldNumber;
                if (preferredFieldNumber != -1 && !this.numberToName.containsKey(preferredBoxed)) {
                    fieldNumber = preferredBoxed;
                }
                else {
                    while (this.numberToName.containsKey(++this.lowestUnassignedFieldNumber)) {}
                    fieldNumber = this.lowestUnassignedFieldNumber;
                }
                assert fieldNumber >= 0;
                this.numberToName.put(fieldNumber, fieldName);
                this.nameToNumber.put(fieldName, fieldNumber);
            }
            return fieldNumber;
        }
        
        synchronized void verifyConsistent(final Integer number, final String name, final DocValuesType dvType) {
            if (!name.equals(this.numberToName.get(number))) {
                throw new IllegalArgumentException("field number " + number + " is already mapped to field name \"" + this.numberToName.get(number) + "\", not \"" + name + "\"");
            }
            if (!number.equals(this.nameToNumber.get(name))) {
                throw new IllegalArgumentException("field name \"" + name + "\" is already mapped to field number \"" + this.nameToNumber.get(name) + "\", not \"" + number + "\"");
            }
            final DocValuesType currentDVType = this.docValuesType.get(name);
            if (dvType != DocValuesType.NONE && currentDVType != null && currentDVType != DocValuesType.NONE && dvType != currentDVType) {
                throw new IllegalArgumentException("cannot change DocValues type from " + currentDVType + " to " + dvType + " for field \"" + name + "\"");
            }
        }
        
        synchronized boolean contains(final String fieldName, final DocValuesType dvType) {
            return this.nameToNumber.containsKey(fieldName) && dvType == this.docValuesType.get(fieldName);
        }
        
        synchronized void clear() {
            this.numberToName.clear();
            this.nameToNumber.clear();
            this.docValuesType.clear();
        }
        
        synchronized void setDocValuesType(final int number, final String name, final DocValuesType dvType) {
            this.verifyConsistent(number, name, dvType);
            this.docValuesType.put(name, dvType);
        }
    }
    
    static final class Builder
    {
        private final HashMap<String, FieldInfo> byName;
        final FieldNumbers globalFieldNumbers;
        
        Builder() {
            this(new FieldNumbers());
        }
        
        Builder(final FieldNumbers globalFieldNumbers) {
            this.byName = new HashMap<String, FieldInfo>();
            assert globalFieldNumbers != null;
            this.globalFieldNumbers = globalFieldNumbers;
        }
        
        public void add(final FieldInfos other) {
            for (final FieldInfo fieldInfo : other) {
                this.add(fieldInfo);
            }
        }
        
        public FieldInfo getOrAdd(final String name) {
            FieldInfo fi = this.fieldInfo(name);
            if (fi == null) {
                final int fieldNumber = this.globalFieldNumbers.addOrGet(name, -1, DocValuesType.NONE);
                fi = new FieldInfo(name, fieldNumber, false, false, false, IndexOptions.NONE, DocValuesType.NONE, -1L, new HashMap<String, String>());
                assert !this.byName.containsKey(fi.name);
                this.globalFieldNumbers.verifyConsistent(fi.number, fi.name, DocValuesType.NONE);
                this.byName.put(fi.name, fi);
            }
            return fi;
        }
        
        private FieldInfo addOrUpdateInternal(final String name, final int preferredFieldNumber, final boolean storeTermVector, final boolean omitNorms, final boolean storePayloads, final IndexOptions indexOptions, final DocValuesType docValues) {
            if (docValues == null) {
                throw new NullPointerException("DocValuesType cannot be null");
            }
            FieldInfo fi = this.fieldInfo(name);
            if (fi == null) {
                final int fieldNumber = this.globalFieldNumbers.addOrGet(name, preferredFieldNumber, docValues);
                fi = new FieldInfo(name, fieldNumber, storeTermVector, omitNorms, storePayloads, indexOptions, docValues, -1L, new HashMap<String, String>());
                assert !this.byName.containsKey(fi.name);
                this.globalFieldNumbers.verifyConsistent(fi.number, fi.name, fi.getDocValuesType());
                this.byName.put(fi.name, fi);
            }
            else {
                fi.update(storeTermVector, omitNorms, storePayloads, indexOptions);
                if (docValues != DocValuesType.NONE) {
                    final boolean updateGlobal = fi.getDocValuesType() == DocValuesType.NONE;
                    if (updateGlobal) {
                        this.globalFieldNumbers.setDocValuesType(fi.number, name, docValues);
                    }
                    fi.setDocValuesType(docValues);
                }
            }
            return fi;
        }
        
        public FieldInfo add(final FieldInfo fi) {
            return this.addOrUpdateInternal(fi.name, fi.number, fi.hasVectors(), fi.omitsNorms(), fi.hasPayloads(), fi.getIndexOptions(), fi.getDocValuesType());
        }
        
        public FieldInfo fieldInfo(final String fieldName) {
            return this.byName.get(fieldName);
        }
        
        FieldInfos finish() {
            return new FieldInfos(this.byName.values().toArray(new FieldInfo[this.byName.size()]));
        }
    }
}
