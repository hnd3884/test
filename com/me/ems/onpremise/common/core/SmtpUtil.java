package com.me.ems.onpremise.common.core;

import java.util.Hashtable;
import javax.mail.Transport;
import com.me.devicemanagement.framework.server.mailmanager.MailProcessor;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.mailmanager.MailerUtils;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SmtpUtil
{
    private Logger logger;
    private JSONObject mailFailureDetails;
    private static List<HashMap<String, String>> authServerDetails;
    
    public SmtpUtil() {
        this.logger = Logger.getLogger(SmtpUtil.class.getName());
        this.mailFailureDetails = new JSONObject();
    }
    
    public JSONObject getMailFailureDetails() {
        return this.mailFailureDetails;
    }
    
    public static List<HashMap<String, String>> getAuthServerDetails() {
        if (SmtpUtil.authServerDetails == null) {
            SmtpUtil.authServerDetails = new ArrayList<HashMap<String, String>>();
            final HashMap<String, String> gmail = new HashMap<String, String>();
            gmail.put("smtpHost", "smtp.gmail.com");
            gmail.put("authUrl", "https://accounts.google.com/o/oauth2/auth");
            gmail.put("tokenUrl", "https://oauth2.googleapis.com/token");
            gmail.put("scope", "https://mail.google.com/");
            gmail.put("urlParameters", "prompt=consent&access_type=offline");
            SmtpUtil.authServerDetails.add(gmail);
            final HashMap<String, String> office365 = new HashMap<String, String>();
            office365.put("smtpHost", "smtp.office365.com");
            office365.put("authUrl", "https://login.microsoftonline.com/common/oauth2/v2.0/authorize");
            office365.put("tokenUrl", "https://login.microsoftonline.com/common/oauth2/v2.0/token");
            office365.put("scope", "offline_access https://outlook.office.com/SMTP.Send");
            office365.put("urlParameters", "prompt=consent");
            SmtpUtil.authServerDetails.add(office365);
        }
        return SmtpUtil.authServerDetails;
    }
    
    public boolean checkMailServer(final Properties props) throws JSONException {
        final String smtpServer = ((Hashtable<K, String>)props).get("smtpHost");
        final String smtpPort = ((Hashtable<K, String>)props).get("smtpPort");
        final String userName = ((Hashtable<K, String>)props).get("smtpUserName");
        final String password = ((Hashtable<K, String>)props).get("smtpPassword");
        final boolean isTLSEnabled = Boolean.parseBoolean(((Hashtable<K, String>)props).get("tlsEnabled"));
        final boolean isSmtpsEnabled = Boolean.parseBoolean(((Hashtable<K, String>)props).get("smtpsEnabled"));
        final boolean isAuthNeeded = Boolean.parseBoolean(((Hashtable<K, String>)props).get("needAuthentication"));
        final String senderAddress = ((Hashtable<K, String>)props).get("senderAddress");
        String toAddress = ((Hashtable<K, String>)props).get("toAddress");
        final int authType = Integer.parseInt(((Hashtable<K, String>)props).get("authType"));
        if (toAddress == null || toAddress.equals("")) {
            toAddress = senderAddress;
        }
        return this.checkMailServer(smtpServer, smtpPort, userName, password, isTLSEnabled, isSmtpsEnabled, isAuthNeeded, senderAddress, toAddress, authType);
    }
    
    public void deleteSmtpSettings() {
        try {
            final DeleteQuery deleteSmtpQuery = (DeleteQuery)new DeleteQueryImpl("SmtpConfiguration");
            DataAccess.delete(deleteSmtpQuery);
        }
        catch (final DataAccessException dae) {
            this.logger.log(Level.WARNING, "SMTP Exception: Exception in deleteSmtpSettings", (Throwable)dae);
        }
    }
    
    private boolean checkMailServer(final String smtpServer, final String smtpPort, final String smtpUserName, final String smtpPassword, final boolean isTlsEnabled, final boolean isSmtpsEnabled, final boolean isAuthNeeded, final String senderAddress, final String toAddress, final int authType) throws JSONException {
        boolean isMailServerValid = false;
        final Transport transport = null;
        try {
            final int port = 25;
            if (smtpServer != null) {
                final Properties mailProps = MailerUtils.getInstance().gerMailServerPorperties(smtpServer, smtpPort, smtpUserName, smtpPassword, isTlsEnabled, isSmtpsEnabled);
                if (authType == 1) {
                    ((Hashtable<String, String>)mailProps).put("mail.smtp.auth.mechanisms", "XOAUTH2");
                }
                if (!isAuthNeeded && authType != 1) {
                    ((Hashtable<String, String>)mailProps).put("mail.smtp.auth", "false");
                    mailProps.remove("mail.smtp.user");
                    mailProps.remove("mail.smtp.user");
                    mailProps.remove("mail.smtp.password");
                }
                this.logger.log(Level.INFO, "SMTP Info: Checking the mail server configuration");
                MailerUtils.getInstance().validateSMTPSSSLCertificate(isSmtpsEnabled, smtpServer, smtpPort);
                final String description = getMailDescription(smtpServer, smtpPort, isTlsEnabled, isSmtpsEnabled);
                final MailDetails mailDetails = new MailDetails(senderAddress, toAddress);
                mailDetails.senderDisplayName = senderAddress;
                mailDetails.subject = MailerUtils.getInstance().getTestMailSub();
                mailDetails.bodyContent = description;
                mailDetails.attachment = null;
                this.mailFailureDetails = MailProcessor.getInstance().sendMail(mailDetails, mailProps);
                final boolean status = this.mailFailureDetails.getBoolean("Status");
                if (status) {
                    isMailServerValid = true;
                }
            }
            else {
                this.logger.log(Level.INFO, "SMTP Info: Mail Server Name : {0} Port: {1}", new Object[] { smtpServer, port });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "SMTP Exception: Exception in checkMailServer:", e);
            if (transport != null) {
                try {
                    transport.close();
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "SMTP Exception: Exception in Closing Connection:", e);
                }
            }
        }
        finally {
            if (transport != null) {
                try {
                    transport.close();
                }
                catch (final Exception e2) {
                    this.logger.log(Level.WARNING, "SMTP Exception: Exception in Closing Connection:", e2);
                }
            }
        }
        return isMailServerValid;
    }
    
    private static String getMailDescription(final String smtpServer, final String smtpPort, final boolean isTlsEnabled, final boolean isSmtpsEnabled) {
        final StringBuilder stf = new StringBuilder();
        stf.append(" <html>&nbsp;<div>\t");
        stf.append("<table border=\"0\" width=\"60%\" cellpadding=\"0\" cellspacing=\"0\" style=\"color: rgb(0, 0, 0); font-size: 13px; border: 1px solid rgb(245, 245, 245); font-family: Lato, Roboto; background-color: rgb(250, 250, 250);\">");
        stf.append("<tbody>");
        stf.append("<tr>");
        stf.append("<td style=\"padding: 10px 30px; color: rgb(255, 255, 255); background-color: rgb(153, 153, 153);\"><span style=\"font-size: 16px; font-weight: bold;\">" + MailerUtils.getInstance().getTestMailSub() + "</span>&nbsp;</td>");
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td>");
        stf.append("<div style=\"font-size: 13px; padding: 20px;\">");
        stf.append("<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" width=\"100%\" style=\"color: rgb(0, 0, 0); font-size: 13px; border: 1px solid rgb(245, 245, 245); font-family: Lato, Roboto;\">");
        stf.append("<tbody>");
        stf.append("<tr>");
        stf.append("<td width=\"50%\" style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Server Name</td>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">" + smtpServer + "</td>");
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Server Port</td>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">" + smtpPort + "</td>");
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Email Type</td>");
        if (isSmtpsEnabled) {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">SMTPS</td>");
        }
        else {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">SMTP</td>");
        }
        stf.append("</tr>");
        stf.append("<tr>");
        stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); font-weight: bold; background-color: rgb(255, 255, 255);\">Enable TLS </td>");
        if (isTlsEnabled) {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">Yes</td>");
        }
        else {
            stf.append("<td style=\"padding-left: 15px; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(245, 245, 245); background-color: rgb(255, 255, 255);\">No</td>");
        }
        stf.append("</tr>");
        stf.append("</tbody>");
        stf.append("</table>");
        stf.append("</div>");
        stf.append("</td>");
        stf.append("</tr>");
        stf.append("</tbody>");
        stf.append("</table>");
        stf.append("</div> </html>");
        return stf.toString();
    }
}
