package javax.sound.sampled;

public abstract class BooleanControl extends Control
{
    private final String trueStateLabel;
    private final String falseStateLabel;
    private boolean value;
    
    protected BooleanControl(final Type type, final boolean value, final String trueStateLabel, final String falseStateLabel) {
        super(type);
        this.value = value;
        this.trueStateLabel = trueStateLabel;
        this.falseStateLabel = falseStateLabel;
    }
    
    protected BooleanControl(final Type type, final boolean b) {
        this(type, b, "true", "false");
    }
    
    public void setValue(final boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return this.value;
    }
    
    public String getStateLabel(final boolean b) {
        return b ? this.trueStateLabel : this.falseStateLabel;
    }
    
    @Override
    public String toString() {
        return new String(super.toString() + " with current value: " + this.getStateLabel(this.getValue()));
    }
    
    public static class Type extends Control.Type
    {
        public static final Type MUTE;
        public static final Type APPLY_REVERB;
        
        protected Type(final String s) {
            super(s);
        }
        
        static {
            MUTE = new Type("Mute");
            APPLY_REVERB = new Type("Apply Reverb");
        }
    }
}
