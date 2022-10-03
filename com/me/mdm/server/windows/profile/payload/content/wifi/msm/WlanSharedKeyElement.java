package com.me.mdm.server.windows.profile.payload.content.wifi.msm;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanSharedKeyElement implements WlanProfileBase
{
    private OMElement sharedKeyRoot;
    private OMElement keyType;
    private OMElement protectedElement;
    private OMElement keyMaterial;
    
    public WlanSharedKeyElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.sharedKeyRoot = OMAbstractFactory.getOMFactory().createOMElement("sharedKey", (OMNamespace)null);
            this.keyType = OMAbstractFactory.getOMFactory().createOMElement("keyType", (OMNamespace)null);
            this.protectedElement = OMAbstractFactory.getOMFactory().createOMElement("protected", (OMNamespace)null);
            this.keyMaterial = OMAbstractFactory.getOMFactory().createOMElement("keyMaterial", (OMNamespace)null);
            this.makeSharedKey(wifiProp);
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void makeSharedKey(final WiFiProperties wifiProp) {
        this.keyType.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.keyType, "passPhrase"));
        this.protectedElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.protectedElement, "false"));
        this.keyMaterial.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.keyMaterial, wifiProp.getPassPhrase()));
    }
    
    private void addChildrenToRoot() {
        this.sharedKeyRoot.addChild((OMNode)this.keyType);
        this.sharedKeyRoot.addChild((OMNode)this.protectedElement);
        this.sharedKeyRoot.addChild((OMNode)this.keyMaterial);
    }
    
    @Override
    public OMElement getOMElement() {
        this.addChildrenToRoot();
        return this.sharedKeyRoot;
    }
}
