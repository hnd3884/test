package com.adventnet.sym.webclient.common;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import java.util.logging.Level;
import com.adventnet.sym.server.util.SyMUtil;
import java.util.logging.Logger;

public class SYMClientUtil extends com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil
{
    private static Logger logger;
    
    private SYMClientUtil() {
    }
    
    public static void getFirewallAndDCOMStatus() throws SyMException {
        int fireWallAndDCOMStatus = -1;
        try {
            final int portNo = SyMUtil.getWebServerPort();
            SYMClientUtil.logger.log(Level.INFO, "getFirewallAndDCOMSettings portNo : {0}", portNo);
            final int httpsPortNo = SyMUtil.getSSLPort();
            SYMClientUtil.logger.log(Level.INFO, "getFirewallAndDCOMSettings httpsPortNo : {0}", httpsPortNo);
            final boolean webServerPortRes = WinAccessProvider.getInstance().isFirewallEnabledInDCServer((long)portNo);
            SYMClientUtil.logger.log(Level.INFO, "getFirewallAndDCOMSettings webServerPortRes : {0}", webServerPortRes);
            final boolean httpsPortRes = WinAccessProvider.getInstance().isFirewallEnabledInDCServer((long)httpsPortNo);
            SYMClientUtil.logger.log(Level.INFO, "getFirewallAndDCOMSettings httpsPortRes : {0}", httpsPortRes);
            if (webServerPortRes && httpsPortRes) {
                SYMClientUtil.logger.log(Level.INFO, "Firewall PORTs(http and https) are not opened.");
                fireWallAndDCOMStatus = 0;
            }
            else if (webServerPortRes && !httpsPortRes) {
                SYMClientUtil.logger.log(Level.INFO, "Firewall PORTs (http) is not opened and https port is opened.");
                fireWallAndDCOMStatus = 1;
            }
            else if (!webServerPortRes && httpsPortRes) {
                SYMClientUtil.logger.log(Level.INFO, "Firewall PORTs (http) is opened and https port is not opened.");
                fireWallAndDCOMStatus = 2;
            }
            else {
                SYMClientUtil.logger.log(Level.INFO, "Firewall PORTs (http and https) are opened.");
            }
            SYMClientUtil.logger.log(Level.INFO, "fireWallAndDCOMStatus : {0}", fireWallAndDCOMStatus);
            SyMUtil.updateSyMParameter("FIREWALL_AND_DCOM_STATUS", String.valueOf(fireWallAndDCOMStatus));
            if (fireWallAndDCOMStatus != -1) {
                final MessageProvider msgProvider = MessageProvider.getInstance();
                msgProvider.unhideMessage("PORT_BLOCKED");
            }
            else {
                MessageProvider.getInstance().hideMessage("PORT_BLOCKED");
            }
        }
        catch (final SyMException ex) {
            SYMClientUtil.logger.log(Level.WARNING, "Caught SyMException : ", (Throwable)ex);
            throw ex;
        }
    }
    
    public static String getPasswordChangedRead() {
        String domains = "";
        try {
            final List domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps((Long)null);
            for (int i = 0; i < domainList.size(); ++i) {
                final Properties props = domainList.get(i);
                final String domainName = ((Hashtable<K, String>)props).get("NAME");
                final Boolean validationStatus = ((Hashtable<K, Boolean>)props).get("VALIDATION_STATUS");
                if (validationStatus.equals(Boolean.FALSE)) {
                    if (domains.length() == 0) {
                        domains = domainName;
                    }
                    else {
                        domains = domains + ", " + domainName;
                    }
                }
            }
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.SEVERE, "Exception ", e);
        }
        return domains;
    }
    
    public static String getPasswordChangedDomainWrite() {
        String domains = "";
        try {
            final List domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps((Long)null);
            for (int i = 0; i < domainList.size(); ++i) {
                final Properties props = domainList.get(i);
                final String domainName = ((Hashtable<K, String>)props).get("NAME");
                final Boolean validationStatus = ((Hashtable<K, Boolean>)props).get("VALIDATION_STATUS");
                if (validationStatus.equals(Boolean.FALSE)) {
                    final String domainEditURL = "&nbsp;<a onclick=\"javascript:editDomainDetails('" + domainName + "');\">" + domainName + "</a>";
                    if (domains.length() == 0) {
                        domains = domainEditURL;
                    }
                    else {
                        domains = domains + "," + domainEditURL;
                    }
                }
            }
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.SEVERE, "Exception ", e);
        }
        return domains;
    }
    
    static {
        SYMClientUtil.logger = Logger.getLogger("ADLogger");
    }
}
