package org.apache.lucene.analysis;

import java.util.Objects;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public final class NumericTokenStream extends TokenStream
{
    public static final String TOKEN_TYPE_FULL_PREC = "fullPrecNumeric";
    public static final String TOKEN_TYPE_LOWER_PREC = "lowerPrecNumeric";
    private final NumericTermAttribute numericAtt;
    private final TypeAttribute typeAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private int valSize;
    private final int precisionStep;
    
    public NumericTokenStream() {
        this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, 16);
    }
    
    public NumericTokenStream(final int precisionStep) {
        this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, precisionStep);
    }
    
    public NumericTokenStream(final AttributeFactory factory, final int precisionStep) {
        super(new NumericAttributeFactory(factory));
        this.numericAtt = this.addAttribute(NumericTermAttribute.class);
        this.typeAtt = this.addAttribute(TypeAttribute.class);
        this.posIncrAtt = this.addAttribute(PositionIncrementAttribute.class);
        this.valSize = 0;
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        this.precisionStep = precisionStep;
        this.numericAtt.setShift(-precisionStep);
    }
    
    public NumericTokenStream setLongValue(final long value) {
        this.numericAtt.init(value, this.valSize = 64, this.precisionStep, -this.precisionStep);
        return this;
    }
    
    public NumericTokenStream setIntValue(final int value) {
        this.numericAtt.init(value, this.valSize = 32, this.precisionStep, -this.precisionStep);
        return this;
    }
    
    public NumericTokenStream setDoubleValue(final double value) {
        this.numericAtt.init(NumericUtils.doubleToSortableLong(value), this.valSize = 64, this.precisionStep, -this.precisionStep);
        return this;
    }
    
    public NumericTokenStream setFloatValue(final float value) {
        this.numericAtt.init(NumericUtils.floatToSortableInt(value), this.valSize = 32, this.precisionStep, -this.precisionStep);
        return this;
    }
    
    @Override
    public void reset() {
        if (this.valSize == 0) {
            throw new IllegalStateException("call set???Value() before usage");
        }
        this.numericAtt.setShift(-this.precisionStep);
    }
    
    @Override
    public boolean incrementToken() {
        if (this.valSize == 0) {
            throw new IllegalStateException("call set???Value() before usage");
        }
        this.clearAttributes();
        final int shift = this.numericAtt.incShift();
        this.typeAtt.setType((shift == 0) ? "fullPrecNumeric" : "lowerPrecNumeric");
        this.posIncrAtt.setPositionIncrement((shift == 0) ? 1 : 0);
        return shift < this.valSize;
    }
    
    public int getPrecisionStep() {
        return this.precisionStep;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(precisionStep=" + this.precisionStep + " valueSize=" + this.numericAtt.getValueSize() + " shift=" + this.numericAtt.getShift() + ")";
    }
    
    private static final class NumericAttributeFactory extends AttributeFactory
    {
        private final AttributeFactory delegate;
        
        NumericAttributeFactory(final AttributeFactory delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public AttributeImpl createAttributeInstance(final Class<? extends Attribute> attClass) {
            if (CharTermAttribute.class.isAssignableFrom(attClass)) {
                throw new IllegalArgumentException("NumericTokenStream does not support CharTermAttribute.");
            }
            return this.delegate.createAttributeInstance(attClass);
        }
    }
    
    public static final class NumericTermAttributeImpl extends AttributeImpl implements NumericTermAttribute, TermToBytesRefAttribute
    {
        private long value;
        private int valueSize;
        private int shift;
        private int precisionStep;
        private BytesRefBuilder bytes;
        
        public NumericTermAttributeImpl() {
            this.value = 0L;
            this.valueSize = 0;
            this.shift = 0;
            this.precisionStep = 0;
            this.bytes = new BytesRefBuilder();
        }
        
        @Override
        public BytesRef getBytesRef() {
            assert this.valueSize == 32;
            if (this.shift >= this.valueSize) {
                this.bytes.clear();
            }
            else if (this.valueSize == 64) {
                NumericUtils.longToPrefixCoded(this.value, this.shift, this.bytes);
            }
            else {
                NumericUtils.intToPrefixCoded((int)this.value, this.shift, this.bytes);
            }
            return this.bytes.get();
        }
        
        @Override
        public int getShift() {
            return this.shift;
        }
        
        @Override
        public void setShift(final int shift) {
            this.shift = shift;
        }
        
        @Override
        public int incShift() {
            return this.shift += this.precisionStep;
        }
        
        @Override
        public long getRawValue() {
            return this.value & ~((1L << this.shift) - 1L);
        }
        
        @Override
        public int getValueSize() {
            return this.valueSize;
        }
        
        @Override
        public void init(final long value, final int valueSize, final int precisionStep, final int shift) {
            this.value = value;
            this.valueSize = valueSize;
            this.precisionStep = precisionStep;
            this.shift = shift;
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void reflectWith(final AttributeReflector reflector) {
            reflector.reflect(TermToBytesRefAttribute.class, "bytes", this.getBytesRef());
            reflector.reflect(NumericTermAttribute.class, "shift", this.shift);
            reflector.reflect(NumericTermAttribute.class, "rawValue", this.getRawValue());
            reflector.reflect(NumericTermAttribute.class, "valueSize", this.valueSize);
        }
        
        @Override
        public void copyTo(final AttributeImpl target) {
            final NumericTermAttribute a = (NumericTermAttribute)target;
            a.init(this.value, this.valueSize, this.precisionStep, this.shift);
        }
        
        @Override
        public NumericTermAttributeImpl clone() {
            final NumericTermAttributeImpl t = (NumericTermAttributeImpl)super.clone();
            (t.bytes = new BytesRefBuilder()).copyBytes(this.getBytesRef());
            return t;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.precisionStep, this.shift, this.value, this.valueSize);
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
            final NumericTermAttributeImpl other = (NumericTermAttributeImpl)obj;
            return this.precisionStep == other.precisionStep && this.shift == other.shift && this.value == other.value && this.valueSize == other.valueSize;
        }
    }
    
    public interface NumericTermAttribute extends Attribute
    {
        int getShift();
        
        long getRawValue();
        
        int getValueSize();
        
        void init(final long p0, final int p1, final int p2, final int p3);
        
        void setShift(final int p0);
        
        int incShift();
    }
}
