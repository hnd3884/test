package com.adventnet.mfw.bean;

import com.adventnet.persistence.DataObject;

public interface Interceptor
{
    void initialize(final DataObject p0) throws Throwable;
    
    Object invoke(final Object p0) throws Throwable;
    
    Interceptor getNext();
    
    void setNext(final Interceptor p0) throws Throwable;
}
