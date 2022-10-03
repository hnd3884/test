package com.sun.beans.editors;

public class FloatEditor extends NumberEditor
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        return (value != null) ? (value + "F") : "null";
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Float.valueOf(s));
    }
}
