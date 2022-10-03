package com.me.mdm.server.device.resource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Network
{
    @SerializedName("bluetooth_mac")
    private String bluetoothMac;
    @SerializedName("carrier_setting_version")
    private String carrierSettingVersion;
    @SerializedName("current_carrier_network")
    private String currentCarrierNetwork;
    @SerializedName("current_mcc")
    private String currentMcc;
    @SerializedName("current_mnc")
    private String currentMnc;
    @SerializedName("data_roaming_enabled")
    private Boolean dataRoamingEnabled;
    @SerializedName("ethernet_ip")
    private String ethernetIp;
    @SerializedName("ethernet_macs")
    private String ethernetMacs;
    @Expose
    private String iccid;
    @SerializedName("is_personal_hotspot_enabled")
    private Boolean isPersonalHotspotEnabled;
    @SerializedName("is_roaming")
    private Boolean isRoaming;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName(value = "device_id", alternate = { "resource_id" })
    private String deviceID;
    @SerializedName("subscriber_carrier_network")
    private String subscriberCarrierNetwork;
    @SerializedName("subscriber_mcc")
    private String subscriberMcc;
    @SerializedName("subscriber_mnc")
    private String subscriberMnc;
    @SerializedName("voice_roaming_enabled")
    private Boolean voiceRoamingEnabled;
    @SerializedName("wifi_ip")
    private String wifiIp;
    @SerializedName("wifi_mac")
    private String wifiMac;
    
    public Network() {
        this.bluetoothMac = "--";
        this.carrierSettingVersion = "--";
        this.currentCarrierNetwork = "--";
        this.currentMcc = "--";
        this.currentMnc = "--";
        this.dataRoamingEnabled = false;
        this.ethernetIp = "--";
        this.ethernetMacs = "--";
        this.iccid = "--";
        this.isPersonalHotspotEnabled = false;
        this.isRoaming = false;
        this.phoneNumber = "--";
        this.subscriberCarrierNetwork = "--";
        this.subscriberMcc = "--";
        this.subscriberMnc = "--";
        this.voiceRoamingEnabled = false;
        this.wifiIp = "--";
        this.wifiMac = "--";
    }
    
    public String getBluetoothMac() {
        return this.bluetoothMac;
    }
    
    public void setBluetoothMac(final String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
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
    
    public Boolean getDataRoamingEnabled() {
        return this.dataRoamingEnabled;
    }
    
    public void setDataRoamingEnabled(final Boolean dataRoamingEnabled) {
        this.dataRoamingEnabled = dataRoamingEnabled;
    }
    
    public String getEthernetIp() {
        return this.ethernetIp;
    }
    
    public void setEthernetIp(final String ethernetIp) {
        this.ethernetIp = ethernetIp;
    }
    
    public String getEthernetMacs() {
        return this.ethernetMacs;
    }
    
    public void setEthernetMacs(final String ethernetMacs) {
        this.ethernetMacs = ethernetMacs;
    }
    
    public String getIccid() {
        return this.iccid;
    }
    
    public void setIccid(final String iccid) {
        this.iccid = iccid;
    }
    
    public Boolean getIsPersonalHotspotEnabled() {
        return this.isPersonalHotspotEnabled;
    }
    
    public void setIsPersonalHotspotEnabled(final Boolean isPersonalHotspotEnabled) {
        this.isPersonalHotspotEnabled = isPersonalHotspotEnabled;
    }
    
    public Boolean getRoaming() {
        return this.isRoaming;
    }
    
    public void setRoaming(final Boolean roaming) {
        this.isRoaming = roaming;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getDeviceID() {
        return this.deviceID;
    }
    
    public void setDeviceID(final String deviceID) {
        this.deviceID = deviceID;
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
    
    public Boolean getVoiceRoamingEnabled() {
        return this.voiceRoamingEnabled;
    }
    
    public void setVoiceRoamingEnabled(final Boolean voiceRoamingEnabled) {
        this.voiceRoamingEnabled = voiceRoamingEnabled;
    }
    
    public String getWifiIp() {
        return this.wifiIp;
    }
    
    public void setWifiIp(final String wifiIp) {
        this.wifiIp = wifiIp;
    }
    
    public String getWifiMac() {
        return this.wifiMac;
    }
    
    public void setWifiMac(final String wifiMac) {
        this.wifiMac = wifiMac;
    }
}
