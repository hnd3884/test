package com.sun.xml.internal.ws.assembler;

import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;

class DefaultClientTubelineAssemblyContext extends TubelineAssemblyContextImpl implements ClientTubelineAssemblyContext
{
    @NotNull
    private final ClientTubeAssemblerContext wrappedContext;
    private final PolicyMap policyMap;
    private final WSPortInfo portInfo;
    private final WSDLPort wsdlPort;
    
    public DefaultClientTubelineAssemblyContext(@NotNull final ClientTubeAssemblerContext context) {
        this.wrappedContext = context;
        this.wsdlPort = context.getWsdlModel();
        this.portInfo = context.getPortInfo();
        this.policyMap = context.getPortInfo().getPolicyMap();
    }
    
    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }
    
    @Override
    public boolean isPolicyAvailable() {
        return this.policyMap != null && !this.policyMap.isEmpty();
    }
    
    @Override
    public WSDLPort getWsdlPort() {
        return this.wsdlPort;
    }
    
    @Override
    public WSPortInfo getPortInfo() {
        return this.portInfo;
    }
    
    @NotNull
    @Override
    public EndpointAddress getAddress() {
        return this.wrappedContext.getAddress();
    }
    
    @NotNull
    @Override
    public WSService getService() {
        return this.wrappedContext.getService();
    }
    
    @NotNull
    @Override
    public WSBinding getBinding() {
        return this.wrappedContext.getBinding();
    }
    
    @Nullable
    @Override
    public SEIModel getSEIModel() {
        return this.wrappedContext.getSEIModel();
    }
    
    @Override
    public Container getContainer() {
        return this.wrappedContext.getContainer();
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
    public ClientTubeAssemblerContext getWrappedContext() {
        return this.wrappedContext;
    }
}
