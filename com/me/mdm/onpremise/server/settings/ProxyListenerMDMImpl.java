package com.me.mdm.onpremise.server.settings;

import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.windows.notification.WNSImpl;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredListener;

public class ProxyListenerMDMImpl implements ProxyConfiguredListener
{
    private static final String MDM = "mdm";
    
    public void proxyConfigured(final Properties proxyDetails) {
        WNSImpl.getInstance().reinitialize();
        final String apnsState = SyMUtil.getSyMParameter("apnsstate");
        if (apnsState != null && MDMEnrollmentUtil.getInstance().isAPNsConfigured()) {
            try {
                APNSImpl.getInstance().reinitialize();
                APNSImpl.getInstance().IsAPNsReachacble();
            }
            catch (final Throwable e) {
                Logger.getLogger("MDMLogger").log(Level.SEVERE, "ProxyListenerMDMImpl APNS reinitialize error ", e);
            }
        }
        if (DownloadManager.proxyType == 3) {
            MessageProvider.getInstance().unhideMessage("CLOSED_NETWORK_MDM");
        }
        else {
            MessageProvider.getInstance().hideMessage("CLOSED_NETWORK_MDM");
        }
    }
    
    public void addUrlsForDomainValidation() {
        ProxyConfiguredHandler.getInstance().addUrlTypeForDomainValidation("mdm");
    }
}
