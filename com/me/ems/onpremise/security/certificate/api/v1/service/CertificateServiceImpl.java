package com.me.ems.onpremise.security.certificate.api.v1.service;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.onpremise.security.certificate.api.model.CertificateChainObject;
import com.me.ems.onpremise.security.certificate.api.core.builders.CertificateChainBuilder;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.onpremise.security.certificate.api.constants.CertificateChainBuilderConstants;
import com.me.ems.onpremise.security.certificate.api.Exception.PromptException;
import com.me.ems.onpremise.security.certificate.api.Exception.CertificateAPIException;
import com.me.ems.onpremise.security.certificate.api.core.handlers.CertificateCacheHandler;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import com.me.ems.onpremise.security.certificate.api.core.handlers.ImportSSLCertificateChangeHandler;
import java.util.HashMap;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.onpremise.security.certificate.api.core.builders.DEROrCRTCertificateChainBuilder;
import java.util.List;
import com.me.ems.onpremise.security.certificate.api.core.builders.PFXCertificateChainBuilder;
import java.util.Properties;
import java.nio.file.Path;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.io.File;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.ems.onpremise.security.certificate.api.model.ImportCertificateResponse;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.security.certificate.api.model.CertificateFormBean;
import java.util.Set;
import java.util.Map;
import com.me.ems.onpremise.security.certificate.api.core.utils.SSLCertificateUtil;
import java.util.HashSet;
import com.me.ems.onpremise.security.certificate.api.core.utils.CertificateUtils;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.ems.onpremise.security.certificate.api.model.Certificate;
import com.me.ems.onpremise.security.certificate.factory.CertificateService;

public class CertificateServiceImpl implements CertificateService
{
    @Override
    public Certificate getCertificateDetails() {
        final Certificate certificate = new Certificate();
        Logger.getLogger("ImportCertificateLogger").log(Level.INFO, "loading import certificate Page!!!");
        certificate.setIsTrustedCommunicationEnabled(Boolean.parseBoolean(SecurityUtil.getSecurityParameter("SC_VALUE")));
        final String serverCertificatePath = CertificateUtils.getServerCertificateWebSettingsFilePath();
        if (serverCertificatePath == null) {
            return certificate;
        }
        final Map certificateDetails = CertificateUtils.getCertificateDetails(serverCertificatePath);
        Set hostNamesListSet = new HashSet();
        try {
            hostNamesListSet = SSLCertificateUtil.getInstance().getSSLCertificateHostNames();
            Logger.getLogger("ImportCertificateLogger").log(Level.INFO, "List of host Names " + hostNamesListSet);
        }
        catch (final Exception ex) {
            Logger.getLogger("ImportCertificateLogger").log(Level.INFO, "Exception in loading and getting certificate host name details" + ex);
        }
        certificate.setThirdPartyCertificateInstalled(Boolean.FALSE);
        if (certificateDetails == null) {
            return certificate;
        }
        if (null != certificateDetails.get("IssuerName") && "ManageEngineCA".equals(certificateDetails.get("IssuerName"))) {
            return certificate;
        }
        certificate.setThirdPartyCertificateInstalled(Boolean.TRUE);
        certificate.setCommonName(String.valueOf(certificateDetails.get("CertificateName")));
        certificate.setCreationDate(String.valueOf(certificateDetails.get("CreationDate")));
        certificate.setExpiryDate(String.valueOf(certificateDetails.get("ExpiryDate")));
        certificate.setIssuerName(String.valueOf(certificateDetails.get("IssuerName")));
        certificate.setIssuerOrganisationName(String.valueOf(certificateDetails.get("IssuerOrganizationName")));
        certificate.setSubjectAlternativeNames(hostNamesListSet);
        return certificate;
    }
    
    @Override
    public ImportCertificateResponse importCertificate(final CertificateFormBean certificateFormBean, final SecurityContext securityContext) throws CertificateAPIException {
        final ImportCertificateResponse importCertificateResponse = new ImportCertificateResponse();
        final String productDisplayName = ProductUrlLoader.getInstance().getValue("displayname");
        try {
            Boolean pfxIntermediateManualUpload = Boolean.FALSE;
            final Boolean confirmedSelfSignedCA = certificateFormBean.getConfirmedSelfSignedCA();
            final Boolean confirmedChangeInNatSettings = certificateFormBean.getConfirmedChangeInNatSettings();
            final String serverHome = System.getProperty("server.home");
            final String uploadFilePath = serverHome + File.separator + WebServerUtil.getWebServerName() + File.separator + "conf" + File.separator + "uploaded_files";
            Path intermediateCertificateFilePath = null;
            final String serverCertificateFileName = certificateFormBean.getServerCertificateFilePath().getFileName().toString();
            final String serverCertificateFilePath = uploadFilePath + File.separator + serverCertificateFileName;
            final String password = certificateFormBean.getPfxPassword();
            if (!certificateFormBean.getAutomatic()) {
                intermediateCertificateFilePath = certificateFormBean.getIntermediateCertificateFilePathList().get(0);
            }
            if (intermediateCertificateFilePath != null && !"".equals(intermediateCertificateFilePath.getFileName().toString())) {
                if (serverCertificateFileName.toLowerCase().endsWith("pfx") || serverCertificateFileName.toLowerCase().endsWith("jks") || serverCertificateFileName.toLowerCase().endsWith("keystore")) {
                    pfxIntermediateManualUpload = true;
                }
                certificateFormBean.setIntermediateGiven(true);
            }
            final String uploadedServerCertificateFilePath = uploadFilePath + File.separator + certificateFormBean.getServerCertificateFilePath().getFileName();
            List<Path> uploadedIntermediateCertificatesPath = certificateFormBean.getIntermediateCertificateFilePathList();
            if (uploadedIntermediateCertificatesPath != null) {
                uploadedIntermediateCertificatesPath = CertificateUtils.splitMultipleCertificatesInEachFileToCertificateFileList(uploadedIntermediateCertificatesPath);
            }
            CertificateChainBuilder certImportHandler = null;
            final Properties userConfigProperties = new Properties();
            userConfigProperties.setProperty("confirmedSelfSignedCA", confirmedSelfSignedCA.toString());
            userConfigProperties.setProperty("confirmedChangeInNatSettings", confirmedChangeInNatSettings.toString());
            userConfigProperties.setProperty("uploadedCertificateFilePath", uploadedServerCertificateFilePath);
            userConfigProperties.setProperty("pfxIntermediateManualUplaod", Boolean.toString(pfxIntermediateManualUpload));
            userConfigProperties.setProperty("productDisplayName", productDisplayName);
            userConfigProperties.setProperty("uploadedCertificateFileName", certificateFormBean.getServerCertificateFileName());
            final String certificateExtension = CertificateUtils.getExtension(serverCertificateFilePath);
            if (password != null && (certificateExtension.equalsIgnoreCase(".jks") || certificateExtension.equalsIgnoreCase(".pfx") || certificateExtension.equalsIgnoreCase(".keystore"))) {
                if (certificateExtension.equalsIgnoreCase(".jks")) {
                    userConfigProperties.setProperty("jksUpload", Boolean.toString(Boolean.TRUE));
                }
                else if (certificateExtension.equalsIgnoreCase(".keystore")) {
                    userConfigProperties.setProperty("keystoreUpload", Boolean.toString(Boolean.TRUE));
                }
                if (pfxIntermediateManualUpload) {
                    userConfigProperties.setProperty("pfxIntermediateManualUplaod", Boolean.toString(pfxIntermediateManualUpload));
                    certImportHandler = new PFXCertificateChainBuilder(certificateFormBean.getServerCertificateFilePath(), password, certificateFormBean.getIntermediateCertificateFilePathList(), userConfigProperties);
                }
                else {
                    certImportHandler = new PFXCertificateChainBuilder(certificateFormBean.getServerCertificateFilePath(), password, userConfigProperties);
                }
            }
            else {
                final Boolean isAutoDownloadIntermediateCerts = certificateFormBean.getAutomatic();
                userConfigProperties.setProperty("isAutoDownloadIntermediateCerts", isAutoDownloadIntermediateCerts.toString());
                if (uploadedIntermediateCertificatesPath != null && !uploadedIntermediateCertificatesPath.isEmpty()) {
                    certImportHandler = new DEROrCRTCertificateChainBuilder(certificateFormBean.getServerCertificateFilePath(), certificateFormBean.getPrivateKeyFilePath(), uploadedIntermediateCertificatesPath, userConfigProperties);
                }
                else if (isAutoDownloadIntermediateCerts) {
                    certImportHandler = new DEROrCRTCertificateChainBuilder(certificateFormBean.getServerCertificateFilePath(), certificateFormBean.getPrivateKeyFilePath(), userConfigProperties);
                }
            }
            String loginName = null;
            try {
                final User user = (User)securityContext.getUserPrincipal();
                loginName = ((user != null) ? user.getName() : null);
            }
            catch (final Exception ex) {
                Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Exception in getting currently logged in user name");
            }
            final HashMap cacheMap = new HashMap();
            CertificateChainObject certificateResponseObject = null;
            if (certImportHandler != null) {
                certificateResponseObject = certImportHandler.processRequest();
            }
            if (certificateResponseObject != null) {
                final ImportSSLCertificateChangeHandler certChangeHandler = new ImportSSLCertificateChangeHandler(certificateResponseObject, userConfigProperties);
                certChangeHandler.processRequest();
            }
            importCertificateResponse.setStatusCode(80000L);
            importCertificateResponse.setResponseMessage(I18N.getMsg("dc.ssl.import.certificate.success.status", new Object[] { productDisplayName }));
            importCertificateResponse.setPlaceHolderParams(new ArrayList());
            SecurityUtil.updateSecurityParameter("IMPORT_SSL_RESTART_REQUIRED", "true");
            cacheMap.put("CERT_CHAIN_NOT_VERIFIED", false);
            MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
            cacheMap.put("SSL_CERTIFICATE_EXPIRED", false);
            MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRED");
            MessageProvider.getInstance().hideMessage("SSL_CERTIFICATE_EXPIRY_MSG");
            DCEventLogUtil.getInstance().addEventLogEntry(121, loginName, "dc.ssl.import.certificate.success", (Object)null);
            if (this.isNATConfigurationNeeded()) {
                cacheMap.put("SSL_HOST_NAME_MISMATCH", true);
                MessageProvider.getInstance().unhideMessage("SSL_HOST_NAME_MISMATCH");
                importCertificateResponse.setStatusCode(80001L);
                importCertificateResponse.setResponseMessage(I18N.getMsg("dc.ssl.import.certificate.nat.configure.needed", new Object[0]));
                final List paramList = new ArrayList();
                paramList.add(SSLCertificateUtil.getInstance().getSSLCertificateHostNames().toString());
                importCertificateResponse.setPlaceHolderParams(paramList);
            }
            MessageProvider.getInstance().unhideMessage("REQUIRED_SERVICE_RESTART");
            SSLCertificateUtil.resetInstance();
            CertificateCacheHandler.getInstance().putAll(cacheMap);
        }
        catch (final Exception exception) {
            Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Exception in Import Certificate  : ", exception);
            if (exception instanceof CertificateAPIException) {
                throw (CertificateAPIException)exception;
            }
            if (!(exception instanceof PromptException)) {
                throw new CertificateAPIException("CERT001");
            }
            if (((PromptException)exception).getPromptCode().equals(CertificateChainBuilderConstants.CONFIRM_NAT_ADDRESS_MISMATCH_STATUS_CODE)) {
                importCertificateResponse.setStatusCode(CertificateChainBuilderConstants.CONFIRM_NAT_ADDRESS_MISMATCH_STATUS_CODE);
                importCertificateResponse.setPlaceHolderParams(new ArrayList());
                try {
                    if (Boolean.parseBoolean(SecurityUtil.getSecurityParameter("SC_VALUE"))) {
                        importCertificateResponse.setResponseMessage(I18N.getMsg("ems.ssl.import.cert.diff_nat_sc_enabled", new Object[0]));
                    }
                    else {
                        importCertificateResponse.setResponseMessage(I18NUtil.getJSMsgFromLocale(SyMUtil.getUserLocale(), "dc.ssl.cert.confirmNatSettings.message", new Object[0]));
                    }
                }
                catch (final Exception e) {
                    importCertificateResponse.setResponseMessage("Nat Settings change is required, Please confirm ");
                }
                return importCertificateResponse;
            }
        }
        finally {
            CertificateUtils.deleteUploadDirectory();
            SecurityOneLineLogger.log("Security_Management", "Import_Cert_Modify", certificateFormBean.toString(), Level.INFO);
        }
        return importCertificateResponse;
    }
    
    private void addFileToUploadList(final ArrayList<Path> listOfFilesToUpload, final Path certificateFilePath) {
        if (certificateFilePath != null && !"".equals(certificateFilePath.getFileName())) {
            listOfFilesToUpload.add(certificateFilePath);
        }
    }
    
    private boolean isNATConfigurationNeeded() {
        try {
            String givenNATAddress = null;
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            if (natProps != null && natProps.get("NAT_ADDRESS") != null && !((Hashtable<K, Object>)natProps).get("NAT_ADDRESS").equals("")) {
                givenNATAddress = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            }
            if (givenNATAddress != null) {
                givenNATAddress = givenNATAddress.trim();
                final Boolean isValid = SSLCertificateUtil.getInstance().checkHostNameValidWithSSL(givenNATAddress);
                return !isValid;
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Exception in determining if Nat configuration needed..", ex);
            Logger.getLogger("ImportCertificateLogger").severe("Exception in determining if Nat configuration needed..");
        }
        return false;
    }
}
