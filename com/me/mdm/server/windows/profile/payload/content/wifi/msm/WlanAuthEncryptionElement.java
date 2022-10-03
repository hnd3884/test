package com.me.mdm.server.windows.profile.payload.content.wifi.msm;

import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanEncryption;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanAuthentication;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanAuthEncryptionElement implements WlanProfileBase
{
    private OMElement authEncryptionRoot;
    private OMElement authentication;
    private OMElement encryption;
    private OMElement useOneX;
    
    public WlanAuthEncryptionElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.authEncryptionRoot = OMAbstractFactory.getOMFactory().createOMElement("authEncryption", (OMNamespace)null);
            this.authentication = OMAbstractFactory.getOMFactory().createOMElement("authentication", (OMNamespace)null);
            this.encryption = OMAbstractFactory.getOMFactory().createOMElement("encryption", (OMNamespace)null);
            this.useOneX = OMAbstractFactory.getOMFactory().createOMElement("useOneX", (OMNamespace)null);
            this.makeAuthEncryption(wifiProp);
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void makeAuthEncryption(final WiFiProperties wifiProp) {
        switch (wifiProp.getAuthenticationType()) {
            case OPEN: {
                this.authentication.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authentication, WlanAuthentication.OPEN.getValue()));
                this.encryption.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.encryption, WlanEncryption.NONE.getValue()));
                this.useOneX.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.useOneX, "false"));
                break;
            }
            case WPA_PERSONAL: {
                this.authentication.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authentication, WlanAuthentication.WPA_PERSONAL.getValue()));
                this.encryption.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.encryption, WlanEncryption.TKIP.getValue()));
                this.useOneX.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.useOneX, "false"));
                break;
            }
            case WPA2_PERSONAL: {
                this.authentication.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authentication, WlanAuthentication.WPA2_PERSONAL.getValue()));
                this.encryption.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.encryption, WlanEncryption.AES.getValue()));
                this.useOneX.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.useOneX, "false"));
                break;
            }
            case WPA_ENTERPRISE: {
                this.authentication.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authentication, WlanAuthentication.WPA_ENTERPRISE.getValue()));
                this.encryption.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.encryption, WlanEncryption.AES.getValue()));
                this.useOneX.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.useOneX, "false"));
                break;
            }
            case WPA2_ENTERPRISE: {
                this.authentication.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authentication, WlanAuthentication.WPA2_ENTERPRISE.getValue()));
                this.encryption.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.encryption, WlanEncryption.AES.getValue()));
                this.useOneX.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.useOneX, "true"));
                break;
            }
        }
    }
    
    private void addChildrenToRoot() {
        this.authEncryptionRoot.addChild((OMNode)this.authentication);
        this.authEncryptionRoot.addChild((OMNode)this.encryption);
        this.authEncryptionRoot.addChild((OMNode)this.useOneX);
    }
    
    @Override
    public OMElement getOMElement() {
        this.addChildrenToRoot();
        return this.authEncryptionRoot;
    }
}
