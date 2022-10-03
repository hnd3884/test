package com.sun.xml.internal.ws.api.addressing;

import java.net.URL;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class OneWayFeature extends WebServiceFeature
{
    public static final String ID = "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
    private String messageId;
    private WSEndpointReference replyTo;
    private WSEndpointReference sslReplyTo;
    private WSEndpointReference from;
    private WSEndpointReference faultTo;
    private WSEndpointReference sslFaultTo;
    private String relatesToID;
    private boolean useAsyncWithSyncInvoke;
    
    public OneWayFeature() {
        this.useAsyncWithSyncInvoke = false;
        this.enabled = true;
    }
    
    public OneWayFeature(final boolean enabled) {
        this.useAsyncWithSyncInvoke = false;
        this.enabled = enabled;
    }
    
    public OneWayFeature(final boolean enabled, final WSEndpointReference replyTo) {
        this.useAsyncWithSyncInvoke = false;
        this.enabled = enabled;
        this.replyTo = replyTo;
    }
    
    @FeatureConstructor({ "enabled", "replyTo", "from", "relatesTo" })
    public OneWayFeature(final boolean enabled, final WSEndpointReference replyTo, final WSEndpointReference from, final String relatesTo) {
        this.useAsyncWithSyncInvoke = false;
        this.enabled = enabled;
        this.replyTo = replyTo;
        this.from = from;
        this.relatesToID = relatesTo;
    }
    
    public OneWayFeature(final AddressingPropertySet a, final AddressingVersion v) {
        this.useAsyncWithSyncInvoke = false;
        this.enabled = true;
        this.messageId = a.getMessageId();
        this.relatesToID = a.getRelatesTo();
        this.replyTo = this.makeEPR(a.getReplyTo(), v);
        this.faultTo = this.makeEPR(a.getFaultTo(), v);
    }
    
    private WSEndpointReference makeEPR(final String x, final AddressingVersion v) {
        if (x == null) {
            return null;
        }
        return new WSEndpointReference(x, v);
    }
    
    public String getMessageId() {
        return this.messageId;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
    }
    
    public boolean hasSslEprs() {
        return this.sslReplyTo != null || this.sslFaultTo != null;
    }
    
    @ManagedAttribute
    public WSEndpointReference getReplyTo() {
        return this.replyTo;
    }
    
    public WSEndpointReference getReplyTo(final boolean ssl) {
        return (ssl && this.sslReplyTo != null) ? this.sslReplyTo : this.replyTo;
    }
    
    public void setReplyTo(final WSEndpointReference address) {
        this.replyTo = address;
    }
    
    public WSEndpointReference getSslReplyTo() {
        return this.sslReplyTo;
    }
    
    public void setSslReplyTo(final WSEndpointReference sslReplyTo) {
        this.sslReplyTo = sslReplyTo;
    }
    
    @ManagedAttribute
    public WSEndpointReference getFrom() {
        return this.from;
    }
    
    public void setFrom(final WSEndpointReference address) {
        this.from = address;
    }
    
    @ManagedAttribute
    public String getRelatesToID() {
        return this.relatesToID;
    }
    
    public void setRelatesToID(final String id) {
        this.relatesToID = id;
    }
    
    public WSEndpointReference getFaultTo() {
        return this.faultTo;
    }
    
    public WSEndpointReference getFaultTo(final boolean ssl) {
        return (ssl && this.sslFaultTo != null) ? this.sslFaultTo : this.faultTo;
    }
    
    public void setFaultTo(final WSEndpointReference address) {
        this.faultTo = address;
    }
    
    public WSEndpointReference getSslFaultTo() {
        return this.sslFaultTo;
    }
    
    public void setSslFaultTo(final WSEndpointReference sslFaultTo) {
        this.sslFaultTo = sslFaultTo;
    }
    
    public boolean isUseAsyncWithSyncInvoke() {
        return this.useAsyncWithSyncInvoke;
    }
    
    public void setUseAsyncWithSyncInvoke(final boolean useAsyncWithSyncInvoke) {
        this.useAsyncWithSyncInvoke = useAsyncWithSyncInvoke;
    }
    
    public static WSEndpointReference enableSslForEpr(@NotNull final WSEndpointReference epr, @Nullable final String sslHost, final int sslPort) {
        if (!epr.isAnonymous()) {
            String address = epr.getAddress();
            URL url;
            try {
                url = new URL(address);
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
            String protocol = url.getProtocol();
            if (!protocol.equalsIgnoreCase("https")) {
                protocol = "https";
                String host = url.getHost();
                if (sslHost != null) {
                    host = sslHost;
                }
                int port = url.getPort();
                if (sslPort > 0) {
                    port = sslPort;
                }
                try {
                    url = new URL(protocol, host, port, url.getFile());
                }
                catch (final Exception e2) {
                    throw new RuntimeException(e2);
                }
                address = url.toExternalForm();
                return new WSEndpointReference(address, epr.getVersion());
            }
        }
        return epr;
    }
}
