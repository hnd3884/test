package org.apache.lucene.document;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableFieldType;

public class FieldType implements IndexableFieldType
{
    private boolean stored;
    private boolean tokenized;
    private boolean storeTermVectors;
    private boolean storeTermVectorOffsets;
    private boolean storeTermVectorPositions;
    private boolean storeTermVectorPayloads;
    private boolean omitNorms;
    private IndexOptions indexOptions;
    private NumericType numericType;
    private boolean frozen;
    private int numericPrecisionStep;
    private DocValuesType docValuesType;
    
    public FieldType(final FieldType ref) {
        this.tokenized = true;
        this.indexOptions = IndexOptions.NONE;
        this.numericPrecisionStep = 16;
        this.docValuesType = DocValuesType.NONE;
        this.stored = ref.stored();
        this.tokenized = ref.tokenized();
        this.storeTermVectors = ref.storeTermVectors();
        this.storeTermVectorOffsets = ref.storeTermVectorOffsets();
        this.storeTermVectorPositions = ref.storeTermVectorPositions();
        this.storeTermVectorPayloads = ref.storeTermVectorPayloads();
        this.omitNorms = ref.omitNorms();
        this.indexOptions = ref.indexOptions();
        this.numericType = ref.numericType();
        this.numericPrecisionStep = ref.numericPrecisionStep();
        this.docValuesType = ref.docValuesType();
    }
    
    public FieldType() {
        this.tokenized = true;
        this.indexOptions = IndexOptions.NONE;
        this.numericPrecisionStep = 16;
        this.docValuesType = DocValuesType.NONE;
    }
    
    protected void checkIfFrozen() {
        if (this.frozen) {
            throw new IllegalStateException("this FieldType is already frozen and cannot be changed");
        }
    }
    
    public void freeze() {
        this.frozen = true;
    }
    
    @Override
    public boolean stored() {
        return this.stored;
    }
    
    public void setStored(final boolean value) {
        this.checkIfFrozen();
        this.stored = value;
    }
    
    @Override
    public boolean tokenized() {
        return this.tokenized;
    }
    
    public void setTokenized(final boolean value) {
        this.checkIfFrozen();
        this.tokenized = value;
    }
    
    @Override
    public boolean storeTermVectors() {
        return this.storeTermVectors;
    }
    
    public void setStoreTermVectors(final boolean value) {
        this.checkIfFrozen();
        this.storeTermVectors = value;
    }
    
    @Override
    public boolean storeTermVectorOffsets() {
        return this.storeTermVectorOffsets;
    }
    
    public void setStoreTermVectorOffsets(final boolean value) {
        this.checkIfFrozen();
        this.storeTermVectorOffsets = value;
    }
    
    @Override
    public boolean storeTermVectorPositions() {
        return this.storeTermVectorPositions;
    }
    
    public void setStoreTermVectorPositions(final boolean value) {
        this.checkIfFrozen();
        this.storeTermVectorPositions = value;
    }
    
    @Override
    public boolean storeTermVectorPayloads() {
        return this.storeTermVectorPayloads;
    }
    
    public void setStoreTermVectorPayloads(final boolean value) {
        this.checkIfFrozen();
        this.storeTermVectorPayloads = value;
    }
    
    @Override
    public boolean omitNorms() {
        return this.omitNorms;
    }
    
    public void setOmitNorms(final boolean value) {
        this.checkIfFrozen();
        this.omitNorms = value;
    }
    
    @Override
    public IndexOptions indexOptions() {
        return this.indexOptions;
    }
    
    public void setIndexOptions(final IndexOptions value) {
        this.checkIfFrozen();
        if (value == null) {
            throw new NullPointerException("IndexOptions cannot be null");
        }
        this.indexOptions = value;
    }
    
    public void setNumericType(final NumericType type) {
        this.checkIfFrozen();
        this.numericType = type;
    }
    
    public NumericType numericType() {
        return this.numericType;
    }
    
    public void setNumericPrecisionStep(final int precisionStep) {
        this.checkIfFrozen();
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >= 1 (got " + precisionStep + ")");
        }
        this.numericPrecisionStep = precisionStep;
    }
    
    public int numericPrecisionStep() {
        return this.numericPrecisionStep;
    }
    
    @Override
    public final String toString() {
        final StringBuilder result = new StringBuilder();
        if (this.stored()) {
            result.append("stored");
        }
        if (this.indexOptions != IndexOptions.NONE) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("indexed");
            if (this.tokenized()) {
                result.append(",tokenized");
            }
            if (this.storeTermVectors()) {
                result.append(",termVector");
            }
            if (this.storeTermVectorOffsets()) {
                result.append(",termVectorOffsets");
            }
            if (this.storeTermVectorPositions()) {
                result.append(",termVectorPosition");
            }
            if (this.storeTermVectorPayloads()) {
                result.append(",termVectorPayloads");
            }
            if (this.omitNorms()) {
                result.append(",omitNorms");
            }
            if (this.indexOptions != IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                result.append(",indexOptions=");
                result.append(this.indexOptions);
            }
            if (this.numericType != null) {
                result.append(",numericType=");
                result.append(this.numericType);
                result.append(",numericPrecisionStep=");
                result.append(this.numericPrecisionStep);
            }
        }
        if (this.docValuesType != DocValuesType.NONE) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("docValuesType=");
            result.append(this.docValuesType);
        }
        return result.toString();
    }
    
    @Override
    public DocValuesType docValuesType() {
        return this.docValuesType;
    }
    
    public void setDocValuesType(final DocValuesType type) {
        this.checkIfFrozen();
        if (type == null) {
            throw new NullPointerException("DocValuesType cannot be null");
        }
        this.docValuesType = type;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.docValuesType == null) ? 0 : this.docValuesType.hashCode());
        result = 31 * result + this.indexOptions.hashCode();
        result = 31 * result + this.numericPrecisionStep;
        result = 31 * result + ((this.numericType == null) ? 0 : this.numericType.hashCode());
        result = 31 * result + (this.omitNorms ? 1231 : 1237);
        result = 31 * result + (this.storeTermVectorOffsets ? 1231 : 1237);
        result = 31 * result + (this.storeTermVectorPayloads ? 1231 : 1237);
        result = 31 * result + (this.storeTermVectorPositions ? 1231 : 1237);
        result = 31 * result + (this.storeTermVectors ? 1231 : 1237);
        result = 31 * result + (this.stored ? 1231 : 1237);
        result = 31 * result + (this.tokenized ? 1231 : 1237);
        return result;
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
        final FieldType other = (FieldType)obj;
        return this.docValuesType == other.docValuesType && this.indexOptions == other.indexOptions && this.numericPrecisionStep == other.numericPrecisionStep && this.numericType == other.numericType && this.omitNorms == other.omitNorms && this.storeTermVectorOffsets == other.storeTermVectorOffsets && this.storeTermVectorPayloads == other.storeTermVectorPayloads && this.storeTermVectorPositions == other.storeTermVectorPositions && this.storeTermVectors == other.storeTermVectors && this.stored == other.stored && this.tokenized == other.tokenized;
    }
    
    public enum NumericType
    {
        INT, 
        LONG, 
        FLOAT, 
        DOUBLE;
    }
}
