package com.me.mdm.server.windows.profile.payload.content.wifi.msm;

import com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap.WlanEapConfigElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanOneXElement implements WlanProfileBase
{
    private OMNamespace oneXElementNamespace;
    private OMElement oneXElementRoot;
    private OMElement authMode;
    private OMElement eapConfig;
    
    public WlanOneXElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.oneXElementNamespace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/networking/OneX/v1", "");
            this.oneXElementRoot = OMAbstractFactory.getOMFactory().createOMElement("OneX", this.oneXElementNamespace);
            (this.authMode = OMAbstractFactory.getOMFactory().createOMElement("authMode", (OMNamespace)null)).addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authMode, wifiProp.getAuthMode().getValue()));
            this.eapConfig = new WlanEapConfigElement(wifiProp).getOMElement();
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void addChildrenToRoot() {
        this.oneXElementRoot.addChild((OMNode)this.authMode);
        this.oneXElementRoot.addChild((OMNode)this.eapConfig);
    }
    
    @Override
    public OMElement getOMElement() {
        this.addChildrenToRoot();
        return this.oneXElementRoot;
    }
}
