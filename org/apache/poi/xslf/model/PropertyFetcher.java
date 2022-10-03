package org.apache.poi.xslf.model;

import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.util.Internal;

@Internal
public abstract class PropertyFetcher<T>
{
    private T _value;
    
    public abstract boolean fetch(final XSLFShape p0);
    
    public T getValue() {
        return this._value;
    }
    
    public void setValue(final T val) {
        this._value = val;
    }
}
