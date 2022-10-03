package org.apache.lucene.codecs;

import org.apache.lucene.util.Bits;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import java.io.IOException;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

public abstract class DocValuesProducer implements Closeable, Accountable
{
    protected DocValuesProducer() {
    }
    
    public abstract NumericDocValues getNumeric(final FieldInfo p0) throws IOException;
    
    public abstract BinaryDocValues getBinary(final FieldInfo p0) throws IOException;
    
    public abstract SortedDocValues getSorted(final FieldInfo p0) throws IOException;
    
    public abstract SortedNumericDocValues getSortedNumeric(final FieldInfo p0) throws IOException;
    
    public abstract SortedSetDocValues getSortedSet(final FieldInfo p0) throws IOException;
    
    public abstract Bits getDocsWithField(final FieldInfo p0) throws IOException;
    
    public abstract void checkIntegrity() throws IOException;
    
    public DocValuesProducer getMergeInstance() throws IOException {
        return this;
    }
}
