package org.apache.lucene.index;

import java.util.Objects;
import java.util.Map;

public final class FieldInfo
{
    public final String name;
    public final int number;
    private DocValuesType docValuesType;
    private boolean storeTermVector;
    private boolean omitNorms;
    private IndexOptions indexOptions;
    private boolean storePayloads;
    private final Map<String, String> attributes;
    private long dvGen;
    
    public FieldInfo(final String name, final int number, final boolean storeTermVector, final boolean omitNorms, final boolean storePayloads, final IndexOptions indexOptions, final DocValuesType docValues, final long dvGen, final Map<String, String> attributes) {
        this.docValuesType = DocValuesType.NONE;
        this.indexOptions = IndexOptions.NONE;
        this.name = Objects.requireNonNull(name);
        this.number = number;
        this.docValuesType = Objects.requireNonNull(docValues, "DocValuesType cannot be null (field: \"" + name + "\")");
        this.indexOptions = Objects.requireNonNull(indexOptions, "IndexOptions cannot be null (field: \"" + name + "\")");
        if (indexOptions != IndexOptions.NONE) {
            this.storeTermVector = storeTermVector;
            this.storePayloads = storePayloads;
            this.omitNorms = omitNorms;
        }
        else {
            this.storeTermVector = false;
            this.storePayloads = false;
            this.omitNorms = false;
        }
        this.dvGen = dvGen;
        this.attributes = Objects.requireNonNull(attributes);
        assert this.checkConsistency();
    }
    
    public boolean checkConsistency() {
        if (this.indexOptions != IndexOptions.NONE) {
            if (this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0 && this.storePayloads) {
                throw new IllegalStateException("indexed field '" + this.name + "' cannot have payloads without positions");
            }
        }
        else {
            if (this.storeTermVector) {
                throw new IllegalStateException("non-indexed field '" + this.name + "' cannot store term vectors");
            }
            if (this.storePayloads) {
                throw new IllegalStateException("non-indexed field '" + this.name + "' cannot store payloads");
            }
            if (this.omitNorms) {
                throw new IllegalStateException("non-indexed field '" + this.name + "' cannot omit norms");
            }
        }
        if (this.dvGen != -1L && this.docValuesType == DocValuesType.NONE) {
            throw new IllegalStateException("field '" + this.name + "' cannot have a docvalues update generation without having docvalues");
        }
        return true;
    }
    
    void update(final boolean storeTermVector, final boolean omitNorms, final boolean storePayloads, final IndexOptions indexOptions) {
        if (indexOptions == null) {
            throw new NullPointerException("IndexOptions cannot be null (field: \"" + this.name + "\")");
        }
        if (this.indexOptions != indexOptions) {
            if (this.indexOptions == IndexOptions.NONE) {
                this.indexOptions = indexOptions;
            }
            else if (indexOptions != IndexOptions.NONE) {
                this.indexOptions = ((this.indexOptions.compareTo(indexOptions) < 0) ? this.indexOptions : indexOptions);
            }
        }
        if (this.indexOptions != IndexOptions.NONE) {
            this.storeTermVector |= storeTermVector;
            this.storePayloads |= storePayloads;
            if (indexOptions != IndexOptions.NONE && this.omitNorms != omitNorms) {
                this.omitNorms = true;
            }
        }
        if (this.indexOptions == IndexOptions.NONE || this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
            this.storePayloads = false;
        }
        assert this.checkConsistency();
    }
    
    void setDocValuesType(final DocValuesType type) {
        if (type == null) {
            throw new NullPointerException("DocValuesType cannot be null (field: \"" + this.name + "\")");
        }
        if (this.docValuesType != DocValuesType.NONE && type != DocValuesType.NONE && this.docValuesType != type) {
            throw new IllegalArgumentException("cannot change DocValues type from " + this.docValuesType + " to " + type + " for field \"" + this.name + "\"");
        }
        this.docValuesType = type;
        assert this.checkConsistency();
    }
    
    public IndexOptions getIndexOptions() {
        return this.indexOptions;
    }
    
    public void setIndexOptions(final IndexOptions newIndexOptions) {
        if (this.indexOptions != newIndexOptions) {
            if (this.indexOptions == IndexOptions.NONE) {
                this.indexOptions = newIndexOptions;
            }
            else if (newIndexOptions != IndexOptions.NONE) {
                this.indexOptions = ((this.indexOptions.compareTo(newIndexOptions) < 0) ? this.indexOptions : newIndexOptions);
            }
        }
        if (this.indexOptions == IndexOptions.NONE || this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
            this.storePayloads = false;
        }
    }
    
    public DocValuesType getDocValuesType() {
        return this.docValuesType;
    }
    
    void setDocValuesGen(final long dvGen) {
        this.dvGen = dvGen;
        assert this.checkConsistency();
    }
    
    public long getDocValuesGen() {
        return this.dvGen;
    }
    
    void setStoreTermVectors() {
        this.storeTermVector = true;
        assert this.checkConsistency();
    }
    
    void setStorePayloads() {
        if (this.indexOptions != IndexOptions.NONE && this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0) {
            this.storePayloads = true;
        }
        assert this.checkConsistency();
    }
    
    public boolean omitsNorms() {
        return this.omitNorms;
    }
    
    public void setOmitsNorms() {
        if (this.indexOptions == IndexOptions.NONE) {
            throw new IllegalStateException("cannot omit norms: this field is not indexed");
        }
        this.omitNorms = true;
    }
    
    public boolean hasNorms() {
        return this.indexOptions != IndexOptions.NONE && !this.omitNorms;
    }
    
    public boolean hasPayloads() {
        return this.storePayloads;
    }
    
    public boolean hasVectors() {
        return this.storeTermVector;
    }
    
    public String getAttribute(final String key) {
        return this.attributes.get(key);
    }
    
    public String putAttribute(final String key, final String value) {
        return this.attributes.put(key, value);
    }
    
    public Map<String, String> attributes() {
        return this.attributes;
    }
}
