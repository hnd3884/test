package com.sun.corba.se.impl.protocol.giopmsgheaders;

public interface CancelRequestMessage extends Message
{
    public static final int CANCEL_REQ_MSG_SIZE = 4;
    
    int getRequestId();
}
