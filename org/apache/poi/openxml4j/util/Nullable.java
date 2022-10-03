package org.apache.poi.openxml4j.util;

import org.apache.poi.util.Removal;

@Removal(version = "4.2")
@Deprecated
public final class Nullable<E>
{
    private E value;
    
    public Nullable() {
    }
    
    public Nullable(final E value) {
        this.value = value;
    }
    
    public E getValue() {
        return this.value;
    }
    
    public boolean hasValue() {
        return this.value != null;
    }
    
    public void nullify() {
        this.value = null;
    }
}
