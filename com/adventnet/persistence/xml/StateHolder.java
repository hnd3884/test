package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataObject;

public interface StateHolder
{
    void set(final Object p0);
    
    DataObject get();
}
