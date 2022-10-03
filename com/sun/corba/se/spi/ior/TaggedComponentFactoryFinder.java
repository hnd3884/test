package com.sun.corba.se.spi.ior;

import org.omg.IOP.TaggedComponent;
import org.omg.CORBA.ORB;

public interface TaggedComponentFactoryFinder extends IdentifiableFactoryFinder
{
    com.sun.corba.se.spi.ior.TaggedComponent create(final ORB p0, final TaggedComponent p1);
}
