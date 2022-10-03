package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanEapMethodElement implements WlanProfileBase
{
    private OMElement eapMethodRoot;
    private OMNamespace eapChildNamespace;
    private OMElement type;
    private OMElement vendorId;
    private OMElement vendorType;
    private OMElement authorId;
    
    public WlanEapMethodElement(final WiFiProperties wifiProp) throws Exception {
        try {
            this.eapMethodRoot = OMAbstractFactory.getOMFactory().createOMElement("EapMethod", (OMNamespace)null);
            this.eapChildNamespace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/EapCommon", "");
            (this.type = OMAbstractFactory.getOMFactory().createOMElement("Type", this.eapChildNamespace)).addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.type, wifiProp.getEapType().getValue()));
            (this.vendorId = OMAbstractFactory.getOMFactory().createOMElement("VendorId", this.eapChildNamespace)).addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.vendorId, "0"));
            (this.vendorType = OMAbstractFactory.getOMFactory().createOMElement("VendorType", this.eapChildNamespace)).addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.vendorType, "0"));
            (this.authorId = OMAbstractFactory.getOMFactory().createOMElement("AuthorId", this.eapChildNamespace)).addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.authorId, "0"));
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void addChildrenToRoot() {
        this.eapMethodRoot.addChild((OMNode)this.type);
        this.eapMethodRoot.addChild((OMNode)this.vendorId);
        this.eapMethodRoot.addChild((OMNode)this.vendorType);
        this.eapMethodRoot.addChild((OMNode)this.authorId);
    }
    
    @Override
    public OMElement getOMElement() {
        this.addChildrenToRoot();
        return this.eapMethodRoot;
    }
}
