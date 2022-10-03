package javax.swing;

import java.io.Serializable;

public class SpinnerNumberModel extends AbstractSpinnerModel implements Serializable
{
    private Number stepSize;
    private Number value;
    private Comparable minimum;
    private Comparable maximum;
    
    public SpinnerNumberModel(final Number value, final Comparable minimum, final Comparable maximum, final Number stepSize) {
        if (value == null || stepSize == null) {
            throw new IllegalArgumentException("value and stepSize must be non-null");
        }
        if ((minimum != null && minimum.compareTo(value) > 0) || (maximum != null && maximum.compareTo(value) < 0)) {
            throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
        }
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepSize = stepSize;
    }
    
    public SpinnerNumberModel(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, (Number)n4);
    }
    
    public SpinnerNumberModel(final double n, final double n2, final double n3, final double n4) {
        this(new Double(n), new Double(n2), new Double(n3), new Double(n4));
    }
    
    public SpinnerNumberModel() {
        this(0, null, null, 1);
    }
    
    public void setMinimum(final Comparable minimum) {
        if (minimum == null) {
            if (this.minimum == null) {
                return;
            }
        }
        else if (minimum.equals(this.minimum)) {
            return;
        }
        this.minimum = minimum;
        this.fireStateChanged();
    }
    
    public Comparable getMinimum() {
        return this.minimum;
    }
    
    public void setMaximum(final Comparable maximum) {
        if (maximum == null) {
            if (this.maximum == null) {
                return;
            }
        }
        else if (maximum.equals(this.maximum)) {
            return;
        }
        this.maximum = maximum;
        this.fireStateChanged();
    }
    
    public Comparable getMaximum() {
        return this.maximum;
    }
    
    public void setStepSize(final Number stepSize) {
        if (stepSize == null) {
            throw new IllegalArgumentException("null stepSize");
        }
        if (!stepSize.equals(this.stepSize)) {
            this.stepSize = stepSize;
            this.fireStateChanged();
        }
    }
    
    public Number getStepSize() {
        return this.stepSize;
    }
    
    private Number incrValue(final int n) {
        Number n3;
        if (this.value instanceof Float || this.value instanceof Double) {
            final double n2 = this.value.doubleValue() + this.stepSize.doubleValue() * n;
            if (this.value instanceof Double) {
                n3 = new Double(n2);
            }
            else {
                n3 = new Float(n2);
            }
        }
        else {
            final long n4 = this.value.longValue() + this.stepSize.longValue() * n;
            if (this.value instanceof Long) {
                n3 = n4;
            }
            else if (this.value instanceof Integer) {
                n3 = (int)n4;
            }
            else if (this.value instanceof Short) {
                n3 = (short)n4;
            }
            else {
                n3 = (byte)n4;
            }
        }
        if (this.maximum != null && this.maximum.compareTo(n3) < 0) {
            return null;
        }
        if (this.minimum != null && this.minimum.compareTo(n3) > 0) {
            return null;
        }
        return n3;
    }
    
    @Override
    public Object getNextValue() {
        return this.incrValue(1);
    }
    
    @Override
    public Object getPreviousValue() {
        return this.incrValue(-1);
    }
    
    public Number getNumber() {
        return this.value;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void setValue(final Object o) {
        if (o == null || !(o instanceof Number)) {
            throw new IllegalArgumentException("illegal value");
        }
        if (!o.equals(this.value)) {
            this.value = (Number)o;
            this.fireStateChanged();
        }
    }
}
