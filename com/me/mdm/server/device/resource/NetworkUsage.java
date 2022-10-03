package com.me.mdm.server.device.resource;

import com.google.gson.annotations.SerializedName;

public class NetworkUsage
{
    @SerializedName("outgoing_network_usage")
    private Double outgoingNetworkUsage;
    @SerializedName("incoming_wifi_usage")
    private Double incomingWIFIUsage;
    @SerializedName("outgoing_wifi_usage")
    private Double outgoingWIFIUsage;
    @SerializedName("incoming_network_usage")
    private Double incomingNetworkUsage;
    @SerializedName(value = "device_id", alternate = { "resource_id" })
    private String deviceID;
    
    public Double getOutgoingNetworkUsage() {
        return this.outgoingNetworkUsage;
    }
    
    public void setOutgoingNetworkUsage(final Double outgoingNetworkUsage) {
        this.outgoingNetworkUsage = outgoingNetworkUsage;
    }
    
    public Double getIncomingWIFIUsage() {
        return this.incomingWIFIUsage;
    }
    
    public void setIncomingWIFIUsage(final Double incomingWIFIUsage) {
        this.incomingWIFIUsage = incomingWIFIUsage;
    }
    
    public Double getOutgoingWIFIUsage() {
        return this.outgoingWIFIUsage;
    }
    
    public void setOutgoingWIFIUsage(final Double outgoingWIFIUsage) {
        this.outgoingWIFIUsage = outgoingWIFIUsage;
    }
    
    public Double getIncomingNetworkUsage() {
        return this.incomingNetworkUsage;
    }
    
    public void setIncomingNetworkUsage(final Double incomingNetworkUsage) {
        this.incomingNetworkUsage = incomingNetworkUsage;
    }
    
    public String getDeviceID() {
        return this.deviceID;
    }
    
    public void setDeviceID(final String deviceID) {
        this.deviceID = deviceID;
    }
}
