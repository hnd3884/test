package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.encoding.CorbaInputObject;

public interface IORTransformer
{
    IOR unmarshal(final CorbaInputObject p0);
    
    void marshal(final CorbaOutputObject p0, final IOR p1);
}
