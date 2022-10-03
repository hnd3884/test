package com.sun.beans.editors;

public class ShortEditor extends NumberEditor
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        return (value != null) ? ("((short)" + value + ")") : "null";
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Short.decode(s));
    }
}
