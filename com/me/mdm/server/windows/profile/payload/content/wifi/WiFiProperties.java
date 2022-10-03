package com.me.mdm.server.windows.profile.payload.content.wifi;

import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanEapTypes;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanAuthMode;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanConnectionType;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanAuthentication;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanConnectionMode;

public class WiFiProperties
{
    private String ssidName;
    private WlanConnectionMode connectionMode;
    private WlanAuthentication authentication;
    private String passPhrase;
    private WlanConnectionType connectionType;
    private WlanAuthMode authMode;
    private WlanEapTypes eapType;
    private WlanEapTypes innerEapType;
    private String trustedRootCAThumbrpint;
    private boolean fastReconnect;
    private boolean innerEapOptional;
    private boolean performServerValidation;
    private String trustedRootCACertificate;
    
    public WiFiProperties() {
        this.ssidName = null;
        this.connectionMode = WlanConnectionMode.AUTO;
        this.authentication = null;
        this.passPhrase = null;
        this.connectionType = WlanConnectionType.ESS;
        this.authMode = WlanAuthMode.USER;
        this.eapType = null;
        this.innerEapType = null;
        this.trustedRootCAThumbrpint = null;
        this.fastReconnect = true;
        this.innerEapOptional = false;
        this.performServerValidation = true;
        this.trustedRootCACertificate = null;
    }
    
    public void setSsidName(final String ssidName) {
        if (ssidName != null) {
            this.ssidName = ssidName;
        }
    }
    
    public String getSsidName() {
        return this.ssidName;
    }
    
    public void setConnectionMode(final WlanConnectionMode connectionMode) {
        if (connectionMode != null) {
            this.connectionMode = connectionMode;
        }
    }
    
    public WlanConnectionMode getConnectionMode() {
        return this.connectionMode;
    }
    
    public void setAuthenticationType(final WlanAuthentication authentication) {
        if (authentication != null) {
            this.authentication = authentication;
        }
    }
    
    public WlanAuthentication getAuthenticationType() {
        return this.authentication;
    }
    
    public void setPassPhrase(final String passPhrase) {
        if (passPhrase != null) {
            this.passPhrase = passPhrase;
        }
    }
    
    public String getPassPhrase() {
        return this.passPhrase;
    }
    
    public WlanConnectionType getConnectionType() {
        return this.connectionType;
    }
    
    public void setAuthMode(final WlanAuthMode authMode) {
        if (authMode != null) {
            this.authMode = authMode;
        }
    }
    
    public WlanAuthMode getAuthMode() {
        return this.authMode;
    }
    
    public void setEapType(final WlanEapTypes eapType) {
        if (eapType != null) {
            this.eapType = eapType;
        }
    }
    
    public WlanEapTypes getEapType() {
        return this.eapType;
    }
    
    public void setInnerEapType(final WlanEapTypes innerEapType) {
        if (innerEapType != null) {
            this.innerEapType = innerEapType;
        }
    }
    
    public WlanEapTypes getInnerEapType() {
        return this.innerEapType;
    }
    
    public void setTrustedRootCAThumbrpint(final String trustedRootCAThumbrpint) {
        if (trustedRootCAThumbrpint != null) {
            this.trustedRootCAThumbrpint = trustedRootCAThumbrpint;
        }
    }
    
    public String getTrustedRootCAThumbrpint() {
        return this.trustedRootCAThumbrpint;
    }
    
    public void setFastReconnect(final boolean fastReconnect) {
        this.fastReconnect = fastReconnect;
    }
    
    public boolean getFastReconnect() {
        return this.fastReconnect;
    }
    
    public void setInnerEapOptional(final boolean innerEapOptional) {
        this.innerEapOptional = innerEapOptional;
    }
    
    public boolean getInnerEapOptional() {
        return this.innerEapOptional;
    }
    
    public void setPerformServerValidation(final boolean performServerValidation) {
        this.performServerValidation = performServerValidation;
    }
    
    public boolean getPerformServerValidation() {
        return this.performServerValidation;
    }
    
    public void setTrustedRootCACertificate(final String b64EncodedCert) {
        this.trustedRootCACertificate = b64EncodedCert;
    }
    
    public String getTrusterRootCACertificate() {
        return this.trustedRootCACertificate;
    }
}
