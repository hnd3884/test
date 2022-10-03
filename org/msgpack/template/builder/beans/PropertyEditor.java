package org.msgpack.template.builder.beans;

public interface PropertyEditor
{
    void setAsText(final String p0) throws IllegalArgumentException;
    
    String[] getTags();
    
    String getJavaInitializationString();
    
    String getAsText();
    
    void setValue(final Object p0);
    
    Object getValue();
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    boolean supportsCustomEditor();
    
    boolean isPaintable();
}
