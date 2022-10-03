package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Packet;

public interface EndpointCallBridge
{
    JavaCallInfo deserializeRequest(final Packet p0);
    
    Packet serializeResponse(final JavaCallInfo p0);
    
    JavaMethod getOperationModel();
}
