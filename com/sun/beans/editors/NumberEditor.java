package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public abstract class NumberEditor extends PropertyEditorSupport
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        return (value != null) ? value.toString() : "null";
    }
}
