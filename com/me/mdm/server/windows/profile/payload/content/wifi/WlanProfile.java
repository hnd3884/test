package com.me.mdm.server.windows.profile.payload.content.wifi;

import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanConnectionMode;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanConnectionType;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import com.me.mdm.server.windows.profile.payload.content.wifi.msm.WlanMsmElement;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

public class WlanProfile
{
    private OMNamespace wlanProfileNamespace;
    private OMElement wlanProfileRoot;
    private OMElement name;
    private OMElement ssidConfig;
    private OMElement connectionType;
    private OMElement connectionMode;
    private OMElement msm;
    
    public WlanProfile(final WiFiProperties wifiProp) throws Exception {
        try {
            if (wifiProp.getSsidName() != null) {
                this.wlanProfileNamespace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/networking/WLAN/profile/v1", "");
                this.wlanProfileRoot = OMAbstractFactory.getOMFactory().createOMElement("WLANProfile", this.wlanProfileNamespace);
                this.name = OMAbstractFactory.getOMFactory().createOMElement("name", (OMNamespace)null);
                this.setSsidName(wifiProp.getSsidName());
                this.ssidConfig = new WlanSsidConfigElement(wifiProp.getSsidName()).getOMElement();
                this.connectionType = OMAbstractFactory.getOMFactory().createOMElement("connectionType", (OMNamespace)null);
                this.setConnectionType(wifiProp.getConnectionType());
                this.connectionMode = OMAbstractFactory.getOMFactory().createOMElement("connectionMode", (OMNamespace)null);
                this.setConnectionMode(wifiProp.getConnectionMode());
                this.msm = new WlanMsmElement(wifiProp).getOMElement();
            }
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void setSsidName(final String ssidName) {
        this.name.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.name, ssidName));
    }
    
    private void setConnectionType(final WlanConnectionType type) {
        this.connectionType.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.connectionType, type.getValue()));
    }
    
    private void setConnectionMode(final WlanConnectionMode autoConnect) {
        this.connectionMode.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.connectionMode, autoConnect.getValue()));
    }
    
    protected void addChildrenToRoot() {
        this.wlanProfileRoot.addChild((OMNode)this.name);
        this.wlanProfileRoot.addChild((OMNode)this.ssidConfig);
        this.wlanProfileRoot.addChild((OMNode)this.connectionType);
        this.wlanProfileRoot.addChild((OMNode)this.connectionMode);
        this.wlanProfileRoot.addChild((OMNode)this.msm);
    }
    
    @Override
    public String toString() {
        this.addChildrenToRoot();
        String wlanXml = this.wlanProfileRoot.toString();
        wlanXml = wlanXml.replaceAll(" xmlns=\"\"", "");
        return wlanXml;
    }
}
