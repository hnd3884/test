package com.me.mdm.onpremise.server.settings;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Date;
import java.util.HashMap;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.certificate.CertificateCacheHandler;
import com.me.mdm.core.enrollment.AndroidQREnrollmentHandler;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.settings.nat.NATObject;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.settings.nat.NATListener;

public class NATListenerMDMImpl implements NATListener
{
    private static Logger logger;
    
    public void natModified(final NATObject obj) {
        NATListenerMDMImpl.logger.log(Level.INFO, "Inside NatChnaged()");
        try {
            MDMUtil.addOrUpdateMDMServerInfo();
            AppsUtil.getInstance().regenerateManifestFile();
            DEPEnrollmentUtil.createAndAssignAllDEPProfileAsynchronously();
            AppleConfiguratorEnrollmentHandler.openUrlChangeMsg();
            new AndroidQREnrollmentHandler().regenerateQRCodes();
            final HashMap map = CertificateCacheHandler.getInstance().getAll();
            if (map != null) {
                CertificateCacheHandler.getInstance().put("SSL_HOST_NAME_MISMATCH", (Object)false);
                MessageProvider.getInstance().hideMessage("SSL_HOST_NAME_MISMATCH");
            }
            else {
                final String NATAddress = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties().getProperty("NAT_ADDRESS", "");
                SSLCertificateUtil.getInstance().verifyCertificate(NATAddress);
            }
            MessageProvider.getInstance().hideMessage("NAT_NOT_CONFIGURED");
            MessageProvider.getInstance().hideMessage("NAT_RECOMMENDATION");
            if ("true".equals(MDMUtil.getSyMParameter("forwarding_server_config")) && obj.isRegenerateRequired) {
                MessageProvider.getInstance().unhideMessage("REQUIRED_FORWARDINGSERVER_UPDATE_CERTIFICATE");
            }
            if (obj.isRegenerateRequired) {
                MDMMessageHandler.getInstance().messageAction("DOWNLOAD_PPKG_TOOL", (Long)null);
                MDMMessageHandler.getInstance().messageAction("DOWNLOAD_LAPTOP_TOOL", (Long)null);
            }
            final Date sslCreationDate = SSLCertificateUtil.getInstance().getSSLCertificateCreationDate();
            ApiFactoryProvider.getCacheAccessAPI().putCache("SSL_CREATION_DATE", (Object)sslCreationDate, 1);
            ApiFactoryProvider.getCacheAccessAPI().putCache("FQDN", (Object)obj.givenNATAddress, 1);
            NATReachabilityTask.isNATexposed(obj.givenNATAddress, ((Hashtable<K, Integer>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_HTTPS_PORT"));
            new UserAssignmentRuleHandler().postUserAssignmentSettingsforAllCustomers(Boolean.TRUE);
        }
        catch (final Exception ex) {
            NATListenerMDMImpl.logger.log(Level.WARNING, "Exception in natChanged()", ex);
        }
    }
    
    public String isNATUpdateSafe(final NATObject object) {
        try {
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
            final String givenNATAddress = object.givenNATAddress;
            final Boolean isNATValidWithCert = SSLCertificateUtil.getInstance().checkHostNameValidWithSSL(givenNATAddress);
            final HashMap natPorts = object.natPorts;
            final String newHTTPSPort = natPorts.get("NAT_HTTPS_PORT");
            final String oldHTTPSPort = ((Hashtable<K, Object>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_HTTPS_PORT").toString();
            if (managedDeviceCount > 0 && (!isNATValidWithCert || (newHTTPSPort != null && oldHTTPSPort != null && !newHTTPSPort.equals(oldHTTPSPort)))) {
                return "update_not_safe";
            }
        }
        catch (final Exception ex) {
            NATListenerMDMImpl.logger.log(Level.SEVERE, null, ex);
        }
        return "update_safe";
    }
    
    public HashMap setValuesInNATForm(final HashMap dynaForm) {
        return dynaForm;
    }
    
    public NATObject getNATports() {
        return new NATObject();
    }
    
    static {
        NATListenerMDMImpl.logger = Logger.getLogger("ServerSettingsLogger");
    }
}
