package com.me.devicemanagement.onpremise.webclient.admin;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;

public class FirewallAndDCOMPortAction
{
    String className;
    Logger somLogger;
    
    public FirewallAndDCOMPortAction() {
        this.className = FirewallAndDCOMPortAction.class.getName();
        this.somLogger = Logger.getLogger(this.className);
    }
    
    public boolean configureFirewallAndDCOMSettings(final String firewallAndDCOMStatus) {
        boolean configureStatus = true;
        try {
            int enableValue = -1;
            final String httpchatnioStatus = SyMUtil.getSyMParameter("SHOW_CHAT_PORT_FIREWALL_EXCEPTION");
            if (firewallAndDCOMStatus != null && !firewallAndDCOMStatus.equals("")) {
                enableValue = Integer.parseInt(firewallAndDCOMStatus);
            }
            if (enableValue != -1 || httpchatnioStatus.equals("true")) {
                final int portNo = SyMUtil.getWebServerPort();
                final int httpsPort = SyMUtil.getSSLPort();
                final int httpnioPort = SyMUtil.getNioWebServerPort();
                final WinAccessProvider winAccess = WinAccessProvider.getInstance();
                this.somLogger.log(Level.INFO, "Recevied firewall port : " + portNo);
                this.somLogger.log(Level.INFO, "Recevied https port : " + httpsPort);
                this.somLogger.log(Level.INFO, "Recevied enableValue : " + enableValue);
                if (enableValue == 0) {
                    if (winAccess.openFirewallPort((long)portNo)) {
                        this.somLogger.log(Level.INFO, "Firewall PORT " + portNo + " opened successfully");
                    }
                    else {
                        this.somLogger.log(Level.INFO, "Problem while opening Firewall port " + portNo);
                        configureStatus = false;
                    }
                    if (winAccess.openFirewallPort((long)httpsPort)) {
                        this.somLogger.log(Level.INFO, "HTTPS PORT " + httpsPort + " opened successfully");
                    }
                    else {
                        this.somLogger.log(Level.INFO, "Problem while opening HTTPS PORT " + httpsPort);
                        configureStatus = false;
                    }
                }
                else if (enableValue == 1) {
                    if (winAccess.openFirewallPort((long)portNo)) {
                        this.somLogger.log(Level.INFO, "Firewall PORT " + portNo + " opened successfully");
                    }
                    else {
                        this.somLogger.log(Level.INFO, "Problem while opening Firewall port " + portNo);
                        configureStatus = false;
                    }
                }
                else if (enableValue == 2) {
                    if (winAccess.openFirewallPort((long)httpsPort)) {
                        this.somLogger.log(Level.INFO, "HTTPS PORT " + httpsPort + " opened successfully");
                    }
                    else {
                        this.somLogger.log(Level.INFO, "Problem while opening HTTPS PORT " + httpsPort);
                        configureStatus = false;
                    }
                }
                if (httpchatnioStatus != null && !httpchatnioStatus.equals("") && httpchatnioStatus.equals("true")) {
                    if (winAccess.openFirewallPort((long)httpnioPort)) {
                        this.somLogger.log(Level.INFO, "HTTP NIO PORT " + httpnioPort + " opened successfully");
                    }
                    else {
                        this.somLogger.log(Level.INFO, "Problem while opening HTTP NIO PORT " + httpnioPort);
                        configureStatus = false;
                    }
                }
                if (configureStatus) {
                    SyMUtil.updateSyMParameter("FIREWALL_AND_DCOM_STATUS", "-1");
                    SyMUtil.updateSyMParameter("SHOW_CHAT_PORT_FIREWALL_EXCEPTION", "false");
                    final MessageProvider msgProvider = MessageProvider.getInstance();
                    msgProvider.hideMessage("PORT_BLOCKED");
                }
                this.somLogger.log(Level.INFO, "Enable value should be 0 or 1 or 2. Wrong enable value : " + enableValue);
            }
            this.somLogger.log(Level.INFO, "configureStatus :" + configureStatus);
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.WARNING, "Exception while confiuring firewall and DCOM settings", ex);
        }
        return configureStatus;
    }
}
