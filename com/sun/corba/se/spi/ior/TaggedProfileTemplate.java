package com.sun.corba.se.spi.ior;

import org.omg.IOP.TaggedComponent;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.OutputStream;
import java.util.Iterator;
import java.util.List;

public interface TaggedProfileTemplate extends List, Identifiable, WriteContents, MakeImmutable
{
    Iterator iteratorById(final int p0);
    
    TaggedProfile create(final ObjectKeyTemplate p0, final ObjectId p1);
    
    void write(final ObjectKeyTemplate p0, final ObjectId p1, final OutputStream p2);
    
    boolean isEquivalent(final TaggedProfileTemplate p0);
    
    TaggedComponent[] getIOPComponents(final ORB p0, final int p1);
}
