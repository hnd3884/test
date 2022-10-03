package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkModel
{
    @JsonProperty("bluetooth_mac")
    private String bluetoothMac;
    @JsonProperty("carrier_setting_version")
    private String carrierSettingVersion;
    @JsonProperty("current_carrier_network")
    private String currentCarrierNetwork;
    @JsonProperty("current_mcc")
    private String currentMcc;
    @JsonProperty("current_mnc")
    private String currentMnc;
    @JsonProperty("data_roaming_enabled")
    private Boolean dataRoamingEnabled;
    @JsonProperty("ethernet_ip")
    private String ethernetIp;
    @JsonProperty("ethernet_macs")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ethernetMacs;
    private String iccid;
    @JsonProperty("is_personal_hotspot_enabled")
    private boolean personalHotspotEnabled;
    @JsonProperty("is_roaming")
    private boolean roaming;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonAlias({ "resource_id" })
    @JsonProperty("device_id")
    private String deviceID;
    @JsonProperty("subscriber_carrier_network")
    private String subscriberCarrierNetwork;
    @JsonProperty("subscriber_mcc")
    private String subscriberMcc;
    @JsonProperty("subscriber_mnc")
    private String subscriberMnc;
    @JsonProperty("voice_roaming_enabled")
    private Boolean voiceRoamingEnabled;
    @JsonProperty("wifi_ip")
    private String wifiIp;
    @JsonProperty("wifi_mac")
    private String wifiMac;
    @JsonProperty("imsi")
    private String imsi;
    @JsonProperty("wifi_ssid")
    private String wifi_ssid;
    @JsonProperty("wifi_ssid_remarks")
    private String wifiSsidRemarks;
    
    public NetworkModel() {
        this.bluetoothMac = "--";
        this.carrierSettingVersion = "--";
        this.currentCarrierNetwork = "--";
        this.currentMcc = "--";
        this.currentMnc = "--";
        this.dataRoamingEnabled = false;
        this.ethernetIp = "--";
        this.iccid = "--";
        this.phoneNumber = "--";
        this.subscriberCarrierNetwork = "--";
        this.subscriberMcc = "--";
        this.subscriberMnc = "--";
        this.voiceRoamingEnabled = false;
        this.wifiIp = "--";
        this.wifiMac = "--";
        this.imsi = "--";
        this.wifi_ssid = "--";
    }
    
    public String getWifiSsidRemarks() {
        return this.wifiSsidRemarks;
    }
    
    public void setWifiSsidRemarks(final String wifiSsidRemarks) {
        this.wifiSsidRemarks = wifiSsidRemarks;
    }
    
    public String getWifi_ssid() {
        return this.wifi_ssid;
    }
    
    public void setWifi_ssid(final String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
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
    
    public boolean isPersonalHotspotEnabled() {
        return this.personalHotspotEnabled;
    }
    
    public void setPersonalHotspotEnabled(final boolean personalHotspotEnabled) {
        this.personalHotspotEnabled = personalHotspotEnabled;
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
    
    public String getImsi() {
        return this.imsi;
    }
    
    public void setImsi(final String imsi) {
        this.imsi = imsi;
    }
}
