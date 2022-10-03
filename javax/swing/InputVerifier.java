package javax.swing;

public abstract class InputVerifier
{
    public abstract boolean verify(final JComponent p0);
    
    public boolean shouldYieldFocus(final JComponent component) {
        return this.verify(component);
    }
}
