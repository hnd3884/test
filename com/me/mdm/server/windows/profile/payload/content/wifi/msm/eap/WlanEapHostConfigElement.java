package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanEapHostConfigElement implements WlanProfileBase
{
    private OMNamespace eapHostConfigNamespace;
    private OMElement eapHostConfigRoot;
    private OMElement eapMethod;
    private OMElement config;
    
    public WlanEapHostConfigElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.eapHostConfigNamespace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/EapHostConfig", "");
            this.eapHostConfigRoot = OMAbstractFactory.getOMFactory().createOMElement("EapHostConfig", this.eapHostConfigNamespace);
            this.eapMethod = new WlanEapMethodElement(wifiProp).getOMElement();
            this.config = new WlanConfigElement(wifiProp).getOMElement();
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    @Override
    public OMElement getOMElement() {
        this.eapHostConfigRoot.addChild((OMNode)this.eapMethod);
        this.eapHostConfigRoot.addChild((OMNode)this.config);
        return this.eapHostConfigRoot;
    }
}
