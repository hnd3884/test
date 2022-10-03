package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.message.Packet;
import com.oracle.webservices.internal.api.databinding.JavaCallInfo;

public interface ClientCallBridge
{
    Packet createRequestPacket(final JavaCallInfo p0);
    
    JavaCallInfo readResponse(final Packet p0, final JavaCallInfo p1) throws Throwable;
    
    Method getMethod();
    
    JavaMethod getOperationModel();
}
