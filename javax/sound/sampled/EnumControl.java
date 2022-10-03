package javax.sound.sampled;

public abstract class EnumControl extends Control
{
    private Object[] values;
    private Object value;
    
    protected EnumControl(final Type type, final Object[] values, final Object value) {
        super(type);
        this.values = values;
        this.value = value;
    }
    
    public void setValue(final Object value) {
        if (!this.isValueSupported(value)) {
            throw new IllegalArgumentException("Requested value " + value + " is not supported.");
        }
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public Object[] getValues() {
        final Object[] array = new Object[this.values.length];
        for (int i = 0; i < this.values.length; ++i) {
            array[i] = this.values[i];
        }
        return array;
    }
    
    private boolean isValueSupported(final Object o) {
        for (int i = 0; i < this.values.length; ++i) {
            if (o.equals(this.values[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return new String(this.getType() + " with current value: " + this.getValue());
    }
    
    public static class Type extends Control.Type
    {
        public static final Type REVERB;
        
        protected Type(final String s) {
            super(s);
        }
        
        static {
            REVERB = new Type("Reverb");
        }
    }
}
