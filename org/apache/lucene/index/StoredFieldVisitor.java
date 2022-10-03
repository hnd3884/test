package org.apache.lucene.index;

import java.io.IOException;

public abstract class StoredFieldVisitor
{
    protected StoredFieldVisitor() {
    }
    
    public void binaryField(final FieldInfo fieldInfo, final byte[] value) throws IOException {
    }
    
    public void stringField(final FieldInfo fieldInfo, final byte[] value) throws IOException {
    }
    
    public void intField(final FieldInfo fieldInfo, final int value) throws IOException {
    }
    
    public void longField(final FieldInfo fieldInfo, final long value) throws IOException {
    }
    
    public void floatField(final FieldInfo fieldInfo, final float value) throws IOException {
    }
    
    public void doubleField(final FieldInfo fieldInfo, final double value) throws IOException {
    }
    
    public abstract Status needsField(final FieldInfo p0) throws IOException;
    
    public enum Status
    {
        YES, 
        NO, 
        STOP;
    }
}
