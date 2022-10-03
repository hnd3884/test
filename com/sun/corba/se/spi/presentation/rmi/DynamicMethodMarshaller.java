package com.sun.corba.se.spi.presentation.rmi;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import java.rmi.RemoteException;
import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Method;

public interface DynamicMethodMarshaller
{
    Method getMethod();
    
    Object[] copyArguments(final Object[] p0, final ORB p1) throws RemoteException;
    
    Object[] readArguments(final InputStream p0);
    
    void writeArguments(final OutputStream p0, final Object[] p1);
    
    Object copyResult(final Object p0, final ORB p1) throws RemoteException;
    
    Object readResult(final InputStream p0);
    
    void writeResult(final OutputStream p0, final Object p1);
    
    boolean isDeclaredException(final Throwable p0);
    
    void writeException(final OutputStream p0, final Exception p1);
    
    Exception readException(final ApplicationException p0);
}
