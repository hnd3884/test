package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import java.util.List;
import org.json.JSONObject;
import org.apache.axiom.om.OMElement;

public class WinMobileAppPayload extends WindowsPayload
{
    String enterpriseID;
    String productID;
    String keyPrefix;
    OMElement packageURL;
    
    public WinMobileAppPayload() {
        this.enterpriseID = null;
        this.productID = null;
        this.keyPrefix = null;
        this.packageURL = null;
        this.keyPrefix = "./User";
    }
    
    public void initializePayload(final JSONObject jsonObject, final String task) {
        try {
            this.enterpriseID = jsonObject.optString("enterpriseID");
            this.productID = String.valueOf(jsonObject.get("productID"));
            this.keyPrefix = this.keyPrefix + "/Vendor/MSFT/EnterpriseModernAppManagement/AppInstallation/" + this.productID;
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void setURL(String url, final List dependency) {
        final String keyName = this.keyPrefix + "/HostedInstall";
        if (url == null) {
            url = "%DownloadURL%";
        }
        (this.packageURL = OMAbstractFactory.getOMFactory().createOMElement("Application", (OMNamespace)null)).addAttribute("PackageUri", url, (OMNamespace)null);
        if (dependency != null && dependency.size() != 0 && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotDistributeDependencies")) {
            final OMElement dependencies = OMAbstractFactory.getOMFactory().createOMElement("Dependencies", (OMNamespace)null);
            for (String path : dependency) {
                if (path.contains("/getmFile.do?")) {
                    path = path.replace("/getmFile.do?&fid=", "/api/v1/mdm/getmfiles/");
                    path = path.replace("%authtoken%", "?%authtoken%&service=mdm");
                }
                final OMElement dep = OMAbstractFactory.getOMFactory().createOMElement("Dependency", (OMNamespace)null);
                dep.addAttribute("PackageUri", "https://%ServerName%:%ServerPort%" + path.replaceAll("\\\\", "/"), (OMNamespace)null);
                dependencies.addChild((OMNode)dep);
            }
            this.packageURL.addChild((OMNode)dependencies);
        }
        else {
            this.packageURL.setText("%DependencySection%");
        }
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, this.packageURL.toString(), "xml"));
    }
    
    public void enableSilentInstall() {
        final String keyName = this.keyPrefix + "/HostedInstall";
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, this.packageURL.toString(), "xml"));
    }
    
    public void addLicenseBlob() {
        final String LicenseKey = "./User/Vendor/MSFT/EnterpriseModernAppManagement/AppLicenses/StoreLicenses/%LicenseID%/AddLicense";
        final OMElement licenseXML = OMAbstractFactory.getOMFactory().createOMElement("License", (OMNamespace)null);
        licenseXML.addAttribute("Content", "%LicenseBlob%", (OMNamespace)null);
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement(LicenseKey, licenseXML.toString(), "xml"));
    }
    
    public void deleteApp() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getAddPayloadCommand());
        winConfigPayload.setPayloadContent(this.getExecPayloadCommand());
        winConfigPayload.setNonAtomicPayloadContent(this.getNonAtomicDeletePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificUpdatePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getAddPayloadCommand());
        winConfigPayload.setPayloadContent(this.getExecPayloadCommand());
        winConfigPayload.setNonAtomicPayloadContent(this.getNonAtomicDeletePayloadCommand());
        return winConfigPayload;
    }
}
