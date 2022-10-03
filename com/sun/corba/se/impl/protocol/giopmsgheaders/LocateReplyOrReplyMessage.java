package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.SystemException;

public interface LocateReplyOrReplyMessage extends Message
{
    int getRequestId();
    
    int getReplyStatus();
    
    SystemException getSystemException(final String p0);
    
    IOR getIOR();
    
    short getAddrDisposition();
}
