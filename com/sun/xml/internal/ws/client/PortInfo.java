package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.WSService;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.binding.BindingImpl;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Collection;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.client.WSPortInfo;

public class PortInfo implements WSPortInfo
{
    @NotNull
    private final WSServiceDelegate owner;
    @NotNull
    public final QName portName;
    @NotNull
    public final EndpointAddress targetEndpoint;
    @NotNull
    public final BindingID bindingId;
    @NotNull
    public final PolicyMap policyMap;
    @Nullable
    public final WSDLPort portModel;
    
    public PortInfo(final WSServiceDelegate owner, final EndpointAddress targetEndpoint, final QName name, final BindingID bindingId) {
        this.owner = owner;
        this.targetEndpoint = targetEndpoint;
        this.portName = name;
        this.bindingId = bindingId;
        this.portModel = this.getPortModel(owner, name);
        this.policyMap = this.createPolicyMap();
    }
    
    public PortInfo(@NotNull final WSServiceDelegate owner, @NotNull final WSDLPort port) {
        this.owner = owner;
        this.targetEndpoint = port.getAddress();
        this.portName = port.getName();
        this.bindingId = port.getBinding().getBindingId();
        this.portModel = port;
        this.policyMap = this.createPolicyMap();
    }
    
    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }
    
    public PolicyMap createPolicyMap() {
        PolicyMap map;
        if (this.portModel != null) {
            map = this.portModel.getOwner().getParent().getPolicyMap();
        }
        else {
            map = PolicyResolverFactory.create().resolve(new PolicyResolver.ClientContext(null, this.owner.getContainer()));
        }
        if (map == null) {
            map = PolicyMap.createPolicyMap(null);
        }
        return map;
    }
    
    public BindingImpl createBinding(final WebServiceFeature[] webServiceFeatures, final Class<?> portInterface) {
        return this.createBinding(new WebServiceFeatureList(webServiceFeatures), portInterface, null);
    }
    
    public BindingImpl createBinding(final WebServiceFeatureList webServiceFeatures, final Class<?> portInterface, final BindingImpl existingBinding) {
        if (existingBinding != null) {
            webServiceFeatures.addAll(existingBinding.getFeatures());
        }
        Iterable<WebServiceFeature> configFeatures;
        if (this.portModel != null) {
            configFeatures = this.portModel.getFeatures();
        }
        else {
            configFeatures = PolicyUtil.getPortScopedFeatures(this.policyMap, this.owner.getServiceName(), this.portName);
        }
        webServiceFeatures.mergeFeatures(configFeatures, false);
        webServiceFeatures.mergeFeatures(this.owner.serviceInterceptor.preCreateBinding(this, portInterface, webServiceFeatures), false);
        final BindingImpl bindingImpl = BindingImpl.create(this.bindingId, webServiceFeatures.toArray());
        this.owner.getHandlerConfigurator().configureHandlers(this, bindingImpl);
        return bindingImpl;
    }
    
    private WSDLPort getPortModel(final WSServiceDelegate owner, final QName portName) {
        if (owner.getWsdlService() != null) {
            final Iterable<? extends WSDLPort> ports = owner.getWsdlService().getPorts();
            for (final WSDLPort port : ports) {
                if (port.getName().equals(portName)) {
                    return port;
                }
            }
        }
        return null;
    }
    
    @Nullable
    @Override
    public WSDLPort getPort() {
        return this.portModel;
    }
    
    @NotNull
    @Override
    public WSService getOwner() {
        return this.owner;
    }
    
    @NotNull
    @Override
    public BindingID getBindingId() {
        return this.bindingId;
    }
    
    @NotNull
    @Override
    public EndpointAddress getEndpointAddress() {
        return this.targetEndpoint;
    }
    
    @Override
    @Deprecated
    public QName getServiceName() {
        return this.owner.getServiceName();
    }
    
    @Override
    public QName getPortName() {
        return this.portName;
    }
    
    @Override
    @Deprecated
    public String getBindingID() {
        return this.bindingId.toString();
    }
}
