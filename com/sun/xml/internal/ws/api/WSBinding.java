package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.MessageContextFactory;
import java.util.Set;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import java.util.List;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import javax.xml.ws.Binding;

public interface WSBinding extends Binding
{
    SOAPVersion getSOAPVersion();
    
    AddressingVersion getAddressingVersion();
    
    @NotNull
    BindingID getBindingId();
    
    @NotNull
    List<Handler> getHandlerChain();
    
    boolean isFeatureEnabled(@NotNull final Class<? extends WebServiceFeature> p0);
    
    boolean isOperationFeatureEnabled(@NotNull final Class<? extends WebServiceFeature> p0, @NotNull final QName p1);
    
    @Nullable
     <F extends WebServiceFeature> F getFeature(@NotNull final Class<F> p0);
    
    @Nullable
     <F extends WebServiceFeature> F getOperationFeature(@NotNull final Class<F> p0, @NotNull final QName p1);
    
    @NotNull
    WSFeatureList getFeatures();
    
    @NotNull
    WSFeatureList getOperationFeatures(@NotNull final QName p0);
    
    @NotNull
    WSFeatureList getInputMessageFeatures(@NotNull final QName p0);
    
    @NotNull
    WSFeatureList getOutputMessageFeatures(@NotNull final QName p0);
    
    @NotNull
    WSFeatureList getFaultMessageFeatures(@NotNull final QName p0, @NotNull final QName p1);
    
    @NotNull
    Set<QName> getKnownHeaders();
    
    boolean addKnownHeader(final QName p0);
    
    @NotNull
    MessageContextFactory getMessageContextFactory();
}
