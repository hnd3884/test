package com.sun.xml.internal.ws.binding;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Collection;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import javax.xml.ws.handler.Handler;
import java.util.List;
import javax.xml.ws.Service;
import com.sun.istack.internal.NotNull;
import java.util.HashSet;
import java.util.Collections;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.ws.soap.SOAPBinding;

public final class SOAPBindingImpl extends BindingImpl implements SOAPBinding
{
    public static final String X_SOAP12HTTP_BINDING = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/";
    private static final String ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
    protected final SOAPVersion soapVersion;
    private Set<QName> portKnownHeaders;
    private Set<QName> bindingUnderstoodHeaders;
    
    SOAPBindingImpl(final BindingID bindingId) {
        this(bindingId, SOAPBindingImpl.EMPTY_FEATURES);
    }
    
    SOAPBindingImpl(final BindingID bindingId, final WebServiceFeature... features) {
        super(bindingId, features);
        this.portKnownHeaders = Collections.emptySet();
        this.bindingUnderstoodHeaders = new HashSet<QName>();
        this.soapVersion = bindingId.getSOAPVersion();
        this.setRoles(new HashSet<String>());
        this.features.addAll(bindingId.createBuiltinFeatureList());
    }
    
    public void setPortKnownHeaders(@NotNull final Set<QName> headers) {
        this.portKnownHeaders = headers;
    }
    
    public boolean understandsHeader(final QName header) {
        return this.serviceMode == Service.Mode.MESSAGE || this.portKnownHeaders.contains(header) || this.bindingUnderstoodHeaders.contains(header);
    }
    
    @Override
    public void setHandlerChain(final List<Handler> chain) {
        this.setHandlerConfig(new HandlerConfiguration(this.getHandlerConfig().getRoles(), chain));
    }
    
    protected void addRequiredRoles(final Set<String> roles) {
        roles.addAll(this.soapVersion.requiredRoles);
    }
    
    @Override
    public Set<String> getRoles() {
        return this.getHandlerConfig().getRoles();
    }
    
    @Override
    public void setRoles(Set<String> roles) {
        if (roles == null) {
            roles = new HashSet<String>();
        }
        if (roles.contains("http://www.w3.org/2003/05/soap-envelope/role/none")) {
            throw new WebServiceException(ClientMessages.INVALID_SOAP_ROLE_NONE());
        }
        this.addRequiredRoles(roles);
        this.setHandlerConfig(new HandlerConfiguration(roles, this.getHandlerConfig()));
    }
    
    @Override
    public boolean isMTOMEnabled() {
        return this.isFeatureEnabled(MTOMFeature.class);
    }
    
    @Override
    public void setMTOMEnabled(final boolean b) {
        this.features.setMTOMEnabled(b);
    }
    
    @Override
    public SOAPFactory getSOAPFactory() {
        return this.soapVersion.getSOAPFactory();
    }
    
    @Override
    public MessageFactory getMessageFactory() {
        return this.soapVersion.getMessageFactory();
    }
}
