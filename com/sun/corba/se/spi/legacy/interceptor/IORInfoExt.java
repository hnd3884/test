package com.sun.corba.se.spi.legacy.interceptor;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public interface IORInfoExt
{
    int getServerPort(final String p0) throws UnknownType;
    
    ObjectAdapter getObjectAdapter();
}
