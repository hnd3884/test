package org.apache.lucene.document;

public class DoubleDocValuesField extends NumericDocValuesField
{
    public DoubleDocValuesField(final String name, final double value) {
        super(name, Double.doubleToRawLongBits(value));
    }
    
    @Override
    public void setDoubleValue(final double value) {
        super.setLongValue(Double.doubleToRawLongBits(value));
    }
    
    @Override
    public void setLongValue(final long value) {
        throw new IllegalArgumentException("cannot change value type from Double to Long");
    }
}
