package com.me.mdm.server.windows.profile.payload.content.wifi;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMElement;

public class WlanSsidConfigElement implements WlanProfileBase
{
    private OMElement ssidConfigRoot;
    private OMElement ssid;
    private OMElement name;
    private OMText nameValue;
    private String ssidName;
    
    public WlanSsidConfigElement(final String ssidName) throws Exception {
        try {
            this.ssidName = ssidName;
            this.makeSSIDConfig();
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void makeSSIDConfig() {
        this.ssidConfigRoot = OMAbstractFactory.getOMFactory().createOMElement("SSIDConfig", (OMNamespace)null);
        this.ssid = OMAbstractFactory.getOMFactory().createOMElement("SSID", (OMNamespace)null);
        this.name = OMAbstractFactory.getOMFactory().createOMElement("name", (OMNamespace)null);
        this.nameValue = OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.name, this.ssidName);
        this.name.addChild((OMNode)this.nameValue);
        this.ssid.addChild((OMNode)this.name);
        this.ssidConfigRoot.addChild((OMNode)this.ssid);
    }
    
    @Override
    public OMElement getOMElement() {
        return this.ssidConfigRoot;
    }
}
