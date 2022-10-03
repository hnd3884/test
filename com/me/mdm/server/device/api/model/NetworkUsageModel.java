package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkUsageModel
{
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("outgoing_network_usage")
    private Double outgoingNetworkUsage;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("incoming_wifi_usage")
    private Double incomingWIFIUsage;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("outgoing_wifi_usage")
    private Double outgoingWIFIUsage;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("incoming_network_usage")
    private Double incomingNetworkUsage;
    @JsonAlias({ "RESOURCE_ID" })
    @JsonProperty("device_id")
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
