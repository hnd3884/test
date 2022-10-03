package com.me.devicemanagement.onpremise.server.general;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredListener;

public class ProxyListenerGeneralImpl implements ProxyConfiguredListener
{
    private static Logger logger;
    
    @Override
    public void proxyConfigured(final Properties proxyDetails) {
        final DownloadManager downloadMgr = DownloadManager.getInstance();
        downloadMgr.setProxyConfiguration(proxyDetails);
        MessageProvider.getInstance().hideMessage("PROXY_NOT_CONFIGURED");
        String proxyTypeStr = "";
        final int proxyType = DownloadManager.proxyType;
        if (proxyType == 1) {
            proxyTypeStr = "Direct Connection";
        }
        else if (proxyType == 2) {
            proxyTypeStr = "Manual Proxy";
        }
        else if (proxyType == 3) {
            proxyTypeStr = "No Connection To Internet";
        }
        else if (proxyType == 4) {
            proxyTypeStr = "Auto Proxy Script";
        }
        if (proxyType != 3) {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "DomainValidationTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            final Properties userProps = new Properties();
            ((Hashtable<String, String>)userProps).put("DBUpdateValidation", "false");
            ((Hashtable<String, String>)userProps).put("proxyValidation", "true");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.devicemanagement.onpremise.server.util.DomainValidator", taskInfoMap, userProps);
        }
        try {
            final String owner = DMUserHandler.getDCUser(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID());
            final String i18n = "desktopcentral.webclient.patch.updated_proxy_settings";
            DCEventLogUtil.getInstance().addEvent(121, owner, (HashMap)null, i18n, (Object)proxyTypeStr, false);
        }
        catch (final Exception e) {
            ProxyListenerGeneralImpl.logger.log(Level.WARNING, "Exception in ProxyListenerGeneralImpl:proxyConfigured() ", e);
        }
    }
    
    @Override
    public void addUrlsForDomainValidation() {
    }
    
    static {
        ProxyListenerGeneralImpl.logger = Logger.getLogger(ProxyListenerGeneralImpl.class.getName());
    }
}
