package com.lowagie.text;

public interface LargeElement extends Element
{
    void setComplete(final boolean p0);
    
    boolean isComplete();
    
    void flushContent();
}
