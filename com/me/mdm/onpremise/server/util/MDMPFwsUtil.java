package com.me.mdm.onpremise.server.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.mail.MailHandler;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMPFwsUtil
{
    private static Logger logger;
    public static final String UPGRADE_SGS_MSG = "UPGRADE_SGS";
    public static final String CHECK_SGS_COMPATIBILITY = "CHECK_SGS_COMPATIBILITY";
    
    public static boolean sendMailForFws(final String mailSubject, final String mailContent, final String serverURL) {
        String remarksText = "dc.admin.fws_server_down_mail_event";
        String userName = "";
        try {
            final String moduleName = "FwServer";
            MDMPFwsUtil.logger.log(Level.INFO, "MAil send method called..");
            final Properties smtpProps = ApiFactoryProvider.getMailSettingAPI().getMailServerDetailsProps();
            final String strToAddress = SyMUtil.getEMailAddress(moduleName);
            final DataObject dobj = SyMUtil.getEmailAddDO(moduleName);
            if (dobj.isEmpty()) {
                MDMPFwsUtil.logger.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return false;
            }
            final Row emailAlertRow = dobj.getRow("EMailAddr");
            if (emailAlertRow == null) {
                MDMPFwsUtil.logger.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return false;
            }
            final Boolean isEnable = (Boolean)emailAlertRow.get("SEND_MAIL");
            if (!isEnable) {
                MDMPFwsUtil.logger.log(Level.WARNING, "E-Mail Alerts is Stopped !!!");
                return false;
            }
            if (strToAddress == null) {
                MDMPFwsUtil.logger.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
                return false;
            }
            MDMPFwsUtil.logger.log(Level.WARNING, "SMTP Properties :    {0}", smtpProps);
            final String frAdd = ((Hashtable<K, String>)smtpProps).get("mail.fromAddress");
            final String subject = I18N.getMsg("dc.admin.fws.mailsubject", new Object[0]);
            final MailDetails maildetails = new MailDetails(frAdd, strToAddress);
            maildetails.senderDisplayName = ((Hashtable<K, String>)smtpProps).get("mail.fromName");
            maildetails.bodyContent = mailContent;
            maildetails.ccAddress = null;
            maildetails.subject = mailSubject;
            maildetails.attachment = null;
            MailHandler.getInstance().addToMailQueue(maildetails, 0);
            remarksText = I18N.getMsg(remarksText, new Object[] { serverURL, strToAddress });
            userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            DCEventLogUtil.getInstance().addEvent(8001, userName, (HashMap)null, remarksText, (Object)null, true);
            return true;
        }
        catch (final Exception ex) {
            MDMPFwsUtil.logger.log(Level.WARNING, "Exception while sending alert mail", ex);
            return false;
        }
    }
    
    public static void checkFwServerUp() {
        MDMPFwsUtil.logger.log(Level.INFO, "checkFwServerUp() is going to execute from DCGlobalTask");
        final String configuredStatus = SyMUtil.getSyMParameter("forwarding_server_config");
        if (configuredStatus != null && configuredStatus.equalsIgnoreCase("true")) {
            try {
                final Properties natProps = NATHandler.getNATConfigurationProperties();
                String serverName = null;
                if (FwsUtil.fsProps.getProperty("publicIP") != null) {
                    serverName = FwsUtil.fsProps.getProperty("publicIP");
                }
                else {
                    serverName = natProps.getProperty("NAT_ADDRESS");
                }
                final int httpsPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
                final Boolean serverRunning = FwsUtil.isServerUp(serverName, httpsPort);
                String mailSubject = null;
                final StringBuilder mailContent = new StringBuilder();
                final String link = "https://" + serverName + ":" + httpsPort;
                if (!serverRunning) {
                    mailSubject = I18N.getMsg("dc.admin.fws.mail.subject", new Object[] { null });
                    mailContent.append("<div style=\"height: 40%;margin: 5%;width: 80%;padding: 3%;border: 1px solid #c2bbbb;border-left: 5px solid #F44336;\">");
                    mailContent.append(I18N.getMsg("dc.admin.fos.mail.admin", new Object[0]));
                    mailContent.append("<p style=\"margin-top: 4%;font: 16px Lato;color: red;\">");
                    mailContent.append(I18N.getMsg("mdm.admin.fws.mail.server_down_content", new Object[] { link, ProductUrlLoader.getInstance().getValue("mdmUrl") + "/how-to/mdm-securing-server-communication-forwarding-server.html#troubleshooting_tips&mdmpi" }));
                    mailContent.append("<p style=\"margin-top: 5%;font: 14px Lato;\">");
                    mailContent.append(I18N.getMsg("dc.admin.fos.sign", new Object[0]));
                    mailContent.append("</p>\n");
                    mailContent.append("<p style=\"margin-top: -1%;font: 11px Lato;color: #8a6e6e;\">");
                    mailContent.append(I18N.getMsg("dc.admin.fos.automated_mail_warning", new Object[0]));
                    mailContent.append("</p></div>");
                    sendMailForFws(mailSubject, mailContent.toString(), link);
                    return;
                }
                Boolean crtSyncFailed = Boolean.TRUE;
                final JSONObject serverStatus = FwsUtil.getFwsServerStatus(serverName, httpsPort);
                crtSyncFailed = FwsUtil.getCertSyncDetails(serverName, httpsPort);
                if (crtSyncFailed) {
                    return;
                }
                mailSubject = I18N.getMsg("mdm.admin.fws.issue_mail_subject", new Object[] { null });
                mailContent.append("<div style='margin: 5%; width: 80%; padding: 3%;border: 1px solid #c2bbbb;border-left: 5px solid #F44336;'>");
                mailContent.append(I18N.getMsg("dc.admin.fos.mail.admin", new Object[0]));
                mailContent.append("<p>");
                mailContent.append(I18N.getMsg("mdm.admin.fws.mail_issue_header", new Object[0]));
                mailContent.append("</p>");
                mailContent.append("<ul style=\"line-height:2;\">");
                if (!crtSyncFailed) {
                    mailContent.append("<li>");
                    mailContent.append(I18N.getMsg("mdm.admin.fws.crt_sync_failed", new Object[] { ProductUrlLoader.getInstance().getValue("prodUrl") + "/how-to/mdm-securing-server-communication-forwarding-server.html#troubleshooting_tips&mdmpi" }));
                    mailContent.append("</li>");
                }
                mailContent.append("</ul>");
                mailContent.append("</p>");
                mailContent.append("  <p style='margin-top: 5%;font: 14px Lato;'>");
                mailContent.append(I18N.getMsg("dc.admin.fos.sign", new Object[0]));
                mailContent.append("</p>\n");
                mailContent.append("<p style='margin-top: -1%;font: 11px Lato;color: #8a6e6e;'>");
                mailContent.append(I18N.getMsg("dc.admin.fos.automated_mail_warning", new Object[0]));
                mailContent.append("</p></div>");
                sendMailForFws(mailSubject, mailContent.toString(), link);
            }
            catch (final DataAccessException e) {
                MDMPFwsUtil.logger.log(Level.SEVERE, "DataAccessException while checking forwarding server status", (Throwable)e);
            }
            catch (final Exception e2) {
                MDMPFwsUtil.logger.log(Level.SEVERE, "Exception while checking forwarding server status", e2);
            }
        }
        showOrHideSgsIncompatibilityMessage();
    }
    
    public static void showOrHideSgsIncompatibilityMessage() {
        try {
            final boolean isSgsConfigured = Boolean.parseBoolean(SyMUtil.getSyMParameter("forwarding_server_config"));
            final boolean isSgsUpToDate = FwsUtil.fsProps == null || FwsUtil.fsProps.getProperty("buildNumber") == null || FwsUtil.isSecureGatewayServerUpToDate();
            if (isSgsConfigured && !isSgsUpToDate) {
                final String checkCompatibility = SyMUtil.getSyMParameter("CHECK_SGS_COMPATIBILITY");
                if (checkCompatibility == null || Boolean.parseBoolean(checkCompatibility)) {
                    MessageProvider.getInstance().unhideMessage("UPGRADE_SGS");
                    SyMUtil.updateSyMParameter("CHECK_SGS_COMPATIBILITY", Boolean.FALSE.toString());
                    MDMPFwsUtil.logger.log(Level.INFO, "Open SGS upgrade message");
                }
            }
            else {
                MessageProvider.getInstance().hideMessage("UPGRADE_SGS");
                MDMPFwsUtil.logger.log(Level.INFO, "Hide SGS upgrade message as it may be up to date or may not be configured");
            }
        }
        catch (final Exception ex) {
            MDMPFwsUtil.logger.log(Level.INFO, "Error while checking for SGS compatibility", ex);
        }
    }
    
    static {
        MDMPFwsUtil.logger = Logger.getLogger(FwsUtil.class.getName());
    }
}
