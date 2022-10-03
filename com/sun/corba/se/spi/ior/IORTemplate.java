package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import java.util.List;

public interface IORTemplate extends List, IORFactory, MakeImmutable
{
    Iterator iteratorById(final int p0);
    
    ObjectKeyTemplate getObjectKeyTemplate();
}
