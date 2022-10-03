package javax.sound.sampled;

public abstract class FloatControl extends Control
{
    private float minimum;
    private float maximum;
    private float precision;
    private int updatePeriod;
    private final String units;
    private final String minLabel;
    private final String maxLabel;
    private final String midLabel;
    private float value;
    
    protected FloatControl(final Type type, final float minimum, final float maximum, final float precision, final int updatePeriod, final float value, final String units, final String s, final String s2, final String s3) {
        super(type);
        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum value " + minimum + " exceeds maximum value " + maximum + ".");
        }
        if (value < minimum) {
            throw new IllegalArgumentException("Initial value " + value + " smaller than allowable minimum value " + minimum + ".");
        }
        if (value > maximum) {
            throw new IllegalArgumentException("Initial value " + value + " exceeds allowable maximum value " + maximum + ".");
        }
        this.minimum = minimum;
        this.maximum = maximum;
        this.precision = precision;
        this.updatePeriod = updatePeriod;
        this.value = value;
        this.units = units;
        this.minLabel = ((s == null) ? "" : s);
        this.midLabel = ((s2 == null) ? "" : s2);
        this.maxLabel = ((s3 == null) ? "" : s3);
    }
    
    protected FloatControl(final Type type, final float n, final float n2, final float n3, final int n4, final float n5, final String s) {
        this(type, n, n2, n3, n4, n5, s, "", "", "");
    }
    
    public void setValue(final float value) {
        if (value > this.maximum) {
            throw new IllegalArgumentException("Requested value " + value + " exceeds allowable maximum value " + this.maximum + ".");
        }
        if (value < this.minimum) {
            throw new IllegalArgumentException("Requested value " + value + " smaller than allowable minimum value " + this.minimum + ".");
        }
        this.value = value;
    }
    
    public float getValue() {
        return this.value;
    }
    
    public float getMaximum() {
        return this.maximum;
    }
    
    public float getMinimum() {
        return this.minimum;
    }
    
    public String getUnits() {
        return this.units;
    }
    
    public String getMinLabel() {
        return this.minLabel;
    }
    
    public String getMidLabel() {
        return this.midLabel;
    }
    
    public String getMaxLabel() {
        return this.maxLabel;
    }
    
    public float getPrecision() {
        return this.precision;
    }
    
    public int getUpdatePeriod() {
        return this.updatePeriod;
    }
    
    public void shift(final float n, final float value, final int n2) {
        if (n < this.minimum) {
            throw new IllegalArgumentException("Requested value " + n + " smaller than allowable minimum value " + this.minimum + ".");
        }
        if (n > this.maximum) {
            throw new IllegalArgumentException("Requested value " + n + " exceeds allowable maximum value " + this.maximum + ".");
        }
        this.setValue(value);
    }
    
    @Override
    public String toString() {
        return new String(this.getType() + " with current value: " + this.getValue() + " " + this.units + " (range: " + this.minimum + " - " + this.maximum + ")");
    }
    
    public static class Type extends Control.Type
    {
        public static final Type MASTER_GAIN;
        public static final Type AUX_SEND;
        public static final Type AUX_RETURN;
        public static final Type REVERB_SEND;
        public static final Type REVERB_RETURN;
        public static final Type VOLUME;
        public static final Type PAN;
        public static final Type BALANCE;
        public static final Type SAMPLE_RATE;
        
        protected Type(final String s) {
            super(s);
        }
        
        static {
            MASTER_GAIN = new Type("Master Gain");
            AUX_SEND = new Type("AUX Send");
            AUX_RETURN = new Type("AUX Return");
            REVERB_SEND = new Type("Reverb Send");
            REVERB_RETURN = new Type("Reverb Return");
            VOLUME = new Type("Volume");
            PAN = new Type("Pan");
            BALANCE = new Type("Balance");
            SAMPLE_RATE = new Type("Sample Rate");
        }
    }
}
