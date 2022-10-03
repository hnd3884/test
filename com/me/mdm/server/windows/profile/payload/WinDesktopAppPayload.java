package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;

public class WinDesktopAppPayload extends WinMobileAppPayload
{
    public WinDesktopAppPayload() {
        this.keyPrefix = "./Device";
    }
    
    @Override
    public void deleteApp() {
        (this.packageURL = OMAbstractFactory.getOMFactory().createOMElement("Package", (OMNamespace)null)).addAttribute("Name", "%PackageFullName%", (OMNamespace)null);
        this.packageURL.addAttribute("RemoveForAllUsers", "1", (OMNamespace)null);
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement("./Device/Vendor/MSFT/EnterpriseModernAppManagement/AppManagement/RemovePackage", this.packageURL.toString(), "xml"));
    }
    
    @Override
    public void enableSilentInstall() {
        final String keyName = this.keyPrefix + "/HostedInstall";
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, this.packageURL.toString(), "xml"));
    }
    
    @Override
    public void addLicenseBlob() {
        final String LicenseKey = "./Device/Vendor/MSFT/EnterpriseModernAppManagement/AppLicenses/StoreLicenses/%LicenseID%/AddLicense";
        final OMElement licenseXML = OMAbstractFactory.getOMFactory().createOMElement("License", (OMNamespace)null);
        licenseXML.addAttribute("Content", "%LicenseBlob%", (OMNamespace)null);
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement(LicenseKey, licenseXML.toString(), "xml"));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getExecPayloadCommand());
        return winConfigPayload;
    }
    
    public void setDeletePayload() {
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix));
    }
}
