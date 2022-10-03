package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;

interface Handler
{
    ObjectKeyTemplate handle(final int p0, final int p1, final InputStream p2, final OctetSeqHolder p3);
}
