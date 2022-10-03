package com.sun.corba.se.spi.presentation.rmi;

import org.omg.CORBA.portable.OutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.Object;

public interface DynamicStub extends Object
{
    void setDelegate(final Delegate p0);
    
    Delegate getDelegate();
    
    ORB getORB();
    
    String[] getTypeIds();
    
    void connect(final ORB p0) throws RemoteException;
    
    boolean isLocal();
    
    OutputStream request(final String p0, final boolean p1);
}
