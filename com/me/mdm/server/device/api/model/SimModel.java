package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimModel
{
    @JsonProperty("carrier_setting_version")
    private String carrierSettingVersion;
    @JsonProperty("current_carrier_network")
    private String currentCarrierNetwork;
    @JsonProperty("current_mcc")
    private String currentMcc;
    @JsonProperty("current_mnc")
    private String currentMnc;
    @JsonProperty(value = "iccid", defaultValue = "--")
    private String iccid;
    @JsonProperty(value = "imei", defaultValue = "--")
    private String imei;
    @JsonProperty(value = "imsi", defaultValue = "--")
    private String imsi;
    @JsonProperty("is_roaming")
    private boolean roaming;
    @JsonProperty(value = "phone_number", defaultValue = "--")
    private String phoneNumber;
    @JsonAlias({ "resource_id" })
    @JsonProperty("device_id")
    private Long deviceID;
    @JsonProperty("sim_id")
    private Long simId;
    @JsonProperty(value = "subscriber_carrier_network", defaultValue = "--")
    private String subscriberCarrierNetwork;
    @JsonProperty(value = "subscriber_mcc", defaultValue = "--")
    private String subscriberMcc;
    @JsonProperty(value = "subscriber_mnc", defaultValue = "--")
    private String subscriberMnc;
    @JsonProperty("slot")
    private String slot;
    @JsonProperty(value = "label", defaultValue = "--")
    private String label;
    @JsonProperty(value = "label_id", defaultValue = "--")
    private String label_id;
    @JsonProperty("is_data_preferred")
    private Boolean is_data_preferred;
    @JsonProperty("is_voice_preferred")
    private Boolean is_voice_preferred;
    
    public SimModel() {
        this.carrierSettingVersion = "--";
        this.currentCarrierNetwork = "--";
        this.currentMcc = "--";
        this.currentMnc = "--";
        this.iccid = "--";
        this.imei = "--";
        this.imsi = "--";
        this.phoneNumber = "--";
        this.subscriberCarrierNetwork = "--";
        this.subscriberMcc = "--";
        this.subscriberMnc = "--";
        this.slot = null;
        this.label = "--";
        this.label_id = "--";
        this.is_data_preferred = null;
        this.is_voice_preferred = null;
    }
    
    public String getCarrierSettingVersion() {
        return this.carrierSettingVersion;
    }
    
    public void setCarrierSettingVersion(final String carrierSettingVersion) {
        this.carrierSettingVersion = carrierSettingVersion;
    }
    
    public String getCurrentCarrierNetwork() {
        return this.currentCarrierNetwork;
    }
    
    public void setCurrentCarrierNetwork(final String currentCarrierNetwork) {
        this.currentCarrierNetwork = currentCarrierNetwork;
    }
    
    public String getCurrentMcc() {
        return this.currentMcc;
    }
    
    public void setCurrentMcc(final String currentMcc) {
        this.currentMcc = currentMcc;
    }
    
    public String getCurrentMnc() {
        return this.currentMnc;
    }
    
    public void setCurrentMnc(final String currentMnc) {
        this.currentMnc = currentMnc;
    }
    
    public String getIccid() {
        return this.iccid;
    }
    
    public void setIccid(final String iccid) {
        this.iccid = iccid;
    }
    
    public String getImei() {
        return this.imei;
    }
    
    public void setImei(final String imei) {
        this.imei = imei;
    }
    
    public String getImsi() {
        return this.imsi;
    }
    
    public void setImsi(final String imsi) {
        this.imsi = imsi;
    }
    
    public boolean isRoaming() {
        return this.roaming;
    }
    
    public void setRoaming(final boolean roaming) {
        this.roaming = roaming;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public Long getDeviceID() {
        return this.deviceID;
    }
    
    public void setDeviceID(final Long deviceID) {
        this.deviceID = deviceID;
    }
    
    public Long getSimId() {
        return this.simId;
    }
    
    public void setSimId(final Long simId) {
        this.simId = simId;
    }
    
    public String getSubscriberCarrierNetwork() {
        return this.subscriberCarrierNetwork;
    }
    
    public void setSubscriberCarrierNetwork(final String subscriberCarrierNetwork) {
        this.subscriberCarrierNetwork = subscriberCarrierNetwork;
    }
    
    public String getSubscriberMcc() {
        return this.subscriberMcc;
    }
    
    public void setSubscriberMcc(final String subscriberMcc) {
        this.subscriberMcc = subscriberMcc;
    }
    
    public String getSubscriberMnc() {
        return this.subscriberMnc;
    }
    
    public void setSubscriberMnc(final String subscriberMnc) {
        this.subscriberMnc = subscriberMnc;
    }
    
    public void setSlot(final String slot) {
        this.slot = slot;
    }
    
    public String getSlot() {
        return this.slot;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel_id(final String label_id) {
        this.label_id = label_id;
    }
    
    public String getLabel_id() {
        return this.label_id;
    }
    
    public void setIs_data_preferred(final Boolean is_data_preferred) {
        this.is_data_preferred = is_data_preferred;
    }
    
    public Boolean getIs_data_preferred() {
        return this.is_data_preferred;
    }
    
    public void setIs_voice_preferred(final Boolean is_voice_preferred) {
        this.is_voice_preferred = is_voice_preferred;
    }
    
    public Boolean getIs_voice_preferred() {
        return this.is_voice_preferred;
    }
}
