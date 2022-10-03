package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import java.io.InputStream;
import java.io.IOException;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import java.io.OutputStream;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.api.message.Packet;

public interface Databinding extends com.oracle.webservices.internal.api.databinding.Databinding
{
    EndpointCallBridge getEndpointBridge(final Packet p0) throws DispatchException;
    
    ClientCallBridge getClientBridge(final Method p0);
    
    void generateWSDL(final WSDLGenInfo p0);
    
    @Deprecated
    ContentType encode(final Packet p0, final OutputStream p1) throws IOException;
    
    @Deprecated
    void decode(final InputStream p0, final String p1, final Packet p2) throws IOException;
    
    MessageContextFactory getMessageContextFactory();
}
