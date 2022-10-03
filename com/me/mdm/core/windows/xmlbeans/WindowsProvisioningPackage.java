package com.me.mdm.core.windows.xmlbeans;

import java.util.ArrayList;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.UUID;
import org.json.JSONObject;
import javax.xml.bind.annotation.XmlElement;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "WindowsCustomizations")
public class WindowsProvisioningPackage
{
    static Logger logger;
    private ProvisioningPackageConfig packageConfig;
    private CustomizationSettings settings;
    
    public ProvisioningPackageConfig getPackageConfig() {
        return this.packageConfig;
    }
    
    @XmlElement(name = "PackageConfig")
    public void setPackageConfig(final ProvisioningPackageConfig packageConfig) {
        this.packageConfig = packageConfig;
    }
    
    public CustomizationSettings getSettings() {
        return this.settings;
    }
    
    @XmlElement(name = "Settings")
    public void setSettings(final CustomizationSettings settings) {
        this.settings = settings;
    }
    
    public static WindowsProvisioningPackage getProvisioningPackageBean(final JSONObject dataJson) {
        final WindowsProvisioningPackage provisioningPackage = new WindowsProvisioningPackage();
        final ProvisioningPackageConfig provisioningPackageConfig = new ProvisioningPackageConfig();
        provisioningPackageConfig.setPackageGUID("{" + UUID.randomUUID() + "}");
        provisioningPackageConfig.setPackageName("AdminEnrollment");
        provisioningPackageConfig.setPackageVersion("1.0");
        provisioningPackageConfig.setPackageOwnerType("OEM");
        provisioningPackageConfig.setPackageRank("1");
        provisioningPackageConfig.setXmlNameSpace("urn:schemas-Microsoft-com:Windows-ICD-Package-Config.v1.0");
        provisioningPackage.setPackageConfig(provisioningPackageConfig);
        final CustomizationSettings customSettings = new CustomizationSettings();
        customSettings.setXmlNameSpace("urn:schemas-microsoft-com:windows-provisioning");
        final Customization customization = new Customization();
        final CommonPayloadCollection commonPayloadCollection = new CommonPayloadCollection();
        Boolean isThirdPartyCertificate = Boolean.FALSE;
        try {
            isThirdPartyCertificate = dataJson.getBoolean("isThirdPartyCertificate");
        }
        catch (final JSONException exp) {
            WindowsProvisioningPackage.logger.log(Level.SEVERE, "Exception while generating customizationXML for Windows Admin Enrollment PPKG {0}", (Throwable)exp);
        }
        if (!isThirdPartyCertificate) {
            final CertificatesPPKGPayload certificatesPPKGPayload = getCertificatesPayload(dataJson);
            commonPayloadCollection.setCertificatesPayload(certificatesPPKGPayload);
        }
        final WorkplacePPKGPayload workplaceEnrollmentPayload = getWorkplaceEnrollmentPayload(dataJson);
        commonPayloadCollection.setWorkplaceEnrollmentPayload(workplaceEnrollmentPayload);
        customization.setCommonSettings(commonPayloadCollection);
        customSettings.setCustomization(customization);
        provisioningPackage.setSettings(customSettings);
        return provisioningPackage;
    }
    
    private static CertificatesPPKGPayload getCertificatesPayload(final JSONObject dataJson) {
        final CertificatesPPKGPayload certificateListPPKGPayload = new CertificatesPPKGPayload();
        final RootCertificatesContainer rootCertificatesContainer = new RootCertificatesContainer();
        final ArrayList<RootCertificate> rootCertificateList = new ArrayList<RootCertificate>();
        String certName = null;
        String certPath = null;
        try {
            certName = String.valueOf(dataJson.get("certificateName"));
            certPath = String.valueOf(dataJson.get("certificatePath"));
        }
        catch (final JSONException exp) {
            WindowsProvisioningPackage.logger.log(Level.SEVERE, "Exception while generating CertificatePayload for CustomizationXML {0}", (Throwable)exp);
        }
        final RootCertificate serverSelfSignedCARootCertificate = new RootCertificate();
        serverSelfSignedCARootCertificate.setCertificateName(certName);
        serverSelfSignedCARootCertificate.setName(certName);
        serverSelfSignedCARootCertificate.setCertificatePath(certPath);
        rootCertificateList.add(serverSelfSignedCARootCertificate);
        rootCertificatesContainer.setRootCertificateList(rootCertificateList);
        certificateListPPKGPayload.setRootCertificates(rootCertificatesContainer);
        return certificateListPPKGPayload;
    }
    
    private static WorkplacePPKGPayload getWorkplaceEnrollmentPayload(final JSONObject dataJson) {
        final WorkplacePPKGPayload workplaceEnrollmentPayload = new WorkplacePPKGPayload();
        final EnrollmentContainer enrollmentContainer = new EnrollmentContainer();
        final ArrayList<EnrollmentUserPrincipalName> enrollmentUserPrincipalList = new ArrayList<EnrollmentUserPrincipalName>();
        String technicianMailId = null;
        String discoveryServiceUrl = null;
        String secret = null;
        String authPolicy = null;
        try {
            technicianMailId = String.valueOf(dataJson.get("technicianMailId"));
            discoveryServiceUrl = String.valueOf(dataJson.get("discoveryServiceUrl"));
            secret = String.valueOf(dataJson.get("secret"));
            authPolicy = String.valueOf(dataJson.get("authPolicy"));
        }
        catch (final JSONException exp) {
            WindowsProvisioningPackage.logger.log(Level.SEVERE, "Exception while generating WorkplacePayload for CustomizationXML {0}", (Throwable)exp);
        }
        final EnrollmentUserPrincipalName enrollmentUserPrincipalName = new EnrollmentUserPrincipalName();
        enrollmentUserPrincipalName.setUserPrincipalName(technicianMailId);
        enrollmentUserPrincipalName.setName(technicianMailId);
        enrollmentUserPrincipalName.setAuthPolicy(authPolicy);
        enrollmentUserPrincipalName.setDiscoveryServiceUrl(discoveryServiceUrl);
        enrollmentUserPrincipalName.setAuthSecret(secret);
        enrollmentUserPrincipalList.add(enrollmentUserPrincipalName);
        enrollmentContainer.setUserPrincipalEnrollments(enrollmentUserPrincipalList);
        workplaceEnrollmentPayload.setErollmentContainer(enrollmentContainer);
        return workplaceEnrollmentPayload;
    }
    
    static {
        WindowsProvisioningPackage.logger = Logger.getLogger("MDMEnrollment");
    }
}
