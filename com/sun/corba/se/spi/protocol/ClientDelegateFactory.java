package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.transport.CorbaContactInfoList;

public interface ClientDelegateFactory
{
    CorbaClientDelegate create(final CorbaContactInfoList p0);
}
