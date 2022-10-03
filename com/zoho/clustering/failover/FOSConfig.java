package com.zoho.clustering.failover;

import java.net.UnknownHostException;
import java.net.InetAddress;
import com.zoho.clustering.util.MyProperties;

public class FOSConfig
{
    private String nodeId;
    private String publicIP;
    private String publicIPIfName;
    private String publicIPNetMask;
    private int ipCheckIntervalInSecs;
    private int ipCheckRetryCount;
    private String masterURL;
    private String httpPollURI;
    private int httpPollConnTimeout;
    private int httpPollReadTimeout;
    private int httpPollIntervalInSecs;
    private int httpPollRetryCount;
    private String hostnameVerifierClassName;
    private boolean mutable;
    
    public FOSConfig() {
        this.mutable = true;
    }
    
    public FOSConfig(final String prefix, final MyProperties props, final FOS.Mode mode) {
        this.mutable = true;
        this.nodeId = props.value(prefix + ".nodeId", getHostAddrString());
        this.publicIP = props.value(prefix + ".publicIP");
        this.publicIPIfName = props.optionalValue(prefix + ".publicIPIfName");
        this.publicIPNetMask = props.value(prefix + ".publicIPNetMask", "255.255.255.0");
        final String ipcheckPrefix = prefix + ".master.ipcheck";
        this.ipCheckIntervalInSecs = props.intValue(ipcheckPrefix + ".intervalInSecs", 60);
        this.ipCheckRetryCount = props.intValue(ipcheckPrefix + ".retryCount", 4);
        this.hostnameVerifierClassName = props.optionalValue(prefix + ".hostnameVerifierClassName");
        this.masterURL = props.value(prefix + ".slave.masterURL");
        final String httpPollPrefix = prefix + ".slave.httppoll";
        this.httpPollURI = props.value(httpPollPrefix + ".uri");
        this.httpPollConnTimeout = props.intValue(httpPollPrefix + ".connTimeoutMillis", 1000);
        this.httpPollReadTimeout = props.intValue(httpPollPrefix + ".readTimeoutMillis", 1000);
        this.httpPollIntervalInSecs = props.intValue(httpPollPrefix + ".intervalInSecs", 90);
        this.httpPollRetryCount = props.intValue(httpPollPrefix + ".retryCount", 3);
    }
    
    private static String getHostAddrString() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (final UnknownHostException exp) {
            throw new RuntimeException("Problem while finding this host's IPAddress", exp);
        }
    }
    
    public String nodeId() {
        return this.nodeId;
    }
    
    public String publicIP() {
        return this.publicIP;
    }
    
    public String publicIPIfName() {
        return this.publicIPIfName;
    }
    
    public String publicIPNetMask() {
        return this.publicIPNetMask;
    }
    
    public int ipCheckIntervalInSecs() {
        return this.ipCheckIntervalInSecs;
    }
    
    public int ipCheckRetryCount() {
        return this.ipCheckRetryCount;
    }
    
    public String masterURL() {
        return this.masterURL;
    }
    
    public String httpPollURI() {
        return this.httpPollURI;
    }
    
    public int httpPollConnTimeout() {
        return this.httpPollConnTimeout;
    }
    
    public int httpPollReadTimeout() {
        return this.httpPollReadTimeout;
    }
    
    public int httpPollIntervalInSecs() {
        return this.httpPollIntervalInSecs;
    }
    
    public int httpPollRetryCount() {
        return this.httpPollRetryCount;
    }
    
    public String hostnameVerifierClassName() {
        return this.hostnameVerifierClassName;
    }
    
    public void makeImmutable() {
        this.mutable = false;
    }
    
    private void assertMutability() {
        if (!this.mutable) {
            throw new IllegalStateException("This FOSConfig object is Not mutable");
        }
    }
    
    public void setNodeId(final String nodeId) {
        this.assertMutability();
        this.nodeId = nodeId;
    }
    
    public void setPublicIP(final String publicIP) {
        this.assertMutability();
        this.publicIP = publicIP;
    }
    
    public void setPublicIPIfName(final String publicIPIfName) {
        this.assertMutability();
        this.publicIPIfName = publicIPIfName;
    }
    
    public void setPublicIPNetMask(final String publicIPNetMask) {
        this.assertMutability();
        this.publicIPNetMask = publicIPNetMask;
    }
    
    public void setIPCheckIntervalInSecs(final int ipCheckIntervalInSecs) {
        this.assertMutability();
        this.ipCheckIntervalInSecs = ipCheckIntervalInSecs;
    }
    
    public void setIPCheckRetryCount(final int ipCheckRetryCount) {
        this.assertMutability();
        this.ipCheckRetryCount = ipCheckRetryCount;
    }
    
    public void setMasterURL(final String masterURL) {
        this.assertMutability();
        this.masterURL = masterURL;
    }
    
    public void setHttpPollURI(final String httpPollURI) {
        this.assertMutability();
        this.httpPollURI = httpPollURI;
    }
    
    public void setHttpPollConnTimeout(final int httpPollConnTimeout) {
        this.assertMutability();
        this.httpPollConnTimeout = httpPollConnTimeout;
    }
    
    public void setHttpPollReadTimeout(final int httpPollReadTimeout) {
        this.assertMutability();
        this.httpPollReadTimeout = httpPollReadTimeout;
    }
    
    public void setHttpPollIntervalInSecs(final int httpPollIntervalInSecs) {
        this.assertMutability();
        this.httpPollIntervalInSecs = httpPollIntervalInSecs;
    }
    
    public void setHttpPollRetryCount(final int httpPollRetryCount) {
        this.assertMutability();
        this.httpPollRetryCount = httpPollRetryCount;
    }
    
    public void setHostnameVerifierClassName(final String hostnameVerifierClassName) {
        this.assertMutability();
        this.hostnameVerifierClassName = hostnameVerifierClassName;
    }
}
