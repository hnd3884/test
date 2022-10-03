package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface ExceptionHandler
{
    boolean isDeclaredException(final Class p0);
    
    void writeException(final OutputStream p0, final Exception p1);
    
    Exception readException(final ApplicationException p0);
}
