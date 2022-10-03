package com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap;

import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanEapTypes;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfileBase;

public class WlanEapTypeElement implements WlanProfileBase
{
    private OMNamespace eapTypeElementNamespace;
    private OMElement eapTypeElementRoot;
    private OMElement serverValidation;
    private OMElement fastReconnect;
    private OMElement innerEapOptional;
    private OMElement eap;
    private OMElement extensions;
    private OMElement credentialsSource;
    private OMElement useWinLogonCredentials;
    private boolean isInnerEap;
    
    public WlanEapTypeElement(final WiFiProperties wifiProp, final boolean innerEap) throws Exception {
        try {
            this.isInnerEap = innerEap;
            if (innerEap) {
                this.eapTypeElementNamespace = OMAbstractFactory.getOMFactory().createOMNamespace(this.getEapTypeNamespace(wifiProp.getInnerEapType()), "");
                this.eapTypeElementRoot = OMAbstractFactory.getOMFactory().createOMElement("EapType", this.eapTypeElementNamespace);
                this.useWinLogonCredentials = this.getWinLogonCredentials(false);
            }
            else {
                this.eapTypeElementNamespace = OMAbstractFactory.getOMFactory().createOMNamespace(this.getEapTypeNamespace(wifiProp.getEapType()), "");
                this.eapTypeElementRoot = OMAbstractFactory.getOMFactory().createOMElement("EapType", this.eapTypeElementNamespace);
                this.addProtocolDependentTags(wifiProp);
                this.extensions = new WlanEapExtentionElement(wifiProp).getOMElement();
            }
        }
        catch (final Exception exception) {
            throw exception;
        }
    }
    
    private void addProtocolDependentTags(final WiFiProperties wifiProp) throws Exception {
        switch (wifiProp.getEapType()) {
            case EAP_PEAP: {
                this.serverValidation = this.getServerValidationElement(wifiProp);
                this.fastReconnect = this.getFastReconnect(wifiProp.getFastReconnect());
                this.innerEapOptional = this.getInnerEapOptional(wifiProp.getInnerEapOptional());
                if (wifiProp.getInnerEapType() != null) {
                    this.eap = new WlanEapElement(wifiProp, true).getOMElement();
                    break;
                }
                break;
            }
            case EAP_TLS: {
                this.credentialsSource = this.getCredentialsSource();
                this.serverValidation = this.getServerValidationElement(wifiProp);
                break;
            }
        }
    }
    
    private OMElement getCredentialsSource() {
        final OMElement credentialsSource = OMAbstractFactory.getOMFactory().createOMElement("CredentialsSource", (OMNamespace)null);
        final OMElement certificateStore = OMAbstractFactory.getOMFactory().createOMElement("CertificateStore", (OMNamespace)null);
        final OMElement simpleCertSelection = OMAbstractFactory.getOMFactory().createOMElement("SimpleCertSelection", (OMNamespace)null);
        simpleCertSelection.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)simpleCertSelection, "true"));
        certificateStore.addChild((OMNode)simpleCertSelection);
        credentialsSource.addChild((OMNode)certificateStore);
        return credentialsSource;
    }
    
    private String getEapTypeNamespace(final WlanEapTypes innerEapType) {
        switch (innerEapType) {
            case EAP_PEAP: {
                return "http://www.microsoft.com/provisioning/MsPeapConnectionPropertiesV1";
            }
            case EAP_MSCHAPv2: {
                return "http://www.microsoft.com/provisioning/MsChapV2ConnectionPropertiesV1";
            }
            case EAP_TLS: {
                return "http://www.microsoft.com/provisioning/EapTlsConnectionPropertiesV1";
            }
            default: {
                return null;
            }
        }
    }
    
    private OMElement getServerValidationElement(final WiFiProperties wifiProp) {
        final OMElement serverValidationRoot = OMAbstractFactory.getOMFactory().createOMElement("ServerValidation", (OMNamespace)null);
        final OMElement disableUserPromptForServerValidation = OMAbstractFactory.getOMFactory().createOMElement("DisableUserPromptForServerValidation", (OMNamespace)null);
        disableUserPromptForServerValidation.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)disableUserPromptForServerValidation, "false"));
        final OMElement serverNames = OMAbstractFactory.getOMFactory().createOMElement("ServerNames", (OMNamespace)null);
        final OMElement trustedRootCA = OMAbstractFactory.getOMFactory().createOMElement("TrustedRootCA", (OMNamespace)null);
        if (wifiProp.getTrustedRootCAThumbrpint() != null) {
            trustedRootCA.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)trustedRootCA, wifiProp.getTrustedRootCAThumbrpint()));
        }
        serverValidationRoot.addChild((OMNode)disableUserPromptForServerValidation);
        serverValidationRoot.addChild((OMNode)serverNames);
        serverValidationRoot.addChild((OMNode)trustedRootCA);
        return serverValidationRoot;
    }
    
    private OMElement getFastReconnect(final boolean fastRecon) {
        final OMElement fastReconnectElement = OMAbstractFactory.getOMFactory().createOMElement("FastReconnect", (OMNamespace)null);
        fastReconnectElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)fastReconnectElement, String.valueOf(fastRecon)));
        return fastReconnectElement;
    }
    
    private OMElement getInnerEapOptional(final boolean innerEapOptional) {
        final OMElement innerEapOptionalElement = OMAbstractFactory.getOMFactory().createOMElement("InnerEapOptional", (OMNamespace)null);
        innerEapOptionalElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)innerEapOptionalElement, String.valueOf(innerEapOptional)));
        return innerEapOptionalElement;
    }
    
    private OMElement getWinLogonCredentials(final boolean useWinLogon) {
        final OMElement useWinLogonElement = OMAbstractFactory.getOMFactory().createOMElement("UseWinLogonCredentials", (OMNamespace)null);
        useWinLogonElement.addChild((OMNode)OMAbstractFactory.getOMFactory().createOMText((OMContainer)useWinLogonElement, String.valueOf(useWinLogon)));
        return useWinLogonElement;
    }
    
    private void addChildrenToRoot() {
        if (this.isInnerEap) {
            this.eapTypeElementRoot.addChild((OMNode)this.useWinLogonCredentials);
        }
        else {
            if (this.credentialsSource != null) {
                this.eapTypeElementRoot.addChild((OMNode)this.credentialsSource);
            }
            if (this.serverValidation != null) {
                this.eapTypeElementRoot.addChild((OMNode)this.serverValidation);
            }
            if (this.fastReconnect != null) {
                this.eapTypeElementRoot.addChild((OMNode)this.fastReconnect);
            }
            if (this.innerEapOptional != null) {
                this.eapTypeElementRoot.addChild((OMNode)this.innerEapOptional);
            }
            if (this.eap != null) {
                this.eapTypeElementRoot.addChild((OMNode)this.eap);
            }
            if (this.extensions != null) {
                this.eapTypeElementRoot.addChild((OMNode)this.extensions);
            }
        }
    }
    
    @Override
    public OMElement getOMElement() {
        this.addChildrenToRoot();
        return this.eapTypeElementRoot;
    }
}
