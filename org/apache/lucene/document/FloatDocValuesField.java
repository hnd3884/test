package org.apache.lucene.document;

public class FloatDocValuesField extends NumericDocValuesField
{
    public FloatDocValuesField(final String name, final float value) {
        super(name, Float.floatToRawIntBits(value));
    }
    
    @Override
    public void setFloatValue(final float value) {
        super.setLongValue(Float.floatToRawIntBits(value));
    }
    
    @Override
    public void setLongValue(final long value) {
        throw new IllegalArgumentException("cannot change value type from Float to Long");
    }
}
