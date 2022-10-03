package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanConfigElement implements WlanProfileBase
{
    private OMNamespace configElementNamespace;
    private OMElement configElementRoot;
    private OMElement eap;
    
    public WlanConfigElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.configElementNamespace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/EapHostConfig", "");
            this.configElementRoot = OMAbstractFactory.getOMFactory().createOMElement("Config", this.configElementNamespace);
            this.eap = new WlanEapElement(wifiProp, false).getOMElement();
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    @Override
    public OMElement getOMElement() {
        this.configElementRoot.addChild((OMNode)this.eap);
        return this.configElementRoot;
    }
}
