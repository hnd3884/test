package org.omg.CORBA.portable;

import org.omg.CORBA.SystemException;

public interface InvokeHandler
{
    OutputStream _invoke(final String p0, final InputStream p1, final ResponseHandler p2) throws SystemException;
}
