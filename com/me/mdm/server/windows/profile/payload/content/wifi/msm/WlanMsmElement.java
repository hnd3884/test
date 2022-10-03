package com.me.mdm.server.windows.profile.payload.content.wifi.msm;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanMsmElement implements WlanProfileBase
{
    private OMElement msmRoot;
    private OMElement security;
    
    public WlanMsmElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.msmRoot = OMAbstractFactory.getOMFactory().createOMElement("MSM", (OMNamespace)null);
            this.security = new WlanSecurityElement(wifiProp).getOMElement();
            this.msmRoot.addChild((OMNode)this.security);
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    @Override
    public OMElement getOMElement() {
        return this.msmRoot;
    }
}
