package javax.sound.sampled;

public abstract class CompoundControl extends Control
{
    private Control[] controls;
    
    protected CompoundControl(final Type type, final Control[] controls) {
        super(type);
        this.controls = controls;
    }
    
    public Control[] getMemberControls() {
        final Control[] array = new Control[this.controls.length];
        for (int i = 0; i < this.controls.length; ++i) {
            array[i] = this.controls[i];
        }
        return array;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.controls.length; ++i) {
            if (i != 0) {
                sb.append(", ");
                if (i + 1 == this.controls.length) {
                    sb.append("and ");
                }
            }
            sb.append(this.controls[i].getType());
        }
        return new String(this.getType() + " Control containing " + (Object)sb + " Controls.");
    }
    
    public static class Type extends Control.Type
    {
        protected Type(final String s) {
            super(s);
        }
    }
}
