package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanEapTypes;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanEapExtentionElement implements WlanProfileBase
{
    OMElement extensionsRoot;
    
    public WlanEapExtentionElement(final WiFiProperties wifiProp) {
        if (wifiProp.getEapType() == WlanEapTypes.EAP_PEAP) {
            this.extensionsRoot = OMAbstractFactory.getOMFactory().createOMElement("PeapExtensions", (OMNamespace)null);
            final OMNamespace peapConnectionPropertiesV2 = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/MsPeapConnectionPropertiesV2", "");
            final OMNamespace peapConnectionPropertiesV3 = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/MsPeapConnectionPropertiesV3", "");
            final OMElement performServerValidationElement = OMAbstractFactory.getOMFactory().createOMElement("PerformServerValidation", peapConnectionPropertiesV2);
            performServerValidationElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)performServerValidationElement, String.valueOf(wifiProp.getPerformServerValidation())));
            final OMElement acceptServerNameElement = OMAbstractFactory.getOMFactory().createOMElement("AcceptServerName", peapConnectionPropertiesV2);
            acceptServerNameElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)acceptServerNameElement, String.valueOf(false)));
            final OMElement peapExtensionsv2 = OMAbstractFactory.getOMFactory().createOMElement("PeapExtensionsV2", peapConnectionPropertiesV2);
            final OMElement allowPromptingCaNotFound = OMAbstractFactory.getOMFactory().createOMElement("AllowPromptingWhenServerCANotFound", peapConnectionPropertiesV3);
            allowPromptingCaNotFound.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)allowPromptingCaNotFound, String.valueOf(true)));
            peapExtensionsv2.addChild((OMNode)allowPromptingCaNotFound);
            this.extensionsRoot.addChild((OMNode)performServerValidationElement);
            this.extensionsRoot.addChild((OMNode)acceptServerNameElement);
            this.extensionsRoot.addChild((OMNode)peapExtensionsv2);
        }
        if (wifiProp.getEapType() == WlanEapTypes.EAP_TLS) {
            final OMNamespace TLSConnectionPropertiesV2 = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/EapTlsConnectionPropertiesV2", "");
            final OMNamespace TLSConnectionPropertiesV3 = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.microsoft.com/provisioning/EapTlsConnectionPropertiesV3", "");
            this.extensionsRoot = OMAbstractFactory.getOMFactory().createOMElement("TLSExtensions", TLSConnectionPropertiesV2);
            final OMElement performServerValidationElement = OMAbstractFactory.getOMFactory().createOMElement("PerformServerValidation", TLSConnectionPropertiesV2);
            performServerValidationElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)performServerValidationElement, String.valueOf(wifiProp.getPerformServerValidation())));
            final OMElement acceptServerNameElement = OMAbstractFactory.getOMFactory().createOMElement("AcceptServerName", TLSConnectionPropertiesV2);
            acceptServerNameElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)acceptServerNameElement, String.valueOf(false)));
            final OMElement filteringInfo = OMAbstractFactory.getOMFactory().createOMElement("FilteringInfo", TLSConnectionPropertiesV3);
            final OMElement CAHashList = OMAbstractFactory.getOMFactory().createOMElement("CAHashList", (OMNamespace)null);
            final OMAttribute enabled = OMAbstractFactory.getOMFactory().createOMAttribute("Enabled", (OMNamespace)null, "true");
            CAHashList.addAttribute(enabled);
            final OMElement issuerHash = OMAbstractFactory.getOMFactory().createOMElement("IssuerHash", (OMNamespace)null);
            if (wifiProp.getTrustedRootCAThumbrpint() != null) {
                issuerHash.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)issuerHash, wifiProp.getTrustedRootCAThumbrpint()));
            }
            CAHashList.addChild((OMNode)issuerHash);
            filteringInfo.addChild((OMNode)CAHashList);
            this.extensionsRoot.addChild((OMNode)performServerValidationElement);
            this.extensionsRoot.addChild((OMNode)acceptServerNameElement);
            this.extensionsRoot.addChild((OMNode)filteringInfo);
        }
    }
    
    @Override
    public OMElement getOMElement() {
        return this.extensionsRoot;
    }
}
