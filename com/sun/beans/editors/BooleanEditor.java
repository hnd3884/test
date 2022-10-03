package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class BooleanEditor extends PropertyEditorSupport
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        return (value != null) ? value.toString() : "null";
    }
    
    @Override
    public String getAsText() {
        final Object value = this.getValue();
        return (value instanceof Boolean) ? this.getValidName((boolean)value) : null;
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        if (s == null) {
            this.setValue(null);
        }
        else if (this.isValidName(true, s)) {
            this.setValue(Boolean.TRUE);
        }
        else {
            if (!this.isValidName(false, s)) {
                throw new IllegalArgumentException(s);
            }
            this.setValue(Boolean.FALSE);
        }
    }
    
    @Override
    public String[] getTags() {
        return new String[] { this.getValidName(true), this.getValidName(false) };
    }
    
    private String getValidName(final boolean b) {
        return b ? "True" : "False";
    }
    
    private boolean isValidName(final boolean b, final String s) {
        return this.getValidName(b).equalsIgnoreCase(s);
    }
}
