package com.ocpsoft.pretty.time;

public interface TimeUnit
{
    long getMillisPerUnit();
    
    long getMaxQuantity();
    
    String getName();
    
    String getPluralName();
    
    TimeFormat getFormat();
}
