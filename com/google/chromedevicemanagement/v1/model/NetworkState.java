package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class NetworkState extends GenericJson
{
    @Key
    private String gatewayIpAddress;
    @Key
    private String lanIpAddress;
    @Key
    private String reportTime;
    @Key
    private Integer signalStrengthDbm;
    @Key
    private String wanIpAddress;
    
    public String getGatewayIpAddress() {
        return this.gatewayIpAddress;
    }
    
    public NetworkState setGatewayIpAddress(final String gatewayIpAddress) {
        this.gatewayIpAddress = gatewayIpAddress;
        return this;
    }
    
    public String getLanIpAddress() {
        return this.lanIpAddress;
    }
    
    public NetworkState setLanIpAddress(final String lanIpAddress) {
        this.lanIpAddress = lanIpAddress;
        return this;
    }
    
    public String getReportTime() {
        return this.reportTime;
    }
    
    public NetworkState setReportTime(final String reportTime) {
        this.reportTime = reportTime;
        return this;
    }
    
    public Integer getSignalStrengthDbm() {
        return this.signalStrengthDbm;
    }
    
    public NetworkState setSignalStrengthDbm(final Integer signalStrengthDbm) {
        this.signalStrengthDbm = signalStrengthDbm;
        return this;
    }
    
    public String getWanIpAddress() {
        return this.wanIpAddress;
    }
    
    public NetworkState setWanIpAddress(final String wanIpAddress) {
        this.wanIpAddress = wanIpAddress;
        return this;
    }
    
    public NetworkState set(final String s, final Object o) {
        return (NetworkState)super.set(s, o);
    }
    
    public NetworkState clone() {
        return (NetworkState)super.clone();
    }
}
