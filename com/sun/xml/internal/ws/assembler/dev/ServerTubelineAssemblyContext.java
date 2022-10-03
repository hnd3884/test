package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;

public interface ServerTubelineAssemblyContext extends TubelineAssemblyContext
{
    @NotNull
    Codec getCodec();
    
    @NotNull
    WSEndpoint getEndpoint();
    
    PolicyMap getPolicyMap();
    
    @Nullable
    SEIModel getSEIModel();
    
    @NotNull
    Tube getTerminalTube();
    
    ServerTubeAssemblerContext getWrappedContext();
    
    @Nullable
    WSDLPort getWsdlPort();
    
    boolean isPolicyAvailable();
    
    boolean isSynchronous();
    
    void setCodec(@NotNull final Codec p0);
}
