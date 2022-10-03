package com.sun.beans.editors;

public class IntegerEditor extends NumberEditor
{
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Integer.decode(s));
    }
}
