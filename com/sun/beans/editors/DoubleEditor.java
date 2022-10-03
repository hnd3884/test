package com.sun.beans.editors;

public class DoubleEditor extends NumberEditor
{
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Double.valueOf(s));
    }
}
