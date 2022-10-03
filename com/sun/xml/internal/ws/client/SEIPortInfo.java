package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.binding.BindingImpl;
import javax.xml.ws.WebServiceFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.SOAPSEIModel;

public final class SEIPortInfo extends PortInfo
{
    public final Class sei;
    public final SOAPSEIModel model;
    
    public SEIPortInfo(final WSServiceDelegate owner, final Class sei, final SOAPSEIModel model, @NotNull final WSDLPort portModel) {
        super(owner, portModel);
        this.sei = sei;
        this.model = model;
        assert sei != null && model != null;
    }
    
    @Override
    public BindingImpl createBinding(final WebServiceFeature[] webServiceFeatures, final Class<?> portInterface) {
        final BindingImpl binding = super.createBinding(webServiceFeatures, portInterface);
        return this.setKnownHeaders(binding);
    }
    
    public BindingImpl createBinding(final WebServiceFeatureList webServiceFeatures, final Class<?> portInterface) {
        final BindingImpl binding = super.createBinding(webServiceFeatures, portInterface, null);
        return this.setKnownHeaders(binding);
    }
    
    private BindingImpl setKnownHeaders(final BindingImpl binding) {
        if (binding instanceof SOAPBindingImpl) {
            ((SOAPBindingImpl)binding).setPortKnownHeaders(this.model.getKnownHeaders());
        }
        return binding;
    }
}
