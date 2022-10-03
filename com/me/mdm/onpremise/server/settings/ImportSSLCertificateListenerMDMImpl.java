package com.me.mdm.onpremise.server.settings;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.onpremise.security.certificate.api.core.handlers.CertificateCacheHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.security.cert.X509Certificate;
import org.json.JSONObject;
import com.me.ems.onpremise.security.certificate.api.core.events.ImportSSLCertificateChangeEvent;
import java.util.Date;
import com.me.mdm.server.enrollment.ios.IOSUpgradeMobileConfigCommandHandler;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.ems.onpremise.security.certificate.api.core.utils.SSLCertificateUtil;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import java.util.logging.Logger;
import com.me.ems.onpremise.security.certificate.api.core.listeners.ImportSSLCertificateListener;

public class ImportSSLCertificateListenerMDMImpl implements ImportSSLCertificateListener
{
    private static final String SYNC_DATA_URL = "/syncDataServlet";
    Logger logger;
    
    public ImportSSLCertificateListenerMDMImpl() {
        this.logger = Logger.getLogger("ImportCertificateLogger");
    }
    
    public void certificateChanged() {
        try {
            this.resetCertificateTypeCache();
            DEPEnrollmentUtil.createAndAssignAllDEPProfileAsynchronously();
            final Date sslCreationDate = SSLCertificateUtil.getInstance().getSSLCertificateCreationDate();
            if ("true".equals(MDMUtil.getSyMParameter("forwarding_server_config"))) {
                try {
                    this.logger.log(Level.INFO, "Syncing changed certificate in forwarding server");
                    final String response = FwsUtil.getResponseFromSecureGatewayServer("/syncDataServlet");
                    this.logger.log(Level.INFO, "Response from forwarding server {0}", response);
                }
                catch (final Exception e) {
                    this.logger.log(Level.INFO, "Exception occured during certificate sync ", e);
                }
            }
            ApiFactoryProvider.getCacheAccessAPI().putCache("SSL_CREATION_DATE", (Object)sslCreationDate, 1);
            MDMMessageHandler.getInstance().messageAction("DOWNLOAD_PPKG_TOOL", (Long)null);
            MDMMessageHandler.getInstance().messageAction("DOWNLOAD_LAPTOP_TOOL", (Long)null);
            Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "ServerSSLCertificateListenerMDMImpl: Adding upgrade mobile config for eligible devices.");
            IOSUpgradeMobileConfigCommandHandler.getInstance().addIosUpgradeMobileConfigCommand((Long)null, false, false);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in natChanged()", ex);
        }
    }
    
    public JSONObject canUploadCertificate(final ImportSSLCertificateChangeEvent changeEvent) {
        final JSONObject remarkJSON = new JSONObject();
        try {
            if (changeEvent.certificateType == 3) {
                if (this.validateEnterpriseCA((X509Certificate)changeEvent.certificateChainObject.getRootCACertificate())) {
                    this.logger.log(Level.INFO, "Enterprise CA found to be known hence allowing certificate import.");
                    remarkJSON.put("status", true);
                    return remarkJSON;
                }
                final int managedDeviceCount = ManagedDeviceHandler.getInstance().getAppleManagedDeviceCount() + ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount();
                if (managedDeviceCount > 5) {
                    remarkJSON.put("status", false);
                    remarkJSON.put("errorCode", 80029);
                    remarkJSON.put("errorMsg", (Object)"Many devices already enrolled will become unmanaged");
                    remarkJSON.put("errorKey", (Object)"dc.ssl.server.certificate.enterprise_ca.restrict");
                    return remarkJSON;
                }
                if (managedDeviceCount > 0) {
                    remarkJSON.put("status", false);
                    remarkJSON.put("errorCode", 80027);
                    remarkJSON.put("errorMsg", (Object)"Few devices already enrolled will become unmanaged, get confirmation");
                    remarkJSON.put("errorKey", (Object)"dc.ssl.cert.confirmSelfSignedCA.message");
                    return remarkJSON;
                }
                remarkJSON.put("status", true);
                return remarkJSON;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in preCertificateUpload()", ex);
        }
        return null;
    }
    
    private void resetCertificateTypeCache() {
        CertificateCacheHandler.getInstance().put("certificateType", (Object)null);
    }
    
    private boolean validateEnterpriseCA(final X509Certificate enterpriseCA) throws Exception {
        final String rootCAThumbPrint = SyMUtil.getSyMParameter("root_certificate_thumbprint");
        if (rootCAThumbPrint == null) {
            return Boolean.FALSE;
        }
        if (rootCAThumbPrint.equals(SSLCertificateUtil.getInstance().getThumbPrint(enterpriseCA))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
