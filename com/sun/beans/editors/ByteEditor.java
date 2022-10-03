package com.sun.beans.editors;

public class ByteEditor extends NumberEditor
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        return (value != null) ? ("((byte)" + value + ")") : "null";
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Byte.decode(s));
    }
}
