package java.beans;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Graphics;

public interface PropertyEditor
{
    void setValue(final Object p0);
    
    Object getValue();
    
    boolean isPaintable();
    
    void paintValue(final Graphics p0, final Rectangle p1);
    
    String getJavaInitializationString();
    
    String getAsText();
    
    void setAsText(final String p0) throws IllegalArgumentException;
    
    String[] getTags();
    
    Component getCustomEditor();
    
    boolean supportsCustomEditor();
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
}
