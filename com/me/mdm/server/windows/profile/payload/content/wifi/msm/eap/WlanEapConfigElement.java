package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanEapConfigElement implements WlanProfileBase
{
    private OMElement eapConfigRoot;
    private OMElement eapHostConfig;
    
    public WlanEapConfigElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.eapConfigRoot = OMAbstractFactory.getOMFactory().createOMElement("EAPConfig", (OMNamespace)null);
            this.eapHostConfig = new WlanEapHostConfigElement(wifiProp).getOMElement();
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    @Override
    public OMElement getOMElement() {
        this.eapConfigRoot.addChild((OMNode)this.eapHostConfig);
        return this.eapConfigRoot;
    }
    
    @Override
    public String toString() {
        return this.eapHostConfig.toString();
    }
}
