package com.sun.corba.se.org.omg.CORBA;

import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Object;

public abstract class ORB extends org.omg.CORBA_2_3.ORB
{
    public void register_initial_reference(final String s, final org.omg.CORBA.Object object) throws InvalidName {
        throw new NO_IMPLEMENT();
    }
}
