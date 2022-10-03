package com.me.mdm.server.windows.profile.payload.content.wifi.msm;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanSecurityElement implements WlanProfileBase
{
    private OMElement securityRoot;
    private OMElement authEncryption;
    private OMElement sharedKey;
    private OMElement oneX;
    
    public WlanSecurityElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.securityRoot = OMAbstractFactory.getOMFactory().createOMElement("security", (OMNamespace)null);
            this.makeSecurityBlob(wifiProp);
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void makeSecurityBlob(final WiFiProperties wifiProp) throws Exception {
        switch (wifiProp.getAuthenticationType()) {
            case OPEN: {
                this.authEncryption = new WlanAuthEncryptionElement(wifiProp).getOMElement();
                break;
            }
            case WPA_PERSONAL:
            case WPA2_PERSONAL: {
                this.authEncryption = new WlanAuthEncryptionElement(wifiProp).getOMElement();
                this.sharedKey = new WlanSharedKeyElement(wifiProp).getOMElement();
                break;
            }
            case WPA_ENTERPRISE:
            case WPA2_ENTERPRISE: {
                this.authEncryption = new WlanAuthEncryptionElement(wifiProp).getOMElement();
                this.oneX = new WlanOneXElement(wifiProp).getOMElement();
                break;
            }
        }
    }
    
    private void addChildrenToRoot() {
        if (this.authEncryption != null) {
            this.securityRoot.addChild((OMNode)this.authEncryption);
        }
        if (this.sharedKey != null) {
            this.securityRoot.addChild((OMNode)this.sharedKey);
        }
        if (this.oneX != null) {
            this.securityRoot.addChild((OMNode)this.oneX);
        }
    }
    
    @Override
    public OMElement getOMElement() {
        this.addChildrenToRoot();
        return this.securityRoot;
    }
}
