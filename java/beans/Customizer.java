package java.beans;

public interface Customizer
{
    void setObject(final Object p0);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
}
