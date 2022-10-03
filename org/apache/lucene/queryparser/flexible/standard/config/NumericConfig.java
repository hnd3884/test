package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.Objects;
import org.apache.lucene.document.FieldType;
import java.text.NumberFormat;

public class NumericConfig
{
    private int precisionStep;
    private NumberFormat format;
    private FieldType.NumericType type;
    
    public NumericConfig(final int precisionStep, final NumberFormat format, final FieldType.NumericType type) {
        this.setPrecisionStep(precisionStep);
        this.setNumberFormat(format);
        this.setType(type);
    }
    
    public int getPrecisionStep() {
        return this.precisionStep;
    }
    
    public void setPrecisionStep(final int precisionStep) {
        this.precisionStep = precisionStep;
    }
    
    public NumberFormat getNumberFormat() {
        return this.format;
    }
    
    public FieldType.NumericType getType() {
        return this.type;
    }
    
    public void setType(final FieldType.NumericType type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }
        this.type = type;
    }
    
    public void setNumberFormat(final NumberFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("format cannot be null!");
        }
        this.format = format;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NumericConfig) {
            final NumericConfig other = (NumericConfig)obj;
            if (this.precisionStep == other.precisionStep && this.type == other.type && (this.format == other.format || this.format.equals(other.format))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.precisionStep, this.type, this.format);
    }
}
