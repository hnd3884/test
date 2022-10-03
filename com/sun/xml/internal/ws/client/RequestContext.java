package com.sun.xml.internal.ws.client;

import java.util.TreeMap;
import java.util.List;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Set;
import java.util.Collection;
import com.oracle.webservices.internal.api.message.MessageContext;
import java.util.HashSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Iterator;
import com.oracle.webservices.internal.api.message.DistributedPropertySet;
import java.util.Map;
import com.sun.xml.internal.ws.api.PropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import java.util.logging.Logger;
import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;

public final class RequestContext extends BaseDistributedPropertySet
{
    private static final Logger LOGGER;
    private static ContentNegotiation defaultContentNegotiation;
    @NotNull
    private EndpointAddress endpointAddress;
    public ContentNegotiation contentNegotiation;
    private String soapAction;
    private Boolean soapActionUse;
    private static final PropertyMap propMap;
    
    @Deprecated
    public void addSatellite(@NotNull final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.addSatellite(satellite);
    }
    
    @PropertySet.Property({ "javax.xml.ws.service.endpoint.address" })
    @Deprecated
    public String getEndPointAddressString() {
        return (this.endpointAddress != null) ? this.endpointAddress.toString() : null;
    }
    
    public void setEndPointAddressString(final String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.endpointAddress = EndpointAddress.create(s);
    }
    
    public void setEndpointAddress(@NotNull final EndpointAddress epa) {
        this.endpointAddress = epa;
    }
    
    @NotNull
    public EndpointAddress getEndpointAddress() {
        return this.endpointAddress;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.client.ContentNegotiation" })
    public String getContentNegotiationString() {
        return this.contentNegotiation.toString();
    }
    
    public void setContentNegotiationString(final String s) {
        if (s == null) {
            this.contentNegotiation = ContentNegotiation.none;
        }
        else {
            try {
                this.contentNegotiation = ContentNegotiation.valueOf(s);
            }
            catch (final IllegalArgumentException e) {
                this.contentNegotiation = ContentNegotiation.none;
            }
        }
    }
    
    @PropertySet.Property({ "javax.xml.ws.soap.http.soapaction.uri" })
    public String getSoapAction() {
        return this.soapAction;
    }
    
    public void setSoapAction(final String sAction) {
        this.soapAction = sAction;
    }
    
    @PropertySet.Property({ "javax.xml.ws.soap.http.soapaction.use" })
    public Boolean getSoapActionUse() {
        return this.soapActionUse;
    }
    
    public void setSoapActionUse(final Boolean sActionUse) {
        this.soapActionUse = sActionUse;
    }
    
    RequestContext() {
        this.contentNegotiation = RequestContext.defaultContentNegotiation;
    }
    
    private RequestContext(final RequestContext that) {
        this.contentNegotiation = RequestContext.defaultContentNegotiation;
        for (final Map.Entry<String, Object> entry : that.asMapLocal().entrySet()) {
            if (!RequestContext.propMap.containsKey(entry.getKey())) {
                this.asMap().put(entry.getKey(), entry.getValue());
            }
        }
        this.endpointAddress = that.endpointAddress;
        this.soapAction = that.soapAction;
        this.soapActionUse = that.soapActionUse;
        this.contentNegotiation = that.contentNegotiation;
        that.copySatelliteInto(this);
    }
    
    @Override
    public Object get(final Object key) {
        if (this.supports(key)) {
            return super.get(key);
        }
        return this.asMap().get(key);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        if (this.supports(key)) {
            return super.put(key, value);
        }
        return this.asMap().put(key, value);
    }
    
    public void fill(final Packet packet, final boolean isAddressingEnabled) {
        if (this.endpointAddress != null) {
            packet.endpointAddress = this.endpointAddress;
        }
        packet.contentNegotiation = this.contentNegotiation;
        this.fillSOAPAction(packet, isAddressingEnabled);
        this.mergeRequestHeaders(packet);
        final Set<String> handlerScopeNames = new HashSet<String>();
        this.copySatelliteInto(packet);
        for (final String key : this.asMapLocal().keySet()) {
            if (!this.supportsLocal(key)) {
                handlerScopeNames.add(key);
            }
            if (!RequestContext.propMap.containsKey(key)) {
                final Object value = this.asMapLocal().get(key);
                if (packet.supports(key)) {
                    packet.put(key, value);
                }
                else {
                    packet.invocationProperties.put(key, value);
                }
            }
        }
        if (!handlerScopeNames.isEmpty()) {
            packet.getHandlerScopePropertyNames(false).addAll(handlerScopeNames);
        }
    }
    
    private void mergeRequestHeaders(final Packet packet) {
        final Headers packetHeaders = packet.invocationProperties.get("javax.xml.ws.http.request.headers");
        final Map<String, List<String>> myHeaders = this.asMap().get("javax.xml.ws.http.request.headers");
        if (packetHeaders != null && myHeaders != null) {
            for (final Map.Entry<String, List<String>> entry : myHeaders.entrySet()) {
                final String key = entry.getKey();
                if (key != null && key.trim().length() != 0) {
                    final List<String> listFromPacket = ((TreeMap<K, List<String>>)packetHeaders).get(key);
                    if (listFromPacket != null) {
                        listFromPacket.addAll(entry.getValue());
                    }
                    else {
                        packetHeaders.put(key, myHeaders.get(key));
                    }
                }
            }
            this.asMap().put("javax.xml.ws.http.request.headers", packetHeaders);
        }
    }
    
    private void fillSOAPAction(final Packet packet, final boolean isAddressingEnabled) {
        final boolean p = packet.packetTakesPriorityOverRequestContext;
        final String localSoapAction = p ? packet.soapAction : this.soapAction;
        final Boolean localSoapActionUse = p ? packet.invocationProperties.get("javax.xml.ws.soap.http.soapaction.use") : this.soapActionUse;
        if (((localSoapActionUse != null && localSoapActionUse) || (localSoapActionUse == null && isAddressingEnabled)) && localSoapAction != null) {
            packet.soapAction = localSoapAction;
        }
        if (!isAddressingEnabled && (localSoapActionUse == null || !localSoapActionUse) && localSoapAction != null) {
            RequestContext.LOGGER.warning("BindingProvider.SOAPACTION_URI_PROPERTY is set in the RequestContext but is ineffective, Either set BindingProvider.SOAPACTION_USE_PROPERTY to true or enable AddressingFeature");
        }
    }
    
    public RequestContext copy() {
        return new RequestContext(this);
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return RequestContext.propMap;
    }
    
    @Override
    protected boolean mapAllowsAdditionalProperties() {
        return true;
    }
    
    static {
        LOGGER = Logger.getLogger(RequestContext.class.getName());
        RequestContext.defaultContentNegotiation = ContentNegotiation.obtainFromSystemProperty();
        propMap = BasePropertySet.parse(RequestContext.class);
    }
}
