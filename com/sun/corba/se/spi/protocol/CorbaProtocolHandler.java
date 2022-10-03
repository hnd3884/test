package com.sun.corba.se.spi.protocol;

import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.UnknownException;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.protocol.ProtocolHandler;

public interface CorbaProtocolHandler extends ProtocolHandler
{
    void handleRequest(final RequestMessage p0, final CorbaMessageMediator p1);
    
    void handleRequest(final LocateRequestMessage p0, final CorbaMessageMediator p1);
    
    CorbaMessageMediator createResponse(final CorbaMessageMediator p0, final ServiceContexts p1);
    
    CorbaMessageMediator createUserExceptionResponse(final CorbaMessageMediator p0, final ServiceContexts p1);
    
    CorbaMessageMediator createUnknownExceptionResponse(final CorbaMessageMediator p0, final UnknownException p1);
    
    CorbaMessageMediator createSystemExceptionResponse(final CorbaMessageMediator p0, final SystemException p1, final ServiceContexts p2);
    
    CorbaMessageMediator createLocationForward(final CorbaMessageMediator p0, final IOR p1, final ServiceContexts p2);
    
    void handleThrowableDuringServerDispatch(final CorbaMessageMediator p0, final Throwable p1, final CompletionStatus p2);
}
