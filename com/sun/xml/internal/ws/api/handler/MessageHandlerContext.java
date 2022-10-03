package com.sun.xml.internal.ws.api.handler;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Set;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.handler.MessageContext;

public interface MessageHandlerContext extends MessageContext
{
    Message getMessage();
    
    void setMessage(final Message p0);
    
    Set<String> getRoles();
    
    WSBinding getWSBinding();
    
    @Nullable
    SEIModel getSEIModel();
    
    @Nullable
    WSDLPort getPort();
}
