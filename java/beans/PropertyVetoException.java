package java.beans;

public class PropertyVetoException extends Exception
{
    private static final long serialVersionUID = 129596057694162164L;
    private PropertyChangeEvent evt;
    
    public PropertyVetoException(final String s, final PropertyChangeEvent evt) {
        super(s);
        this.evt = evt;
    }
    
    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.evt;
    }
}
