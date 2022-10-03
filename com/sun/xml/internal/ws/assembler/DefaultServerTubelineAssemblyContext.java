package com.sun.xml.internal.ws.assembler;

import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;

class DefaultServerTubelineAssemblyContext extends TubelineAssemblyContextImpl implements ServerTubelineAssemblyContext
{
    @NotNull
    private final ServerTubeAssemblerContext wrappedContext;
    private final PolicyMap policyMap;
    
    public DefaultServerTubelineAssemblyContext(@NotNull final ServerTubeAssemblerContext context) {
        this.wrappedContext = context;
        this.policyMap = context.getEndpoint().getPolicyMap();
    }
    
    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }
    
    @Override
    public boolean isPolicyAvailable() {
        return this.policyMap != null && !this.policyMap.isEmpty();
    }
    
    @Nullable
    @Override
    public SEIModel getSEIModel() {
        return this.wrappedContext.getSEIModel();
    }
    
    @Nullable
    @Override
    public WSDLPort getWsdlPort() {
        return this.wrappedContext.getWsdlModel();
    }
    
    @NotNull
    @Override
    public WSEndpoint getEndpoint() {
        return this.wrappedContext.getEndpoint();
    }
    
    @NotNull
    @Override
    public Tube getTerminalTube() {
        return this.wrappedContext.getTerminalTube();
    }
    
    @Override
    public boolean isSynchronous() {
        return this.wrappedContext.isSynchronous();
    }
    
    @NotNull
    @Override
    public Codec getCodec() {
        return this.wrappedContext.getCodec();
    }
    
    @Override
    public void setCodec(@NotNull final Codec codec) {
        this.wrappedContext.setCodec(codec);
    }
    
    @Override
    public ServerTubeAssemblerContext getWrappedContext() {
        return this.wrappedContext;
    }
}
