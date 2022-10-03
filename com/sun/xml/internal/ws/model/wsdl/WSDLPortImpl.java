package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import java.util.List;
import javax.xml.ws.WebServiceFeature;
import java.util.Collection;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.EndpointAddress;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;

public final class WSDLPortImpl extends AbstractFeaturedObjectImpl implements EditableWSDLPort
{
    private final QName name;
    private EndpointAddress address;
    private final QName bindingName;
    private final EditableWSDLService owner;
    private WSEndpointReference epr;
    private EditableWSDLBoundPortType boundPortType;
    
    public WSDLPortImpl(final XMLStreamReader xsr, final EditableWSDLService owner, final QName name, final QName binding) {
        super(xsr);
        this.owner = owner;
        this.name = name;
        this.bindingName = binding;
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    public QName getBindingName() {
        return this.bindingName;
    }
    
    @Override
    public EndpointAddress getAddress() {
        return this.address;
    }
    
    @Override
    public EditableWSDLService getOwner() {
        return this.owner;
    }
    
    @Override
    public void setAddress(final EndpointAddress address) {
        assert address != null;
        this.address = address;
    }
    
    @Override
    public void setEPR(@NotNull final WSEndpointReference epr) {
        assert epr != null;
        this.addExtension(epr);
        this.epr = epr;
    }
    
    @Nullable
    @Override
    public WSEndpointReference getEPR() {
        return this.epr;
    }
    
    @Override
    public EditableWSDLBoundPortType getBinding() {
        return this.boundPortType;
    }
    
    @Override
    public void freeze(final EditableWSDLModel root) {
        this.boundPortType = root.getBinding(this.bindingName);
        if (this.boundPortType == null) {
            throw new LocatableWebServiceException(ClientMessages.UNDEFINED_BINDING(this.bindingName), new Locator[] { this.getLocation() });
        }
        if (this.features == null) {
            this.features = new WebServiceFeatureList();
        }
        this.features.setParentFeaturedObject(this.boundPortType);
        this.notUnderstoodExtensions.addAll((Collection<? extends UnknownWSDLExtension>)this.boundPortType.getNotUnderstoodExtensions());
    }
}
