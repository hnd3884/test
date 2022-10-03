package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public interface ObjectKeyFactory
{
    ObjectKey create(final byte[] p0);
    
    ObjectKeyTemplate createTemplate(final InputStream p0);
}
