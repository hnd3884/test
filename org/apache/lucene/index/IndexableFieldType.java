package org.apache.lucene.index;

public interface IndexableFieldType
{
    boolean stored();
    
    boolean tokenized();
    
    boolean storeTermVectors();
    
    boolean storeTermVectorOffsets();
    
    boolean storeTermVectorPositions();
    
    boolean storeTermVectorPayloads();
    
    boolean omitNorms();
    
    IndexOptions indexOptions();
    
    DocValuesType docValuesType();
}
