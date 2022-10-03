package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ContactInfo;

public interface ClientRequestDispatcher
{
    OutputObject beginRequest(final Object p0, final String p1, final boolean p2, final ContactInfo p3);
    
    InputObject marshalingComplete(final Object p0, final OutputObject p1) throws ApplicationException, RemarshalException;
    
    void endRequest(final Broker p0, final Object p1, final InputObject p2);
}
