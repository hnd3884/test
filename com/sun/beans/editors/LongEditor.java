package com.sun.beans.editors;

public class LongEditor extends NumberEditor
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        return (value != null) ? (value + "L") : "null";
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Long.decode(s));
    }
}
