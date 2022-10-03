package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanEapElement implements WlanProfileBase
{
    private OMNamespace eapElementNamespace;
    private OMElement eapElementRoot;
    private OMElement type;
    private OMElement eapType;
    
    public WlanEapElement(final WiFiProperties wifiProp, final boolean isInnerEap) throws Exception {
        try {
            this.eapElementNamespace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/BaseEapConnectionPropertiesV1", "");
            this.eapElementRoot = OMAbstractFactory.getOMFactory().createOMElement("Eap", this.eapElementNamespace);
            this.type = OMAbstractFactory.getOMFactory().createOMElement("Type", (OMNamespace)null);
            if (!isInnerEap) {
                this.type.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.type, wifiProp.getEapType().getValue()));
            }
            else {
                this.type.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)this.type, wifiProp.getInnerEapType().getValue()));
            }
            this.eapType = new WlanEapTypeElement(wifiProp, isInnerEap).getOMElement();
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    @Override
    public OMElement getOMElement() {
        this.eapElementRoot.addChild((OMNode)this.type);
        this.eapElementRoot.addChild((OMNode)this.eapType);
        return this.eapElementRoot;
    }
}
