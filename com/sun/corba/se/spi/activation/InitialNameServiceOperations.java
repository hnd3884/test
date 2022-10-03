package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import org.omg.CORBA.Object;

public interface InitialNameServiceOperations
{
    void bind(final String p0, final org.omg.CORBA.Object p1, final boolean p2) throws NameAlreadyBound;
}
